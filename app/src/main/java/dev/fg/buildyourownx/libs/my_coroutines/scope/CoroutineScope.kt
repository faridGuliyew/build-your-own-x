package dev.fg.buildyourownx.libs.my_coroutines.scope

import kotlin.coroutines.CoroutineContext

class CoroutineScope internal constructor(
    internal val context: CoroutineContext
)