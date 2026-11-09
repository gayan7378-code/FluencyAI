package com.example.data.model

enum class WordMatchStatus {
    PERFECT,
    CLOSE,
    MISSED
}

data class WordAccuracyResult(
    val targetWord: String,
    val spokenWord: String?,
    val status: WordMatchStatus,
    val score: Int // 0 to 100
)

data class EvaluationResult(
    val targetText: String,
    val spokenText: String,
    val overallScore: Int, // 0 - 100
    val words: List<WordAccuracyResult>,
    val feedback: String,
    val tips: String
)

data class DialogueLine(
    val id: String,
    val speakerRole: String, // e.g. "Barista", "Customer"
    val isUserTurn: Boolean,
    val text: String,
    val phoneticNote: String? = null,
    val alternativeOptions: List<String> = emptyList()
)

data class DialogueScenario(
    val id: String,
    val title: String,
    val category: String,
    val difficulty: String, // "Beginner", "Intermediate", "Advanced"
    val iconName: String, // "coffee", "work", "flight", "medical", "hotel"
    val description: String,
    val userRoleName: String,
    val partnerRoleName: String,
    val lines: List<DialogueLine>
)

enum class SoundCategory(val title: String, val description: String) {
    MINIMAL_PAIRS("Minimal Pairs", "Distinguish similar contrasting sounds like /θ/ vs /s/"),
    TRICKY_CONSONANTS("Tricky Consonants", "Master difficult consonants like /r/, /l/, /v/, /w/"),
    WORD_STRESS("Word Stress & Syllables", "Stress the right syllable for natural rhythm"),
    CONNECTED_SPEECH("Connected Speech", "Native linking, elisions, and contractions")
}

data class PronunciationDrill(
    val id: String,
    val category: SoundCategory,
    val title: String,
    val phoneticSymbol: String, // e.g. "/θ/ vs /s/"
    val targetSentence: String,
    val focusWords: List<String>,
    val mouthGuide: String, // "Place tip of tongue lightly between teeth..."
    val audioSpeed: Float = 1.0f
)

data class PhraseItem(
    val id: String,
    val category: String,
    val englishText: String,
    val phoneticHint: String,
    val meaning: String,
    val exampleUsage: String,
    val isBookmarked: Boolean = false
)

data class SpeakingStats(
    val streakDays: Int = 3,
    val totalPhrasesPracticed: Int = 24,
    val averageAccuracy: Int = 88,
    val totalSpeakingMinutes: Int = 42,
    val dailyGoalCompleted: Boolean = false
)

data class FreeSpeechResult(
    val transcript: String,
    val durationSeconds: Int,
    val wordsPerMinute: Int,
    val totalWords: Int,
    val fillerWordsDetected: List<String>,
    val fluencyScore: Int,
    val suggestions: List<String>
)
