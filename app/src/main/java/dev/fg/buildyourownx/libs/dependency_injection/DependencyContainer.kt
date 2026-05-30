package dev.fg.buildyourownx.libs.dependency_injection

/**
 * Contains all dependencies, grouped by their types
 * Used in modules and the actual injector
 * */
class DependencyContainer {
    val singleInitializedDependencies = InitializedDependencyMap()
    val singleDependencies = FactoryDependencyMap()
    val factoryDependencies = FactoryDependencyMap()

    fun mergeContainer(container: DependencyContainer) {
        singleDependencies.putAll(container.singleDependencies)
        factoryDependencies.putAll(container.factoryDependencies)
    }

    fun <T: Any> addDependency(className: String, qualifier: Any?, type: DependencyType, block: () -> T) {
        when (type) {
            DependencyType.SINGLE -> singleDependencies.getOrPut(className) { HashMap() }[qualifier] = block
            DependencyType.FACTORY -> factoryDependencies.getOrPut(className) { HashMap() }[qualifier] = block
        }
    }

    private fun <T: Any> addSingleInitializedDependency(className: String, qualifier: Any?, instance: T) {
        singleInitializedDependencies.getOrPut(className) { HashMap() }[qualifier] = instance
    }

    fun <T: Any> getDependency(className: String, qualifier: Any?) : T {
        return getInitializedSingleDependency(className, qualifier)
            ?: getSingleDependency(className, qualifier)
            ?: getFactoryDependency(className, qualifier)
            ?: error("No dependency for $className with qualifier: $qualifier")
    }

    // Getters for each dependency type
    private fun <T: Any> getInitializedSingleDependency(className: String, qualifier: Any?) : T? {
        return singleInitializedDependencies[className]?.get(qualifier) as? T
    }
    private fun <T: Any> getSingleDependency(className: String, qualifier: Any?) : T? {
        return (singleDependencies[className]?.get(qualifier)?.invoke() as? T)?.also {
            addSingleInitializedDependency(className, qualifier, it)
        }
    }
    private fun <T: Any> getFactoryDependency(className: String, qualifier: Any?) : T? {
        return factoryDependencies[className]?.get(qualifier)?.invoke() as? T
    }
}