package dev.fg.buildyourownx.libs.dependency_injection

import java.util.concurrent.ConcurrentHashMap

typealias InitializedDependencyConcurrentHashMap = ConcurrentHashMap<DependencyKey, Any>
typealias FactoryDependencyConcurrentHashMap = ConcurrentHashMap<DependencyKey, () -> Any>
typealias FactoryDependencyHashMap = HashMap<DependencyKey, () -> Any>