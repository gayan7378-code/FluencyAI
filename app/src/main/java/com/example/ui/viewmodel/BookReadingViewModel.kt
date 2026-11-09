package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SpeechManager
import com.example.audio.SpeechState
import com.example.audio.TtsManager
import com.example.data.model.BookExcerpt
import com.example.data.model.BookReadingEvaluation
import com.example.data.model.DailyReadingData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookReadingViewModel(application: Application) : AndroidViewModel(application) {

    val ttsManager = TtsManager(application)
    val speechManager = SpeechManager(application)

    val excerpts: List<BookExcerpt> = DailyReadingData.excerpts

    private val _selectedExcerpt = MutableStateFlow(excerpts.first())
    val selectedExcerpt: StateFlow<BookExcerpt> = _selectedExcerpt.asStateFlow()

    private val _isReading = MutableStateFlow(false)
    val isReading: StateFlow<Boolean> = _isReading.asStateFlow()

    private val _readingSeconds = MutableStateFlow(0)
    val readingSeconds: StateFlow<Int> = _readingSeconds.asStateFlow()

    private val _wordsStatus = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val wordsStatus: StateFlow<List<Pair<String, Boolean>>> = _wordsStatus.asStateFlow()

    private val _evaluation = MutableStateFlow<BookReadingEvaluation?>(null)
    val evaluation: StateFlow<BookReadingEvaluation?> = _evaluation.asStateFlow()

    private var timerJob: Job? = null

    init {
        updateExcerpt(excerpts.first())

        viewModelScope.launch {
            speechManager.speechState.collect { state ->
                when (state) {
                    is SpeechState.PartialResult -> {
                        updateLiveWords(state.text)
                    }
                    is SpeechState.Success -> {
                        finishReading(state.recognizedText)
                    }
                    else -> {}
                }
            }
        }
    }

    fun selectExcerpt(excerpt: BookExcerpt) {
        updateExcerpt(excerpt)
    }

    private fun updateExcerpt(excerpt: BookExcerpt) {
        ttsManager.stop()
        speechManager.cancel()
        _isReading.value = false
        timerJob?.cancel()
        _readingSeconds.value = 0
        _evaluation.value = null
        _selectedExcerpt.value = excerpt

        val words = excerpt.passageText.split(" ").filter { it.isNotBlank() }
        _wordsStatus.value = words.map { Pair(it, false) }
    }

    fun startReading() {
        ttsManager.stop()
        _evaluation.value = null
        _isReading.value = true
        _readingSeconds.value = 0

        val words = _selectedExcerpt.value.passageText.split(" ").filter { it.isNotBlank() }
        _wordsStatus.value = words.map { Pair(it, false) }

        speechManager.startListening()

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isReading.value) {
                delay(1000)
                _readingSeconds.value += 1
            }
        }
    }

    private fun updateLiveWords(spokenText: String) {
        val spokenWords = spokenText.lowercase().split(Regex("[^a-z0-9]")).filter { it.isNotBlank() }
        val originalWords = _selectedExcerpt.value.passageText.split(" ").filter { it.isNotBlank() }

        val updated = originalWords.map { word ->
            val clean = word.lowercase().replace(Regex("[^a-z0-9]"), "")
            Pair(word, spokenWords.contains(clean))
        }
        _wordsStatus.value = updated
    }

    fun stopReading(manualText: String? = null) {
        _isReading.value = false
        timerJob?.cancel()
        speechManager.stopListening()

        val text = manualText ?: _selectedExcerpt.value.passageText
        finishReading(text)
    }

    private fun finishReading(spokenText: String) {
        _isReading.value = false
        timerJob?.cancel()

        val duration = _readingSeconds.value.coerceAtLeast(1)
        val targetWords = _selectedExcerpt.value.passageText.split(" ").filter { it.isNotBlank() }
        val spokenWords = spokenText.lowercase().split(Regex("[^a-z0-9]")).filter { it.isNotBlank() }

        var correct = 0
        val finalStatus = targetWords.map { original ->
            val clean = original.lowercase().replace(Regex("[^a-z0-9]"), "")
            val matched = spokenWords.contains(clean)
            if (matched) correct++
            Pair(original, matched)
        }
        _wordsStatus.value = finalStatus

        val accuracy = if (targetWords.isNotEmpty()) (correct * 100) / targetWords.size else 100
        val wpm = (targetWords.size * 60) / duration

        val challenging = _selectedExcerpt.value.challengingWords.map { (word, ipa) ->
            Pair(word, "$ipa • Place stress on capitalized vowel")
        }

        val feedback = when {
            accuracy >= 90 && wpm in 120..150 -> "Outstanding literary cadence! Native-level reading rhythm 🌟"
            accuracy >= 80 -> "Clear reading with great comprehension. Keep practicing tricky multisyllabic words."
            else -> "Good attempt! Tap the audio button to hear the author's intended cadence, then repeat."
        }

        _evaluation.value = BookReadingEvaluation(
            accuracyPercent = accuracy,
            readingSpeedWpm = wpm,
            totalWordsRead = targetWords.size,
            mispronouncedWords = challenging,
            feedback = feedback
        )
    }

    fun playAudio(speed: Float = 1.0f) {
        ttsManager.setSpeed(speed)
        ttsManager.speak(_selectedExcerpt.value.passageText)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        ttsManager.shutdown()
        speechManager.destroy()
    }
}
