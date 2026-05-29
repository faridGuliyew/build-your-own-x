package dev.fg.buildyourownx.libs.image_loader.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import dev.fg.buildyourownx.libs.image_loader.ImageLoaderResult
import dev.fg.buildyourownx.libs.image_loader.LocalImageLoader
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ImageLoaderImage(
    url: String,
    loadingBitmap: ImageBitmap,
    errorBitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    key: Any? = null,
    transitionSpec: (AnimatedContentTransitionScope<ImageBitmap>.() -> ContentTransform)? = null,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null
) {
    val ctx = LocalContext.current
    val imageLoader = LocalImageLoader.current
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var targetSize by remember { mutableStateOf<IntSize?>(null) }

    LaunchedEffect(url, key, targetSize) {
        val size = targetSize ?: return@LaunchedEffect

        val cached = imageLoader.getCachedImage(url)?.asImageBitmap()
        if (cached != null) {
            imageBitmap = cached
            return@LaunchedEffect
        }

        imageLoader.getImage(url, size, key, ctx).collectLatest {
            imageBitmap = when (it) {
                is ImageLoaderResult.Success -> it.bitmap.asImageBitmap()
                is ImageLoaderResult.Failure -> errorBitmap
                ImageLoaderResult.Loading -> loadingBitmap
            }
        }
    }
    val bitmap = imageBitmap ?: loadingBitmap
    if (transitionSpec != null) {
        AnimatedContent(bitmap, transitionSpec = transitionSpec) {
            ImageLoaderImageBase(
                modifier = modifier,
                onSizeResolved = { size ->
                    if (targetSize != size) {
                        targetSize = size
                    }
                },
                bitmap = it,
                contentDescription = contentDescription,
                contentScale = contentScale
            )
        }
    } else {
        ImageLoaderImageBase(
            modifier = modifier,
            onSizeResolved = { size ->
                if (targetSize != size) {
                    targetSize = size
                }
            },
            bitmap = bitmap,
            contentDescription = contentDescription,
            contentScale = contentScale
        )
    }
}

@Composable
internal fun ImageLoaderImageBase(
    modifier: Modifier,
    onSizeResolved: (IntSize) -> Unit,
    bitmap: ImageBitmap,
    contentDescription: String?,
    contentScale: ContentScale
) {
    Image(
        modifier = modifier
            .legacyResolveSize(onSizeResolved),
        bitmap = bitmap,
        contentDescription = contentDescription,
        contentScale = contentScale
    )
}