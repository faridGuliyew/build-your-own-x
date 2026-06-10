package dev.fg.buildyourownx.libs.my_coroutines

import dev.fg.buildyourownx.libs.my_coroutines.misc.currentCoroutineContext
import dev.fg.buildyourownx.libs.my_coroutines.misc.extensions.join
import dev.fg.buildyourownx.libs.my_coroutines.scope.CoroutineScope
import kotlin.coroutines.CoroutineContext

suspend fun withContext(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit) {
    val scope = CoroutineScope(currentCoroutineContext())
    scope.launch(context = context, block = block)?.join()
}