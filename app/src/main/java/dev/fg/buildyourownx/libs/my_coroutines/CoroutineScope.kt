package dev.fg.buildyourownx.libs.my_coroutines

import dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher.Dispatcher
import dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher.Dispatchers
import dev.fg.buildyourownx.libs.my_coroutines.context_elements.job.Job
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.resume

class CoroutineScope(
    private var context: CoroutineContext
) {
    init {
        if (context[Job.Key] == null) { context += Job("CoroutineScope",null) }
        if (context[Dispatcher.Key] == null) { context += Dispatchers.Default }
    }

    fun launch(name: String? = null, block: suspend CoroutineScope.() -> Unit) {
        val parentContext = this.context
        val parentJob = parentContext[Job.Key]!!
        val thisJob = Job(name, parentJob)
        if (!parentJob.attachChild(thisJob)) return

        val thisContext = parentContext + thisJob
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
    }
}