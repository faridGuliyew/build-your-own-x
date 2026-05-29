package dev.fg.buildyourownx.libs.image_loader

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.flow.Flow

interface ImageLoader {
    fun getCachedImage(url: String): Bitmap?
    fun getImage(url: String, reqSize: IntSize, key: Any?, context: Context,): Flow<ImageLoaderResult>

    class Builder {
        private val config = Config()
        fun setMaxParallelism(count: Int) {
            require(count >= 1)
            config.maxParallelism = count
        }
        fun setBitmapMemoryCacheSize(size: Int) {
            require(size >= 0)
            config.bitmapMemoryCacheSize = size
        }
        fun build() : ImageLoader {
            return ImageLoaderImpl(config)
        }
    }

    data class Config (
        var maxParallelism: Int = 2,
        var bitmapMemoryCacheSize: Int = (Runtime.getRuntime().maxMemory()).toInt() / 5
    )
}