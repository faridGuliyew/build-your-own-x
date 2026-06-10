package dev.fg.buildyourownx.libs.my_coroutines

import dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher.Dispatcher
import dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher.Dispatchers
import dev.fg.buildyourownx.libs.my_coroutines.context_elements.job.Job
import dev.fg.buildyourownx.libs.my_coroutines.scope.CoroutineScope
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.resume

fun CoroutineScope.launch(context: CoroutineContext = EmptyCoroutineContext, name: String? = null, block: suspend CoroutineScope.() -> Unit) : Job? {
    val parentContext = this.context
    val parentJob = parentContext[Job.Key]!!
    val thisJob = Job(name, parentJob)
    if (!parentJob.attachChild(thisJob)) return null

    val thisContext = parentContext + thisJob + context
    val cont = block.createCoroutine(
        completion = object : Continuation<Unit> {
            override val context: CoroutineContext = thisContext

            // called when a coroutine ends. check children
            override fun resumeWith(result: Result<Unit>) {
                result.onSuccess { thisJob.complete(true) }
                result.onFailure { thisJob.cancel() }
            }
        },
        receiver = CoroutineScope(thisContext)
    )
    (thisContext[Dispatcher.Key] ?: Dispatchers.Default).dispatch {
        cont.resume(Unit)
    }
    return thisJob
}