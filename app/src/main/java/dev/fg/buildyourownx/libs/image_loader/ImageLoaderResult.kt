package dev.fg.buildyourownx.libs.image_loader

import android.graphics.Bitmap

sealed interface ImageLoaderResult {
    data object Loading : ImageLoaderResult
    data class Success (val bitmap: Bitmap) : ImageLoaderResult
    data class Failure (val t: Throwable?) : Exception(t), ImageLoaderResult
}