package dev.fg.buildyourownx.libs.navigation_visualizer.ui.state

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import dev.fg.buildyourownx.libs.navigation_visualizer.model.HikerLogEntry
import dev.fg.buildyourownx.libs.navigation_visualizer.model.NavigationOrigin
import dev.fg.buildyourownx.libs.navigation_visualizer.utils.NavUtils

class HikerState {
    val logEntries = mutableStateListOf<HikerLogEntry>()
    val resumedDurations = mutableStateMapOf<String, Long>()

    private val resumeStartTimes = mutableMapOf<String, Long>()
    private val lastNavigationTimes = mutableMapOf<String, Long>()
    private val observedEntries = mutableSetOf<String>()

    fun detectOrigin(
        destination: NavDestination,
        bundle: Bundle?,
        stackTrace: Array<StackTraceElement>,
        isTypeSafe: Boolean
    ): NavigationOrigin {
        // Check if it is deepLink
        val deepLinkOrigin = detectDeepLink(bundle)
        if (deepLinkOrigin != null) return deepLinkOrigin

        val callerIndex = if (isTypeSafe) 8 else 7

        // Check for start destination & back press
        for (i in (callerIndex - 2)..(callerIndex + 2)) {
            val frame = stackTrace.getOrNull(i) ?: continue

            if (frame.methodName == "performTrampolineDispatch") {
                return NavigationOrigin.StartDestination
            }

            if (matchesBackPress(frame)) {
                val info = (frame.fileName?.removeSuffix(".kt") ?: "Unknown") + "." + frame.methodName
                return NavigationOrigin.BackPress(info)
            }
        }

        val callerFrame = stackTrace.getOrNull(callerIndex)
        return if (callerFrame != null) {
            NavigationOrigin.Explicit(
                fileName = callerFrame.fileName ?: "Unknown",
                lineNumber = callerFrame.lineNumber,
                methodName = callerFrame.methodName
            )
        } else {
            NavigationOrigin.Unknown
        }
    }

    private fun detectDeepLink(bundle: Bundle?): NavigationOrigin? {
        if (bundle == null) return null
        val key = "android-support-nav:controller:deepLinkIntent"
        @Suppress("DEPRECATION")
        val intent = bundle.getParcelable(key) as? Intent ?: return null
        val dataUri = intent.data?.toString() ?: return null
        val internalPrefix = "android-app://androidx.navigation/"
        return if (dataUri.startsWith(internalPrefix)) {
            null
        } else {
            NavigationOrigin.DeepLink(dataUri)
        }
    }

    private fun matchesBackPress(frame: StackTraceElement): Boolean {
        val fileName = frame.fileName ?: return false
        val method = frame.methodName
        if (method == "popBackStack" && fileName.contains("DialogNavigator")) return true
        if (method == "popBackStack" && fileName.contains("ComposeNavigator")) return true
        if (method == "popBackStack" && fileName.contains("FragmentNavigator")) return true
        if (method == "popBackStack" && fileName.contains("ActivityNavigator")) return true
        if (method == "popWithTransition" && fileName.contains("NavController")) return true
        if (method == "popBackStack" && fileName.contains("NavController")) return true
        if (method == "navigateUp" && fileName.contains("NavController")) return true
        if (method.contains("onBackPressed", ignoreCase = true)) return true
        if (method == "handleOnBackPressed") return true
        return false
    }

    fun getPotentialWarnings(
        entryId: String,
        route: String,
        origin: NavigationOrigin,
        backstackSize: Int
    ) : List<String> {
        val now = System.currentTimeMillis()

        val warnings = mutableListOf<String>()
        // 1. Check for rapid navigation to the same screen
        val lastTime = lastNavigationTimes[route]
        if (lastTime != null && (now - lastTime) < DOUBLE_CLICK_THRESHOLD_MS) {
            warnings.add("Possible double-click: navigated to \"$route\" twice within ${now - lastTime}ms")
        }
        lastNavigationTimes[route] = now

        // 2. Check for rapid navigation overall
        val recentCount = logEntries.count { (now - it.timestamp) < 1000L }
        if (recentCount >= 3) {
            warnings.add("Navigation burst: $recentCount navigations in the last second. Possible loop or runaway effect.")
        }
        // 3. Check for duplicate route in backstack (same route stacked multiple times)
        val duplicateCount = logEntries.count {
            it.routeName == route && it.entryId != entryId
        }
        if (duplicateCount >= 2 && origin is NavigationOrigin.Explicit) {
            warnings.add("Duplicate route: \"$route\" appears ${duplicateCount + 1} times in navigation history. Consider using launchSingleTop.")
        }

        // 3. Check for stack depth
        if (backstackSize > DEEP_BACKSTACK_THRESHOLD) {
            warnings.add("Deep backstack: $backstackSize entries. Possible navigation leak.")
        }

        return warnings
    }

    fun recordNavigation(
        entryId: String,
        route: String?,
        origin: NavigationOrigin,
        backstackSize: Int
    ) {
        val now = System.currentTimeMillis()
        val routeName = NavUtils.getSimpleRoute(route) ?: "ROOT"
        val warnings = getPotentialWarnings(entryId, routeName, origin, backstackSize)

        logEntries.add(
            HikerLogEntry(
                entryId = entryId,
                routeName = routeName,
                timestamp = now,
                origin = origin,
                warnings = warnings
            )
        )
    }

    fun observeEntryLifecycle(entry: NavBackStackEntry) {
        if (entry.id in observedEntries) return
        observedEntries.add(entry.id)

        var observer: LifecycleEventObserver? = null
        observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    resumeStartTimes[entry.id] = System.currentTimeMillis()
                }

                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_DESTROY -> {
                    val startTime = resumeStartTimes.remove(entry.id)
                    if (startTime != null) {
                        val elapsed = System.currentTimeMillis() - startTime
                        val current = resumedDurations[entry.id] ?: 0L
                        resumedDurations[entry.id] = current + elapsed
                    }
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        entry.lifecycle.removeObserver(observer!!)
                    }
                }


                else -> {}
            }
        }
        entry.lifecycle.addObserver(observer)
    }

    fun getResumedDuration(entryId: String): Long {
        val accumulated = resumedDurations[entryId] ?: 0L
        val currentStart = resumeStartTimes[entryId]
        return if (currentStart != null) {
            accumulated + (System.currentTimeMillis() - currentStart)
        } else {
            accumulated
        }
    }

    fun getLogForEntry(entryId: String): HikerLogEntry? {
        return logEntries.findLast { it.entryId == entryId }
    }

    companion object {
        private const val DOUBLE_CLICK_THRESHOLD_MS = 500L
        private const val DEEP_BACKSTACK_THRESHOLD = 10
    }
}