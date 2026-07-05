package dev.fg.buildyourownx.libs.navigation_visualizer.extensions

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

fun KClass<*>.getPrimaryConstructorParamNames(): List<String>? {
    return primaryConstructor?.parameters?.mapNotNull { it.name }
}