package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.DialogueLine
import com.example.data.model.DialogueScenario
import com.example.data.model.PhraseItem
import com.example.data.model.PronunciationDrill
import com.example.data.model.SoundCategory
import com.example.data.model.SpeakingStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SpokenEnglishRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("speakflow_prefs", Context.MODE_PRIVATE)

    private val _stats = MutableStateFlow(loadStats())
    val stats: StateFlow<SpeakingStats> = _stats.asStateFlow()

    private val _bookmarkedPhraseIds = MutableStateFlow(loadBookmarks())
    val bookmarkedPhraseIds: StateFlow<Set<String>> = _bookmarkedPhraseIds.asStateFlow()

    private fun loadStats(): SpeakingStats {
        val streak = prefs.getInt("key_streak", 3)
        val phrases = prefs.getInt("key_phrases_count", 28)
        val accuracy = prefs.getInt("key_accuracy", 86)
        val minutes = prefs.getInt("key_minutes", 45)
        val completedToday = prefs.getBoolean("key_daily_completed", false)
        return SpeakingStats(streak, phrases, accuracy, minutes, completedToday)
    }

    private fun loadBookmarks(): Set<String> {
        return prefs.getStringSet("key_bookmarks", emptySet()) ?: emptySet()
    }

    fun toggleBookmark(phraseId: String) {
        val current = _bookmarkedPhraseIds.value.toMutableSet()
        if (current.contains(phraseId)) {
            current.remove(phraseId)
        } else {
            current.add(phraseId)
        }
        prefs.edit().putStringSet("key_bookmarks", current).apply()
        _bookmarkedPhraseIds.value = current
    }

    fun recordPractice(score: Int) {
        val current = _stats.value
        val newPhrases = current.totalPhrasesPracticed + 1
        val newAccuracy = ((current.averageAccuracy * current.totalPhrasesPracticed) + score) / newPhrases
        val updated = current.copy(
            totalPhrasesPracticed = newPhrases,
            averageAccuracy = newAccuracy,
            totalSpeakingMinutes = current.totalSpeakingMinutes + 1,
            dailyGoalCompleted = true
        )
        prefs.edit()
            .putInt("key_phrases_count", updated.totalPhrasesPracticed)
            .putInt("key_accuracy", updated.averageAccuracy)
            .putInt("key_minutes", updated.totalSpeakingMinutes)
            .putBoolean("key_daily_completed", updated.dailyGoalCompleted)
            .apply()
        _stats.value = updated
    }

    fun getScenarios(): List<DialogueScenario> {
        return listOf(
            DialogueScenario(
                id = "coffee_shop",
                title = "Ordering at a Cafe",
                category = "Daily Life",
                difficulty = "Beginner",
                iconName = "coffee",
                description = "Practice ordering customized drinks, breakfast pastry, and paying politely.",
                userRoleName = "Customer",
                partnerRoleName = "Barista",
                lines = listOf(
                    DialogueLine(
                        id = "c1",
                        speakerRole = "Barista",
                        isUserTurn = false,
                        text = "Hi there! Welcome to Bean & Brew. What can I get started for you today?",
                        phoneticNote = "Notice friendly rising intonation on 'today?'"
                    ),
                    DialogueLine(
                        id = "c2",
                        speakerRole = "Customer",
                        isUserTurn = true,
                        text = "Hi, could I please get a medium oat milk latte to go?",
                        phoneticNote = "Link 'could I' -> 'cud-eye' smoothly.",
                        alternativeOptions = listOf("I'll have a medium iced latte, please.", "Can I get an espresso and a croissant?")
                    ),
                    DialogueLine(
                        id = "c3",
                        speakerRole = "Barista",
                        isUserTurn = false,
                        text = "Sure thing! Would you like that sweetened with vanilla or caramel syrup?",
                        phoneticNote = "Clear stress on 'vanilla' and 'caramel'."
                    ),
                    DialogueLine(
                        id = "c4",
                        speakerRole = "Customer",
                        isUserTurn = true,
                        text = "Just one pump of vanilla syrup would be great, thank you.",
                        phoneticNote = "Pronounce 'would be' softly and stress 'great'.",
                        alternativeOptions = listOf("No syrup for me, thanks.", "Just sugar free vanilla, please.")
                    ),
                    DialogueLine(
                        id = "c5",
                        speakerRole = "Barista",
                        isUserTurn = false,
                        text = "Got it! That will be four dollars and fifty cents. Will that be cash or card?",
                        phoneticNote = "Notice reduction: 'that will be' -> 'that'll be'."
                    ),
                    DialogueLine(
                        id = "c6",
                        speakerRole = "Customer",
                        isUserTurn = true,
                        text = "I'll tap with my phone, please. Keep the change!",
                        phoneticNote = "Natural idiomatic finish: 'keep the change'.",
                        alternativeOptions = listOf("I'll pay by card.", "Here is a five dollar bill.")
                    )
                )
            ),
            DialogueScenario(
                id = "job_interview",
                title = "Job Interview Intro",
                category = "Professional",
                difficulty = "Intermediate",
                iconName = "work",
                description = "Master your elevator pitch and answer the classic 'Tell me about yourself'.",
                userRoleName = "Candidate",
                partnerRoleName = "Interviewer",
                lines = listOf(
                    DialogueLine(
                        id = "j1",
                        speakerRole = "Interviewer",
                        isUserTurn = false,
                        text = "Good morning! Thank you for taking the time to speak with us today.",
                        phoneticNote = "Professional, warm greeting."
                    ),
                    DialogueLine(
                        id = "j2",
                        speakerRole = "Candidate",
                        isUserTurn = true,
                        text = "Thank you for having me. I'm really excited about this opportunity.",
                        phoneticNote = "Stress 'really excited' and 'opportunity' /ˌɒpərˈtuːnəti/.",
                        alternativeOptions = listOf("Good morning! It's a pleasure to meet you.", "Thank you, I'm glad to be here.")
                    ),
                    DialogueLine(
                        id = "j3",
                        speakerRole = "Interviewer",
                        isUserTurn = false,
                        text = "To start off, could you walk us through your professional background?",
                        phoneticNote = "Phrasal verb: 'walk us through' means summarize step by step."
                    ),
                    DialogueLine(
                        id = "j4",
                        speakerRole = "Candidate",
                        isUserTurn = true,
                        text = "Certainly! I have spent the last four years building high impact mobile solutions.",
                        phoneticNote = "Notice strong stress on 'certainly' and 'high impact'.",
                        alternativeOptions = listOf("Sure! My background is in software design and project delivery.")
                    ),
                    DialogueLine(
                        id = "j5",
                        speakerRole = "Interviewer",
                        isUserTurn = false,
                        text = "Impressive. What would you say is your greatest technical strength?",
                        phoneticNote = "Clear question cadence."
                    ),
                    DialogueLine(
                        id = "j6",
                        speakerRole = "Candidate",
                        isUserTurn = true,
                        text = "My strength is solving complex problems quickly and collaborating effectively with cross-functional teams.",
                        phoneticNote = "Pronounce 'collaborating' smoothly with stress on the second syllable.",
                        alternativeOptions = listOf("I excel at clean code architecture and mentoring team members.")
                    )
                )
            ),
            DialogueScenario(
                id = "airport_travel",
                title = "Airport Check-In & Gate",
                category = "Travel",
                difficulty = "Beginner",
                iconName = "flight",
                description = "Navigate passport control, checked luggage, and boarding announcements.",
                userRoleName = "Passenger",
                partnerRoleName = "Airline Agent",
                lines = listOf(
                    DialogueLine(
                        id = "a1",
                        speakerRole = "Airline Agent",
                        isUserTurn = false,
                        text = "Hello! May I see your passport and booking reference number?",
                        phoneticNote = "Polite standard inquiry."
                    ),
                    DialogueLine(
                        id = "a2",
                        speakerRole = "Passenger",
                        isUserTurn = true,
                        text = "Here you go. My passport and the digital confirmation on my phone.",
                        phoneticNote = "Natural response: 'Here you go' is idiomatic.",
                        alternativeOptions = listOf("Sure thing, here is my passport.")
                    ),
                    DialogueLine(
                        id = "a3",
                        speakerRole = "Airline Agent",
                        isUserTurn = false,
                        text = "Thank you. Do you have any bags to check in, or just carry-on luggage?",
                        phoneticNote = "Contrast 'check in' vs 'carry-on'."
                    ),
                    DialogueLine(
                        id = "a4",
                        speakerRole = "Passenger",
                        isUserTurn = true,
                        text = "I have one suitcase to check and one backpack as a personal item.",
                        phoneticNote = "Distinguish 'suitcase' and 'backpack'.",
                        alternativeOptions = listOf("Just one carry-on bag for today.")
                    ),
                    DialogueLine(
                        id = "a5",
                        speakerRole = "Airline Agent",
                        isUserTurn = false,
                        text = "Great! You are all set. Boarding starts at gate B twelve in thirty minutes.",
                        phoneticNote = "Important details: 'Gate B12' and 'thirty minutes'."
                    ),
                    DialogueLine(
                        id = "a6",
                        speakerRole = "Passenger",
                        isUserTurn = true,
                        text = "Thank you so much! Which way is security checkpoint B?",
                        phoneticNote = "Polite directional question.",
                        alternativeOptions = listOf("Thanks! Have a wonderful day.")
                    )
                )
            ),
            DialogueScenario(
                id = "restaurant_dinner",
                title = "Dinner at a Restaurant",
                category = "Dining",
                difficulty = "Intermediate",
                iconName = "food",
                description = "Ask for chef specials, mention allergies, and request separate checks.",
                userRoleName = "Guest",
                partnerRoleName = "Server",
                lines = listOf(
                    DialogueLine(
                        id = "r1",
                        speakerRole = "Server",
                        isUserTurn = false,
                        text = "Good evening! Can I interest you in our evening specials or a beverage?",
                        phoneticNote = "Warm hospitality opening."
                    ),
                    DialogueLine(
                        id = "r2",
                        speakerRole = "Guest",
                        isUserTurn = true,
                        text = "Good evening! What would you recommend for fresh seafood dishes?",
                        phoneticNote = "Stress 'recommend' /ˌrek.əˈmend/ on the third syllable.",
                        alternativeOptions = listOf("We are ready to order drinks first, please.")
                    ),
                    DialogueLine(
                        id = "r3",
                        speakerRole = "Server",
                        isUserTurn = false,
                        text = "The pan-seared salmon with roasted asparagus is outstanding tonight.",
                        phoneticNote = "Sensory vocabulary: 'pan-seared' and 'roasted'."
                    ),
                    DialogueLine(
                        id = "r4",
                        speakerRole = "Guest",
                        isUserTurn = true,
                        text = "That sounds delicious! Could we also get the dressing on the side?",
                        phoneticNote = "Common dining phrase: 'on the side'.",
                        alternativeOptions = listOf("I will have the grilled salmon, please.")
                    )
                )
            ),
            DialogueScenario(
                id = "doctor_visit",
                title = "Doctor's Appointment",
                category = "Health",
                difficulty = "Intermediate",
                iconName = "medical",
                description = "Express physical symptoms, pain level, and ask about side effects.",
                userRoleName = "Patient",
                partnerRoleName = "Doctor",
                lines = listOf(
                    DialogueLine(
                        id = "d1",
                        speakerRole = "Doctor",
                        isUserTurn = false,
                        text = "Hello! Come on in. What seems to be troubling you today?",
                        phoneticNote = "Standard clinical opening in English."
                    ),
                    DialogueLine(
                        id = "d2",
                        speakerRole = "Patient",
                        isUserTurn = true,
                        text = "I've had a sore throat and a persistent headache since Tuesday.",
                        phoneticNote = "Clear distinction: 'throat' /θroʊt/ and 'headache' /ˈhedeɪk/.",
                        alternativeOptions = listOf("I've been feeling feverish and exhausted lately.")
                    ),
                    DialogueLine(
                        id = "d3",
                        speakerRole = "Doctor",
                        isUserTurn = false,
                        text = "I see. Have you taken any over-the-counter medicine to relieve the symptoms?",
                        phoneticNote = "Compound term: 'over-the-counter' (OTC)."
                    ),
                    DialogueLine(
                        id = "d4",
                        speakerRole = "Patient",
                        isUserTurn = true,
                        text = "I took some ibuprofen yesterday, but the fever keeps coming back.",
                        phoneticNote = "Watch the pronunciation of 'ibuprofen' /ˌaɪbjuːˈproʊfən/.",
                        alternativeOptions = listOf("Just some tea and honey, but it hasn't helped much.")
                    )
                )
            )
        )
    }

    fun getPronunciationDrills(): List<PronunciationDrill> {
        return listOf(
            PronunciationDrill(
                id = "p1",
                category = SoundCategory.MINIMAL_PAIRS,
                title = "/θ/ vs /s/ (Voiceless TH vs S)",
                phoneticSymbol = "/θ/ vs /s/",
                targetSentence = "I think this old wooden boat might sink.",
                focusWords = listOf("think", "sink"),
                mouthGuide = "For /θ/ in 'think', place the tip of your tongue gently between your front teeth and blow air softly. For /s/ in 'sink', keep your tongue behind closed teeth."
            ),
            PronunciationDrill(
                id = "p2",
                category = SoundCategory.MINIMAL_PAIRS,
                title = "/v/ vs /w/ (V sound vs W sound)",
                phoneticSymbol = "/v/ vs /w/",
                targetSentence = "The green vine produced sweet winter wine.",
                focusWords = listOf("vine", "wine"),
                mouthGuide = "For /v/ in 'vine', touch your top teeth to your lower lip and vibrate your vocal cords. For /w/ in 'wine', round your lips like you are whistling without touching teeth."
            ),
            PronunciationDrill(
                id = "p3",
                category = SoundCategory.MINIMAL_PAIRS,
                title = "/r/ vs /l/ (Liquid Consonants)",
                phoneticSymbol = "/r/ vs /l/",
                targetSentence = "Turn right at the red traffic light.",
                focusWords = listOf("right", "light"),
                mouthGuide = "For /r/ in 'right', curl your tongue tip back without touching the roof of your mouth. For /l/ in 'light', firmly touch the tip to the alveolar ridge behind your top teeth."
            ),
            PronunciationDrill(
                id = "p4",
                category = SoundCategory.MINIMAL_PAIRS,
                title = "/iː/ vs /ɪ/ (Long E vs Short I)",
                phoneticSymbol = "/iː/ vs /ɪ/",
                targetSentence = "The fluffy sheep boarded the giant cruise ship.",
                focusWords = listOf("sheep", "ship"),
                mouthGuide = "For /iː/ in 'sheep', smile widely with tight lips. For /ɪ/ in 'ship', relax your mouth muscles and jaw slightly."
            ),
            PronunciationDrill(
                id = "p5",
                category = SoundCategory.WORD_STRESS,
                title = "Comfortable (Syllable Reduction)",
                phoneticSymbol = "ˈkʌmftəbl",
                targetSentence = "This modern sofa is exceptionally comfortable.",
                focusWords = listOf("comfortable", "exceptionally"),
                mouthGuide = "Native speakers reduce 'comfortable' to 3 syllables: 'KUMF-tuh-buhl'. Do not pronounce the middle 'or' as a separate syllable."
            ),
            PronunciationDrill(
                id = "p6",
                category = SoundCategory.WORD_STRESS,
                title = "Photograph vs Photographer",
                phoneticSymbol = "ˈfoʊtəɡræf / fəˈtɑːɡrəfər",
                targetSentence = "The skilled photographer took a stunning photograph.",
                focusWords = listOf("photographer", "photograph"),
                mouthGuide = "Notice the stress shift! PHO-tograph (stress on 1st syllable) vs pho-TO-grapher (stress on 2nd syllable with schwa at start)."
            ),
            PronunciationDrill(
                id = "p7",
                category = SoundCategory.CONNECTED_SPEECH,
                title = "Going to -> Gonna (Elision & Linking)",
                phoneticSymbol = "ˈɡɔːnə",
                targetSentence = "What are you going to do this weekend?",
                focusWords = listOf("going", "to", "what", "are"),
                mouthGuide = "In fast natural speech, 'What are you going to' often blends into 'Whatcha gonna'. Practice both the clear and linked version."
            ),
            PronunciationDrill(
                id = "p8",
                category = SoundCategory.CONNECTED_SPEECH,
                title = "Consonant-to-Vowel Linking",
                phoneticSymbol = "lɪŋk.ɪŋ",
                targetSentence = "Could you pick it up and turn it off?",
                focusWords = listOf("pick", "it", "up", "turn", "off"),
                mouthGuide = "Link ending consonants to the next vowel: 'pi-ki-tup' and 'tur-ni-toff'. This creates the smooth, fluid rhythm of fluent English."
            )
        )
    }

    fun getPhrases(): List<PhraseItem> {
        val bookmarks = _bookmarkedPhraseIds.value
        val raw = listOf(
            PhraseItem(
                id = "ph1",
                category = "Business & Professional",
                englishText = "Let's touch base next week to align on priorities.",
                phoneticHint = "tʌtʃ beɪs",
                meaning = "Briefly talk or meet to discuss progress or updates.",
                exampleUsage = "I have a tight deadline today, so let's touch base next Tuesday."
            ),
            PhraseItem(
                id = "ph2",
                category = "Business & Professional",
                englishText = "Could you give me a ballpark estimate for this project?",
                phoneticHint = "ˈbɔːl.pɑːrk ˈes.tə.mət",
                meaning = "An approximate or rough calculation.",
                exampleUsage = "I don't need exact figures right now, just a ballpark estimate."
            ),
            PhraseItem(
                id = "ph3",
                category = "Business & Professional",
                englishText = "I'd love to circle back on this once we have more data.",
                phoneticHint = "ˈsɜːr.kəl bæk",
                meaning = "Return to an unresolved discussion at a later time.",
                exampleUsage = "Good question. Let's circle back after reviewing customer feedback."
            ),
            PhraseItem(
                id = "ph4",
                category = "Everyday Chit-Chat",
                englishText = "Long time no see! What have you been up to lately?",
                phoneticHint = "lɔːŋ taɪm noʊ siː",
                meaning = "Friendly way to greet someone you haven't seen in a while.",
                exampleUsage = "Hey Sarah! Long time no see! What have you been up to?"
            ),
            PhraseItem(
                id = "ph5",
                category = "Everyday Chit-Chat",
                englishText = "I couldn't agree more with your perspective on that.",
                phoneticHint = "aɪ ˈkʊdnt əˈɡriː mɔːr",
                meaning = "Polite and strong way to express total agreement.",
                exampleUsage = "That movie was brilliant. I couldn't agree more!"
            ),
            PhraseItem(
                id = "ph6",
                category = "Idioms & Native Slang",
                englishText = "Don't beat around the bush; just tell me what happened.",
                phoneticHint = "biːt əˈraʊnd ðə bʊʃ",
                meaning = "Avoid speaking directly or delay getting to the main point.",
                exampleUsage = "Stop beating around the bush and give us the bottom line."
            ),
            PhraseItem(
                id = "ph7",
                category = "Idioms & Native Slang",
                englishText = "It is completely up in the air until the manager confirms.",
                phoneticHint = "ʌp ɪn ði er",
                meaning = "Uncertain, undecided, or still pending a final decision.",
                exampleUsage = "Our travel plans for December are still up in the air."
            ),
            PhraseItem(
                id = "ph8",
                category = "Travel & Emergencies",
                englishText = "Excuse me, could you point me in the direction of the subway station?",
                phoneticHint = "pɔɪnt miː ɪn ðə dəˈrek.ʃən",
                meaning = "Polite way to ask a passerby for directions.",
                exampleUsage = "I am a bit lost. Could you point me toward Central Park?"
            ),
            PhraseItem(
                id = "ph9",
                category = "Travel & Emergencies",
                englishText = "Is there an all-night pharmacy in the immediate vicinity?",
                phoneticHint = "ɪˈmiːdiət vəˈsɪnəti",
                meaning = "In the immediate nearby neighborhood or area.",
                exampleUsage = "I need cough syrup urgently. Is there a pharmacy in the vicinity?"
            )
        )
        return raw.map { it.copy(isBookmarked = bookmarks.contains(it.id)) }
    }
}
