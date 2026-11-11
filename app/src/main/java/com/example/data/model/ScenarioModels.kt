package com.example.data.model

enum class UserLevel(val label: String, val description: String) {
    BEGINNER("Beginner", "Simple sentences, clear words, guided corrections"),
    INTERMEDIATE("Intermediate", "Natural everyday cadence, idioms & fluency"),
    ADVANCED("Advanced", "Rich vocabulary, nuanced native phrasing & rhetoric")
}

data class GrammarCorrection(
    val originalText: String,
    val correctedText: String,
    val ruleName: String,
    val sinhalaExplanation: String
)

data class PhoneticCorrection(
    val word: String,
    val ipa: String,
    val syllableStress: String,
    val mouthTip: String
)

data class TurnScores(
    val fluencyScore: Int, // 0 - 100
    val pronunciationScore: Int, // 0 - 100
    val grammarScore: Int // 0 - 100
)

data class PhonemeScore(
    val phonemeIpa: String, // e.g. "/θ/", "/æ/", "/l/"
    val word: String,       // Word containing this phoneme
    val scorePercentage: Int, // 0 - 100%
    val isAccurate: Boolean,
    val feedbackTip: String
)

data class PhonemeAnalysisResult(
    val overallPercentage: Int, // 0 - 100%
    val vowelsPercentage: Int,  // 0 - 100%
    val consonantsPercentage: Int, // 0 - 100%
    val stressCadencePercentage: Int, // 0 - 100%
    val phonemeScores: List<PhonemeScore> = emptyList(),
    val feedbackSummary: String = ""
)

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val scores: TurnScores? = null,
    val grammarCorrection: GrammarCorrection? = null,
    val phoneticCorrections: List<PhoneticCorrection> = emptyList(),
    val nativeUpgradeAlternative: String? = null,
    val phonemeAnalysis: PhonemeAnalysisResult? = null
)

data class AiScenario(
    val id: String,
    val title: String,
    val category: String, // "Daily Life", "Career", "Travel", "Dining", "Medical"
    val iconEmoji: String,
    val description: String,
    val aiRole: String,
    val userRole: String,
    val initialGreeting: String,
    val suggestedTopics: List<String>
)

data class AiResponse(
    val replyText: String,
    val correction: GrammarCorrection? = null,
    val formattedCorrectionBlock: String? = null,
    val sinhalaExplanation: String? = null,
    val phoneticCorrections: List<PhoneticCorrection> = emptyList(),
    val scores: TurnScores? = null
)
