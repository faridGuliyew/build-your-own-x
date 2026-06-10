package dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher

import java.util.concurrent.Executors

object Dispatchers {
    val IO = object : Dispatcher() {
        val ioPool = Executors.newFixedThreadPool(60)
        override fun dispatch(task: () -> Unit) {
            ioPool.submit(task)
        }
    }
    val Default = object : Dispatcher() {
        val defaultPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        override fun dispatch(task: () -> Unit) {
            defaultPool.submit(task)
        }
    }
}