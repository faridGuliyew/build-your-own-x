package dev.fg.buildyourownx.libs.dependency_injection.injector

import dev.fg.buildyourownx.libs.dependency_injection.CreationExtras
import kotlin.reflect.KClass

/**
 * Interface that allows for injection.
 * Any class must implement this interface to get access to inject method
 * Only specific methods in `convenience` package are exceptions (such as injectViewModel),
 * and they bypass this requirement.
 */
interface Injectable {
    fun <T : Any> inject(clazz: KClass<T>, qualifier: Any? = null, creationExtras: CreationExtras? = null): T {
        return Inject.get(clazz, qualifier, creationExtras)
    }
}
inline fun <reified T : Any> Injectable.inject(qualifier: Any? = null, creationExtras: CreationExtras? = null): T {
    return inject(T::class, qualifier, creationExtras)
}