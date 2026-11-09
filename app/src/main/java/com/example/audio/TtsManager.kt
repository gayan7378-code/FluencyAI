package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(private val context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentUtteranceId = MutableStateFlow<String?>(null)
    val currentUtteranceId: StateFlow<String?> = _currentUtteranceId.asStateFlow()

    private var currentSpeechRate: Float = 1.0f
    private var currentLocale: Locale = Locale.US

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                applySettings()
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                        _currentUtteranceId.value = utteranceId
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                    }
                })
            } else {
                Log.e("TtsManager", "TTS initialization failed with status: $status")
            }
        }
    }

    private fun applySettings() {
        tts?.let { engine ->
            val result = engine.setLanguage(currentLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                engine.language = Locale.ENGLISH
            }
            engine.setSpeechRate(currentSpeechRate)
        }
    }

    fun setSpeed(rate: Float) {
        currentSpeechRate = rate
        tts?.setSpeechRate(rate)
    }

    fun setAccent(isBritish: Boolean) {
        currentLocale = if (isBritish) Locale.UK else Locale.US
        applySettings()
    }

    fun speak(text: String, utteranceId: String = System.currentTimeMillis().toString()) {
        if (!isInitialized) return
        tts?.stop()
        _isSpeaking.value = true
        _currentUtteranceId.value = utteranceId

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        _currentUtteranceId.value = null
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
