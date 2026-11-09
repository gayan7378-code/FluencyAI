package com.example.data.model

object PresentationData {
    val sampleTopics: List<PresentationTopic> = listOf(
        PresentationTopic(
            id = "topic_cloud",
            title = "Scaling Resilient Cloud Architecture",
            category = "Technology",
            iconEmoji = "☁️",
            slides = listOf(
                PresentationSlide(
                    slideNumber = 1,
                    title = "Modern Distributed Cloud Foundations",
                    bulletPoints = listOf(
                        "Decoupling monolithic systems into microservices",
                        "High-availability multi-region active replication",
                        "Zero-downtime automated deployment pipelines"
                    ),
                    speakerDeliveryNotes = "Project your voice confidently. Emphasize 'resilience' and 'zero-downtime'. Maintain a calm, executive pace.",
                    suggestedVocabulary = listOf("Infrastructure", "Fault-tolerant", "Scalability", "Throughput")
                ),
                PresentationSlide(
                    slideNumber = 2,
                    title = "Mitigating Latency & Network Bottlenecks",
                    bulletPoints = listOf(
                        "Edge caching with CDN points of presence",
                        "Database connection pooling and read replicas",
                        "Graceful degradation during traffic spikes"
                    ),
                    speakerDeliveryNotes = "Pause for two seconds after key metrics. Use rising intonation when presenting architectural trade-offs.",
                    suggestedVocabulary = listOf("Latency", "Bottleneck", "Degradation", "Concurrent")
                ),
                PresentationSlide(
                    slideNumber = 3,
                    title = "Key Takeaways & Future Outlook",
                    bulletPoints = listOf(
                        "Autonomous self-healing infrastructure",
                        "Cost optimization via intelligent auto-scaling",
                        "Next steps for engineering roadmap"
                    ),
                    speakerDeliveryNotes = "Conclude with an inspiring, authoritative summary. Invite questions with a friendly open gesture.",
                    suggestedVocabulary = listOf("Optimization", "Predictive", "Milestone", "Strategic")
                )
            )
        ),
        PresentationTopic(
            id = "topic_pitch",
            title = "Pitch Deck: AI-Powered Language Learning",
            category = "Startup",
            iconEmoji = "🚀",
            slides = listOf(
                PresentationSlide(
                    slideNumber = 1,
                    title = "The $60 Billion Speaking Barrier",
                    bulletPoints = listOf(
                        "1.5 billion people learning English worldwide",
                        "90% struggle with oral fluency and speaking confidence",
                        "Private tutors are unaffordable for mainstream students"
                    ),
                    speakerDeliveryNotes = "Hook the audience immediately with the massive market size. Connect emotionally with the learner's struggle.",
                    suggestedVocabulary = listOf("Accessibility", "Prohibitive", "Frustration", "Disruption")
                ),
                PresentationSlide(
                    slideNumber = 2,
                    title = "Our Proprietary Voice Engine",
                    bulletPoints = listOf(
                        "Real-time acoustic phonetic feedback in milliseconds",
                        "Adaptive AI roleplay matching learner proficiency",
                        "Proven 3x faster fluency progression in beta testing"
                    ),
                    speakerDeliveryNotes = "Speak with high energy and conviction. Highlight technical superiority and concrete beta metrics.",
                    suggestedVocabulary = listOf("Acoustic", "Retention", "Proprietary", "Exponential")
                ),
                PresentationSlide(
                    slideNumber = 3,
                    title = "Traction & Investment Opportunity",
                    bulletPoints = listOf(
                        "45% month-over-month organic subscriber growth",
                        "Seeking $2M seed round to accelerate international expansion",
                        "Experienced founding team from Google and Duolingo"
                    ),
                    speakerDeliveryNotes = "Deliver the ask with clarity. Conclude with a strong vision of democratizing English fluency globally.",
                    suggestedVocabulary = listOf("Capital", "Expansion", "Monetization", "Visionary")
                )
            )
        ),
        PresentationTopic(
            id = "topic_climate",
            title = "Clean Energy: The 2030 Transition",
            category = "Sustainability",
            iconEmoji = "🌱",
            slides = listOf(
                PresentationSlide(
                    slideNumber = 1,
                    title = "The Renewable Acceleration",
                    bulletPoints = listOf(
                        "Solar and wind now the cheapest forms of new energy",
                        "Grid-scale battery storage capacity tripled since 2022",
                        "Policy shifts accelerating private sector investment"
                    ),
                    speakerDeliveryNotes = "Set a serious but optimistic tone. Emphasize positive economic tipping points rather than fear.",
                    suggestedVocabulary = listOf("Accelerating", "Subsidies", "Decarbonization", "Capacity")
                ),
                PresentationSlide(
                    slideNumber = 2,
                    title = "Overcoming Grid Modernization Challenges",
                    bulletPoints = listOf(
                        "Transmission infrastructure bottlenecks",
                        "Intermittency solutions via smart distributed grids",
                        "Regulatory permitting reform for high-voltage corridors"
                    ),
                    speakerDeliveryNotes = "Enunciate technical terms clearly. Use structured transitions like 'First... In addition... Ultimately...'",
                    suggestedVocabulary = listOf("Intermittency", "Modernization", "Corridors", "Transmission")
                )
            )
        )
    )
}
