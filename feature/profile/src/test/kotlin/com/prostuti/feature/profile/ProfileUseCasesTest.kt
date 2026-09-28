package com.prostuti.feature.profile

import com.prostuti.core.common.Result
import com.prostuti.core.model.Role
import com.prostuti.core.model.UpdateProfileRequest
import com.prostuti.core.model.UserProfileDto
import com.prostuti.feature.profile.domain.GetProfileUseCase
import com.prostuti.feature.profile.domain.ProfileRepository
import com.prostuti.feature.profile.domain.UpdateProfileUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileUseCasesTest {

    private class FakeProfileRepository : ProfileRepository {
        var profile = UserProfileDto(
            id = "user-123",
            name = "Test Student",
            email = "student@test.com",
            role = Role.STUDENT,
            avatarId = "mascot_1",
            createdAt = "2026-08-26T12:00:00Z",
        )
        var sessionCleared = false

        override suspend fun getProfile(): Result<UserProfileDto> =
            Result.Success(profile)

        override suspend fun updateProfile(request: UpdateProfileRequest): Result<UserProfileDto> {
            profile = profile.copy(name = request.name, avatarId = request.avatarId)
            return Result.Success(profile)
        }

        override fun clearSession() {
            sessionCleared = true
        }
    }

    @Test
    fun `GetProfileUseCase returns profile from repository`() = runBlocking {
        val repo = FakeProfileRepository()
        val useCase = GetProfileUseCase(repo)

        val result = useCase()
        assertTrue(result is Result.Success)
        assertEquals("Test Student", (result as Result.Success).value.name)
        assertEquals("mascot_1", result.value.avatarId)
    }

    @Test
    fun `UpdateProfileUseCase updates name and avatar`() = runBlocking {
        val repo = FakeProfileRepository()
        val useCase = UpdateProfileUseCase(repo)

        val result = useCase(name = "Updated Name", avatarId = "mascot_3")
        assertTrue(result is Result.Success)
        assertEquals("Updated Name", (result as Result.Success).value.name)
        assertEquals("mascot_3", result.value.avatarId)
    }

    @Test
    fun `UpdateProfileUseCase rejects blank name`() = runBlocking {
        val repo = FakeProfileRepository()
        val useCase = UpdateProfileUseCase(repo)

        val result = useCase(name = "   ", avatarId = "mascot_3")
        assertTrue(result is Result.Error)
        assertEquals("Name must not be blank", (result as Result.Error).message)
    }
}
