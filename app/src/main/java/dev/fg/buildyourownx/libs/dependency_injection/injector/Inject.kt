package dev.fg.buildyourownx.libs.dependency_injection.injector

import dev.fg.buildyourownx.libs.dependency_injection.CreationExtras
import dev.fg.buildyourownx.libs.dependency_injection.Module
import kotlin.reflect.KClass

object Inject {
    @PublishedApi
    internal val injector = InjectorImpl()
    fun start(modules: List<Module>) {
        injector.start(modules)
    }

    @PublishedApi
    internal fun <T : Any> get(clazz: KClass<T>, qualifier: Any? = null, creationExtras: CreationExtras? = null): T {
        return injector.get(clazz, qualifier, creationExtras)
    }
}