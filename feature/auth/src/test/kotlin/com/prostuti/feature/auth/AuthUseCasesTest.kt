package com.prostuti.feature.auth

import com.prostuti.core.common.Result
import com.prostuti.core.model.AuthResponse
import com.prostuti.core.model.MeResponse
import com.prostuti.core.model.Role
import com.prostuti.feature.auth.domain.AuthRepository
import com.prostuti.feature.auth.domain.GetCurrentUserUseCase
import com.prostuti.feature.auth.domain.LoginUseCase
import com.prostuti.feature.auth.domain.LoginWithGoogleUseCase
import com.prostuti.feature.auth.domain.RegisterUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthUseCasesTest {

    private class FakeAuthRepository : AuthRepository {
        var registeredUser: String? = null
        var loggedInUser: String? = null
        var googleIdToken: String? = null

        override suspend fun register(name: String, email: String, password: String): Result<AuthResponse> {
            registeredUser = email
            return Result.Success(AuthResponse("jwt-token-123", "user-1", Role.STUDENT))
        }

        override suspend fun login(email: String, password: String): Result<AuthResponse> {
            loggedInUser = email
            return Result.Success(AuthResponse("jwt-token-123", "user-1", Role.STUDENT))
        }

        override suspend fun loginWithGoogle(idToken: String): Result<AuthResponse> {
            googleIdToken = idToken
            return Result.Success(AuthResponse("jwt-google-token-456", "google-user-1", Role.STUDENT))
        }

        override suspend fun me(): Result<MeResponse> =
            Result.Success(MeResponse("user-1", Role.STUDENT))

        override fun clearSession() {}
        override fun hasSession(): Boolean = true
        override fun currentRole(): Role? = Role.STUDENT
    }

    @Test
    fun `LoginWithGoogleUseCase passes id token to repository and succeeds`() = runBlocking {
        val repo = FakeAuthRepository()
        val useCase = LoginWithGoogleUseCase(repo)

        val result = useCase("sample-google-id-token-xyz")

        assertTrue(result is Result.Success)
        val auth = (result as Result.Success).value
        assertEquals("jwt-google-token-456", auth.token)
        assertEquals("sample-google-id-token-xyz", repo.googleIdToken)
    }

    @Test
    fun `RegisterUseCase validates minimum requirements before calling repository`() = runBlocking {
        val repo = FakeAuthRepository()
        val useCase = RegisterUseCase(repo)

        val invalidName = useCase("A", "valid@email.com", "password123")
        assertTrue(invalidName is Result.Error)

        val invalidEmail = useCase("John Doe", "invalidemail", "password123")
        assertTrue(invalidEmail is Result.Error)

        val invalidPassword = useCase("John Doe", "valid@email.com", "short")
        assertTrue(invalidPassword is Result.Error)

        val valid = useCase("John Doe", "valid@email.com", "password123")
        assertTrue(valid is Result.Success)
        assertEquals("valid@email.com", repo.registeredUser)
    }
}
