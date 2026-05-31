package dev.fg.buildyourownx.libs.dependency_injection.dependency_container

import dev.fg.buildyourownx.libs.dependency_injection.CreationExtras
import dev.fg.buildyourownx.libs.dependency_injection.DependencyKey
import dev.fg.buildyourownx.libs.dependency_injection.DependencyType
import dev.fg.buildyourownx.libs.dependency_injection.FactoryDependencyConcurrentHashMap
import dev.fg.buildyourownx.libs.dependency_injection.FactoryDependencyHashMap
import dev.fg.buildyourownx.libs.dependency_injection.InitializedDependencyConcurrentHashMap
import dev.fg.buildyourownx.libs.dependency_injection.InjectorException
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

    override fun <T: Any> addDependency(clazz: KClass<T>, qualifier: Any?, type: DependencyType, block: (CreationExtras?) -> T) {
        val key = DependencyKey(clazz as KClass<Any>, qualifier)
        when (type) {
            DependencyType.SINGLE -> singleDependencies[key] = block
            DependencyType.FACTORY -> factoryDependencies[key] = block
        }
    }

    private val resolvingKeys = ThreadLocal<MutableSet<DependencyKey>>()
    override fun <T: Any> getDependency(clazz: KClass<T>, qualifier: Any?, creationExtras: CreationExtras?, detectChain: Boolean) : T {
        val key = DependencyKey(clazz as KClass<Any>, qualifier)

        if (detectChain) {
            if (resolvingKeys.get() == null) resolvingKeys.set(LinkedHashSet())
            val currentChain = resolvingKeys.get()
            if (currentChain?.add(key) == false) {
                throw InjectorException.UnsupportedException(
                    "Circular dependency detected! Already resolving ${key.className} " +
                            "in the current graph chain: ${currentChain.joinToString(" -> ") { it.className }}"
                )
            }
        }

        try {
            return getInitializedSingleDependency(key)
                ?: getFactoryDependency(key, creationExtras)
                ?: getSingleDependency(key, creationExtras)
                ?: throw InjectorException.DefinitionNotFoundException("No dependency for ${key.className} with qualifier: $qualifier is found in any module")
        } finally {
            resolvingKeys.get()?.remove(key)
        }
    }

    // Getters for each dependency type
    private fun <T: Any> getInitializedSingleDependency(key: DependencyKey) : T? {
        return singleInitializedDependencies[key] as? T
    }
    private fun <T: Any> getSingleDependency(key: DependencyKey, creationExtras: CreationExtras?) : T? {
        val creator = singleDependencies[key] ?: return null

        return synchronized(this) {
            val initializedDependency = getInitializedSingleDependency(key) as T?
            if (initializedDependency != null) return initializedDependency

            val newDependency = creator.invoke(creationExtras) as? T ?: return null
            singleInitializedDependencies[key] = newDependency
            newDependency
        }
    }
    private fun <T: Any> getFactoryDependency(key: DependencyKey, creationExtras: CreationExtras?) : T? {
        return factoryDependencies[key]?.invoke(creationExtras) as? T
    }
}