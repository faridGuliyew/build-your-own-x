package dev.fg.buildyourownx.libs.navigation_visualizer.model

sealed class NavigationOrigin(val description: String) {
    /** Explicit navigate() call from user code */
    data class Explicit(val fileName: String, val lineNumber: Int, val methodName: String) :
        NavigationOrigin("Explicit")

    /** Deep link navigation from an external Intent */
    data class DeepLink(val uri: String) : NavigationOrigin("Deep Link")

    /** System or user back button press */
    data class BackPress(val navigatorInfo: String) : NavigationOrigin("Back Press")

    /** Automatic creation (startDestination, trampoline dispatch) */
    data object StartDestination : NavigationOrigin("Start Destination")

    /** Could not determine origin */
    data object Unknown : NavigationOrigin("Unknown")

    fun displayString(): String = when (this) {
        is Explicit -> "$fileName:$lineNumber ($methodName)"
        is DeepLink -> "Deep Link → $uri"
        is BackPress -> "Back Press ($navigatorInfo)"
        is StartDestination -> "Auto (startDestination)"
        is Unknown -> "Unknown"
    }
}
