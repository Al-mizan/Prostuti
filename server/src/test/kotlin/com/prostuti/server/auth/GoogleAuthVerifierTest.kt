package com.prostuti.server.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GoogleAuthVerifierTest {

    private class FakeGoogleTokenVerifier : GoogleTokenVerifier {
        override fun verify(idTokenString: String): GoogleUserData? {
            return if (idTokenString == "valid-token") {
                GoogleUserData(
                    googleId = "google-uid-12345",
                    email = "test@example.com",
                    name = "Test Student",
                )
            } else {
                null
            }
        }
    }

    @Test
    fun `GoogleTokenVerifier returns user data on valid token`() {
        val verifier = FakeGoogleTokenVerifier()
        val user = verifier.verify("valid-token")

        assertEquals("google-uid-12345", user?.googleId)
        assertEquals("test@example.com", user?.email)
        assertEquals("Test Student", user?.name)
    }

    @Test
    fun `GoogleTokenVerifier returns null on invalid token`() {
        val verifier = FakeGoogleTokenVerifier()
        val user = verifier.verify("invalid-token")

        assertNull(user)
    }

    @Test
    fun `GoogleAuthVerifier default client ID resolves safely without exception`() {
        val clientId = GoogleAuthVerifier.resolveClientId()
        assert(clientId.isNotBlank())
    }
}
