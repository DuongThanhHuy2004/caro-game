package com.example.caro.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caro.audio.SettingsRepository
import com.example.caro.audio.SoundManagerProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(context: Context) : ViewModel() {
    private val repo = SettingsRepository(context.applicationContext)
    private val sound = SoundManagerProvider.get(context)

    val bgmVolume = repo.bgmVolume.stateIn(viewModelScope, SharingStarted.Eagerly, 0.4f)
    val sfxVolume = repo.sfxVolume.stateIn(viewModelScope, SharingStarted.Eagerly, 1.0f)

    fun setBgmVolume(value: Float) {
        viewModelScope.launch {
            repo.setBgmVolume(value)
            sound.setBgmVolume(value)
        }
    }

    fun setSfxVolume(value: Float) {
        viewModelScope.launch {
            repo.setSfxVolume(value)
            sound.setSfxVolume(value)
        }
    }
}