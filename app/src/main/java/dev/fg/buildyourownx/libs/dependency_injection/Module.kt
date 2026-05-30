package dev.fg.buildyourownx.libs.dependency_injection

class Module {
    @PublishedApi
    internal val dependencyContainer = DependencyContainer()

    inline fun <reified T: Any> single(qualifier: Any? = null, noinline block: () -> T) {
        dependencyContainer.addDependency(T::class.qualifiedName!!, qualifier, DependencyType.SINGLE, block)
    }
    inline fun <reified T: Any> factory(qualifier: Any? = null, noinline block: () -> T) {
        dependencyContainer.addDependency(T::class.qualifiedName!!, qualifier, DependencyType.FACTORY, block)
    }

    inline fun <reified T: Any> get(qualifier: Any? = null) : T {
        return dependencyContainer.getDependency(T::class.qualifiedName!!, qualifier)
    }
}