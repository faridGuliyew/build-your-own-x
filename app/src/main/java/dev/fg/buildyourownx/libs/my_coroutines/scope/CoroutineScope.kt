package dev.fg.buildyourownx.libs.my_coroutines.scope

import dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher.Dispatcher
import dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher.Dispatchers
import dev.fg.buildyourownx.libs.my_coroutines.context_elements.job.Job
import kotlin.coroutines.CoroutineContext

class CoroutineScope internal constructor(
    internal val context: CoroutineContext
)