package com.example.data.model

data class CallPersona(
    val id: String,
    val name: String,
    val roleTitle: String,
    val accentCode: String, // "US", "UK", "AUS"
    val accentLabel: String,
    val avatarEmoji: String,
    val bio: String
)

data class CallScenario(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val persona: CallPersona,
    val initialLine: String,
    val suggestedTopics: List<String>
)

data class CallMessage(
    val id: String = System.currentTimeMillis().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class CallDebrief(
    val durationSeconds: Int,
    val turnsCount: Int,
    val fluencyScore: Int,
    val clarityScore: Int,
    val accentTips: List<String>,
    val recommendedIdioms: List<Pair<String, String>>
)
