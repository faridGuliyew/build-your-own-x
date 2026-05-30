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
}