package com.example.data.model

object TongueTwisterData {
    val twisters: List<TongueTwister> = listOf(
        // Warm-Up (Beginner)
        TongueTwister(
            id = "w1",
            difficulty = TwisterDifficulty.WARM_UP,
            title = "Red Lorry, Yellow Lorry",
            targetSentence = "Red lorry, yellow lorry, red lorry, yellow lorry.",
            targetWpm = 120,
            focusPhonemes = "/r/ vs /l/",
            articulatoryTip = "Keep your tongue tip raised for /l/, but curl it back slightly without touching the alveolar ridge for /r/."
        ),
        TongueTwister(
            id = "w2",
            difficulty = TwisterDifficulty.WARM_UP,
            title = "Unique New York",
            targetSentence = "You know you need unique New York, you know you need unique New York.",
            targetWpm = 115,
            focusPhonemes = "/juː/ & /k/ velar stops",
            articulatoryTip = "Ensure clean separation between the palatal glide /j/ and nasal /n/ in 'unique New York'."
        ),
        TongueTwister(
            id = "w3",
            difficulty = TwisterDifficulty.WARM_UP,
            title = "She Sees Cheese",
            targetSentence = "I slit the sheet, the sheet I slit, and on the slitted sheet I sit.",
            targetWpm = 130,
            focusPhonemes = "/s/ vs /ʃ/ & /ɪ/ vs /iː/",
            articulatoryTip = "Distinguish the short vowel /ɪ/ in 'slit' and 'sit' from the long vowel /iː/ in 'sheet'."
        ),

        // Agility (Intermediate)
        TongueTwister(
            id = "a1",
            difficulty = TwisterDifficulty.AGILITY,
            title = "Peter Piper",
            targetSentence = "Peter Piper picked a peck of pickled peppers. How many pickled peppers did Peter Piper pick?",
            targetWpm = 145,
            focusPhonemes = "Aspirated /p/ plosive",
            articulatoryTip = "Release a tiny puff of air with each bilabial plosive /p/ to achieve crisp native diction."
        ),
        TongueTwister(
            id = "a2",
            difficulty = TwisterDifficulty.AGILITY,
            title = "How Much Wood",
            targetSentence = "How much wood would a woodchuck chuck if a woodchuck could chuck wood?",
            targetWpm = 140,
            focusPhonemes = "Labio-velar /w/ & /tʃ/",
            articulatoryTip = "Round your lips tightly for /w/, then press the tongue blade firmly to the palate for affricate /tʃ/."
        ),
        TongueTwister(
            id = "a3",
            difficulty = TwisterDifficulty.AGILITY,
            title = "Fuzzy Wuzzy",
            targetSentence = "Fuzzy Wuzzy was a bear. Fuzzy Wuzzy had no hair. Fuzzy Wuzzy wasn't very fuzzy, was he?",
            targetWpm = 140,
            focusPhonemes = "Voiced /z/ sibilant",
            articulatoryTip = "Keep the vocal cords vibrating continuously through the /z/ sounds in 'Fuzzy' and 'wasn't'."
        ),

        // Master (Advanced)
        TongueTwister(
            id = "m1",
            difficulty = TwisterDifficulty.MASTER,
            title = "Seashells on the Seashore",
            targetSentence = "She sells seashells by the seashore. The shells she sells are surely seashells.",
            targetWpm = 160,
            focusPhonemes = "Alternating /s/ and /ʃ/",
            articulatoryTip = "Retract your tongue slightly for postalveolar /ʃ/ (sh), then bring it forward behind top teeth for alveolar /s/."
        ),
        TongueTwister(
            id = "m2",
            difficulty = TwisterDifficulty.MASTER,
            title = "Thirty-Three Thieves",
            targetSentence = "The thirty-three thieves thought that they thrilled the throne throughout Thursday.",
            targetWpm = 150,
            focusPhonemes = "Interdental /θ/ vs /ð/",
            articulatoryTip = "Gently touch your tongue tip between your upper and lower incisors for voiceless /θ/ in 'thirty-three'."
        ),
        TongueTwister(
            id = "m3",
            difficulty = TwisterDifficulty.MASTER,
            title = "Betty Botter's Butter",
            targetSentence = "Betty Botter bought some butter, but she said the butter's bitter. If I put it in my batter, it will make my batter bitter.",
            targetWpm = 165,
            focusPhonemes = "Alveolar flap /ɾ/ (American T)",
            articulatoryTip = "Lightly flap the tongue tip against the gum ridge between vowels in 'Betty', 'Botter', and 'butter'."
        ),

        // Speed Demon (Extreme)
        TongueTwister(
            id = "s1",
            difficulty = TwisterDifficulty.SPEED_DEMON,
            title = "Pad Kid Poured Curd (MIT Ranked #1)",
            targetSentence = "Pad kid poured curd pulled cod. Pad kid poured curd pulled cod.",
            targetWpm = 180,
            focusPhonemes = "Rapid /p/, /k/, /d/ coda switching",
            articulatoryTip = "Ranked the hardest phrase by MIT researchers! Control your tongue back-to-front transition without dropping plosives."
        ),
        TongueTwister(
            id = "s2",
            difficulty = TwisterDifficulty.SPEED_DEMON,
            title = "The Sixth Sick Sheik",
            targetSentence = "The sixth sick sheik's sixth sheep's sick.",
            targetWpm = 175,
            focusPhonemes = "Quadruple cluster /ksθs/",
            articulatoryTip = "Navigate the extreme consonant cluster /ksθ/ in 'sixth' followed immediately by /s/ in 'sick'."
        ),
        TongueTwister(
            id = "s3",
            difficulty = TwisterDifficulty.SPEED_DEMON,
            title = "Through Three Cheese Trees",
            targetSentence = "Through three cheese trees three free fleas flew. While these fleas flew, freezy breeze blew.",
            targetWpm = 185,
            focusPhonemes = "Liquid blends /θr/, /fr/, /fl/, /bl/",
            articulatoryTip = "Maintain breath support while shifting between dental /θ/ to rhotic /r/ and bilabial /b/ to lateral /l/."
        )
    )
}
