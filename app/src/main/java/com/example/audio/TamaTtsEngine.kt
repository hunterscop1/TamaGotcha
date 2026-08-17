package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

/**
 * Text-to-Speech Engine tuned specifically for cute, high-pitched virtual pet character voice!
 */
class TamaTtsEngine(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    var isVoiceEnabled: Boolean = true

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                // Higher pitch and slightly peppy speech rate for cute anime/Tamagotchi pet vibe!
                tts?.setPitch(1.45f)
                tts?.setSpeechRate(1.1f)
                isInitialized = true
            }
        }
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (!isVoiceEnabled || !isInitialized || text.isBlank()) {
            onComplete?.invoke()
            return
        }

        // Clean out emojis and kaomojis so TTS doesn't read symbol names
        val cleaned = text
            .replace(Regex("[\\p{So}\\p{Cn}]"), "") // remove emojis
            .replace(Regex("[\\(（][^\\)）]*[\\)）]"), "") // remove kaomojis like (｡♥‿♥｡)
            .trim()

        if (cleaned.isNotBlank()) {
            val utteranceId = "tama_dialogue_${System.currentTimeMillis()}"
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(id: String?) {}
                override fun onDone(id: String?) {
                    onComplete?.invoke()
                }
                override fun onError(id: String?) {
                    onComplete?.invoke()
                }
            })
            tts?.speak(cleaned, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } else {
            onComplete?.invoke()
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
