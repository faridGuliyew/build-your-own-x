package dev.fg.buildyourownx.libs.lifecycle_visualizer.utils

import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

object ReflectUtils {
    fun getProperties(instance: Any?): Map<String, Any?>? {
        if (instance == null) return null
        val result = mutableMapOf<String, Any?>()

        // Get all properties belonging to the object's class
        val properties = instance::class.memberProperties as? Collection<KProperty1<Any, *>> ?: return null

        for (property in properties) {
            val value = try {
                property.get(instance)
            } catch (e: Exception) {
                continue
            }

            if (value == null) {
                result[property.name] = null
                continue
            }

            if (shouldPrintDirectly(value)) {
                result[property.name] = value
            } else {
                result[property.name] = getProperties(value)
            }
        }

        return result
    }
}

fun shouldPrintDirectly(value: Any?): Boolean {
    if (value == null) return true
    val kClass = value::class

    return kClass.java.isPrimitive ||
            value is String ||
            value is Number ||
            value is Boolean ||
            value is Char ||
            value is Collection<*> ||
            value is Map<*, *> ||
            value is Array<*>
}