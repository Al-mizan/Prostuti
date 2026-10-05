package com.prostuti.core.network

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.http.takeFrom
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ApiConfigTest {

    @Test
    fun testApiConfigValidation() {
        val config = ApiConfig("https://api-prostuti.onrender.com/")
        assertEquals("https://api-prostuti.onrender.com/", config.baseUrl)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testApiConfigBlankThrows() {
        ApiConfig("   ")
    }

    @Test
    fun testKtorUrlResolutionWithTrailingSlash() {
        val builder = HttpRequestBuilder()
        builder.url("https://api-prostuti.onrender.com/")
        builder.url.takeFrom("api/v1/auth/login")
        assertEquals("https://api-prostuti.onrender.com/api/v1/auth/login", builder.url.buildString())
    }

    @Test
    fun testKtorUrlResolutionWithoutTrailingSlash() {
        val builder = HttpRequestBuilder()
        builder.url("https://api-prostuti.onrender.com")
        builder.url.takeFrom("api/v1/auth/login")
        assertEquals("https://api-prostuti.onrender.com/api/v1/auth/login", builder.url.buildString())
    }
}
