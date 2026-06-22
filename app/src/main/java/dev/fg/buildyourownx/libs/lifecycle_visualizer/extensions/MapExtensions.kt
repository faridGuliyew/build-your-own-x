package dev.fg.buildyourownx.libs.lifecycle_visualizer.extensions

import dev.fg.buildyourownx.libs.lifecycle_visualizer.utils.ReflectUtils
import dev.fg.buildyourownx.libs.lifecycle_visualizer.utils.shouldPrintDirectly

fun <A, B> Map<A, B>.getAsMultilineString(): String {
    return buildString {
        for (entry in this@getAsMultilineString as Map<*,*>) {
            append("${entry.key}: ${entry.value}\n")
        }
    }.trim()
}

fun <A, B> Map<A, B>.getPropertiesAsMultilineString(): String {
    return buildString {
        for (entry in this@getPropertiesAsMultilineString as Map<*,*>) {
            val valueAsString = if (shouldPrintDirectly(entry.value)) {
                entry.value.toString()
            } else {
                ReflectUtils.getProperties(entry.value)
            }
            append("${entry.key}: ${valueAsString}\n")
        }
    }.trim()
}