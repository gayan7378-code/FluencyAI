package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SpeechManager
import com.example.audio.SpeechState
import com.example.audio.TtsManager
import com.example.data.ai.FluencyAiService
import com.example.data.model.CallDebrief
import com.example.data.model.CallMessage
import com.example.data.model.CallPersona
import com.example.data.model.CallScenario
import com.example.data.model.NativeCallData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class CallUiState {
    SETUP,
    RINGING,
    CONNECTED,
    DEBRIEF
}

class NativeVoiceCallViewModel(application: Application) : AndroidViewModel(application) {

    val ttsManager = TtsManager(application)
    val speechManager = SpeechManager(application)

    val personas: List<CallPersona> = NativeCallData.personas
    val scenarios: List<CallScenario> = NativeCallData.callScenarios

    private val _uiState = MutableStateFlow(CallUiState.SETUP)
    val uiState: StateFlow<CallUiState> = _uiState.asStateFlow()

    private val _selectedScenario = MutableStateFlow(scenarios.first())
    val selectedScenario: StateFlow<CallScenario> = _selectedScenario.asStateFlow()

    private val _currentAccent = MutableStateFlow("US")
    val currentAccent: StateFlow<String> = _currentAccent.asStateFlow()

    private val _callSeconds = MutableStateFlow(0)
    val callSeconds: StateFlow<Int> = _callSeconds.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _isSpeakerOn = MutableStateFlow(true)
    val isSpeakerOn: StateFlow<Boolean> = _isSpeakerOn.asStateFlow()

    private val _callMessages = MutableStateFlow<List<CallMessage>>(emptyList())
    val callMessages: StateFlow<List<CallMessage>> = _callMessages.asStateFlow()

    private val _callStatusBanner = MutableStateFlow("Tap to start call")
    val callStatusBanner: StateFlow<String> = _callStatusBanner.asStateFlow()

    private val _debriefResult = MutableStateFlow<CallDebrief?>(null)
    val debriefResult: StateFlow<CallDebrief?> = _debriefResult.asStateFlow()

    private var callTimerJob: Job? = null

    init {
        // Observe speech recognition while call is active
        viewModelScope.launch {
            speechManager.speechState.collect { state ->
                if (_uiState.value == CallUiState.CONNECTED && !_isMuted.value) {
                    when (state) {
                        is SpeechState.Listening -> {
                            _callStatusBanner.value = "Listening to you..."
                        }
                        is SpeechState.PartialResult -> {
                            _callStatusBanner.value = "\"${state.text}\""
                        }
                        is SpeechState.Success -> {
                            onUserSpokeInCall(state.recognizedText)
                        }
                        is SpeechState.Error -> {
                            _callStatusBanner.value = "Listening paused. Tap mic to speak."
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun selectScenario(scenario: CallScenario) {
        _selectedScenario.value = scenario
        _currentAccent.value = scenario.persona.accentCode
        ttsManager.setAccent(scenario.persona.accentCode == "UK")
    }

    fun setAccent(accent: String) {
        _currentAccent.value = accent
        ttsManager.setAccent(accent == "UK")
    }

    fun startCall() {
        ttsManager.stop()
        speechManager.cancel()
        _uiState.value = CallUiState.RINGING
        _callSeconds.value = 0
        _callStatusBanner.value = "Connecting to ${selectedScenario.value.persona.name}..."
        _callMessages.value = emptyList()

        viewModelScope.launch {
            delay(1800) // Simulated ringing duration
            _uiState.value = CallUiState.CONNECTED
            startCallTimer()

            val openingLine = selectedScenario.value.initialLine
            val firstMsg = CallMessage(isUser = false, text = openingLine)
            _callMessages.value = listOf(firstMsg)

            _callStatusBanner.value = "${selectedScenario.value.persona.name} is speaking..."
            ttsManager.speak(openingLine, firstMsg.id)

            delay(2500)
            if (_uiState.value == CallUiState.CONNECTED && !_isMuted.value) {
                _callStatusBanner.value = "Your turn. Speak naturally into microphone..."
                speechManager.startListening()
            }
        }
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (_uiState.value == CallUiState.CONNECTED) {
                delay(1000)
                _callSeconds.value += 1
            }
        }
    }

    private fun onUserSpokeInCall(userSpeech: String) {
        if (userSpeech.isBlank()) return
        val userMsg = CallMessage(isUser = true, text = userSpeech.trim())
        val updatedHistory = _callMessages.value + userMsg
        _callMessages.value = updatedHistory

        _callStatusBanner.value = "${selectedScenario.value.persona.name} is thinking..."

        viewModelScope.launch {
            delay(600)
            val aiResponseText = FluencyAiService.generateNativeVoiceCallTurn(
                scenario = selectedScenario.value,
                history = updatedHistory,
                userSpeech = userSpeech
            )

            val aiMsg = CallMessage(isUser = false, text = aiResponseText)
            _callMessages.value = _callMessages.value + aiMsg

            _callStatusBanner.value = "${selectedScenario.value.persona.name} is speaking..."
            ttsManager.speak(aiResponseText, aiMsg.id)

            // Wait for TTS to finish then listen again
            delay(3500)
            if (_uiState.value == CallUiState.CONNECTED && !_isMuted.value) {
                _callStatusBanner.value = "Listening to you..."
                speechManager.startListening()
            }
        }
    }

    fun toggleMute() {
        val next = !_isMuted.value
        _isMuted.value = next
        if (next) {
            speechManager.stopListening()
            _callStatusBanner.value = "Microphone muted"
        } else {
            _callStatusBanner.value = "Listening to you..."
            speechManager.startListening()
        }
    }

    fun toggleSpeaker() {
        _isSpeakerOn.value = !_isSpeakerOn.value
    }

    fun endCall() {
        callTimerJob?.cancel()
        ttsManager.stop()
        speechManager.cancel()

        val duration = _callSeconds.value
        val turns = _callMessages.value.count { it.isUser }

        val debrief = FluencyAiService.buildCallDebrief(
            scenario = selectedScenario.value,
            durationSeconds = duration,
            turnsCount = turns
        )
        _debriefResult.value = debrief
        _uiState.value = CallUiState.DEBRIEF
    }

    fun returnToSetup() {
        _uiState.value = CallUiState.SETUP
        _debriefResult.value = null
        _callSeconds.value = 0
        _callMessages.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        callTimerJob?.cancel()
        ttsManager.shutdown()
        speechManager.destroy()
    }
}
