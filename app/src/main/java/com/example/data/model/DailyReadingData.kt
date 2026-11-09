package com.example.data.model

object DailyReadingData {
    val excerpts: List<BookExcerpt> = listOf(
        BookExcerpt(
            id = "b1",
            bookTitle = "Stanford Commencement Address",
            author = "Steve Jobs",
            category = "Inspirational",
            passageText = "Your time is limited, so don't waste it living someone else's life. Don't be trapped by dogma, which is living with the results of other people's thinking. Don't let the noise of others' opinions drown out your own inner voice. And most important, have the courage to follow your heart and intuition.",
            targetWpm = 135,
            challengingWords = listOf(
                "dogma" to "ˈdɔːɡ.mə",
                "intuition" to "ˌɪn.tuːˈɪʃ.ən",
                "courage" to "ˈkɜːr.ɪdʒ"
            )
        ),
        BookExcerpt(
            id = "b2",
            bookTitle = "The Great Gatsby",
            author = "F. Scott Fitzgerald",
            category = "Classic Literature",
            passageText = "In my younger and more vulnerable years my father gave me some advice that I've been turning over in my mind ever since. Whenever you feel like criticizing anyone, he told me, just remember that all the people in this world haven't had the advantages that you've had.",
            targetWpm = 130,
            challengingWords = listOf(
                "vulnerable" to "ˈvʌl.nər.ə.bəl",
                "criticizing" to "ˈkrɪt.ɪ.saɪ.zɪŋ",
                "advantages" to "ədˈvæn.tɪ.dʒɪz"
            )
        ),
        BookExcerpt(
            id = "b3",
            bookTitle = "Atomic Habits",
            author = "James Clear",
            category = "Self-Improvement",
            passageText = "Habits are the compound interest of self-improvement. The same way that money multiplies through compound interest, the effects of your habits multiply as you repeat them. They seem to make little difference on any given day, and yet the impact they deliver over the months and years can be enormous.",
            targetWpm = 140,
            challengingWords = listOf(
                "compound" to "ˈkɑːm.paʊnd",
                "multiplies" to "ˈmʌl.tə.plaɪz",
                "enormous" to "ɪˈnɔːr.məs"
            )
        ),
        BookExcerpt(
            id = "b4",
            bookTitle = "To Kill a Mockingbird",
            author = "Harper Lee",
            category = "Literature",
            passageText = "You never really understand a person until you consider things from his point of view, until you climb into his skin and walk around in it. Real courage is when you know you're licked before you begin, but you begin anyway and see it through no matter what.",
            targetWpm = 125,
            challengingWords = listOf(
                "consider" to "kənˈsɪd.ər",
                "courage" to "ˈkɜːr.ɪdʒ",
                "understand" to "ˌʌn.dɚˈstænd"
            )
        ),
        BookExcerpt(
            id = "b5",
            bookTitle = "A Brief History of Time",
            author = "Stephen Hawking",
            category = "Science",
            passageText = "We find ourselves in a bewildering world. We want to make sense of what we see around us and to ask: What is the nature of the universe? What is our place in it and where did it and we come from? Why is it the way it is?",
            targetWpm = 120,
            challengingWords = listOf(
                "bewildering" to "bɪˈwɪl.dɚ.ɪŋ",
                "universe" to "ˈjuː.nə.vɝːs",
                "nature" to "ˈneɪ.tʃɚ"
            )
        )
    )
}
