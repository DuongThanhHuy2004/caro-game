package com.example.caro.audio

import android.content.Context

object SoundManagerProvider {
    private var instance: SoundManager? = null

    fun get(context: Context): SoundManager {
        if (instance == null) {
            instance = SoundManager(context.applicationContext)
        }
        return instance!!
    }

    fun release() {
        instance?.release()
        instance = null
    }
}