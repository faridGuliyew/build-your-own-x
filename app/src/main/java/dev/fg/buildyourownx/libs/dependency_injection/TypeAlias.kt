package dev.fg.buildyourownx.libs.dependency_injection

typealias InitializedDependencyMap = HashMap<String, HashMap<Any?, Any>>
typealias FactoryDependencyMap = HashMap<String, HashMap<Any?, () -> Any>>