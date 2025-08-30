package com.example.oralvis

import android.app.Application
import com.example.oralvis.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class SessionCamApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SessionCamApp)
            modules(appModule)
        }
    }
}