package com.prostuti.core.network

import org.koin.dsl.module

val networkModule = module {
    single<ModelTestApi> { ModelTestApiImpl(client = get()) }
}
