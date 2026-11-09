package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.PronunciationEvaluator
import com.example.audio.SpeechManager
import com.example.audio.SpeechState
import com.example.audio.TtsManager
import com.example.data.model.DialogueLine
import com.example.data.model.DialogueScenario
import com.example.data.model.EvaluationResult
import com.example.data.model.FreeSpeechResult
import com.example.data.model.PhraseItem
import com.example.data.model.PronunciationDrill
import com.example.data.model.SoundCategory
import com.example.data.model.SpeakingStats
import com.example.data.repository.SpokenEnglishRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    ROLEPLAY_CHAT,
    NATIVE_CALL,
    TONGUE_TWISTERS,
    PRESENTATION,
    BOOK_READING,
    DIALOGUES,
    PRONUNCIATION,
    PHRASEBOOK,
    FREE_SPEECH
}

class SpeakFlowViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SpokenEnglishRepository(application)
    val ttsManager = TtsManager(application)
    val speechManager = SpeechManager(application)

    // Navigation & Screen State
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Scenarios & Dialogue State
    val scenarios: List<DialogueScenario> = repository.getScenarios()
    private val _selectedScenario = MutableStateFlow(scenarios.first())
    val selectedScenario: StateFlow<DialogueScenario> = _selectedScenario.asStateFlow()

    private val _currentDialogueLineIndex = MutableStateFlow(0)
    val currentDialogueLineIndex: StateFlow<Int> = _currentDialogueLineIndex.asStateFlow()

    // Pronunciation Drills State
    val pronunciationDrills: List<PronunciationDrill> = repository.getPronunciationDrills()
    private val _selectedSoundCategory = MutableStateFlow<SoundCategory?>(null)
    val selectedSoundCategory: StateFlow<SoundCategory?> = _selectedSoundCategory.asStateFlow()

    private val _selectedDrill = MutableStateFlow(pronunciationDrills.first())
    val selectedDrill: StateFlow<PronunciationDrill> = _selectedDrill.asStateFlow()

    // Phrasebook State
    private val _phrasebook = MutableStateFlow(repository.getPhrases())
    val phrasebook: StateFlow<List<PhraseItem>> = _phrasebook.asStateFlow()

    private val _phraseCategoryFilter = MutableStateFlow<String?>(null)
    val phraseCategoryFilter: StateFlow<String?> = _phraseCategoryFilter.asStateFlow()

    private val _activeTestedPhrase = MutableStateFlow<PhraseItem?>(null)
    val activeTestedPhrase: StateFlow<PhraseItem?> = _activeTestedPhrase.asStateFlow()

    // Current target sentence being evaluated
    private val _currentTargetSentence = MutableStateFlow("")
    val currentTargetSentence: StateFlow<String> = _currentTargetSentence.asStateFlow()

    // Evaluation Result
    private val _lastEvaluationResult = MutableStateFlow<EvaluationResult?>(null)
    val lastEvaluationResult: StateFlow<EvaluationResult?> = _lastEvaluationResult.asStateFlow()

    // Free Speech Practice State
    private val _freeSpeechRecording = MutableStateFlow(false)
    val freeSpeechRecording: StateFlow<Boolean> = _freeSpeechRecording.asStateFlow()

    private val _freeSpeechSeconds = MutableStateFlow(0)
    val freeSpeechSeconds: StateFlow<Int> = _freeSpeechSeconds.asStateFlow()

    private val _freeSpeechResult = MutableStateFlow<FreeSpeechResult?>(null)
    val freeSpeechResult: StateFlow<FreeSpeechResult?> = _freeSpeechResult.asStateFlow()

    private var freeSpeechTimerJob: Job? = null

    // Audio & Settings State
    private val _currentSpeed = MutableStateFlow(1.0f)
    val currentSpeed: StateFlow<Float> = _currentSpeed.asStateFlow()

    private val _isBritishAccent = MutableStateFlow(false)
    val isBritishAccent: StateFlow<Boolean> = _isBritishAccent.asStateFlow()

    // Repository Stats
    val stats: StateFlow<SpeakingStats> = repository.stats

    init {
        // Observe speech manager results
        viewModelScope.launch {
            speechManager.speechState.collect { state ->
                if (state is SpeechState.Success) {
                    onSpeechRecognized(state.recognizedText)
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        ttsManager.stop()
        speechManager.cancel()
        _lastEvaluationResult.value = null
        _currentScreen.value = screen
    }

    fun setSpeechSpeed(speed: Float) {
        _currentSpeed.value = speed
        ttsManager.setSpeed(speed)
    }

    fun setAccent(isBritish: Boolean) {
        _isBritishAccent.value = isBritish
        ttsManager.setAccent(isBritish)
    }

    // --- DIALOGUE METHODS ---

    fun selectScenario(scenario: DialogueScenario) {
        ttsManager.stop()
        speechManager.cancel()
        _selectedScenario.value = scenario
        _currentDialogueLineIndex.value = 0
        _lastEvaluationResult.value = null
        val currentLine = scenario.lines.firstOrNull()
        if (currentLine != null) {
            _currentTargetSentence.value = currentLine.text
            if (!currentLine.isUserTurn) {
                // Automatically play partner's opening line
                speakText(currentLine.text, currentLine.id)
            }
        }
    }

    fun nextDialogueLine() {
        val scenario = _selectedScenario.value
        val nextIdx = _currentDialogueLineIndex.value + 1
        if (nextIdx < scenario.lines.size) {
            ttsManager.stop()
            speechManager.cancel()
            _lastEvaluationResult.value = null
            _currentDialogueLineIndex.value = nextIdx
            val line = scenario.lines[nextIdx]
            _currentTargetSentence.value = line.text
            if (!line.isUserTurn) {
                speakText(line.text, line.id)
            }
        }
    }

    fun previousDialogueLine() {
        val prevIdx = _currentDialogueLineIndex.value - 1
        if (prevIdx >= 0) {
            ttsManager.stop()
            speechManager.cancel()
            _lastEvaluationResult.value = null
            _currentDialogueLineIndex.value = prevIdx
            val line = _selectedScenario.value.lines[prevIdx]
            _currentTargetSentence.value = line.text
            if (!line.isUserTurn) {
                speakText(line.text, line.id)
            }
        }
    }

    // --- PRONUNCIATION DRILL METHODS ---

    fun selectSoundCategory(category: SoundCategory?) {
        _selectedSoundCategory.value = category
    }

    fun selectDrill(drill: PronunciationDrill) {
        ttsManager.stop()
        speechManager.cancel()
        _selectedDrill.value = drill
        _currentTargetSentence.value = drill.targetSentence
        _lastEvaluationResult.value = null
    }

    // --- PHRASEBOOK METHODS ---

    fun filterPhraseCategory(cat: String?) {
        _phraseCategoryFilter.value = cat
    }

    fun toggleBookmark(phraseId: String) {
        repository.toggleBookmark(phraseId)
        _phrasebook.value = repository.getPhrases()
    }

    fun setTestPhrase(phrase: PhraseItem?) {
        _activeTestedPhrase.value = phrase
        if (phrase != null) {
            _currentTargetSentence.value = phrase.englishText
            _lastEvaluationResult.value = null
        }
    }

    // --- SPEECH & EVALUATION ---

    fun startListeningForCurrentTarget(target: String) {
        ttsManager.stop()
        _currentTargetSentence.value = target
        _lastEvaluationResult.value = null
        speechManager.startListening()
    }

    fun stopListening() {
        speechManager.stopListening()
    }

    fun evaluateManualSpeech(target: String, spoken: String) {
        _currentTargetSentence.value = target
        onSpeechRecognized(spoken)
    }

    private fun onSpeechRecognized(spokenText: String) {
        val target = _currentTargetSentence.value
        if (target.isNotBlank()) {
            val eval = PronunciationEvaluator.evaluate(target, spokenText)
            _lastEvaluationResult.value = eval
            repository.recordPractice(eval.overallScore)
        }
    }

    fun speakText(text: String, id: String = System.currentTimeMillis().toString()) {
        speechManager.cancel()
        ttsManager.speak(text, id)
    }

    // --- FREE SPEECH PRACTICE ---

    fun startFreeSpeech() {
        ttsManager.stop()
        _freeSpeechResult.value = null
        _freeSpeechSeconds.value = 0
        _freeSpeechRecording.value = true
        speechManager.startListening()

        freeSpeechTimerJob?.cancel()
        freeSpeechTimerJob = viewModelScope.launch {
            while (_freeSpeechRecording.value) {
                delay(1000)
                _freeSpeechSeconds.value += 1
            }
        }
    }

    fun stopFreeSpeech(finalTranscript: String) {
        _freeSpeechRecording.value = false
        freeSpeechTimerJob?.cancel()
        speechManager.stopListening()

        val duration = _freeSpeechSeconds.value.coerceAtLeast(1)
        val words = finalTranscript.split(" ").filter { it.isNotBlank() }
        val wordCount = words.size
        val wpm = (wordCount * 60) / duration

        val commonFillers = listOf("um", "uh", "like", "you know", "basically", "actually", "so", "right")
        val detected = mutableListOf<String>()
        val lower = finalTranscript.lowercase()
        for (filler in commonFillers) {
            if (lower.contains(filler)) {
                detected.add(filler)
            }
        }

        val fluencyScore = when {
            wpm in 110..160 && detected.isEmpty() -> 95
            wpm in 90..180 -> 85
            wpm in 60..210 -> 72
            else -> 60
        }

        val suggestions = mutableListOf<String>()
        if (wpm < 90) {
            suggestions.add("Speaking pace was a bit slow (${wpm} WPM). Aim for 120-150 WPM for natural conversational English.")
        } else if (wpm > 170) {
            suggestions.add("Speaking pace was quite fast (${wpm} WPM). Slow down slightly to ensure clear consonant articulation.")
        } else {
            suggestions.add("Excellent pacing at ${wpm} WPM! Well within the native conversational zone.")
        }

        if (detected.isNotEmpty()) {
            suggestions.add("Notice filler words: ${detected.joinToString(", ")}. Try taking a short silent pause instead of saying 'um' or 'like'.")
        } else {
            suggestions.add("Zero filler words detected! Clean and confident delivery.")
        }

        val result = FreeSpeechResult(
            transcript = finalTranscript.ifBlank { "I practiced my spoken English today." },
            durationSeconds = duration,
            wordsPerMinute = wpm,
            totalWords = wordCount,
            fillerWordsDetected = detected,
            fluencyScore = fluencyScore,
            suggestions = suggestions
        )
        _freeSpeechResult.value = result
        repository.recordPractice(fluencyScore)
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
        speechManager.destroy()
        freeSpeechTimerJob?.cancel()
    }
}
