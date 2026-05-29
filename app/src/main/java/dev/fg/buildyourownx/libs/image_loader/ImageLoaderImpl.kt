package dev.fg.buildyourownx.libs.image_loader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.LruCache
import android.util.TimingLogger
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.also

internal class ImageLoaderImpl(config: ImageLoader.Config) : ImageLoader {

    val semaphore = Semaphore(config.maxParallelism)

    val bitmapMemoryCache = object : LruCache<String, Bitmap>(config.bitmapMemoryCacheSize) {
        override fun sizeOf(key: String?, value: Bitmap): Int {
            return value.byteCount
        }
    }

    private suspend fun ensureActive(/*label: String*/) {
        if (!currentCoroutineContext().isActive) {
//            println("$label cancelled!")
            throw CancellationException()
        }
    }

    override fun getCachedImage(url: String): Bitmap? {
        return bitmapMemoryCache.get(url)
    }

    override fun getImage(
        url: String,
        reqSize: IntSize,
        key: Any?,
        context: Context
    ): Flow<ImageLoaderResult> {
        return flow {
            emit(ImageLoaderResult.Loading)

            semaphore.withPermit { // Limit parallelism
                val connection = (URL(url).openConnection() as HttpsURLConnection).apply {
                    useCaches = true
                }

                val tmpFile = File.createTempFile("img_", ".tmp", context.cacheDir)
                try {
                    ensureActive(/*"getImage() before connect"*/)
                    val responseCode = connection.responseCode

                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        emit(ImageLoaderResult.Failure(Exception("Response code: $responseCode")))
                        return@flow
                    }

                    // Read the response into a temp file
                    ensureActive(/*"getImage after connect/before disk save"*/)
                    val inputStream = connection.getInputStream()
                    tmpFile.createNewFile()
                    tmpFile.outputStream().use { inputStream.copyTo(it) }

                    ensureActive(/*"getImage before bounds decode"*/)
                    val bitmapOptions = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    tmpFile.inputStream().use {
                        BitmapFactory.decodeStream(it, null, bitmapOptions)
                        // bitmapOptions.outWidth & bitmapOptions.outHeight is now available
                    }
                    // Prepare options for actual decode
                    bitmapOptions.apply {
                        inJustDecodeBounds = false
                        inPreferredConfig =
                            if (Build.VERSION.SDK_INT >= 26) Bitmap.Config.HARDWARE else Bitmap.Config.ARGB_8888
                        inSampleSize = calculateInSampleSize(
                            bitmapOptions,
                            reqSize.width,
                            reqSize.height
                        )
                    }
                    ensureActive(/*"getImage BEFORE ACTUAL DECODE"*/)
                    val bitmap = tmpFile.inputStream().use {
                        BitmapFactory.decodeStream(it, null, bitmapOptions)
                    }
                    if (bitmap == null) {
                        emit(ImageLoaderResult.Failure(Throwable("Bitmap decode returned null")))
                    } else {
                        bitmapMemoryCache.put(url, bitmap)
                        emit(ImageLoaderResult.Success(bitmap))
                    }
                } finally {
                    tmpFile.delete()
                    connection.disconnect()
                }
            }
        }.flowOn(Dispatchers.IO).catch {
            emit(ImageLoaderResult.Failure(it))
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}