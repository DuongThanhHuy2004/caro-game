package com.example.caro.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool

class SoundManager(private val context: Context) {

    // Nhạc nền
    private var mediaPlayer: MediaPlayer? = null

    // Sound effects
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var soundPlace = 0
    private var soundWin = 0
    private var sfxVolumeLevel = 1.0f
    private var bgmVolumeLevel = 0.4f

    init {
        soundPlace = soundPool.load(context, com.example.caro.R.raw.place, 1)
        soundWin = soundPool.load(context, com.example.caro.R.raw.win, 1)
    }

    fun startBgm() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, com.example.caro.R.raw.bgm).apply {
                isLooping = true
                setVolume(bgmVolumeLevel, bgmVolumeLevel)
                start()
            }
        } else if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    fun stopBgm() {
        mediaPlayer?.pause()
    }

    fun playPlace() {
        soundPool.play(soundPlace, sfxVolumeLevel, sfxVolumeLevel, 0, 0, 1f)
    }

    fun playWin() {
        soundPool.play(soundWin, sfxVolumeLevel, sfxVolumeLevel, 0, 0, 1f)
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        soundPool.release()
    }

    fun setBgmVolume(value: Float) {
        bgmVolumeLevel = value
        mediaPlayer?.setVolume(value, value)
    }

    fun setSfxVolume(value: Float) {
        sfxVolumeLevel = value
    }
}