package dev.fg.buildyourownx.libs.dependency_injection

sealed class InjectorException (override val cause: Throwable) : Exception(cause) {
    data class UnsupportedException (override val message: String) : InjectorException(Throwable(message))
    data class UnknownException(override val cause: Throwable) : InjectorException(cause)
}