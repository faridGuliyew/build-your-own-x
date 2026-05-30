package dev.fg.buildyourownx.libs.dependency_injection

object Injector {
    private var isStarted = false
    @PublishedApi
    internal val dependencyContainer = DependencyContainer()

    fun start(modules: List<Module>) {
        if (isStarted) error("Injector already started")
        isStarted = true

        for (module in modules) {
            dependencyContainer.mergeContainer(module.dependencyContainer)
        }
    }

    inline fun <reified T> get(qualifier: Any? = null) : T {
        return dependencyContainer.getDependency(T::class.qualifiedName!!, qualifier)
    }

    fun verify() {
        // Create all single dependencies
        dependencyContainer.singleDependencies.values.forEach { it.invoke() }
        // Create all factory dependencies
        dependencyContainer.factoryDependencies.values.forEach { it.invoke() }
    }
}