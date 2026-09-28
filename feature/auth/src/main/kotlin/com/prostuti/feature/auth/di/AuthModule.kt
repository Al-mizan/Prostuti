package com.prostuti.feature.auth.di

import com.prostuti.feature.auth.data.AuthRepositoryImpl
import com.prostuti.feature.auth.domain.AuthRepository
import com.prostuti.feature.auth.domain.GetCurrentUserUseCase
import com.prostuti.feature.auth.domain.LoginUseCase
import com.prostuti.feature.auth.domain.RegisterUseCase
import com.prostuti.feature.auth.presentation.AuthUiState
import com.prostuti.feature.auth.presentation.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

/**
 * One module per feature, per SKILL.md §5. Registered in androidApp's
 * startKoin { modules(...) } call.
 */
val authModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }

    // One VM per mode — the screen passes the mode via Koin's `parametersOf`.
    viewModel { (mode: AuthUiState.Mode) ->
        AuthViewModel(mode, get(), get())
    }
}