package dev.fg.buildyourownx.libs.lifecycle_visualizer.model

data class HikerLogEntry(
    val entryId: String,
    val routeName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val origin: NavigationOrigin = NavigationOrigin.Unknown,
    val warnings: List<String> = emptyList(),
    var resumedDurationMs: Long = 0L
)
