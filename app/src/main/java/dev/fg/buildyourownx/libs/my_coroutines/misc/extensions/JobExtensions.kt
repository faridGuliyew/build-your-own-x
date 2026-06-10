package dev.fg.buildyourownx.libs.my_coroutines.misc.extensions

import dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher.Dispatcher
import dev.fg.buildyourownx.libs.my_coroutines.context_elements.job.Job
import dev.fg.buildyourownx.libs.my_coroutines.misc.currentCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun Job.ensureActive() {
    if (!isActive) throw Exception("Job is cancelled!")
}

suspend fun Job.join() {
    val context = currentCoroutineContext()
    suspendCoroutine {
        invokeOnDetach {
            context[Dispatcher.Key]!!.dispatch {
                it.resume(Unit)
            }
        }
    }
}