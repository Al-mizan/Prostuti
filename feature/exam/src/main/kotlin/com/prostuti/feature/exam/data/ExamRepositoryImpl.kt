package com.prostuti.feature.exam.data

import com.prostuti.core.common.Result
import com.prostuti.core.common.SessionStore
import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.ExamAnswerSubmission
import com.prostuti.core.model.ExamResultDto
import com.prostuti.core.model.ExamSessionDto
import com.prostuti.core.model.LeaderboardEntryDto
import com.prostuti.core.model.StartExamSessionRequest
import com.prostuti.core.model.SubmitExamRequest
import com.prostuti.core.network.ExamApi
import com.prostuti.core.network.QuestionBankApi
import com.prostuti.feature.exam.domain.ExamRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.serialization.SerializationException

class ExamRepositoryImpl(
    private val examApi: ExamApi,
    private val questionBankApi: QuestionBankApi,
    private val sessionStore: SessionStore,
) : ExamRepository {

    override suspend fun getAvailableSessions(): Result<List<BcsSessionSummaryDto>> = try {
        val result = questionBankApi.getSessions()
        Result.Success(result)
    } catch (e: ClientRequestException) {
        handleClientError(e)
    } catch (e: ServerResponseException) {
        Result.Error("Server error: ${e.response.status.value}")
    } catch (e: SerializationException) {
        Result.Error("Unexpected response format from server")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error occurred")
    }

    override suspend fun startSession(
        examSession: String,
        questionCount: Int,
        durationMinutes: Int,
    ): Result<ExamSessionDto> = try {
        val result = examApi.startSession(
            StartExamSessionRequest(
                examSession = examSession,
                questionCount = questionCount,
                durationMinutes = durationMinutes,
            )
        )
        Result.Success(result)
    } catch (e: ClientRequestException) {
        handleClientError(e)
    } catch (e: ServerResponseException) {
        Result.Error("Server error: ${e.response.status.value}")
    } catch (e: SerializationException) {
        Result.Error("Unexpected response format from server")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error occurred")
    }

    override suspend fun submitExam(
        sessionId: String,
        answers: List<ExamAnswerSubmission>,
        timeTakenSeconds: Int,
    ): Result<ExamResultDto> = try {
        val result = examApi.submitExam(
            sessionId = sessionId,
            request = SubmitExamRequest(answers = answers, timeTakenSeconds = timeTakenSeconds)
        )
        Result.Success(result)
    } catch (e: ClientRequestException) {
        handleClientError(e)
    } catch (e: ServerResponseException) {
        Result.Error("Server error: ${e.response.status.value}")
    } catch (e: SerializationException) {
        Result.Error("Unexpected response format from server")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error occurred")
    }

    override suspend fun getLeaderboard(examSession: String): Result<List<LeaderboardEntryDto>> = try {
        val result = examApi.getLeaderboard(examSession)
        Result.Success(result)
    } catch (e: ClientRequestException) {
        handleClientError(e)
    } catch (e: ServerResponseException) {
        Result.Error("Server error: ${e.response.status.value}")
    } catch (e: SerializationException) {
        Result.Error("Unexpected response format from server")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error occurred")
    }

    private fun <T> handleClientError(e: ClientRequestException): Result<T> {
        return if (e.response.status.value == 401) {
            sessionStore.clear()
            Result.Error("Session expired. Please log in again.")
        } else {
            Result.Error("Request failed (${e.response.status.value})")
        }
    }
}
