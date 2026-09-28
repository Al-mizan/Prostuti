package com.prostuti.core.common

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.prostuti.core.model.AuthResponse
import com.prostuti.core.model.Role

/**
 * Persists the current session — JWT + role + user id — in
 * EncryptedSharedPreferences so it survives app restarts but is encrypted
 * at rest per backend_design.md §8.
 *
 * Single instance per app: Koin registers it as `single`.
 */
class SessionStore(private val prefs: SharedPreferences) {

    val token: String? get() = prefs.getString(KEY_TOKEN, null)
    val userId: String? get() = prefs.getString(KEY_USER_ID, null)
    val role: Role? get() = prefs.getString(KEY_ROLE, null)?.let { runCatching { Role.valueOf(it) }.getOrNull() }

    fun hasSession(): Boolean = !token.isNullOrBlank()

    fun save(auth: AuthResponse) {
        prefs.edit()
            .putString(KEY_TOKEN, auth.token)
            .putString(KEY_USER_ID, auth.userId)
            .putString(KEY_ROLE, auth.role.name)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        const val PREFS_NAME = "prostuti_secure_session"
        private const val KEY_TOKEN = "jwt"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_ROLE = "role"

        /**
         * The MasterKey API requires a `name` resource. The default
         * `MasterKey.DEFAULT_MASTER_KEY_ALIAS` resolves to "_androidx_security_master_key_"
         * which the OS creates on first use; no extra file is needed.
         */
        fun create(context: Context): SessionStore {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            val prefs = EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
            return SessionStore(prefs)
        }
    }
}
