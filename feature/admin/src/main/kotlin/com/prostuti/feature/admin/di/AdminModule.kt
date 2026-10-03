package com.prostuti.feature.admin.di

import com.prostuti.feature.admin.data.AdminRepositoryImpl
import com.prostuti.feature.admin.domain.AdminRepository
import com.prostuti.feature.admin.domain.DeleteAdminQuestionUseCase
import com.prostuti.feature.admin.domain.GetAdminQuestionsUseCase
import com.prostuti.feature.admin.domain.GetAdminUsersUseCase
import com.prostuti.feature.admin.domain.ImportCsvUseCase
import com.prostuti.feature.admin.domain.UpdateAdminQuestionUseCase
import com.prostuti.feature.admin.domain.UpdateUserRoleUseCase
import com.prostuti.feature.admin.presentation.AdminViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val adminModule = module {
    single<AdminRepository> { AdminRepositoryImpl(api = get(), sessionStore = get()) }

    factory { ImportCsvUseCase(repository = get()) }
    factory { GetAdminQuestionsUseCase(repository = get()) }
    factory { UpdateAdminQuestionUseCase(repository = get()) }
    factory { DeleteAdminQuestionUseCase(repository = get()) }
    factory { GetAdminUsersUseCase(repository = get()) }
    factory { UpdateUserRoleUseCase(repository = get()) }

    viewModel {
        AdminViewModel(
            importCsvUseCase = get(),
            getAdminQuestionsUseCase = get(),
            updateAdminQuestionUseCase = get(),
            deleteAdminQuestionUseCase = get(),
            getAdminUsersUseCase = get(),
            updateUserRoleUseCase = get(),
        )
    }
}
