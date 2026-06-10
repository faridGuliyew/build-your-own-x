package dev.fg.buildyourownx.libs.my_coroutines

import dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher.Dispatcher
import dev.fg.buildyourownx.libs.my_coroutines.context_elements.job.Job
import dev.fg.buildyourownx.libs.my_coroutines.misc.currentCoroutineContext
import dev.fg.buildyourownx.libs.my_coroutines.misc.extensions.ensureActive
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private val delayScheduler = Executors.newScheduledThreadPool(10)
suspend fun delay(duration: Long) {
    val coroutineContext = currentCoroutineContext()
    val job = coroutineContext[Job.Key]!!
    job.ensureActive()

    suspendCoroutine {
        val future: ScheduledFuture<*> = delayScheduler.schedule(
            {
                coroutineContext[Dispatcher.Key]!!.dispatch {
                    if (!job.isActive) {
                        it.resumeWithException(Exception("Job is cancelled"))
                    } else {
                        it.resume(Unit)
                    }
                }
            },
            duration,
            TimeUnit.MILLISECONDS
        )

        job.invokeOnDetach { future.cancel(false) }
    }
}