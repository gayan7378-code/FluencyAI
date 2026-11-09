package com.example.data.model

data class PresentationSlide(
    val slideNumber: Int,
    val title: String,
    val bulletPoints: List<String>,
    val speakerDeliveryNotes: String,
    val suggestedVocabulary: List<String>
)

data class PresentationTopic(
    val id: String,
    val title: String,
    val category: String,
    val iconEmoji: String,
    val slides: List<PresentationSlide>
)

data class PresentationEvaluation(
    val topicTitle: String,
    val overallScoreOutOf10: Float, // e.g. 8.7
    val performanceTier: String, // "Mastery", "Proficient", "Developing"
    val articulationScore: Int, // 0 - 100
    val cadencePacingScore: Int, // 0 - 100
    val rhetoricVocabScore: Int, // 0 - 100
    val slideAlignmentScore: Int, // 0 - 100
    val strengths: List<String>,
    val concreteImprovements: List<String>,
    val difficultWordsWithIpa: List<Pair<String, String>>
)

data class PresentationScoreRecord(
    val id: String = System.currentTimeMillis().toString(),
    val topic: String,
    val scoreOutOf10: Float,
    val dateLabel: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class BookExcerpt(
    val id: String,
    val bookTitle: String,
    val author: String,
    val category: String,
    val passageText: String,
    val targetWpm: Int = 130,
    val challengingWords: List<Pair<String, String>> // word to IPA
)

data class BookReadingEvaluation(
    val accuracyPercent: Int,
    val readingSpeedWpm: Int,
    val totalWordsRead: Int,
    val mispronouncedWords: List<Pair<String, String>>, // word to IPA & tip
    val feedback: String
)
