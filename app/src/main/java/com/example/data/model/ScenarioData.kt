package com.example.data.model

object ScenarioData {
    val allScenarios: List<AiScenario> = listOf(
        AiScenario(
            id = "casual_coffee",
            title = "Casual Daily Chat",
            category = "Daily Life",
            iconEmoji = "☕",
            description = "Friendly everyday conversation catching up with a friend over coffee or tea.",
            aiRole = "Close Friend",
            userRole = "Friend",
            initialGreeting = "Hey! It's so great to see you today! How has your week been going so far?",
            suggestedTopics = listOf("Weekend plans", "Favorite weekend hobbies", "Recent movies or shows", "Cooking dinner")
        ),
        AiScenario(
            id = "job_interview",
            title = "Job Interview",
            category = "Career",
            iconEmoji = "💼",
            description = "Professional behavioral and technical interview with a senior hiring manager.",
            aiRole = "Hiring Manager",
            userRole = "Job Candidate",
            initialGreeting = "Good morning! Thanks for meeting with me today. To get started, could you introduce yourself and tell me about your background?",
            suggestedTopics = listOf("Work experience", "Overcoming a major challenge", "Strengths and weaknesses", "Why this company")
        ),
        AiScenario(
            id = "restaurant_dining",
            title = "Restaurant Ordering",
            category = "Dining",
            iconEmoji = "🍽️",
            description = "Order gourmet food, ask for dietary adjustments, and request the check politely.",
            aiRole = "Bistro Server",
            userRole = "Dinner Guest",
            initialGreeting = "Good evening! Welcome to The Olive Grove. Can I get you started with something refreshing to drink while you look over our menu?",
            suggestedTopics = listOf("Chef recommendations", "Vegetarian or gluten-free options", "Beverage pairings", "Splitting the bill")
        ),
        AiScenario(
            id = "airport_customs",
            title = "Airport & Travel Customs",
            category = "Travel",
            iconEmoji = "✈️",
            description = "Clear immigration, answer passport control questions, and state your travel purpose.",
            aiRole = "Immigration Officer",
            userRole = "International Traveler",
            initialGreeting = "Passport and landing card, please. What is the primary purpose of your visit, and how long do you plan to stay?",
            suggestedTopics = listOf("Purpose of trip", "Hotel accommodation", "Length of stay", "Customs declarations")
        ),
        AiScenario(
            id = "shopping_boutique",
            title = "Shopping & Boutique",
            category = "Shopping",
            iconEmoji = "🛍️",
            description = "Ask for sizes, colors, fabric advice, and discuss return policies with a stylist.",
            aiRole = "Store Associate",
            userRole = "Shopper",
            initialGreeting = "Hello! Welcome to Trend & Tailor. Are you looking for anything specific today, or just browsing our new seasonal collection?",
            suggestedTopics = listOf("Trying a different size", "Available color variants", "Discount promotions", "Exchange policies")
        ),
        AiScenario(
            id = "medical_clinic",
            title = "Medical Clinic Consultation",
            category = "Health",
            iconEmoji = "🩺",
            description = "Explain physical symptoms, describe pain intensity, and ask about dosage side effects.",
            aiRole = "Attending Physician",
            userRole = "Patient",
            initialGreeting = "Hello! Please take a seat. What brings you in to see me today? What symptoms have you been experiencing?",
            suggestedTopics = listOf("Describing symptom timeline", "Pain rating 1 to 10", "Medication allergies", "Prescription directions")
        ),
        AiScenario(
            id = "networking_mixer",
            title = "Networking Mixer",
            category = "Professional",
            iconEmoji = "🤝",
            description = "Connect with peers at an industry conference, share elevator pitches, and exchange contacts.",
            aiRole = "Industry Colleague",
            userRole = "Professional Attendee",
            initialGreeting = "Hi there! Enjoying the keynote? I'm Alex from CloudSphere. What branch of the tech industry are you working in?",
            suggestedTopics = listOf("Current industry trends", "Company initiatives", "Exchanging LinkedIn info", "Conference takeaways")
        )
    )
}
