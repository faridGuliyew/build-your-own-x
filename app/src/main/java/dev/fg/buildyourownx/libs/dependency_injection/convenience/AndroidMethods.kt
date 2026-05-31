package dev.fg.buildyourownx.libs.dependency_injection.convenience

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.fg.buildyourownx.libs.dependency_injection.VMCreationExtras
import dev.fg.buildyourownx.libs.dependency_injection.injector.Inject

inline fun <reified VM: ViewModel> ComponentActivity.injectViewModel(
    qualifier: Any? = null,
    params: Map<Any, Any>? = null
): Lazy<VM> {
    return viewModels<VM>(
        factoryProducer = {
            viewModelFactory {
                initializer {
                    Inject.get(
                        clazz = VM::class,
                        qualifier = qualifier,
                        creationExtras = VMCreationExtras(
                            params = params,
                            savedStateHandle = createSavedStateHandle()
                        )
                    )
                }
            }
        }
    )
}