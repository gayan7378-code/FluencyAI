package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed class SpeechState {
    object Idle : SpeechState()
    object Listening : SpeechState()
    data class PartialResult(val text: String) : SpeechState()
    data class Success(val recognizedText: String) : SpeechState()
    data class Error(val message: String) : SpeechState()
}

class SpeechManager(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _speechState = MutableStateFlow<SpeechState>(SpeechState.Idle)
    val speechState: StateFlow<SpeechState> = _speechState.asStateFlow()

    private val _soundLevel = MutableStateFlow(0f)
    val soundLevel: StateFlow<Float> = _soundLevel.asStateFlow()

    private var isListeningNow = false

    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    private fun getOrCreateRecognizer(): SpeechRecognizer? {
        if (speechRecognizer == null) {
            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            _speechState.value = SpeechState.Listening
                            isListeningNow = true
                        }

                        override fun onBeginningOfSpeech() {
                            _speechState.value = SpeechState.Listening
                        }

                        override fun onRmsChanged(rmsdB: Float) {
                            val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                            _soundLevel.value = normalized
                        }

                        override fun onBufferReceived(buffer: ByteArray?) {}

                        override fun onEndOfSpeech() {
                            isListeningNow = false
                            _soundLevel.value = 0f
                        }

                        override fun onError(error: Int) {
                            isListeningNow = false
                            _soundLevel.value = 0f
                            val errorMsg = when (error) {
                                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Check your microphone."
                                SpeechRecognizer.ERROR_CLIENT -> "Client recognition error."
                                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
                                SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition."
                                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Speech recognition network timeout."
                                SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Please speak clearly."
                                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognizer is busy. Please wait a moment."
                                SpeechRecognizer.ERROR_SERVER -> "Speech server error."
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech heard. Tap mic and try again."
                                else -> "Speech recognition error: $error"
                            }
                            Log.w("SpeechManager", "Speech error: $errorMsg ($error)")
                            _speechState.value = SpeechState.Error(errorMsg)
                        }

                        override fun onResults(results: Bundle?) {
                            isListeningNow = false
                            _soundLevel.value = 0f
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val bestMatch = matches?.firstOrNull()?.trim() ?: ""
                            if (bestMatch.isNotBlank()) {
                                Log.d("SpeechManager", "Live Speech recognized: $bestMatch")
                                _speechState.value = SpeechState.Success(bestMatch)
                            } else {
                                _speechState.value = SpeechState.Error("Could not recognize words. Please try again.")
                            }
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                            if (!partial.isNullOrBlank()) {
                                _speechState.value = SpeechState.PartialResult(partial)
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }
            } catch (e: Exception) {
                Log.e("SpeechManager", "Failed to create SpeechRecognizer", e)
            }
        }
        return speechRecognizer
    }

    fun startListening() {
        mainHandler.post {
            try {
                val recognizer = getOrCreateRecognizer()
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
                    putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "en-US")
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak English clearly to Mili")
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1500L)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
                }
                _speechState.value = SpeechState.Listening
                isListeningNow = true
                recognizer?.startListening(intent)
            } catch (e: Exception) {
                Log.e("SpeechManager", "Failed to start listening", e)
                _speechState.value = SpeechState.Error("Unable to start speech recognition: ${e.message}")
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                Log.e("SpeechManager", "Error stopping listening", e)
            }
            isListeningNow = false
            _soundLevel.value = 0f
        }
    }

    fun cancel() {
        mainHandler.post {
            try {
                speechRecognizer?.cancel()
            } catch (e: Exception) {
                Log.e("SpeechManager", "Error cancelling listening", e)
            }
            isListeningNow = false
            _soundLevel.value = 0f
            _speechState.value = SpeechState.Idle
        }
    }

    /**
     * Allows simulating or typing speech directly for emulator / testing / noisy environments
     */
    fun submitSimulatedSpeech(text: String) {
        _speechState.value = SpeechState.Success(text.trim())
    }

    fun resetState() {
        _speechState.value = SpeechState.Idle
        _soundLevel.value = 0f
    }

    fun destroy() {
        mainHandler.post {
            try {
                speechRecognizer?.destroy()
                speechRecognizer = null
            } catch (e: Exception) {
                Log.e("SpeechManager", "Error destroying speech recognizer", e)
            }
        }
    }
}
