package dev.fg.buildyourownx.libs.image_loader.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize

internal class LegacySizeResolver(
    private val onSizeResolved: (IntSize) -> Unit
) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else Int.MAX_VALUE
        val height = if (constraints.hasBoundedHeight) constraints.maxHeight else Int.MAX_VALUE

        onSizeResolved(IntSize(width, height))

        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }
}

internal fun Modifier.legacyResolveSize(onSizeResolved: (IntSize) -> Unit): Modifier =
    this.then(LegacySizeResolver(onSizeResolved))