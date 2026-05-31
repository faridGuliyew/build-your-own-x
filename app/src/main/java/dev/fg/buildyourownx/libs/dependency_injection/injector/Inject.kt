package dev.fg.buildyourownx.libs.dependency_injection.injector

import dev.fg.buildyourownx.libs.dependency_injection.Module

object Inject {
    @PublishedApi
    internal val injector = InjectorImpl()
    fun start(modules: List<Module>) {
        injector.start(modules)
    }

    inline fun <reified T : Any> get(qualifier: Any? = null): T {
        return injector.get<T>(qualifier)
    }
}