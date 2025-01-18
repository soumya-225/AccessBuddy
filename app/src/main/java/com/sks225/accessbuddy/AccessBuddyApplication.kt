package com.sks225.accessbuddy

import android.app.Application
import com.sks225.accessbuddy.repository.AppContainer

class AccessBuddyApplication : Application() {
    lateinit var container: AppContainer
    
    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    override fun onTerminate() {
        container.textToSpeechHandler.shutdown()
        super.onTerminate()
    }
}