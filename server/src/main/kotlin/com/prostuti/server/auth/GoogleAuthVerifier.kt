package com.prostuti.server.auth

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import io.github.cdimascio.dotenv.dotenv
import java.util.Collections

data class GoogleUserData(
    val googleId: String,
    val email: String,
    val name: String,
)

interface GoogleTokenVerifier {
    fun verify(idTokenString: String): GoogleUserData?
}

class GoogleAuthVerifier(
    webClientId: String = resolveClientId()
) : GoogleTokenVerifier {

    private val verifier: GoogleIdTokenVerifier = GoogleIdTokenVerifier.Builder(
        NetHttpTransport(),
        GsonFactory.getDefaultInstance()
    )
        .setAudience(Collections.singletonList(webClientId))
        .build()

    override fun verify(idTokenString: String): GoogleUserData? {
        return try {
            val idToken: GoogleIdToken? = verifier.verify(idTokenString)
            if (idToken != null) {
                val payload = idToken.payload
                val userId = payload.subject
                val email = payload.email
                val name = (payload["name"] as? String)?.takeIf { it.isNotBlank() }
                    ?: email.substringBefore("@")
                GoogleUserData(googleId = userId, email = email, name = name)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        fun resolveClientId(): String {
            val dotenv = dotenv { ignoreIfMissing = true }
            return dotenv["WEB_GOOGLE_CLIENT_ID"]
                ?: System.getenv("WEB_GOOGLE_CLIENT_ID")
                ?: "746885731832-ccsjstr7t6tiu8esaujlut4g0msfid25.apps.googleusercontent.com"
        }
    }
}
