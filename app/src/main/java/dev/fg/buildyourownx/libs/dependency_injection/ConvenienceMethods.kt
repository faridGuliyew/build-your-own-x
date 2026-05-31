package dev.fg.buildyourownx.libs.dependency_injection

import dev.fg.buildyourownx.libs.dependency_injection.injector.Inject
import dev.fg.buildyourownx.libs.dependency_injection.injector.InjectorImpl

fun module(block: Module.() -> Unit) : Module {
    val module = Module(Inject.injector.dependencyContainer)
    block(module)
    return module
}