package dev.fg.buildyourownx.libs.dependency_injection

import kotlin.reflect.KClass

data class DependencyKey(
    val clazz: KClass<Any>,
    val qualifier: Any?
) {
    val className = clazz.qualifiedName!!
}