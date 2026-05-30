package dev.fg.buildyourownx.libs.dependency_injection

data class DependencyKey(
    val className: String,
    val qualifier: Any?
)