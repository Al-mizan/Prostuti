package com.prostuti.app

import android.app.Application
import com.prostuti.app.di.appModule
import com.prostuti.feature.auth.di.authModule
import com.prostuti.feature.exam.di.examModule
import com.prostuti.feature.practice.di.practiceModule
import com.prostuti.feature.profile.di.profileModule
import com.prostuti.feature.questionbank.di.questionBankModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ProstutiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.INFO)
            androidContext(this@ProstutiApplication)
            modules(appModule, authModule, profileModule, questionBankModule, practiceModule, examModule)
        }
    }
}