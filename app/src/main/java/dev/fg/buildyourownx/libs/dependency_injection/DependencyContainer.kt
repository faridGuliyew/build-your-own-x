package dev.fg.buildyourownx.libs.dependency_injection

interface WritableDependencyContainer {
    val singleDependencies: FactoryDependencyConcurrentHashMap
    val factoryDependencies: FactoryDependencyHashMap

    fun <T: Any> addDependency(className: String, qualifier: Any?, type: DependencyType, block: () -> T)
}
/**
 * Contains all dependencies, grouped by their types
 * Used in modules and the actual injector
 * */
class DependencyContainer: WritableDependencyContainer {
    private val singleInitializedDependencies = InitializedDependencyConcurrentHashMap()
    override val singleDependencies = FactoryDependencyConcurrentHashMap()
    override val factoryDependencies = FactoryDependencyHashMap()

    fun mergeContainer(container: WritableDependencyContainer) {
        singleDependencies.putAll(container.singleDependencies)
        factoryDependencies.putAll(container.factoryDependencies)
    }

    override fun <T: Any> addDependency(className: String, qualifier: Any?, type: DependencyType, block: () -> T) {
        val key = DependencyKey(className, qualifier)
        when (type) {
            DependencyType.SINGLE -> singleDependencies[key] = block
            DependencyType.FACTORY -> factoryDependencies[key] = block
        }
    }

    private fun <T: Any> addSingleInitializedDependency(key: DependencyKey, instance: T) {
        singleInitializedDependencies[key] = instance
    }

    fun <T: Any> getDependency(className: String, qualifier: Any?) : T {
        val key = DependencyKey(className, qualifier)
        return getInitializedSingleDependency(key)
            ?: getFactoryDependency(key)
            ?: getSingleDependency(key)
            ?: error("No dependency for $className with qualifier: $qualifier")
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
            addSingleInitializedDependency(key, newDependency)
            newDependency
        }
    }
    private fun <T: Any> getFactoryDependency(key: DependencyKey) : T? {
        return factoryDependencies[key]?.invoke() as? T
    }
}