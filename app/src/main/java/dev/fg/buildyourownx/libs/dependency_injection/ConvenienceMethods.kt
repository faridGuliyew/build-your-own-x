package dev.fg.buildyourownx.libs.dependency_injection

fun module(block: Module.() -> Unit) : Module {
    val module = Module(Injector.dependencyContainer)
    block(module)
    return module
}