package com.prostuti.app.di

import com.prostuti.app.BuildConfig
import com.prostuti.core.common.SessionStore
import com.prostuti.core.network.ApiConfig
import com.prostuti.core.network.AuthApi
import com.prostuti.core.network.HttpClientFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * App-wide Koin module — wires shared singletons (SessionStore, HttpClient)
 * that every feature module consumes. Feature modules register themselves
 * (authModule, ...) in [com.prostuti.app.ProstutiApplication].
 */
val appModule = module {
    single { SessionStore.create(androidContext()) }

    single {
        val config = ApiConfig(baseUrl = BuildConfig.API_BASE_URL)
        HttpClientFactory.create(config, sessionStore = get())
    }

    single { AuthApi(client = get()) }
    single { com.prostuti.core.network.ProfileApi(client = get()) }
    single { com.prostuti.core.network.QuestionBankApi(client = get()) }
    single { com.prostuti.core.network.PracticeApi(client = get()) }
}