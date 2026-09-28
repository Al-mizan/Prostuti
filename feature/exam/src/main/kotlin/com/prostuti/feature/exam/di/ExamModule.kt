package com.prostuti.feature.exam.di

import com.prostuti.feature.exam.data.ExamRepositoryImpl
import com.prostuti.feature.exam.domain.ExamRepository
import com.prostuti.feature.exam.domain.GetAvailableSessionsUseCase
import com.prostuti.feature.exam.domain.GetLeaderboardUseCase
import com.prostuti.feature.exam.domain.StartExamSessionUseCase
import com.prostuti.feature.exam.domain.SubmitExamUseCase
import com.prostuti.feature.exam.presentation.ExamViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val examModule = module {
    single<ExamRepository> {
        ExamRepositoryImpl(
            examApi = get(),
            questionBankApi = get(),
            sessionStore = get(),
        )
    }

    factory { GetAvailableSessionsUseCase(repository = get()) }
    factory { StartExamSessionUseCase(repository = get()) }
    factory { SubmitExamUseCase(repository = get()) }
    factory { GetLeaderboardUseCase(repository = get()) }

    viewModel {
        ExamViewModel(
            getAvailableSessions = get(),
            startExamSession = get(),
            submitExamUseCase = get(),
            getLeaderboardUseCase = get(),
        )
    }
}
