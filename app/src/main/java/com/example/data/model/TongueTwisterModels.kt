package com.example.data.model

enum class TwisterDifficulty(val label: String, val badgeColorHex: Long) {
    WARM_UP("Warm-Up", 0xFF10B981),
    AGILITY("Agility", 0xFF3B82F6),
    MASTER("Master", 0xFFF59E0B),
    SPEED_DEMON("Speed Demon", 0xFFEF4444)
}

data class TongueTwister(
    val id: String,
    val difficulty: TwisterDifficulty,
    val title: String,
    val targetSentence: String,
    val targetWpm: Int,
    val focusPhonemes: String, // e.g. "/r/ vs /l/"
    val articulatoryTip: String
)

data class TwisterEvaluation(
    val accuracyPercent: Int,
    val wordsPerMinute: Int,
    val elapsedSeconds: Float,
    val targetSpeedBeaten: Boolean,
    val matchedWords: List<Pair<String, Boolean>>,
    val tips: String
)
