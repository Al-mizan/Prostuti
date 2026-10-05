package com.prostuti.feature.profile.di

import com.prostuti.feature.profile.data.ProfileRepositoryImpl
import com.prostuti.feature.profile.domain.GetProfileUseCase
import com.prostuti.feature.profile.domain.ProfileRepository
import com.prostuti.feature.profile.domain.UpdateProfileUseCase
import com.prostuti.feature.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get()) }
    factory { GetProfileUseCase(get()) }
    factory { UpdateProfileUseCase(get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get()) }
}
