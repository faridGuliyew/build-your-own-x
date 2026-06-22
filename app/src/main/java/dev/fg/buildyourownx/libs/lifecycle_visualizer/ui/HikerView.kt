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
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.fg.buildyourownx.libs.lifecycle_visualizer.VisualizerState
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

    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { controller, destination, bundle ->
            // Capture the call stack at the moment this function is called
            val stackTrace = Throwable().stackTrace
            val callerIndex = if (NavUtils.isTypeSafeRoute(destination.route)) 8 else 7

            println("-----${destination.route}----")
            stackTrace.getOrNull(callerIndex)?.let {
                println("${it.fileName}:${it.lineNumber} (${it.methodName})")
            }
            println("-----")
        }
    }

    LaunchedEffect(currentBackstack.size) {
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
                .align(Alignment.Center) // Start at center, drag from there
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
                    onMinimize = { visualizerState = VisualizerState.BUBBLE },
                    onEntrySelected = { selectedEntryId = it.id }
                )
            }
        }
    }
}