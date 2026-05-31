package dev.fg.buildyourownx.libs.dependency_injection.injector

import dev.fg.buildyourownx.libs.dependency_injection.Module
import dev.fg.buildyourownx.libs.dependency_injection.dependency_container.DependencyContainer
import kotlin.reflect.KClass

/**
 * Injector is the core class that is responsible for providing dependencies.
 * It constructs its dependency graph from modules passed into it.
 * */
interface Injector {
    val dependencyContainer: DependencyContainer
    fun start(modules: List<Module>)

    fun <T : Any> get(clazz: KClass<T>, qualifier: Any? = null): T

    fun verifyModules()
}
inline fun <reified T : Any> Injector.get(qualifier: Any? = null): T {
    return this.get(T::class, qualifier)
}