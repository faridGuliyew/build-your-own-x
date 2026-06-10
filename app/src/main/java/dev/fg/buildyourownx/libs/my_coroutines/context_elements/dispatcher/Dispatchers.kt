package dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher

import android.os.Handler
import android.os.Looper
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
    val Main = object : Dispatcher() {
        override fun dispatch(task: () -> Unit) {
            Handler(Looper.getMainLooper()).post(task)
        }
    }
}