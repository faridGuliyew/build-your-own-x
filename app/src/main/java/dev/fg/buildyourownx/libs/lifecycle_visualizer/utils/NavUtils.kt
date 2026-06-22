package dev.fg.buildyourownx.libs.lifecycle_visualizer.utils

import androidx.core.os.bundleOf
import androidx.navigation.NavBackStackEntry
import dev.fg.buildyourownx.libs.lifecycle_visualizer.extensions.getPropertiesAsMultilineString
import kotlin.reflect.KClass

object NavUtils {
    fun getArgsMap(entry: NavBackStackEntry): Map<String, Any> {
        val args = entry.arguments ?: bundleOf()
        val navTypes = entry.destination.arguments.mapValues { it.value.type }
        return buildMap {
            for (key in args.keySet()) {
                val value = navTypes[key]?.get(args, key) ?: continue
                put(key, value)
            }
        }
    }

    fun getArgs(entry: NavBackStackEntry): String {
        return getArgsMap(entry).getPropertiesAsMultilineString()
    }

    // Return route name without params
    fun getSimpleRoute(route: String?): String? {
        if (route == null) return null
        val separatorList = listOf('/', '?')
        for (i in route.indices) {
            if (route[i] in separatorList) return route.substring(0, i)
        }
        return route
    }

    fun getRouteKClass(route: String?): KClass<*>? {
        return runCatching { Class.forName(getSimpleRoute(route)!!) }.getOrNull()?.kotlin
    }

    fun isTypeSafeRoute(route: String?) : Boolean {
        return getRouteKClass(route) != null
    }
}