package dev.fg.buildyourownx.libs.lifecycle_visualizer.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.fg.buildyourownx.libs.lifecycle_visualizer.model.VisualizerState
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.components.BubbleMode
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.components.ExpandedPanelMode
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.state.HikerState
import dev.fg.buildyourownx.libs.lifecycle_visualizer.utils.NavUtils
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("RestrictedApi")
@Composable
fun HikerView(navController: NavController) {
    val currentBackstack by navController.currentBackStack.collectAsStateWithLifecycle()
    var visualizerState by remember { mutableStateOf(VisualizerState.BUBBLE) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var selectedEntryId by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    // Central state holder — survives recompositions
    val hikerState = remember { HikerState() }

    // Register destination changed listener for origin detection & logging
    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { controller, destination, bundle ->
            val stackTrace = Throwable().stackTrace
            val isTypeSafe = NavUtils.isTypeSafeRoute(destination.route)

            val origin = hikerState.detectOrigin(
                destination = destination,
                bundle = bundle,
                stackTrace = stackTrace,
                isTypeSafe = isTypeSafe
            )

            // Get the entry ID for the destination that was just navigated to
            val entryId = controller.currentBackStackEntry?.id ?: "unknown"

            hikerState.recordNavigation(
                entryId = entryId,
                route = destination.route,
                origin = origin,
                backstackSize = controller.currentBackStack.value.size
            )
        }
    }

    // Observe lifecycle for each backstack entry to track RESUMED duration
    LaunchedEffect(currentBackstack.size) {
        for (entry in currentBackstack) {
            hikerState.observeEntryLifecycle(entry)
        }

        if (currentBackstack.isNotEmpty() && visualizerState == VisualizerState.EXPANDED) {
            listState.animateScrollToItem(currentBackstack.lastIndex)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Draggable Container
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .align(Alignment.Center)
        ) {
            AnimatedVisibility(
                visible = visualizerState == VisualizerState.BUBBLE,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val route = currentBackstack.lastOrNull()?.destination?.route
                val simpleRoute = NavUtils.getSimpleRoute(route) ?: "ROOT"
                val klass = NavUtils.getRouteKClass(simpleRoute)

                BubbleMode(
                    backstackCount = currentBackstack.size,
                    currentRoute = klass?.simpleName ?: simpleRoute,
                    onExpand = { visualizerState = VisualizerState.EXPANDED }
                )
            }

            AnimatedVisibility(
                visible = visualizerState == VisualizerState.EXPANDED,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                ExpandedPanelMode(
                    backstack = currentBackstack,
                    listState = listState,
                    selectedEntryId = selectedEntryId,
                    hikerState = hikerState,
                    onMinimize = { visualizerState = VisualizerState.BUBBLE },
                    onEntrySelected = { selectedEntryId = it.id }
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "start") {
        composable("start") {}
    }
    HikerView(navController)
}