package dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

abstract class Dispatcher : AbstractCoroutineContextElement(Key) {
    companion object Key : CoroutineContext.Key<Dispatcher>
    abstract fun dispatch(task: () -> Unit)
}