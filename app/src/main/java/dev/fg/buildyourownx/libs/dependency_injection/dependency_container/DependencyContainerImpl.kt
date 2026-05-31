package dev.fg.buildyourownx.libs.dependency_injection.dependency_container

import dev.fg.buildyourownx.libs.dependency_injection.DependencyKey
import dev.fg.buildyourownx.libs.dependency_injection.DependencyType
import dev.fg.buildyourownx.libs.dependency_injection.FactoryDependencyConcurrentHashMap
import dev.fg.buildyourownx.libs.dependency_injection.FactoryDependencyHashMap
import dev.fg.buildyourownx.libs.dependency_injection.InitializedDependencyConcurrentHashMap
import kotlin.collections.set
import kotlin.reflect.KClass

/**
 * Main Implementation of this interface.
 */
class DependencyContainerImpl: DependencyContainer {
    private val singleInitializedDependencies = InitializedDependencyConcurrentHashMap()
    override val singleDependencies = FactoryDependencyConcurrentHashMap()
    override val factoryDependencies = FactoryDependencyHashMap()

    override fun mergeContainer(container: WritableDependencyContainer) {
        singleDependencies.putAll(container.singleDependencies)
        factoryDependencies.putAll(container.factoryDependencies)
    }

    override fun <T: Any> addDependency(clazz: KClass<T>, qualifier: Any?, type: DependencyType, block: () -> T) {
        val key = DependencyKey(clazz as KClass<Any>, qualifier)
        when (type) {
            DependencyType.SINGLE -> singleDependencies[key] = block
            DependencyType.FACTORY -> factoryDependencies[key] = block
        }
    }

    override fun <T: Any> getDependency(clazz: KClass<T>, qualifier: Any?) : T {
        val key = DependencyKey(clazz as KClass<Any>, qualifier)
        return getInitializedSingleDependency(key)
            ?: getFactoryDependency(key)
            ?: getSingleDependency(key)
            ?: error("No dependency for ${key.className} with qualifier: $qualifier")
    }

    // Getters for each dependency type
    private fun <T: Any> getInitializedSingleDependency(key: DependencyKey) : T? {
        return singleInitializedDependencies[key] as? T
    }
    private fun <T: Any> getSingleDependency(key: DependencyKey) : T? {
        return synchronized(this) {
            val initializedDependency = getInitializedSingleDependency(key) as T?
            if (initializedDependency != null) return initializedDependency

            val newDependency = singleDependencies[key]?.invoke() as? T ?: return null
            singleInitializedDependencies[key] = newDependency
            newDependency
        }
    }
    private fun <T: Any> getFactoryDependency(key: DependencyKey) : T? {
        return factoryDependencies[key]?.invoke() as? T
    }
}