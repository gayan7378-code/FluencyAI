package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SpeechManager
import com.example.audio.SpeechState
import com.example.data.ai.FluencyAiService
import com.example.data.model.PresentationData
import com.example.data.model.PresentationEvaluation
import com.example.data.model.PresentationScoreRecord
import com.example.data.model.PresentationSlide
import com.example.data.model.PresentationTopic
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PresentationViewModel(application: Application) : AndroidViewModel(application) {

    val speechManager = SpeechManager(application)
    val topics: List<PresentationTopic> = PresentationData.sampleTopics

    private val _selectedTopic = MutableStateFlow(topics.first())
    val selectedTopic: StateFlow<PresentationTopic> = _selectedTopic.asStateFlow()

    private val _currentSlideIndex = MutableStateFlow(0)
    val currentSlideIndex: StateFlow<Int> = _currentSlideIndex.asStateFlow()

    private val _isPresenting = MutableStateFlow(false)
    val isPresenting: StateFlow<Boolean> = _isPresenting.asStateFlow()

    private val _presentationSeconds = MutableStateFlow(0)
    val presentationSeconds: StateFlow<Int> = _presentationSeconds.asStateFlow()

    private val _liveTranscript = MutableStateFlow("")
    val liveTranscript: StateFlow<String> = _liveTranscript.asStateFlow()

    private val _evaluation = MutableStateFlow<PresentationEvaluation?>(null)
    val evaluation: StateFlow<PresentationEvaluation?> = _evaluation.asStateFlow()

    private val _scoreHistory = MutableStateFlow<List<PresentationScoreRecord>>(
        listOf(
            PresentationScoreRecord(topic = "Introductory Elevator Pitch", scoreOutOf10 = 7.4f, dateLabel = "Oct 28"),
            PresentationScoreRecord(topic = "Product Architecture Review", scoreOutOf10 = 8.1f, dateLabel = "Nov 02"),
            PresentationScoreRecord(topic = "Sprint Demo & Q&A", scoreOutOf10 = 8.6f, dateLabel = "Nov 05")
        )
    )
    val scoreHistory: StateFlow<List<PresentationScoreRecord>> = _scoreHistory.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            speechManager.speechState.collect { state ->
                when (state) {
                    is SpeechState.PartialResult -> {
                        _liveTranscript.value = state.text
                    }
                    is SpeechState.Success -> {
                        _liveTranscript.value = state.recognizedText
                    }
                    else -> {}
                }
            }
        }
    }

    fun selectTopic(topic: PresentationTopic) {
        _selectedTopic.value = topic
        _currentSlideIndex.value = 0
        _evaluation.value = null
        _liveTranscript.value = ""
    }

    fun nextSlide() {
        val total = _selectedTopic.value.slides.size
        if (_currentSlideIndex.value < total - 1) {
            _currentSlideIndex.value += 1
        }
    }

    fun prevSlide() {
        if (_currentSlideIndex.value > 0) {
            _currentSlideIndex.value -= 1
        }
    }

    fun startPresentation() {
        _evaluation.value = null
        _liveTranscript.value = ""
        _presentationSeconds.value = 0
        _isPresenting.value = true

        speechManager.startListening()

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isPresenting.value) {
                delay(1000)
                _presentationSeconds.value += 1
            }
        }
    }

    fun finishPresentation(manualTranscript: String? = null) {
        _isPresenting.value = false
        timerJob?.cancel()
        speechManager.stopListening()

        val text = if (!manualTranscript.isNullOrBlank()) manualTranscript else _liveTranscript.value.ifBlank {
            "Today I am presenting our architectural framework for high scalability and zero-downtime resilience."
        }

        val eval = FluencyAiService.evaluatePresentation(
            topic = _selectedTopic.value,
            transcribedSpeech = text,
            elapsedSeconds = _presentationSeconds.value
        )
        _evaluation.value = eval

        val dateLabel = SimpleDateFormat("MMM dd", Locale.US).format(Date())
        val newRecord = PresentationScoreRecord(
            topic = _selectedTopic.value.title,
            scoreOutOf10 = eval.overallScoreOutOf10,
            dateLabel = dateLabel
        )
        _scoreHistory.value = _scoreHistory.value + newRecord
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        speechManager.destroy()
    }
}
