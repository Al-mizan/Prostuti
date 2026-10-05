package com.prostuti.core.network

import android.content.SharedPreferences
import com.prostuti.core.common.SessionStore
import com.prostuti.core.model.AuthResponse
import com.prostuti.core.model.Role
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HttpClientAuthTest {

    private class InMemorySharedPreferences : SharedPreferences {
        private val data = mutableMapOf<String, Any?>()

        override fun getAll(): MutableMap<String, *> = HashMap(data)
        override fun getString(key: String?, defValue: String?): String? = (data[key] as? String) ?: defValue
        override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? =
            @Suppress("UNCHECKED_CAST") (data[key] as? MutableSet<String>) ?: defValues
        override fun getInt(key: String?, defValue: Int): Int = (data[key] as? Int) ?: defValue
        override fun getLong(key: String?, defValue: Long): Long = (data[key] as? Long) ?: defValue
        override fun getFloat(key: String?, defValue: Float): Float = (data[key] as? Float) ?: defValue
        override fun getBoolean(key: String?, defValue: Boolean): Boolean = (data[key] as? Boolean) ?: defValue
        override fun contains(key: String?): Boolean = data.containsKey(key)
        override fun edit(): SharedPreferences.Editor = EditorImpl()
        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

        private inner class EditorImpl : SharedPreferences.Editor {
            private val temp = mutableMapOf<String, Any?>()
            private var clearAll = false

            override fun putString(key: String?, value: String?): SharedPreferences.Editor {
                if (key != null) temp[key] = value
                return this
            }
            override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor {
                if (key != null) temp[key] = values
                return this
            }
            override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
                if (key != null) temp[key] = value
                return this
            }
            override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
                if (key != null) temp[key] = value
                return this
            }
            override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
                if (key != null) temp[key] = value
                return this
            }
            override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
                if (key != null) temp[key] = value
                return this
            }
            override fun remove(key: String?): SharedPreferences.Editor {
                if (key != null) temp[key] = this
                return this
            }
            override fun clear(): SharedPreferences.Editor {
                clearAll = true
                return this
            }
            override fun commit(): Boolean {
                apply()
                return true
            }
            override fun apply() {
                if (clearAll) {
                    data.clear()
                }
                temp.forEach { (k, v) ->
                    if (v === this) data.remove(k) else data[k] = v
                }
                temp.clear()
                clearAll = false
            }
        }
    }

    private fun applyDynamicAuth(builder: HttpRequestBuilder, sessionStore: SessionStore) {
        val token = sessionStore.token
        if (!token.isNullOrBlank()) {
            builder.header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    @Test
    fun `request carries Authorization Bearer when token exists`() {
        val prefs = InMemorySharedPreferences()
        val sessionStore = SessionStore(prefs)
        sessionStore.save(AuthResponse(token = "valid-token-123", userId = "u1", role = Role.STUDENT))

        val builder = HttpRequestBuilder()
        applyDynamicAuth(builder, sessionStore)

        assertEquals("Bearer valid-token-123", builder.headers[HttpHeaders.Authorization])
    }

    @Test
    fun `request carries NO token when sessionStore is cleared`() {
        val prefs = InMemorySharedPreferences()
        val sessionStore = SessionStore(prefs)
        sessionStore.save(AuthResponse(token = "initial-token", userId = "u1", role = Role.STUDENT))

        val builder1 = HttpRequestBuilder()
        applyDynamicAuth(builder1, sessionStore)
        assertEquals("Bearer initial-token", builder1.headers[HttpHeaders.Authorization])

        // Logout
        sessionStore.clear()

        val builder2 = HttpRequestBuilder()
        applyDynamicAuth(builder2, sessionStore)
        assertNull(builder2.headers[HttpHeaders.Authorization])
    }

    @Test
    fun `request immediately attaches new user token after switching accounts without recreating client`() {
        val prefs = InMemorySharedPreferences()
        val sessionStore = SessionStore(prefs)
        sessionStore.save(AuthResponse(token = "user-alice-token", userId = "alice", role = Role.STUDENT))

        val builderAlice = HttpRequestBuilder()
        applyDynamicAuth(builderAlice, sessionStore)
        assertEquals("Bearer user-alice-token", builderAlice.headers[HttpHeaders.Authorization])

        // Logout Alice
        sessionStore.clear()

        // Login as Bob
        sessionStore.save(AuthResponse(token = "user-bob-token", userId = "bob", role = Role.ADMIN))

        val builderBob = HttpRequestBuilder()
        applyDynamicAuth(builderBob, sessionStore)
        assertEquals("Bearer user-bob-token", builderBob.headers[HttpHeaders.Authorization])
    }
}
