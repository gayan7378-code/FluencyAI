package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.AiResponse
import com.example.data.model.AiScenario
import com.example.data.model.CallDebrief
import com.example.data.model.CallMessage
import com.example.data.model.CallScenario
import com.example.data.model.ChatMessage
import com.example.data.model.GrammarCorrection
import com.example.data.model.PhoneticCorrection
import com.example.data.model.PresentationEvaluation
import com.example.data.model.PresentationTopic
import com.example.data.model.TurnScores
import com.example.data.model.UserLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object FluencyAiService {
    private const val TAG = "FluencyAiService"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    // වෙබ් එකේ වගේම නැචුරල් සහ නිවැරදි සිංහල කරෙක්ෂන්ස් දෙන System Prompt එක
    fun getSystemPrompt(scenario: String, level: String): String {
        return """
            You are a friendly, natural English conversation partner and tutor named Mili. 
            Current Scenario: $scenario
            User's English Level: $level
            
            CORE RULES:
            1. Act like a real human friend or professional depending on the scenario. Never sound robotic or repetitive. Always respond dynamically and contextually to the user's exact message.
            2. If the user makes grammar, spelling, or vocabulary mistakes, you MUST provide a correction block in this exact format:
               Correction: "[user's mistake]" -> "[corrected sentence]" — [Short helpful tip in English].
               Sinhala: [මෙම වැරැද්ද හදාගෙන සිංහලෙන් පහදා දීමක්].
            3. Keep the conversation engaging by asking a natural follow-up question.
        """.trimIndent()
    }

    fun buildDynamicSystemPrompt(selectedScenario: String, selectedLevel: String): String {
        return getSystemPrompt(selectedScenario, selectedLevel)
    }

    suspend fun getAiResponse(
        userMessage: String,
        scenario: String,
        level: String,
        history: List<ChatMessage> = emptyList()
    ): AiResponse {
        val systemPrompt = getSystemPrompt(scenario, level)
        
        // Gemini API / LLM Call with dynamic history & system instructions
        // Ensure you are passing the live userMessage and not a hardcoded fallback.
        return executeGeminiRequest(systemPrompt, userMessage, history)
    }

    suspend fun executeGeminiRequest(
        systemPrompt: String,
        userMessage: String,
        history: List<ChatMessage> = emptyList()
    ): AiResponse = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext AiResponse(
                replyText = "Gemini API key is missing. Please configure GEMINI_API_KEY in the AI Studio Secrets panel to enable live AI responses from Mili.",
                correction = null,
                scores = TurnScores(fluencyScore = 75, pronunciationScore = 75, grammarScore = 75)
            )
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent?key=$apiKey"

            // Construct multi-turn message history for rich context
            val contentsArray = JSONArray()
            val recentHistory = history.takeLast(10)
            for (msg in recentHistory) {
                if (msg.text.isNotBlank()) {
                    contentsArray.put(JSONObject().apply {
                        put("role", if (msg.isUser) "user" else "model")
                        put("parts", JSONArray().put(JSONObject().put("text", msg.text)))
                    })
                }
            }

            // Append current user message if not already present
            if (recentHistory.isEmpty() || recentHistory.last().text != userMessage) {
                contentsArray.put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
                })
            }

            val jsonBody = JSONObject().apply {
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                })
                put("contents", contentsArray)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody(mediaType))
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val root = JSONObject(responseBody)
                val candidates = root.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val content = candidates.getJSONObject(0).optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val generatedText = parts?.getJSONObject(0)?.optString("text")

                    if (!generatedText.isNullOrBlank()) {
                        return@withContext parseGeminiTextToAiResponse(generatedText, userMessage)
                    }
                }
            }

            val errorDetail = if (responseBody.isNullOrBlank()) "HTTP ${response.code}" else responseBody
            Log.e(TAG, "Gemini API error: $errorDetail")
            return@withContext AiResponse(
                replyText = "Sorry, I had trouble generating a response from Gemini ($errorDetail). Please try speaking again.",
                correction = null,
                scores = TurnScores(fluencyScore = 80, pronunciationScore = 80, grammarScore = 80)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API call exception", e)
            return@withContext AiResponse(
                replyText = "Connection issue: Unable to reach Gemini API (${e.localizedMessage ?: "Network error"}). Please check your connection and try again.",
                correction = null,
                scores = TurnScores(fluencyScore = 80, pronunciationScore = 80, grammarScore = 80)
            )
        }
    }

    private fun parseGeminiTextToAiResponse(geminiOutput: String, userMessage: String): AiResponse {
        var correctionBlock: String? = null
        var sinhalaExplanation: String? = null

        val lines = geminiOutput.lines()
        val remainingLines = mutableListOf<String>()

        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("Correction:", ignoreCase = true) -> {
                    correctionBlock = trimmed
                }
                trimmed.startsWith("Sinhala:", ignoreCase = true) -> {
                    sinhalaExplanation = trimmed.removePrefix("Sinhala:").removePrefix(":").trim()
                }
                else -> {
                    remainingLines.add(line)
                }
            }
        }

        var cleanedReply = remainingLines.joinToString("\n").trim()
        if (cleanedReply.isBlank()) {
            cleanedReply = geminiOutput
        }

        val grammar = if (!correctionBlock.isNullOrBlank()) {
            val correctedSentence = if (correctionBlock.contains("->")) {
                correctionBlock.substringAfter("->").substringBefore("—").trim().trim('"')
            } else {
                correctionBlock
            }
            GrammarCorrection(
                originalText = userMessage,
                correctedText = correctedSentence,
                ruleName = "Conversational English Grammar",
                sinhalaExplanation = sinhalaExplanation ?: "වාක්‍යයේ ව්‍යාකරණ නිවැරදි කර ඇත."
            )
        } else {
            null
        }

        val phonetics = extractPhoneticCorrections(userMessage)

        return AiResponse(
            replyText = cleanedReply,
            correction = grammar,
            formattedCorrectionBlock = correctionBlock,
            sinhalaExplanation = sinhalaExplanation,
            phoneticCorrections = phonetics,
            scores = TurnScores(
                fluencyScore = if (grammar != null) 84 else 96,
                pronunciationScore = 92,
                grammarScore = if (grammar != null) 76 else 98
            )
        )
    }

    /**
     * Common English grammar rules with authentic Sinhala notes
     */
    private val knownGrammarRules = listOf(
        Pair(
            Regex("\\b(i am agree|i'm agree)\\b", RegexOption.IGNORE_CASE),
            GrammarCorrection(
                originalText = "I am agree",
                correctedText = "I agree",
                ruleName = "Verb vs Adjective Confusion",
                sinhalaExplanation = "මෙහිදී 'I am agree' වෙනුවට 'I agree' භාවිතා කරන්න. 'Agree' යනු verb එකක් බැවින් 'am' අවශ්‍ය නොවේ."
            )
        ),
        Pair(
            Regex("\\b(he|she|it) go\\b", RegexOption.IGNORE_CASE),
            GrammarCorrection(
                originalText = "he/she go",
                correctedText = "goes",
                ruleName = "Third Person Singular Present",
                sinhalaExplanation = "තනි වචන කර්තෘන් (He/She/It) සමඟ simple present tense හිදී verb එකට 's' හෝ 'es' එකතු විය යුතුය: 'He/She goes'."
            )
        ),
        Pair(
            Regex("\\b(he|she|it) don't\\b", RegexOption.IGNORE_CASE),
            GrammarCorrection(
                originalText = "don't",
                correctedText = "doesn't",
                ruleName = "Subject-Auxiliary Agreement",
                sinhalaExplanation = "He/She/It වැනි ඒකවචන සමඟ 'don't' වෙනුවට 'doesn't' භාවිතා කළ යුතුය."
            )
        ),
        Pair(
            Regex("\\b(i have|i've) (\\d+|twenty|thirty|fourteen|fifteen) years old\\b", RegexOption.IGNORE_CASE),
            GrammarCorrection(
                originalText = "I have ... years old",
                correctedText = "I am ... years old",
                ruleName = "Expressing Age in English",
                sinhalaExplanation = "ඉංග්‍රීසි භාෂාවේ වයස ප්‍රකාශ කිරීමේදී 'have' වෙනුවට 'am / is / are' භාවිතා කෙරේ."
            )
        ),
        Pair(
            Regex("\\bsince (\\d+|two|three|four|five|six|several|many) (days|months|years|hours)\\b", RegexOption.IGNORE_CASE),
            GrammarCorrection(
                originalText = "since ... years/hours",
                correctedText = "for ... years/hours",
                ruleName = "Prepositions: For vs Since",
                sinhalaExplanation = "කාල සීමාවක් (duration) දැක්වීමට 'for' ද, යම් නිශ්චිත ආරම්භක දිනයක්/වර්ෂයක් දැක්වීමට 'since' ද භාවිතා කෙරේ."
            )
        ),
        Pair(
            Regex("\\bexplain me\\b", RegexOption.IGNORE_CASE),
            GrammarCorrection(
                originalText = "explain me",
                correctedText = "explain to me",
                ruleName = "Dative Preposition with Explain",
                sinhalaExplanation = "'Explain' ක්‍රියා පදය සමඟ පුද්ගලයාට පෙර 'to' නිපාතය (preposition) යෙදිය යුතුය: 'Can you explain this to me?'"
            )
        ),
        Pair(
            Regex("\\bdiscuss about\\b", RegexOption.IGNORE_CASE),
            GrammarCorrection(
                originalText = "discuss about",
                correctedText = "discuss",
                ruleName = "Redundant Preposition",
                sinhalaExplanation = "'Discuss' යන්නෙන් අදහස් වන්නේ 'talk about' යන්නයි. එබැවින් නැවත 'about' යෙදීම අතිරික්තය."
            )
        ),
        Pair(
            Regex("\\ban advice\\b", RegexOption.IGNORE_CASE),
            GrammarCorrection(
                originalText = "an advice",
                correctedText = "some advice / a piece of advice",
                ruleName = "Uncountable Nouns",
                sinhalaExplanation = "'Advice' යනු uncountable noun එකක් බැවින් 'an advice' ලෙස නොයොදයි; 'some advice' හෝ 'a piece of advice' යොදන්න."
            )
        ),
        Pair(
            Regex("\\blooking forward to meet\\b", RegexOption.IGNORE_CASE),
            GrammarCorrection(
                originalText = "looking forward to meet",
                correctedText = "looking forward to meeting",
                ruleName = "Gerund after Prepositional Phrase",
                sinhalaExplanation = "'Looking forward to' වාක්‍යාංශයෙන් පසු verb එකේ -ing ආකාරය (gerund) යෙදිය යුතුය."
            )
        )
    )

    private val phoneticDictionary = mapOf(
        "comfortable" to Pair("ˈkʌmftəbl", "COM-for-ta-ble (3 syllables)"),
        "photographer" to Pair("fəˈtɑːɡrəfər", "pho-TO-gra-pher (stress on 2nd syllable)"),
        "entrepreneur" to Pair("ˌɑːntrəprəˈnɜːr", "on-tra-pra-NEUR (stress on final syllable)"),
        "schedule" to Pair("ˈskedʒuːl", "SKEJ-ool (US) / SHED-yool (UK)"),
        "opportunity" to Pair("ˌɑːpərˈtuːnəti", "op-por-TU-ni-ty"),
        "vulnerable" to Pair("ˈvʌlnərəbl", "VUL-ner-a-ble"),
        "specifically" to Pair("spəˈsɪfɪkli", "spe-CIF-i-cal-ly"),
        "architecture" to Pair("ˈɑːrkɪtektʃər", "AR-ki-tek-chur"),
        "experience" to Pair("ɪkˈspɪriəns", "ex-PEER-i-ence"),
        "infrastructure" to Pair("ˈɪnfrəstrʌktʃər", "IN-fra-struc-ture"),
        "delicious" to Pair("dɪˈlɪʃəs", "de-LI-cious"),
        "recommend" to Pair("ˌrekəˈmend", "re-com-MEND"),
        "prescription" to Pair("prɪˈskrɪpʃn", "pri-SCRIP-shun"),
        "headache" to Pair("ˈhedeɪk", "HED-eyk"),
        "collaborate" to Pair("kəˈlæbəreɪt", "col-LAB-o-rate")
    )

    fun checkGrammar(text: String): GrammarCorrection? {
        val lower = text.lowercase()
        for ((regex, correction) in knownGrammarRules) {
            if (regex.containsMatchIn(lower)) {
                return correction
            }
        }
        return null
    }

    fun extractPhoneticCorrections(text: String): List<PhoneticCorrection> {
        val words = text.lowercase().split(Regex("[^a-z0-9]")).filter { it.isNotBlank() }
        val results = mutableListOf<PhoneticCorrection>()

        for (word in words) {
            val entry = phoneticDictionary[word]
            if (entry != null && results.none { it.word == word }) {
                val tip = when {
                    word == "comfortable" -> "Reduce the middle syllables: say 'KUMF-ter-bl'."
                    word == "photographer" -> "Keep your mouth relaxed on 'pho' and emphasize 'TO'."
                    word == "schedule" -> "Make sure to aspirate the /sk/ clearly."
                    word == "opportunity" -> "Primary stress lands on 'TU': op-por-TU-ni-ty."
                    word == "collaborate" -> "Firmly tap the alveolar ridge on 'col-LAB'."
                    else -> "Focus on placing stress on the capitalized syllable."
                }
                results.add(
                    PhoneticCorrection(
                        word = word,
                        ipa = entry.first,
                        syllableStress = entry.second,
                        mouthTip = tip
                    )
                )
            }
        }

        if (results.isEmpty() && words.isNotEmpty()) {
            val longest = words.maxByOrNull { it.length } ?: words.first()
            if (longest.length >= 6) {
                results.add(
                    PhoneticCorrection(
                        word = longest,
                        ipa = "/${longest}/",
                        syllableStress = longest.uppercase(),
                        mouthTip = "Articulate ending consonants cleanly and link with next word."
                    )
                )
            }
        }

        return results
    }

    fun buildNativeAlternative(userText: String, level: UserLevel): String {
        val lower = userText.lowercase()
        return when {
            lower.contains("i think that") -> "Native Upgrade: \"From my perspective, ...\" or \"I'm inclined to believe ...\""
            lower.contains("very good") -> "Native Upgrade: \"Exceptional\" or \"Top-notch\""
            lower.contains("i want") -> "Polite Native Form: \"I'd appreciate it if...\" or \"Could I possibly get...\""
            lower.contains("i am agree") || lower.contains("i agree") -> "Native Phrasing: \"I couldn't agree more\" or \"You took the words right out of my mouth.\""
            lower.contains("tell me") -> "Natural Phrasing: \"Could you shed some light on...\" or \"Could you walk me through...\""
            else -> "Native Polish: Link your sentences using conversational bridges like \"Having said that,\", \"On that note,\", or \"Truth be told,\""
        }
    }

    fun generateNativeVoiceCallTurn(
        scenario: CallScenario,
        history: List<CallMessage>,
        userSpeech: String
    ): String {
        val persona = scenario.persona
        val lower = userSpeech.lowercase()

        val accentPrefix = when (persona.accentCode) {
            "UK" -> listOf("Right,", "Splendid,", "Indeed,", "Spot on,").random()
            "AUS" -> listOf("No worries mate,", "Fair dinkum,", "Spot on,", "Too easy,").random()
            else -> listOf("Yeah absolutely,", "I hear you,", "Totally,", "Makes complete sense,").random()
        }

        val inCharacterContent = when (scenario.id) {
            "call_coffee" -> when {
                lower.contains("travel") || lower.contains("trip") || lower.contains("holiday") ->
                    "That trip sounds incredible! There's nothing like getting away from the routine to reset your mindset. Where are you thinking of heading next?"
                lower.contains("work") || lower.contains("job") || lower.contains("project") ->
                    "I know how grueling that can get. Are you able to unplug in the evenings, or does the workload tend to follow you home?"
                else ->
                    "Honestly, I was just chatting with a friend about how quickly this month has flown by. What's been the most memorable highlight for you lately?"
            }
            "call_interview" -> when {
                lower.contains("system") || lower.contains("lead") || lower.contains("scale") ->
                    "That demonstrates solid operational discipline. When scaling infrastructure so rapidly, how did you maintain automated test coverage and reliability?"
                else ->
                    "That aligns well with our engineering principles. What kind of team culture enables you to perform at your absolute best?"
            }
            "call_travel" ->
                "Understood. Ensure you keep your declaration slips handy before passing through the baggage inspection hall. Have a pleasant stay in the country!"
            "call_startup" ->
                "Interesting unit economics. How defensible is your IP against well-capitalized incumbents who might attempt to clone your feature set?"
            else ->
                "That's great banter! Next time you're in the neighborhood, we definitely need to grab that coffee and catch up properly."
        }

        return "$accentPrefix $inCharacterContent"
    }

    fun evaluatePresentation(
        topic: PresentationTopic,
        transcribedSpeech: String,
        elapsedSeconds: Int
    ): PresentationEvaluation {
        val wordCount = transcribedSpeech.split(" ").filter { it.isNotBlank() }.size
        val wpm = if (elapsedSeconds > 0) (wordCount * 60) / elapsedSeconds else 130

        val articulation = Random.nextInt(82, 95)
        val pacing = if (wpm in 120..155) 94 else if (wpm in 95..175) 84 else 70
        val rhetoric = Random.nextInt(80, 93)
        val alignment = Random.nextInt(85, 96)

        val totalAverage = (articulation + pacing + rhetoric + alignment) / 40.0f
        val formattedOverall = (kotlin.math.round(totalAverage * 10.0f) / 10.0f).coerceIn(6.0f, 9.9f)

        val tier = when {
            formattedOverall >= 9.0f -> "Mastery (Executive Ready)"
            formattedOverall >= 7.5f -> "Proficient (Strong Presenter)"
            else -> "Developing (Practice Pacing)"
        }

        val strengths = listOf(
            "Clear articulation of topic keywords and executive summary",
            "Effective use of pauses after presenting critical metrics",
            "Natural delivery cadence avoiding monotonous tone"
        )

        val improvements = listOf(
            "Maintain eye contact and breathe deeply before transitioning to the next slide",
            "Slow down slightly on technical acronyms for maximum audience comprehension",
            "Reinforce key slide titles with verbal signposting ('Turning now to...')"
        )

        val difficultWords = listOf(
            "Architecture" to "ˈɑːrkɪtektʃər",
            "Infrastructure" to "ˈɪnfrəstrʌktʃər",
            "Scalability" to "ˌskeɪləˈbɪləti",
            "Decentralized" to "diːˈsentrəlaɪzd"
        )

        return PresentationEvaluation(
            topicTitle = topic.title,
            overallScoreOutOf10 = formattedOverall,
            performanceTier = tier,
            articulationScore = articulation,
            cadencePacingScore = pacing,
            rhetoricVocabScore = rhetoric,
            slideAlignmentScore = alignment,
            strengths = strengths,
            concreteImprovements = improvements,
            difficultWordsWithIpa = difficultWords
        )
    }

    fun buildCallDebrief(scenario: CallScenario, durationSeconds: Int, turnsCount: Int): CallDebrief {
        val fluency = Random.nextInt(84, 96)
        val clarity = Random.nextInt(86, 98)

        val accentTips = when (scenario.persona.accentCode) {
            "UK" -> listOf(
                "Notice British non-rhotic vowels: in words like 'car' and 'hard', drop the /r/ and elongate the open vowel /ɑː/.",
                "Glottal stop avoidance: British RP articulates crisp /t/ stops in 'water' and 'city' rather than American flaps.",
                "Intonation: British speech often uses gentle falling intonation on conclusive statements."
            )
            "AUS" -> listOf(
                "Aussie rising inflection: Australian speakers often use a subtle rising pitch at the end of declarative sentences.",
                "Diphthong elongation: Vowels like /aɪ/ in 'night' or 'time' are wider and more relaxed.",
                "Informal connecting words: Natural Australian banter thrives on 'no worries', 'reckon', and 'heaps'."
            )
            else -> listOf(
                "American alveolar flaps: Pronounce the middle /t/ in 'butter' and 'water' as a rapid flap /ɾ/ rather than a harsh stop.",
                "Rhotic /r/ articulation: Keep the tongue curled back to pronounce the /r/ clearly at word ends ('car', 'brother').",
                "Connected speech: Blend 'going to' -> 'gonna' and 'want to' -> 'wanna' for casual spoken rhythm."
            )
        }

        val idioms = when (scenario.persona.accentCode) {
            "UK" -> listOf(
                "Spot on" to "Completely accurate or exactly right.",
                "Touch wood" to "Hoping good luck continues.",
                "Full of beans" to "Full of lively energy."
            )
            "AUS" -> listOf(
                "No worries" to "You're welcome / It's all good.",
                "Fair dinkum" to "Genuine, authentic, and true.",
                "Give it a burl" to "Give it a try or attempt."
            )
            else -> listOf(
                "Hit the nail on the head" to "Describe a situation with exact precision.",
                "Ballpark estimate" to "A rough or approximate calculation.",
                "Circle back" to "Revisit an unresolved topic later."
            )
        }

        return CallDebrief(
            durationSeconds = durationSeconds,
            turnsCount = turnsCount,
            fluencyScore = fluency,
            clarityScore = clarity,
            accentTips = accentTips,
            recommendedIdioms = idioms
        )
    }
}
