package dev.fg.buildyourownx.libs.dependency_injection.dependency_container

import dev.fg.buildyourownx.libs.dependency_injection.DependencyType
import dev.fg.buildyourownx.libs.dependency_injection.FactoryDependencyConcurrentHashMap
import dev.fg.buildyourownx.libs.dependency_injection.FactoryDependencyHashMap
import kotlin.reflect.KClass

/**
 * Contains all dependencies.
 * Only supports adding dependencies, not getting them
 * */
interface WritableDependencyContainer {
    val singleDependencies: FactoryDependencyConcurrentHashMap
    val factoryDependencies: FactoryDependencyHashMap

    fun <T: Any> addDependency(clazz: KClass<T>, qualifier: Any?, type: DependencyType, block: () -> T)
}