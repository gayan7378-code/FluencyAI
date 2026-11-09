package com.example.audio

import com.example.data.model.EvaluationResult
import com.example.data.model.WordAccuracyResult
import com.example.data.model.WordMatchStatus
import kotlin.math.max
import kotlin.math.min

object PronunciationEvaluator {

    /**
     * Cleans up a string for fair comparison (strips punctuation, trims whitespace, lowercase)
     */
    fun cleanText(text: String): String {
        return text.lowercase()
            .replace(Regex("[^a-z0-9'\\s]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    /**
     * Calculates the Levenshtein distance between two strings
     */
    fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    /**
     * Computes similarity between two single words (0 to 100)
     */
    fun wordSimilarity(target: String, spoken: String): Int {
        val t = target.trim().lowercase()
        val s = spoken.trim().lowercase()
        if (t == s) return 100
        val maxLen = max(t.length, s.length)
        if (maxLen == 0) return 100
        val dist = levenshteinDistance(t, s)
        val score = ((maxLen - dist).toDouble() / maxLen.toDouble() * 100).toInt()
        return max(0, score)
    }

    /**
     * Evaluates a spoken sentence against the target sentence
     */
    fun evaluate(targetSentence: String, spokenSentence: String): EvaluationResult {
        val cleanedTarget = cleanText(targetSentence)
        val cleanedSpoken = cleanText(spokenSentence)

        val targetWords = cleanedTarget.split(" ").filter { it.isNotBlank() }
        val spokenWords = cleanedSpoken.split(" ").filter { it.isNotBlank() }.toMutableList()

        if (targetWords.isEmpty()) {
            return EvaluationResult(
                targetText = targetSentence,
                spokenText = spokenSentence,
                overallScore = 100,
                words = emptyList(),
                feedback = "Great job!",
                tips = "Keep up the smooth rhythm."
            )
        }

        val wordResults = mutableListOf<WordAccuracyResult>()
        var totalWordScores = 0

        // Greedy alignment: match target words to closest remaining spoken words in window
        var spokenIndex = 0

        for (tWord in targetWords) {
            var bestSpokenWord: String? = null
            var bestScore = 0
            var bestSpokenPos = -1

            // Look ahead up to 3 positions
            val searchWindowEnd = min(spokenWords.size, spokenIndex + 4)
            for (idx in spokenIndex until searchWindowEnd) {
                val candidate = spokenWords[idx]
                val score = wordSimilarity(tWord, candidate)
                if (score > bestScore) {
                    bestScore = score
                    bestSpokenWord = candidate
                    bestSpokenPos = idx
                }
            }

            val status: WordMatchStatus
            val finalScore: Int

            if (bestScore >= 80) {
                status = WordMatchStatus.PERFECT
                finalScore = bestScore
                if (bestSpokenPos >= 0) {
                    spokenIndex = bestSpokenPos + 1
                }
            } else if (bestScore >= 50) {
                status = WordMatchStatus.CLOSE
                finalScore = bestScore
                if (bestSpokenPos >= 0) {
                    spokenIndex = bestSpokenPos + 1
                }
            } else {
                status = WordMatchStatus.MISSED
                finalScore = 0
                bestSpokenWord = null
            }

            wordResults.add(
                WordAccuracyResult(
                    targetWord = tWord,
                    spokenWord = bestSpokenWord,
                    status = status,
                    score = finalScore
                )
            )
            totalWordScores += finalScore
        }

        val averageScore = if (targetWords.isNotEmpty()) totalWordScores / targetWords.size else 0

        val (feedback, tips) = when {
            averageScore >= 90 -> Pair(
                "Outstanding! Native-level articulation 🌟",
                "Your pronunciation, stress, and clarity were exceptional."
            )
            averageScore >= 75 -> Pair(
                "Great work! Very clear and natural 👍",
                "Try emphasizing key content words and link consonants smoothly."
            )
            averageScore >= 55 -> Pair(
                "Good effort! A few sounds need attention 🎯",
                "Listen to the native audio again. Pay close attention to highlighted words."
            )
            else -> Pair(
                "Keep practicing! Slow down your speech 💡",
                "Tap 'Slow (0.75x)' to hear each syllable clearly, then repeat word by word."
            )
        }

        return EvaluationResult(
            targetText = targetSentence,
            spokenText = spokenSentence,
            overallScore = averageScore,
            words = wordResults,
            feedback = feedback,
            tips = tips
        )
    }
}
