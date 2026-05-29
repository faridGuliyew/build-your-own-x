# ImageLoader

A custom, low-level Android image loading and caching library built from scratch in Kotlin and optimized for Jetpack Compose. 

This engine is part of a deep-dive exploration into Android internals, designed to bypass abstract high-level frameworks.

## 🚀 Core Architectural Features

* **Parallelism Throttling:** Uses `Semaphore` to limit concurrent network operations, avoiding excessive bandwidth usage
* **In-Memory Cache:** Maintains `LruCache` to reduce load times & network requests for recently loaded images
* **Downsampling:** Implements scaling images down, using `inSampleSize`
* **Hardware Accelerated Graphics:** Decodes streams directly into `Bitmap.Config.HARDWARE` configurations, minimizing JVM Heap usage
* **OOM Prevention** Allocates a very minimal buffer to write socket stream into a temporary file, no extra JVM Heap is used
* **Coroutine Support:** Performs network requests and I/O operations on `Dispatchers.IO` via  `Flow`, and handles cancellation gracefully
---

## 🛠️ Usage

Initialize the loader and expose it via `CompositionLocal`:

```kotlin
val imageLoader = ImageLoader.Builder()
// Add desired configs
.build()

CompositionLocalProvider (LocalImageLoader provides imageLoader) {
  ... your app code goes here
}
```
