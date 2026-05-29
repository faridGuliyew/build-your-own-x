package dev.fg.buildyourownx.libs.image_loader

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalImageLoader: ProvidableCompositionLocal<ImageLoader> = staticCompositionLocalOf {
    ImageLoader.Builder().build()
}