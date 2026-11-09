package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SpeechManager
import com.example.audio.SpeechState
import com.example.audio.TtsManager
import com.example.data.model.TongueTwister
import com.example.data.model.TongueTwisterData
import com.example.data.model.TwisterDifficulty
import com.example.data.model.TwisterEvaluation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TongueTwisterViewModel(application: Application) : AndroidViewModel(application) {

    val ttsManager = TtsManager(application)
    val speechManager = SpeechManager(application)

    val allTwisters: List<TongueTwister> = TongueTwisterData.twisters

    private val _selectedDifficulty = MutableStateFlow(TwisterDifficulty.WARM_UP)
    val selectedDifficulty: StateFlow<TwisterDifficulty> = _selectedDifficulty.asStateFlow()

    private val _selectedTwister = MutableStateFlow(allTwisters.first())
    val selectedTwister: StateFlow<TongueTwister> = _selectedTwister.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0.0f)
    val elapsedSeconds: StateFlow<Float> = _elapsedSeconds.asStateFlow()

    private val _liveWpm = MutableStateFlow(0)
    val liveWpm: StateFlow<Int> = _liveWpm.asStateFlow()

    private val _highlightedWords = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val highlightedWords: StateFlow<List<Pair<String, Boolean>>> = _highlightedWords.asStateFlow()

    private val _evaluation = MutableStateFlow<TwisterEvaluation?>(null)
    val evaluation: StateFlow<TwisterEvaluation?> = _evaluation.asStateFlow()

    private var stopwatchJob: Job? = null
    private var startTimeMillis = 0L

    init {
        updateTargetTwister(allTwisters.first())

        viewModelScope.launch {
            speechManager.speechState.collect { state ->
                when (state) {
                    is SpeechState.PartialResult -> {
                        updateLiveMatching(state.text)
                    }
                    is SpeechState.Success -> {
                        finishTwister(state.recognizedText)
                    }
                    else -> {}
                }
            }
        }
    }

    fun setDifficulty(diff: TwisterDifficulty) {
        _selectedDifficulty.value = diff
        val firstInDiff = allTwisters.firstOrNull { it.difficulty == diff } ?: allTwisters.first()
        updateTargetTwister(firstInDiff)
    }

    fun selectTwister(twister: TongueTwister) {
        _selectedDifficulty.value = twister.difficulty
        updateTargetTwister(twister)
    }

    private fun updateTargetTwister(twister: TongueTwister) {
        ttsManager.stop()
        speechManager.cancel()
        _isRecording.value = false
        stopwatchJob?.cancel()
        _elapsedSeconds.value = 0.0f
        _liveWpm.value = 0
        _evaluation.value = null
        _selectedTwister.value = twister

        val words = twister.targetSentence.split(" ").filter { it.isNotBlank() }
        _highlightedWords.value = words.map { Pair(it, false) }
    }

    fun startPractice() {
        ttsManager.stop()
        _evaluation.value = null
        _isRecording.value = true
        _elapsedSeconds.value = 0.0f
        _liveWpm.value = 0
        startTimeMillis = System.currentTimeMillis()

        val words = _selectedTwister.value.targetSentence.split(" ").filter { it.isNotBlank() }
        _highlightedWords.value = words.map { Pair(it, false) }

        speechManager.startListening()

        stopwatchJob?.cancel()
        stopwatchJob = viewModelScope.launch {
            while (_isRecording.value) {
                delay(50)
                val elapsed = (System.currentTimeMillis() - startTimeMillis) / 1000.0f
                _elapsedSeconds.value = elapsed

                val matchedCount = _highlightedWords.value.count { it.second }
                if (elapsed > 0.5f) {
                    val wpm = ((matchedCount * 60) / elapsed).toInt()
                    _liveWpm.value = wpm
                }
            }
        }
    }

    private fun updateLiveMatching(partialSpeech: String) {
        val targetWords = _selectedTwister.value.targetSentence.lowercase().split(Regex("[^a-z0-9]")).filter { it.isNotBlank() }
        val spokenWords = partialSpeech.lowercase().split(Regex("[^a-z0-9]")).filter { it.isNotBlank() }

        val originalWords = _selectedTwister.value.targetSentence.split(" ").filter { it.isNotBlank() }
        val updated = originalWords.mapIndexed { index, originalWord ->
            val clean = originalWord.lowercase().replace(Regex("[^a-z0-9]"), "")
            val isMatched = spokenWords.contains(clean)
            Pair(originalWord, isMatched)
        }
        _highlightedWords.value = updated
    }

    fun stopPractice(manualSpeech: String? = null) {
        _isRecording.value = false
        stopwatchJob?.cancel()
        speechManager.stopListening()

        val text = manualSpeech ?: _selectedTwister.value.targetSentence
        finishTwister(text)
    }

    private fun finishTwister(spokenText: String) {
        _isRecording.value = false
        stopwatchJob?.cancel()

        val elapsed = _elapsedSeconds.value.coerceAtLeast(1.0f)
        val targetWords = _selectedTwister.value.targetSentence.lowercase().split(Regex("[^a-z0-9]")).filter { it.isNotBlank() }
        val spokenWords = spokenText.lowercase().split(Regex("[^a-z0-9]")).filter { it.isNotBlank() }

        var correctCount = 0
        val originalWords = _selectedTwister.value.targetSentence.split(" ").filter { it.isNotBlank() }
        val finalMatched = originalWords.map { original ->
            val clean = original.lowercase().replace(Regex("[^a-z0-9]"), "")
            val matched = spokenWords.contains(clean)
            if (matched) correctCount++
            Pair(original, matched)
        }
        _highlightedWords.value = finalMatched

        val accuracy = if (targetWords.isNotEmpty()) (correctCount * 100) / targetWords.size else 100
        val wpm = ((targetWords.size * 60) / elapsed).toInt()
        val targetBeaten = accuracy >= 80 && wpm >= _selectedTwister.value.targetWpm

        val tips = when {
            targetBeaten -> "Incredible speed and phoneme precision! You shattered the target benchmark! ⚡"
            accuracy >= 85 -> "Great articulation! You can push your speed slightly faster to beat the benchmark."
            else -> "Take it slow at first. Focus on clear consonant articulation before maximizing speed."
        }

        _evaluation.value = TwisterEvaluation(
            accuracyPercent = accuracy,
            wordsPerMinute = wpm,
            elapsedSeconds = elapsed,
            targetSpeedBeaten = targetBeaten,
            matchedWords = finalMatched,
            tips = tips
        )
    }

    fun playAudio(speed: Float = 1.0f) {
        ttsManager.setSpeed(speed)
        ttsManager.speak(_selectedTwister.value.targetSentence)
    }

    override fun onCleared() {
        super.onCleared()
        stopwatchJob?.cancel()
        ttsManager.shutdown()
        speechManager.destroy()
    }
}
