package dev.fg.buildyourownx.libs.dependency_injection.convenience

import dev.fg.buildyourownx.libs.dependency_injection.Module
import dev.fg.buildyourownx.libs.dependency_injection.injector.Inject

fun module(block: Module.() -> Unit) : Module {
    val module = Module(Inject.injector.dependencyContainer)
    block(module)
    return module
}