package dev.fg.buildyourownx.libs.dependency_injection.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.fg.buildyourownx.libs.dependency_injection.CreationExtras
import dev.fg.buildyourownx.libs.dependency_injection.VMCreationExtras
import dev.fg.buildyourownx.libs.dependency_injection.convenience.injectViewModel
import dev.fg.buildyourownx.libs.dependency_injection.injector.Inject
import dev.fg.buildyourownx.libs.dependency_injection.injector.InjectorImpl

class DependencyInjectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel by injectViewModel<DependencyInjectionViewModel>()

        var user: User?
        repeat(3) {
            user = Inject.get<User>("SingleUser")
            println("User: ${user.username}, ${user.password}. Hashcode: ${user.hashCode()}. Info hash: ${user.info.hashCode()}")
        }
        repeat(3) {
            user = Inject.get<User>("FactoryUser", creationExtras = CreationExtras(mapOf("PASSWORD" to "MY PASSWORD $it")))
            println("User: ${user.username}, ${user.password}. Hashcode: ${user.hashCode()}. Info hash: ${user.info.hashCode()}")
        }
        repeat(3) {
            user = Inject.get<User>()
            println("User: ${user.username}, ${user.password}. Hashcode: ${user.hashCode()}. Info hash: ${user.info.hashCode()}")
        }
    }
}