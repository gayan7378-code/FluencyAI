package com.example.data.model

object NativeCallData {
    val personas: List<CallPersona> = listOf(
        CallPersona(
            id = "marcus_us",
            name = "Marcus Vance",
            roleTitle = "VP of Product • New York",
            accentCode = "US",
            accentLabel = "American (US)",
            avatarEmoji = "🗽",
            bio = "Energetic, clear General American cadence with crisp consonants and natural business slang."
        ),
        CallPersona(
            id = "emma_uk",
            name = "Emma Kensington",
            roleTitle = "Linguistics Lecturer • Oxford",
            accentCode = "UK",
            accentLabel = "British (UK)",
            avatarEmoji = "🇬🇧",
            bio = "Warm, elegant Received Pronunciation with non-rhotic vowels and articulate British idioms."
        ),
        CallPersona(
            id = "liam_aus",
            name = "Liam Gallagher",
            roleTitle = "Creative Director • Sydney",
            accentCode = "AUS",
            accentLabel = "Australian (AUS)",
            avatarEmoji = "🦘",
            bio = "Laid-back Aussie banter, friendly conversational flow, and authentic Australian expressions."
        ),
        CallPersona(
            id = "sophia_us",
            name = "Dr. Sophia Martinez",
            roleTitle = "Executive Career Coach • San Francisco",
            accentCode = "US",
            accentLabel = "American (US)",
            avatarEmoji = "🌉",
            bio = "Polished Silicon Valley style, highly encouraging, focusing on professional clarity."
        ),
        CallPersona(
            id = "oliver_uk",
            name = "Oliver Bennett",
            roleTitle = "Cultural Diplomat • London",
            accentCode = "UK",
            accentLabel = "British (UK)",
            avatarEmoji = "👑",
            bio = "Polite, structured British conversational style, perfect for formal and diplomatic English."
        ),
        CallPersona(
            id = "chloe_aus",
            name = "Chloe Mitchell",
            roleTitle = "Travel & Culture Host • Melbourne",
            accentCode = "AUS",
            accentLabel = "Australian (AUS)",
            avatarEmoji = "🏄‍♀️",
            bio = "Vibrant, friendly, enthusiastic Australian English with relaxed diphthongs and great humor."
        )
    )

    val callScenarios: List<CallScenario> = listOf(
        CallScenario(
            id = "call_coffee",
            title = "Casual Catch-Up Over Coffee",
            category = "Social",
            description = "Informal phone chat catching up with a friend about life, work, and plans.",
            persona = personas[0],
            initialLine = "Hey there! Thanks for hopping on the call. How has your week been treating you?",
            suggestedTopics = listOf("Weekend road trip", "A good book or movie", "Work-life balance", "Cooking recipes")
        ),
        CallScenario(
            id = "call_interview",
            title = "Executive Job Interview Screen",
            category = "Career",
            description = "High-stakes phone screen discussing leadership, system architecture, and career milestones.",
            persona = personas[3],
            initialLine = "Hello! Thanks for taking the time for our preliminary interview. How are you doing today?",
            suggestedTopics = listOf("Walk me through your resume", "A challenging project failure", "Your leadership style", "Salary expectations")
        ),
        CallScenario(
            id = "call_travel",
            title = "Airport Customs & Border Interview",
            category = "Travel",
            description = "Fast-paced questions at passport control regarding itinerary, stay duration, and funds.",
            persona = personas[4],
            initialLine = "Good afternoon. Please step up to the booth. What is the reason for entering the country today?",
            suggestedTopics = listOf("Conference invitation", "Proof of return ticket", "Hotel booking details", "Currency on hand")
        ),
        CallScenario(
            id = "call_startup",
            title = "Venture Pitch & Product Strategy",
            category = "Business",
            description = "Present your business model, traction, and competitive moat to an angel investor.",
            persona = personas[1],
            initialLine = "Good day! I've been looking over your executive summary. Why don't you pitch me your core value proposition in two minutes?",
            suggestedTopics = listOf("Customer acquisition cost", "Competitive advantage", "Target market size", "Monetization strategy")
        ),
        CallScenario(
            id = "call_pub_banter",
            title = "London Pub or Aussie BBQ Banter",
            category = "Culture & Slang",
            description = "Master native colloquialisms, funny cultural references, and spontaneous humor.",
            persona = personas[2],
            initialLine = "G'day mate! Glad you could make it. Grab a seat! What's the latest goss from your side of town?",
            suggestedTopics = listOf("Favorite sports teams", "Best local food spots", "Crazy weather changes", "Holiday travel ideas")
        )
    )
}
