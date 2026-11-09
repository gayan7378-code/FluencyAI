package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SpeechManager
import com.example.audio.SpeechState
import com.example.audio.TtsManager
import com.example.data.ai.FluencyAiService
import com.example.data.model.AiScenario
import com.example.data.model.ChatMessage
import com.example.data.model.ScenarioData
import com.example.data.model.UserLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoleplayChatViewModel(application: Application) : AndroidViewModel(application) {

    val ttsManager = TtsManager(application)
    val speechManager = SpeechManager(application)

    val scenarios: List<AiScenario> = ScenarioData.allScenarios

    private val _selectedScenario = MutableStateFlow(scenarios.first())
    val selectedScenario: StateFlow<AiScenario> = _selectedScenario.asStateFlow()

    private val _selectedLevel = MutableStateFlow(UserLevel.INTERMEDIATE)
    val selectedLevel: StateFlow<UserLevel> = _selectedLevel.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _inspectedMessage = MutableStateFlow<ChatMessage?>(null)
    val inspectedMessage: StateFlow<ChatMessage?> = _inspectedMessage.asStateFlow()

    val currentDynamicPrompt: String
        get() = FluencyAiService.buildDynamicSystemPrompt(
            selectedScenario = _selectedScenario.value.title,
            selectedLevel = _selectedLevel.value.label
        )

    init {
        startScenario(scenarios.first(), UserLevel.INTERMEDIATE)

        // Observe speech results
        viewModelScope.launch {
            speechManager.speechState.collect { state ->
                if (state is SpeechState.Success && state.recognizedText.isNotBlank()) {
                    sendMessage(state.recognizedText)
                }
            }
        }
    }

    fun setLevel(level: UserLevel) {
        _selectedLevel.value = level
    }

    fun startScenario(scenario: AiScenario, level: UserLevel = _selectedLevel.value) {
        ttsManager.stop()
        speechManager.cancel()
        _selectedScenario.value = scenario
        _selectedLevel.value = level
        _inspectedMessage.value = null

        val openingMessage = ChatMessage(
            isUser = false,
            text = scenario.initialGreeting,
            timestamp = System.currentTimeMillis()
        )
        _messages.value = listOf(openingMessage)
        ttsManager.speak(scenario.initialGreeting, openingMessage.id)
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        ttsManager.stop()

        val userMessage = ChatMessage(
            isUser = true,
            text = userText.trim(),
            timestamp = System.currentTimeMillis()
        )
        val currentList = _messages.value + userMessage
        _messages.value = currentList
        _isAiThinking.value = true

        viewModelScope.launch {
            val scenario = _selectedScenario.value
            val level = _selectedLevel.value

            // Generate contextual AI response with Sinhala grammar analysis and phonetics
            val aiResponse = FluencyAiService.generateScenarioChatTurn(
                scenario = scenario,
                level = level,
                history = currentList,
                userUtterance = userText
            )

            _messages.value = _messages.value + aiResponse
            _isAiThinking.value = false

            // Auto-inspect if grammar error was found so user sees the Sinhala explanation immediately!
            if (aiResponse.grammarCorrection != null) {
                _inspectedMessage.value = aiResponse
            }

            // Speak AI response
            ttsManager.speak(aiResponse.text, aiResponse.id)
        }
    }

    fun inspectMessage(message: ChatMessage?) {
        _inspectedMessage.value = message
    }

    fun speakWord(word: String) {
        ttsManager.speak(word)
    }

    fun speakMessage(message: ChatMessage) {
        ttsManager.speak(message.text, message.id)
    }

    fun startListening() {
        ttsManager.stop()
        speechManager.startListening()
    }

    fun stopListening() {
        speechManager.stopListening()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
        speechManager.destroy()
    }
}
