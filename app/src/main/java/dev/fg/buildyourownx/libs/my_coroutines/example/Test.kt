package dev.fg.buildyourownx.libs.my_coroutines.example

import dev.fg.buildyourownx.libs.my_coroutines.CoroutineScope
import dev.fg.buildyourownx.libs.my_coroutines.delay
import kotlin.coroutines.EmptyCoroutineContext

fun myCoroutinesTest() {
    val myScope = CoroutineScope(EmptyCoroutineContext)
    myScope.launch ("PARENT") {
        println("PARENT STARTED on ${Thread.currentThread().name}")

        launch ("CHILD 1") {
            println("CHILD 1 STARTED on ${Thread.currentThread().name}")
            delay(2000)
//            5 / 0
            println("CHILD 1 ENDED on ${Thread.currentThread().name}")
        }

        launch ("CHILD 2") {
            println("CHILD 2 STARTED on ${Thread.currentThread().name}")
            delay(3000)
            println("CHILD 2 ENDED on ${Thread.currentThread().name}")
        }
        launch ("CHILD 3") {
            println("CHILD 3 STARTED on ${Thread.currentThread().name}")
            delay(4000)
            println("CHILD 3 ENDED on ${Thread.currentThread().name}")
        }

        delay(1000)
        println("PARENT ENDED on ${Thread.currentThread().name}")
    }
}