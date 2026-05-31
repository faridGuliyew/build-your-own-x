package dev.fg.buildyourownx.libs.dependency_injection.dependency_container

import dev.fg.buildyourownx.libs.dependency_injection.CreationExtras
import kotlin.reflect.KClass

/**
 * Contains all dependencies, grouped by their types
 * Used in modules and the actual injector
 * */
interface DependencyContainer : WritableDependencyContainer {
    fun mergeContainer(container: WritableDependencyContainer)
    fun <T: Any> getDependency(clazz: KClass<T>, qualifier: Any?, creationExtras: CreationExtras?, detectChain: Boolean) : T
}