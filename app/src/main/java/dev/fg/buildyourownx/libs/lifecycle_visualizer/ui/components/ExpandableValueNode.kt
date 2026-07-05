package dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.theme.EmeraldGreen
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.theme.LightGrayText
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.theme.MutedGray
import kotlin.collections.iterator

/**
 * Recursively renders a value from the argument map.
 * - Primitives, strings, collections → displayed inline.
 * - Nested Maps (custom objects) → collapsed by default with a [+] toggle.
 *
 * @param key The property/argument name
 * @param value The value (could be a primitive, String, Collection, Map, or null)
 * @param depth Current nesting depth for indentation
 */
@Composable
fun ExpandableValueNode(
    key: String,
    value: Any?,
    depth: Int = 0
) {
    val indent = (depth * 12).dp

    when (value) {
        is Map<*, *> -> {
            // This is a nested object — render with expand/collapse toggle
            var expanded by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .padding(start = indent)
                    .clickable { expanded = !expanded }
                    .padding(vertical = 2.dp)
            ) {
                Text(
                    text = if (expanded) "[-]" else "[+]",
                    color = EmeraldGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$key:",
                    color = Color(0xFF93C5FD), // Light blue for object keys
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                if (!expanded) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "{${value.size} fields}",
                        color = MutedGray,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    for ((childKey, childValue) in value) {
                        ExpandableValueNode(
                            key = childKey.toString(),
                            value = childValue,
                            depth = depth + 1
                        )
                    }
                }
            }
        }

        else -> {
            // Leaf value — render inline
            Row(
                modifier = Modifier
                    .padding(start = indent, top = 1.dp, bottom = 1.dp)
            ) {
                Text(
                    text = "$key: ",
                    color = LightGrayText,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = formatLeafValue(value),
                    color = getLeafColor(value),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

private fun formatLeafValue(value: Any?): String {
    return when (value) {
        null -> "null"
        is String -> "\"$value\""
        is Collection<*> -> "[${value.joinToString(", ")}]"
        is Array<*> -> "[${value.joinToString(", ")}]"
        else -> value.toString()
    }
}

private fun getLeafColor(value: Any?): Color {
    return when (value) {
        null -> MutedGray
        is String -> Color(0xFFFBBF24) // Amber for strings
        is Number -> Color(0xFF34D399) // Green for numbers
        is Boolean -> Color(0xFFA78BFA) // Purple for booleans
        is Collection<*> -> Color(0xFFF472B6) // Pink for collections
        is Array<*> -> Color(0xFFF472B6)
        else -> LightGrayText
    }
}

@Preview
@Composable
private fun Preview() {
    ExpandableValueNode(
        key = "Key",
        value = "Value",
        depth = 0
    )
}
