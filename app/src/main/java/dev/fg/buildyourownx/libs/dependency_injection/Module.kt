package dev.fg.buildyourownx.libs.dependency_injection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.fg.buildyourownx.libs.dependency_injection.dependency_container.DependencyContainer
import dev.fg.buildyourownx.libs.dependency_injection.dependency_container.DependencyContainerImpl
import dev.fg.buildyourownx.libs.dependency_injection.dependency_container.WritableDependencyContainer
import dev.fg.buildyourownx.libs.dependency_injection.injector.Injector

open class CreationExtras(
    open val params: Map<Any, Any>? = null
)

class VMCreationExtras(
    override val params: Map<Any, Any>? = null,
    val savedStateHandle: SavedStateHandle
) : CreationExtras()

class Module (
    val injector: Injector
) {
    @PublishedApi
    internal val dependencyContainer: WritableDependencyContainer = DependencyContainerImpl()

    inline fun <reified T: Any> single(qualifier: Any? = null, noinline block: (CreationExtras?) -> T) {
        dependencyContainer.addDependency(T::class, qualifier, DependencyType.SINGLE, block)
    }
    inline fun <reified T: Any> factory(qualifier: Any? = null, noinline block: (CreationExtras?) -> T) {
        dependencyContainer.addDependency(T::class, qualifier, DependencyType.FACTORY, block)
    }

    inline fun <reified T: ViewModel> viewModel(qualifier: Any? = null, noinline block: (VMCreationExtras) -> T) {
        dependencyContainer.addDependency(T::class, qualifier, DependencyType.FACTORY, block as (CreationExtras?) -> T)
    }

    inline fun <reified T: Any> get(qualifier: Any? = null, creationExtras: CreationExtras? = null) : T {
        return injector.dependencyContainer.getDependency(T::class, qualifier, creationExtras, injector.detectChain)
    }
}