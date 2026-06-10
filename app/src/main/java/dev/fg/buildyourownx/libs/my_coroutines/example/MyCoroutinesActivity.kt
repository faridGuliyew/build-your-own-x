package dev.fg.buildyourownx.libs.my_coroutines.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import dev.fg.buildyourownx.libs.my_coroutines.context_elements.dispatcher.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class MyCoroutinesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        myCoroutinesTest()
    }
}