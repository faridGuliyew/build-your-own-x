package dev.fg.buildyourownx.libs.dependency_injection.example

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class DependencyInjectionViewModel (
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var count = 0
    init {
        println("VM created! ${savedStateHandle.get<String>("HELLO_MSG")}")
    }

    fun sayHi() {

    }

    override fun onCleared() {
        println("VM cleared")
        super.onCleared()
    }
}