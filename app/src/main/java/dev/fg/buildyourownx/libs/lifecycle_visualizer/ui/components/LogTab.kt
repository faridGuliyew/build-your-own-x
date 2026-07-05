package dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.fg.buildyourownx.libs.lifecycle_visualizer.model.HikerLogEntry
import dev.fg.buildyourownx.libs.lifecycle_visualizer.model.NavigationOrigin
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.state.HikerState
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.theme.AmberYellow
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.theme.EmeraldGreen
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.theme.MutedGray
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogTab(hikerState: HikerState) {
    val listState = rememberLazyListState()

    // Auto-scroll to latest entry
    LaunchedEffect(hikerState.logEntries.size) {
        if (hikerState.logEntries.isNotEmpty()) {
            listState.animateScrollToItem(hikerState.logEntries.lastIndex)
        }
    }

    if (hikerState.logEntries.isEmpty()) {
        Text(
            text = "No navigation events recorded yet.",
            color = MutedGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 350.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            items = hikerState.logEntries,
            key = { "${it.entryId}_${it.timestamp}" }
        ) { logEntry ->
            LogEntryCard(logEntry = logEntry, hikerState = hikerState)
        }
    }
}

@Composable
private fun LogEntryCard(logEntry: HikerLogEntry, hikerState: HikerState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        // Top row: Route name + timestamp
        Column (
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = formatTimestamp(logEntry.timestamp),
                color = MutedGray,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = logEntry.routeName,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Origin
        Row(verticalAlignment = Alignment.CenterVertically) {
            val originColor = when (logEntry.origin) {
                is NavigationOrigin.Explicit -> EmeraldGreen
                is NavigationOrigin.DeepLink -> Color(0xFFF472B6)
                is NavigationOrigin.BackPress -> AmberYellow
                is NavigationOrigin.StartDestination -> Color(0xFF93C5FD)
                is NavigationOrigin.Unknown -> MutedGray
            }
            OriginDot(originColor)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = logEntry.origin.displayString(),
                color = originColor,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Resumed duration (live counter)
        ResumedDurationLabel(entryId = logEntry.entryId, hikerState = hikerState)

        // Warnings
        if (logEntry.warnings.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.White.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(4.dp))
            for (warning in logEntry.warnings) {
                Text(
                    text = warning,
                    color = Color(0xFFFCA5A5), // Soft red
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun ResumedDurationLabel(entryId: String, hikerState: HikerState) {
    // Refresh the displayed duration every second
    var displayedDuration by remember { mutableLongStateOf(hikerState.getResumedDuration(entryId)) }

    LaunchedEffect(entryId) {
        while (true) {
            displayedDuration = hikerState.getResumedDuration(entryId)
            delay(1000)
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Resumed: ",
            color = MutedGray,
            fontSize = 11.sp
        )
        Text(
            text = formatDuration(displayedDuration),
            color = EmeraldGreen,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun OriginDot(color: Color) {
    Spacer(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(color)
    )
}

private val timeFormatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

private fun formatTimestamp(millis: Long): String {
    return timeFormatter.format(Date(millis))
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val ms = millis % 1000
    return if (minutes > 0) {
        "${minutes}m ${seconds}s"
    } else {
        "${seconds}.${ms / 100}s"
    }
}
