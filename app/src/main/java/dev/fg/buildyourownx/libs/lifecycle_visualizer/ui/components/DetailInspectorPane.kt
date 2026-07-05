package dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.fg.buildyourownx.libs.lifecycle_visualizer.extensions.getPrimaryConstructorParamNames
import dev.fg.buildyourownx.libs.lifecycle_visualizer.model.NavigationOrigin
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.state.HikerState
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.theme.AmberYellow
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.theme.EmeraldGreen
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.theme.LightGrayText
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.theme.MutedGray
import dev.fg.buildyourownx.libs.lifecycle_visualizer.utils.NavUtils
import kotlin.collections.iterator

@SuppressLint("RestrictedApi")
@Composable
fun DetailInspectorPane(entry: NavBackStackEntry, hikerState: HikerState) {
    val clipboardManager = LocalClipboardManager.current
    val route = entry.destination.route ?: "ROOT"
    val simpleRoute = NavUtils.getSimpleRoute(route) ?: "ROOT"
    val klass = NavUtils.getRouteKClass(simpleRoute)
    val argsMap = NavUtils.getReflectedArgsMap(entry)
    val logEntry = hikerState.getLogForEntry(entry.id)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .heightIn(max = 300.dp)
            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
            .verticalScroll(scrollState)
    ) {
        // Header
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("INSPECTOR", color = LightGrayText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "COPY DATA",
                color = EmeraldGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    val argsString = NavUtils.getArgs(entry)
                    clipboardManager.setText(
                        AnnotatedString("Route: $route\nOrigin: ${logEntry?.origin?.displayString() ?: "Unknown"}\nArgs:\n$argsString")
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Route Info
        when (klass) {
            null -> {
                Text(text = "String route:", color = Color.Gray, fontSize = 12.sp)
                Text(
                    text = simpleRoute,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            else -> {
                Text(text = "Safe route:", color = Color.Gray, fontSize = 12.sp)
                Text(
                    text = klass.simpleName.orEmpty(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        Text(text = "Full route:", color = Color.Gray, fontSize = 12.sp)
        Text(
            text = route,
            color = Color.White,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Constructor Params
        if (klass != null) {
            Text(text = "Constructor Params:", color = Color.Gray, fontSize = 12.sp)
            Text(
                text = klass.getPrimaryConstructorParamNames()?.joinToString(", ") ?: "None",
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Origin of Navigation
        if (logEntry != null) {
            SectionDivider()
            Text(text = "Navigation Origin:", color = Color.Gray, fontSize = 12.sp)
            Text(
                text = logEntry.origin.displayString(),
                color = when (logEntry.origin) {
                    is NavigationOrigin.DeepLink -> Color(0xFFF472B6)
                    is NavigationOrigin.BackPress -> AmberYellow
                    is NavigationOrigin.StartDestination -> Color(0xFF93C5FD)
                    is NavigationOrigin.Explicit -> EmeraldGreen
                    else -> MutedGray
                },
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Arguments Section (collapsible tree)
        SectionDivider()
        Text(text = "Arguments:", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))

        if (argsMap.isEmpty()) {
            Text(
                text = "None",
                color = MutedGray,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        } else {
            Column {
                for ((key, value) in argsMap) {
                    ExpandableValueNode(key = key, value = value, depth = 0)
                }
            }
        }
    }
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        thickness = 0.5.dp,
        color = Color.White.copy(alpha = 0.1f)
    )
}

@Preview
@Composable
private fun Preview() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "start") {
        composable("start") {}
    }

    DetailInspectorPane(
        navController.currentBackStackEntry!!,
        hikerState = HikerState()
    )
}