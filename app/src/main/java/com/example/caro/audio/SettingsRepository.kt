package com.example.caro.audio

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val BGM_VOLUME = floatPreferencesKey("bgm_volume")
        val SFX_VOLUME = floatPreferencesKey("sfx_volume")
    }

    val bgmVolume: Flow<Float> = context.dataStore.data.map { it[BGM_VOLUME] ?: 0.4f }
    val sfxVolume: Flow<Float> = context.dataStore.data.map { it[SFX_VOLUME] ?: 1.0f }

    suspend fun setBgmVolume(value: Float) {
        context.dataStore.edit { it[BGM_VOLUME] = value }
    }

    suspend fun setSfxVolume(value: Float) {
        context.dataStore.edit { it[SFX_VOLUME] = value }
    }
}