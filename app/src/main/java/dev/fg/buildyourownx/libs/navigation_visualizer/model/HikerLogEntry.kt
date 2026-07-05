package dev.fg.buildyourownx.libs.navigation_visualizer.model

data class HikerLogEntry(
    val entryId: String,
    val routeName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val origin: NavigationOrigin = NavigationOrigin.Unknown,
    val warnings: List<String> = emptyList(),
    var resumedDurationMs: Long = 0L
)
