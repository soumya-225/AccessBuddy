package com.sks225.accessbuddy.repository

import android.content.Context

class AppContainer(context: Context) {
    val textToSpeechHandler = TextToSpeechHandler(context)
}