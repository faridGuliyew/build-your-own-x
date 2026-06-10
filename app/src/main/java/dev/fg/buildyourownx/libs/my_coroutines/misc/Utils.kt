package dev.fg.buildyourownx.libs.my_coroutines.misc

import dev.fg.buildyourownx.libs.my_coroutines.context_elements.job.Job
import dev.fg.buildyourownx.libs.my_coroutines.misc.extensions.ensureActive
import kotlin.coroutines.coroutineContext


suspend inline fun currentCoroutineContext() = coroutineContext

suspend fun ensureActive() {
    val job = currentCoroutineContext()[Job.Key]!!
    job.ensureActive()
}