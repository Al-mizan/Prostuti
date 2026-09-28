package com.prostuti.feature.history.di

import com.prostuti.feature.history.data.HistoryRepositoryImpl
import com.prostuti.feature.history.domain.GetWrongAnswersUseCase
import com.prostuti.feature.history.domain.GetUserAttemptsUseCase
import com.prostuti.feature.history.domain.HistoryRepository
import com.prostuti.feature.history.presentation.HistoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val historyModule = module {
    single<HistoryRepository> {
        HistoryRepositoryImpl(
            api = get(),
            sessionStore = get(),
        )
    }

    factory { GetUserAttemptsUseCase(repository = get()) }
    factory { GetWrongAnswersUseCase(repository = get()) }

    viewModel {
        HistoryViewModel(
            getUserAttempts = get(),
            getWrongAnswers = get(),
        )
    }
}
