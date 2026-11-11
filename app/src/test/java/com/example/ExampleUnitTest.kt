package com.example

import com.example.audio.PronunciationEvaluator
import com.example.data.model.WordMatchStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testWordSimilarityExactMatch() {
        val score = PronunciationEvaluator.wordSimilarity("coffee", "coffee")
        assertEquals(100, score)
    }

    @Test
    fun testWordSimilarityCloseMatch() {
        val score = PronunciationEvaluator.wordSimilarity("latte", "late")
        assertTrue("Expected score >= 75 for latte vs late", score >= 75)
    }

    @Test
    fun testCleanText() {
        val raw = "Hello, world! How are you doing?"
        val cleaned = PronunciationEvaluator.cleanText(raw)
        assertEquals("hello world how are you doing", cleaned)
    }

    @Test
    fun testEvaluatePerfectSentence() {
        val target = "Could I please get a medium oat milk latte to go?"
        val spoken = "Could I please get a medium oat milk latte to go"
        val eval = PronunciationEvaluator.evaluate(target, spoken)

        assertTrue(eval.overallScore >= 90)
        assertTrue(eval.words.all { it.status == WordMatchStatus.PERFECT })
    }

    @Test
    fun testEvaluateWithMissedWords() {
        val target = "I think this will sink"
        val spoken = "I sink this will sink"
        val eval = PronunciationEvaluator.evaluate(target, spoken)

        assertTrue(eval.words.isNotEmpty())
    }

    @Test
    fun testFluencyAiServiceSystemPrompt() {
        val prompt = com.example.data.ai.FluencyAiService.getSystemPrompt("Coffee Shop", "Beginner")
        assertTrue("Prompt must introduce tutor Mili", prompt.contains("Mili"))
        assertTrue("Prompt must include Coffee Shop scenario", prompt.contains("Coffee Shop"))
        assertTrue("Prompt must include Sinhala instructions", prompt.contains("Sinhala:"))
        assertTrue("Prompt must include Correction format", prompt.contains("Correction:"))
    }

    @Test
    fun testGrammarCorrectionDetection() {
        val correction = com.example.data.ai.FluencyAiService.checkGrammar("I am agree with your proposal")
        assertTrue("Must detect 'I am agree' mistake", correction != null)
        assertEquals("I agree", correction?.correctedText)
        assertTrue("Must include Sinhala explanation", correction?.sinhalaExplanation?.isNotBlank() == true)
    }

    @Test
    fun testPhonemeScoringEnginePercentageScores() {
        val analysis = com.example.audio.PhonemeScoringEngine.analyzeSpeech("I think this coffee is very good")
        assertTrue("Overall score must be in percentage range 0..100", analysis.overallPercentage in 70..100)
        assertTrue("Vowels percentage must be in range 0..100", analysis.vowelsPercentage in 70..100)
        assertTrue("Consonants percentage must be in range 0..100", analysis.consonantsPercentage in 70..100)
        assertTrue("Phonemes list must not be empty", analysis.phonemeScores.isNotEmpty())
        assertTrue("Must include IPA symbols", analysis.phonemeScores.any { it.phonemeIpa.startsWith("/") })
        assertTrue("Must have articulatory feedback", analysis.phonemeScores.all { it.feedbackTip.isNotBlank() })
    }
}
