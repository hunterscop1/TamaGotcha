package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * 8-Bit Retro Audio Synthesizer for authentic Tamagotchi beeps, chimes, fanfares, and alerts
 * using streaming PCM audio to avoid ashmem static buffer allocations and Android Q deprecation warnings.
 */
class TamaAudioEngine {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isSoundEnabled: Boolean = true

    private val sampleRate = 22050
    private var audioTrack: AudioTrack? = null
    private val audioChannel = Channel<ShortArray>(Channel.UNLIMITED)

    init {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(sampleRate / 4)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            if (audioTrack?.state == AudioTrack.STATE_INITIALIZED) {
                audioTrack?.play()
            }

            scope.launch {
                for (buffer in audioChannel) {
                    try {
                        val track = audioTrack
                        if (track != null && track.state == AudioTrack.STATE_INITIALIZED) {
                            track.write(buffer, 0, buffer.size, AudioTrack.WRITE_NON_BLOCKING)
                        }
                    } catch (_: Exception) {
                    }
                }
            }
        } catch (_: Exception) {
            // Fallback gracefully if hardware audio track is unavailable
        }
    }

    fun playBleep() {
        if (!isSoundEnabled) return
        queueTone(880f, 60) // A5
    }

    fun playHappy() {
        if (!isSoundEnabled) return
        queueTone(523.25f, 70) // C5
        queueTone(659.25f, 70) // E5
        queueTone(783.99f, 70) // G5
        queueTone(1046.50f, 140) // C6
    }

    fun playMunch() {
        if (!isSoundEnabled) return
        queueTone(440f, 40)
        queueTone(330f, 40)
        queueTone(550f, 50)
        queueTone(400f, 60)
    }

    fun playAlert() {
        if (!isSoundEnabled) return
        repeat(2) {
            queueTone(987.77f, 90) // B5
            queueTone(1318.51f, 130) // E6
            queueSilence(80)
        }
    }

    fun playLevelUp() {
        if (!isSoundEnabled) return
        val notes = floatArrayOf(440f, 554.37f, 659.25f, 880f, 783.99f, 880f)
        val durations = intArrayOf(70, 70, 70, 100, 70, 200)
        for (i in notes.indices) {
            queueTone(notes[i], durations[i])
        }
    }

    fun playZzz() {
        if (!isSoundEnabled) return
        queueTone(220f, 150)
        queueTone(196f, 200)
    }

    private fun queueTone(freqHz: Float, durationMs: Int) {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        if (numSamples <= 0) return
        val sample = ShortArray(numSamples)
        val fadeSamples = (sampleRate * 0.005).toInt().coerceAtMost(numSamples / 2) // 5ms click-free fade

        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * i / (sampleRate / freqHz)
            val raw = sin(angle)
            val square = if (raw >= 0) 0.55 else -0.55
            val envelope = when {
                i < fadeSamples -> i.toDouble() / fadeSamples
                i > numSamples - fadeSamples -> (numSamples - i).toDouble() / fadeSamples
                else -> 1.0
            }
            sample[i] = (square * envelope * Short.MAX_VALUE * 0.4).toInt().toShort()
        }

        audioChannel.trySend(sample)
    }

    private fun queueSilence(durationMs: Int) {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        if (numSamples <= 0) return
        audioChannel.trySend(ShortArray(numSamples))
    }

    fun release() {
        try {
            audioChannel.close()
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (_: Exception) {}
    }
}
