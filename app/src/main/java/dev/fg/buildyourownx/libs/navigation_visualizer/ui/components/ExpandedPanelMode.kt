package dev.fg.buildyourownx.libs.navigation_visualizer.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.fg.buildyourownx.libs.navigation_visualizer.ui.state.HikerState
import dev.fg.buildyourownx.libs.navigation_visualizer.ui.theme.DarkSurfaceColor
import dev.fg.buildyourownx.libs.navigation_visualizer.ui.theme.EmeraldGreen
import dev.fg.buildyourownx.libs.navigation_visualizer.ui.theme.LightGrayText
import dev.fg.buildyourownx.libs.navigation_visualizer.ui.theme.MutedGray
import dev.fg.buildyourownx.libs.navigation_visualizer.ui.theme.getLifecycleColor
import dev.fg.buildyourownx.libs.navigation_visualizer.utils.NavUtils

private enum class PanelTab { STREAM, LOG }

@SuppressLint("RestrictedApi")
@Composable
fun ExpandedPanelMode(
    backstack: List<NavBackStackEntry>,
    listState: LazyListState,
    selectedEntryId: String?,
    hikerState: HikerState,
    onMinimize: () -> Unit,
    onEntrySelected: (NavBackStackEntry) -> Unit
) {
    var activeTab by remember { mutableStateOf(PanelTab.STREAM) }

    Surface(
        color = DarkSurfaceColor.copy(alpha = 0.95f),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 16.dp,
        modifier = Modifier
            .widthIn(max = 400.dp)
            .padding(16.dp)
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessLow))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hiker",
                    color = EmeraldGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Text(
                    text = "MINIMIZE",
                    color = LightGrayText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onMinimize() }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabButton(
                    label = "STREAM",
                    isActive = activeTab == PanelTab.STREAM,
                    onClick = { activeTab = PanelTab.STREAM },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    label = "LOG",
                    isActive = activeTab == PanelTab.LOG,
                    badgeCount = hikerState.logEntries.sumOf { it.warnings.size },
                    onClick = { activeTab = PanelTab.LOG },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Content
            when (activeTab) {
                PanelTab.STREAM -> StreamTabContent(
                    backstack = backstack,
                    listState = listState,
                    selectedEntryId = selectedEntryId,
                    hikerState = hikerState,
                    onEntrySelected = onEntrySelected
                )
                PanelTab.LOG -> LogTab(hikerState = hikerState)
            }
        }
    }
}

@Composable
private fun TabButton(
    label: String,
    isActive: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isActive) EmeraldGreen.copy(alpha = 0.2f) else Color.Transparent
            )
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = if (isActive) EmeraldGreen else MutedGray,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
            )
            if (badgeCount > 0) {
                Spacer(modifier = Modifier.padding(start = 4.dp))
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(0xFFEF4444), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun StreamTabContent(
    backstack: List<NavBackStackEntry>,
    listState: LazyListState,
    selectedEntryId: String?,
    hikerState: HikerState,
    onEntrySelected: (NavBackStackEntry) -> Unit
) {
    // Navigation Stream
    Text("BACKSTACK", color = LightGrayText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(count = backstack.size) { index ->
            val entry = backstack[index]
            val route = entry.destination.route
            val simpleRoute = NavUtils.getSimpleRoute(route) ?: "ROOT"
            val klass = NavUtils.getRouteKClass(simpleRoute)
            val isSelected = entry.id == selectedEntryId
            val entryLifecycle by entry.lifecycle.currentStateFlow.collectAsStateWithLifecycle()
            val bgColor = getLifecycleColor(entryLifecycle)

            Box(
                modifier = Modifier
                    .size(width = 120.dp, height = 80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) bgColor else bgColor.copy(alpha = 0.3f))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color.White else bgColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onEntrySelected(entry) }
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = entryLifecycle.name,
                        color = if (isSelected) Color.White else bgColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = klass?.simpleName ?: simpleRoute,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2
                    )
                }
            }
        }
    }

    // Detail Inspector Pane
    val selectedEntry = backstack.find { it.id == selectedEntryId }
    AnimatedVisibility(visible = selectedEntry != null) {
        if (selectedEntry != null) {
            DetailInspectorPane(entry = selectedEntry, hikerState = hikerState)
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

    ExpandedPanelMode(
        backstack = navController.currentBackStack.value,
        listState = rememberLazyListState(),
        selectedEntryId = null,
        hikerState = HikerState(),
        onMinimize = {},
        onEntrySelected = {}
    )
}