package com.prostuti.feature.practice.di

import com.prostuti.feature.practice.data.PracticeRepositoryImpl
import com.prostuti.feature.practice.domain.FinishPracticeSessionUseCase
import com.prostuti.feature.practice.domain.PracticeRepository
import com.prostuti.feature.practice.domain.StartPracticeSessionUseCase
import com.prostuti.feature.practice.domain.SubmitPracticeAnswerUseCase
import com.prostuti.feature.practice.presentation.PracticeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val practiceModule = module {
    single<PracticeRepository> {
        PracticeRepositoryImpl(api = get(), sessionStore = get())
    }
    factory { StartPracticeSessionUseCase(get()) }
    factory { SubmitPracticeAnswerUseCase(get()) }
    factory { FinishPracticeSessionUseCase(get()) }
    viewModel { PracticeViewModel(get(), get(), get()) }
}
