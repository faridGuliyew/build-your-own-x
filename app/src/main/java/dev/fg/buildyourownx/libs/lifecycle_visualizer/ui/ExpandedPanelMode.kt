package dev.fg.buildyourownx.libs.lifecycle_visualizer.ui

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import dev.fg.buildyourownx.libs.lifecycle_visualizer.utils.NavUtils

@SuppressLint("RestrictedApi")
@Composable
fun ExpandedPanelMode(
    backstack: List<NavBackStackEntry>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    selectedEntryId: String?,
    onMinimize: () -> Unit,
    onEntrySelected: (NavBackStackEntry) -> Unit
) {
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

            Spacer(modifier = Modifier.height(16.dp))

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
                    DetailInspectorPane(selectedEntry)
                }
            }
        }
    }
}