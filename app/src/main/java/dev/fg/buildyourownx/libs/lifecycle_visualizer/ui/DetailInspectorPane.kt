package dev.fg.buildyourownx.libs.lifecycle_visualizer.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import dev.fg.buildyourownx.libs.lifecycle_visualizer.extensions.getPrimaryConstructorParamNames
import dev.fg.buildyourownx.libs.lifecycle_visualizer.utils.NavUtils

@SuppressLint("RestrictedApi")
@Composable
fun DetailInspectorPane(entry: NavBackStackEntry) {
    val clipboardManager = LocalClipboardManager.current
    val route = entry.destination.route ?: "ROOT"
    val simpleRoute = NavUtils.getSimpleRoute(route) ?: "ROOT"
    val klass = NavUtils.getRouteKClass(simpleRoute)
    val args = NavUtils.getArgs(entry)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
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
                    clipboardManager.setText(AnnotatedString("Route: $route\nArgs:\n$args"))
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (klass) {
            null -> {
                // Route Name
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
                // Route Name
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

        // Class Info if available
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

        // Arguments
        Text(text = "Arguments:", color = Color.Gray, fontSize = 12.sp)
        if (args.isEmpty()) {
            Text(
                text = "None",
                color = MutedGray,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        } else {
            Text(
                text = args,
                color = AmberYellow,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}