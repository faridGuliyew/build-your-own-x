package dev.fg.buildyourownx.libs.my_coroutines.scope.utils

import dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher.Dispatcher
import dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher.Dispatchers
import dev.fg.buildyourownx.libs.my_coroutines.context_elements.job.Job
import dev.fg.buildyourownx.libs.my_coroutines.scope.CoroutineScope as CCoroutineScope
import kotlin.coroutines.CoroutineContext

fun CoroutineScope(context: CoroutineContext) : CCoroutineScope {
    var newContext: CoroutineContext = context
    context.apply {
        if (this[Job.Key] == null) newContext += Job("CoroutineScope", null)
        if (this[Dispatcher.Key] == null) newContext += Dispatchers.Default
    }
    return CCoroutineScope(newContext)
}