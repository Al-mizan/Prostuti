package com.prostuti.core.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.WifiOff
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorComponentsTest {

    @Test
    fun `NetworkOffline properties have expected defaults and icon`() {
        val error = ProstutiErrorType.NetworkOffline
        assertEquals("ইন্টারনেট সংযোগ নেই", error.defaultTitle)
        assertEquals("অনুগ্রহ করে আপনার ইন্টারনেট সংযোগ পরীক্ষা করে পুনরায় চেষ্টা করুন", error.defaultSubtitle)
        assertEquals("পুনরায় চেষ্টা করুন", error.defaultActionLabel)
        assertEquals(Icons.Default.WifiOff, error.icon)
    }

    @Test
    fun `SessionExpired properties have expected defaults and icon`() {
        val error = ProstutiErrorType.SessionExpired
        assertEquals("সেশন সমাপ্ত হয়েছে", error.defaultTitle)
        assertEquals("আপনার নিরাপত্তার জন্য অনুগ্রহ করে পুনরায় লগইন করুন", error.defaultSubtitle)
        assertEquals("লগইন করুন", error.defaultActionLabel)
        assertEquals(Icons.Default.Lock, error.icon)
    }

    @Test
    fun `ServerError properties have expected defaults and icon`() {
        val error = ProstutiErrorType.ServerError
        assertEquals("সার্ভারে সমস্যা হচ্ছে", error.defaultTitle)
        assertEquals(
            "সার্ভারে সাময়িক কারিগরি ত্রুটি দেখা দিয়েছে। আমাদের টিম দ্রুত ঠিক করার চেষ্টা করছে।",
            error.defaultSubtitle
        )
        assertEquals("পুনরায় চেষ্টা করুন", error.defaultActionLabel)
        assertEquals(Icons.Default.CloudOff, error.icon)
    }

    @Test
    fun `NotFound properties have expected defaults and icon`() {
        val error = ProstutiErrorType.NotFound
        assertEquals("কোনো তথ্য পাওয়া যায়নি", error.defaultTitle)
        assertEquals("অনুরোধ করা তথ্যটি খুঁজে পাওয়া যায়নি।", error.defaultSubtitle)
        assertEquals("পুনরায় চেষ্টা করুন", error.defaultActionLabel)
        assertEquals(Icons.Default.SearchOff, error.icon)
    }

    @Test
    fun `Generic error uses custom message in defaultSubtitle`() {
        val error = ProstutiErrorType.Generic("Payment gateway declined the transaction")
        assertEquals("একটি ত্রুটি ঘটেছে", error.defaultTitle)
        assertEquals("Payment gateway declined the transaction", error.defaultSubtitle)
        assertEquals("পুনরায় চেষ্টা করুন", error.defaultActionLabel)
        assertEquals(Icons.Default.WarningAmber, error.icon)

        val blankError = ProstutiErrorType.Generic("")
        assertEquals("কিছু একটা সমস্যা হয়েছে। অনুগ্রহ করে আবার চেষ্টা করুন।", blankError.defaultSubtitle)
    }

    @Test
    fun `Custom error conforms to sealed interface`() {
        val error: ProstutiErrorType = ProstutiErrorType.Custom("Custom detail")
        assertEquals("একটি ত্রুটি ঘটেছে", error.defaultTitle)
        assertEquals("Custom detail", error.defaultSubtitle)
        assertEquals(Icons.Default.WarningAmber, error.icon)
    }

    @Test
    fun `parseErrorType correctly classifies by HTTP status codes`() {
        assertEquals(ProstutiErrorType.SessionExpired, parseErrorType("Anything", statusCode = 401))
        assertEquals(ProstutiErrorType.SessionExpired, parseErrorType("Anything", statusCode = 403))
        assertEquals(ProstutiErrorType.NotFound, parseErrorType("Anything", statusCode = 404))
        assertEquals(ProstutiErrorType.ServerError, parseErrorType("Anything", statusCode = 500))
        assertEquals(ProstutiErrorType.ServerError, parseErrorType("Anything", statusCode = 502))
        assertEquals(ProstutiErrorType.ServerError, parseErrorType("Anything", statusCode = 503))
    }

    @Test
    fun `parseErrorType correctly classifies network keywords`() {
        assertTrue(parseErrorType("Failed to connect to /10.0.2.2:8080") is ProstutiErrorType.NetworkOffline)
        assertTrue(parseErrorType("Read timeout occurred") is ProstutiErrorType.NetworkOffline)
        assertTrue(parseErrorType("Unable to resolve host api.prostuti.com") is ProstutiErrorType.NetworkOffline)
        assertTrue(parseErrorType("Network is unreachable") is ProstutiErrorType.NetworkOffline)
        assertTrue(parseErrorType("Device is offline") is ProstutiErrorType.NetworkOffline)
        assertTrue(parseErrorType("No internet connection") is ProstutiErrorType.NetworkOffline)
        assertTrue(parseErrorType("ইন্টারনেট সংযোগ বিচ্ছিন্ন") is ProstutiErrorType.NetworkOffline)
    }

    @Test
    fun `parseErrorType correctly classifies session and auth keywords`() {
        assertTrue(parseErrorType("Unauthorized request") is ProstutiErrorType.SessionExpired)
        assertTrue(parseErrorType("Token expired or invalid") is ProstutiErrorType.SessionExpired)
        assertTrue(parseErrorType("Session expired, please re-login") is ProstutiErrorType.SessionExpired)
        assertTrue(parseErrorType("লগইন মেয়াদোত্তীর্ণ") is ProstutiErrorType.SessionExpired)
    }

    @Test
    fun `parseErrorType correctly classifies server error keywords`() {
        assertTrue(parseErrorType("Internal server error") is ProstutiErrorType.ServerError)
        assertTrue(parseErrorType("500 Internal Server") is ProstutiErrorType.ServerError)
        assertTrue(parseErrorType("502 Bad Gateway") is ProstutiErrorType.ServerError)
        assertTrue(parseErrorType("503 Service Unavailable") is ProstutiErrorType.ServerError)
        assertTrue(parseErrorType("সার্ভার ত্রুটি") is ProstutiErrorType.ServerError)
    }

    @Test
    fun `parseErrorType correctly classifies not found keywords`() {
        assertTrue(parseErrorType("Exam 1234 not found") is ProstutiErrorType.NotFound)
        assertTrue(parseErrorType("404 Resource missing") is ProstutiErrorType.NotFound)
        assertTrue(parseErrorType("প্রশ্ন খুঁজে পাওয়া যায়নি") is ProstutiErrorType.NotFound)
        assertTrue(parseErrorType("No questions found for session: 47th BCS Preliminary") is ProstutiErrorType.NotFound)
        assertTrue(parseErrorType("No questions available for subject: BENGALI") is ProstutiErrorType.NotFound)
        assertTrue(parseErrorType("কোনো প্রশ্ন পাওয়া যায়নি") is ProstutiErrorType.NotFound)
    }

    @Test
    fun `parseErrorType does not classify session messages as SessionExpired unless auth-related`() {
        assertFalse(parseErrorType("No questions found for session: 47th BCS Preliminary") is ProstutiErrorType.SessionExpired)
        assertFalse(parseErrorType("Exam session 123 started") is ProstutiErrorType.SessionExpired)
        assertFalse(parseErrorType("Practice session created successfully") is ProstutiErrorType.SessionExpired)
    }

    @Test
    fun `parseErrorType falls back to Generic for unclassified errors`() {
        val error = parseErrorType("Invalid score parameter: must be >= 0")
        assertTrue(error is ProstutiErrorType.Generic)
        assertEquals("Invalid score parameter: must be >= 0", (error as ProstutiErrorType.Generic).message)
    }

    @Test
    fun `parseErrorType handles Throwable network exceptions`() {
        val dnsException = UnknownHostException("Unable to resolve host")
        assertEquals(ProstutiErrorType.NetworkOffline, parseErrorType(dnsException))

        val connectException = ConnectException("Connection refused")
        assertEquals(ProstutiErrorType.NetworkOffline, parseErrorType(connectException))

        val timeoutException = SocketTimeoutException("timeout")
        assertEquals(ProstutiErrorType.NetworkOffline, parseErrorType(timeoutException))
    }

    private class FakeApiException(
        override val message: String,
        val statusCode: Int? = null,
    ) : Exception(message)

    @Test
    fun `parseErrorType extracts statusCode from custom exception via reflection`() {
        val authError = FakeApiException("Unauthorized", statusCode = 401)
        assertEquals(ProstutiErrorType.SessionExpired, parseErrorType(authError))

        val serverError = FakeApiException("Crash", statusCode = 500)
        assertEquals(ProstutiErrorType.ServerError, parseErrorType(serverError))

        val notFoundError = FakeApiException("Missing", statusCode = 404)
        assertEquals(ProstutiErrorType.NotFound, parseErrorType(notFoundError))
    }
}
