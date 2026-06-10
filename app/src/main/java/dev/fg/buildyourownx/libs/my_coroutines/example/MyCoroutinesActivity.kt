package dev.fg.buildyourownx.libs.my_coroutines.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher.Dispatchers
import dev.fg.buildyourownx.libs.my_coroutines.delay
import dev.fg.buildyourownx.libs.my_coroutines.launch
import dev.fg.buildyourownx.libs.my_coroutines.misc.extensions.join
import dev.fg.buildyourownx.libs.my_coroutines.scope.utils.CoroutineScope
import dev.fg.buildyourownx.libs.my_coroutines.withContext

class MyCoroutinesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        CoroutineScope(Dispatchers.Default).launch {
            // TEST 1
            launch {
                println("START IN THREAD: ${Thread.currentThread().name}")
                delay(2000)
                println("AFTER DELAY IN THREAD: ${Thread.currentThread().name}")
                withContext(Dispatchers.Main) {
                    launch {
                        println("WITH CONTEXT CHILD START IN THREAD ${Thread.currentThread().name}")
                        delay(5000)
                        println("WITH CONTEXT CHILD END IN THREAD ${Thread.currentThread().name}")
                        withContext(Dispatchers.IO) {
                            println("WITH CONTEXT CHILD 2 START IN THREAD ${Thread.currentThread().name}")
                            delay(5000)
                            println("WITH CONTEXT CHILD 2 END IN THREAD ${Thread.currentThread().name}")
                        }
                    }
                    println("WITH CONTEXT RUNNING IN THREAD: ${Thread.currentThread().name}")
                }
                withContext(Dispatchers.Main) {
                    println("END IN THREAD: ${Thread.currentThread().name}")
                }
            }?.join()

            // TEST 2
            launch (name = "PARENT") {
                println("PARENT STARTED on ${Thread.currentThread().name}")

                launch (name = "CHILD 1") {
                    println("CHILD 1 STARTED on ${Thread.currentThread().name}")
                    delay(2000)
                    println("CHILD 1 ENDED on ${Thread.currentThread().name}")
                }

                launch (name = "CHILD 2") {
                    println("CHILD 2 STARTED on ${Thread.currentThread().name}")
                    delay(3000)
                    println("CHILD 2 ENDED on ${Thread.currentThread().name}")
                    5/0 // simulate exception
                }
                launch (name = "CHILD 3") {
                    println("CHILD 3 STARTED on ${Thread.currentThread().name}")
                    delay(60000)
                    println("CHILD 3 ENDED on ${Thread.currentThread().name}")
                }

                delay(1000)
                println("PARENT ENDED on ${Thread.currentThread().name}")
            }?.invokeOnDetach {
                println("CANCELLATION!")
            }
        }
    }
}