package com.prostuti.feature.questionbank.di

import com.prostuti.feature.questionbank.data.QuestionBankRepositoryImpl
import com.prostuti.feature.questionbank.domain.GetBcsSessionsUseCase
import com.prostuti.feature.questionbank.domain.GetQuestionBankQuestionsUseCase
import com.prostuti.feature.questionbank.domain.QuestionBankRepository
import com.prostuti.feature.questionbank.presentation.QuestionBankViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val questionBankModule = module {
    single<QuestionBankRepository> {
        QuestionBankRepositoryImpl(api = get(), sessionStore = get())
    }
    factory { GetBcsSessionsUseCase(get()) }
    factory { GetQuestionBankQuestionsUseCase(get()) }
    viewModel { QuestionBankViewModel(get(), get()) }
}
