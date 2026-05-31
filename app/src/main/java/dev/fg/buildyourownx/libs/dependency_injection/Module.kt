package dev.fg.buildyourownx.libs.dependency_injection

import dev.fg.buildyourownx.libs.dependency_injection.dependency_container.DependencyContainer
import dev.fg.buildyourownx.libs.dependency_injection.dependency_container.DependencyContainerImpl
import dev.fg.buildyourownx.libs.dependency_injection.dependency_container.WritableDependencyContainer

class Module (
    val globalContainer: DependencyContainer
) {
    @PublishedApi
    internal val dependencyContainer: WritableDependencyContainer = DependencyContainerImpl()

    inline fun <reified T: Any> single(qualifier: Any? = null, noinline block: () -> T) {
        dependencyContainer.addDependency(T::class, qualifier, DependencyType.SINGLE, block)
    }
    inline fun <reified T: Any> factory(qualifier: Any? = null, noinline block: () -> T) {
        dependencyContainer.addDependency(T::class, qualifier, DependencyType.FACTORY, block)
    }

    inline fun <reified T: Any> get(qualifier: Any? = null) : T {
        return globalContainer.getDependency(T::class, qualifier)
    }
}