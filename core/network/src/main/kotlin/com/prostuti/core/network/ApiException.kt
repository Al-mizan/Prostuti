package com.prostuti.core.network

/**
 * Exception thrown when the API response envelope indicates an error
 * (success == false or null data), or when an HTTP operational error occurs.
 */
class ApiException(
    override val message: String,
    val statusCode: Int? = null,
) : Exception(message)
