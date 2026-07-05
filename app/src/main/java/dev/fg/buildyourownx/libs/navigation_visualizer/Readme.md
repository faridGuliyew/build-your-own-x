# Hiker - Jetpack Compose Navigation Visualizer

Hiker is a floating debugging library that helps you track, inspect, and debug Jetpack Compose Navigation.

## Key Features

- **Floating UI Inspector**: A floating window that overlays your host app. It can be minimized to a small bubble.
- **Live Backstack Stream**: View all currently active navigation destinations with their Lifecycle states.
- **Detailed Argument Inspector**: Explore route arguments. Complex objects are automatically parsed using reflection
- **Navigation History & Logs**: A chronological log of navigation events with exact timestamps.
- **Navigation Origin Detection**: Instantly see *how* a screen was opened:
  - 🔗 **Deep Links**
  - 🏠 **Start Destination**
  - 🔙 **Back Presses** (System back, ComposeNavigator, DialogNavigator, etc.)
  - 🚀 **Explicit Navigation** (Displays exact file name, method name, and line number of the caller)
- **Warnings**: Hiker monitors your navigation to detect anomalies:
  - **Double-clicks**: Navigating to the same destination within 500ms.
  - **Navigation Bursts**: 3+ navigation events within 1 second.
  - **Deep Backstacks**: Having 10+ active entries, possible memory leaks or missing `popUpTo` logic.
  - **Duplicate Routes**: The same route stacked multiple times in history.
  - **Self-Navigation**: Navigating to the route that is already currently active.

## Usage

Simply integrate the `HikerView` into your primary Compose hierarchy, wrapping it over your `NavHost` or placing it alongside it in a `Box`:

```kotlin
val navController = rememberNavController()

Box(modifier = Modifier.fillMaxSize()) {
    NavHost(navController = navController, startDestination = "home") {
        // ... your routes ...
    }

    // Attach Hiker Floating Window
    HikerView(navController)
}
```

## Example Application
An example app demonstrating its functionality is located in the `example` package.
