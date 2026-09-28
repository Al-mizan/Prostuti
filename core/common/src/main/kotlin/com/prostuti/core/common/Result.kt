package com.prostuti.core.common

/**
 * Lightweight Result type. Repositories return this from data calls;
 * ViewModels reduce to UiState. Kept here instead of core/network so the
 * domain layer (which depends on core/common, not core/network) can use it.
 *
 * Not `kotlin.Result` — that one is sealed and can't be re-exposed cleanly
 * across module boundaries without `runCatching` glue. This is a plain
 * sealed interface, cheap to construct, easy to pattern-match on.
 */
sealed interface Result<out T> {
    data class Success<T>(val value: T) : Result<T>
    data class Error(val message: String, val cause: Throwable? = null) : Result<Nothing>
}

inline fun <T, R> Result<T>.map(block: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(block(value))
    is Result.Error -> this
}
