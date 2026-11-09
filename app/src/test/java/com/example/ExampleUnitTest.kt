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
}
