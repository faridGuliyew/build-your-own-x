package dev.fg.buildyourownx.libs.dependency_injection.injector

import dev.fg.buildyourownx.libs.dependency_injection.CreationExtras
import dev.fg.buildyourownx.libs.dependency_injection.InjectorException
import dev.fg.buildyourownx.libs.dependency_injection.Module
import dev.fg.buildyourownx.libs.dependency_injection.dependency_container.DependencyContainerImpl
import kotlin.reflect.KClass

/**
 * Main implementation of Injector interface.
 * */
class InjectorImpl : Injector {
    override val dependencyContainer = DependencyContainerImpl()
    override val detectChain = true

    override fun start(modules: List<Module>) {
        for (module in modules) {
            dependencyContainer.mergeContainer(module.dependencyContainer)
        }
    }

    override fun <T : Any> get(clazz: KClass<T>, qualifier: Any?, creationExtras: CreationExtras?): T {
        return dependencyContainer.getDependency(clazz, qualifier, creationExtras, detectChain = detectChain)
    }

    override fun verifyModules() {
        throw InjectorException.UnsupportedException("verifyModules() is expensive & shouldn't be called on ${this::class.qualifiedName}. Use test implementation instead.")
    }
}