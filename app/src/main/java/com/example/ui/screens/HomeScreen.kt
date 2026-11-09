package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SpeakingStats
import com.example.ui.AppScreen
import com.example.ui.SpeakFlowViewModel
import com.example.ui.components.VoiceSettingsBar
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryBlue

@Composable
fun HomeScreen(
    viewModel: SpeakFlowViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.stats.collectAsState()
    val speed by viewModel.currentSpeed.collectAsState()
    val isBritish by viewModel.isBritishAccent.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Top Welcome Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FluencyAI 🎙️",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Conversational Fluency • AI Roleplay • Voice Coaching",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Daily Streak Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(AccentAmber.copy(alpha = 0.15f))
                        .border(1.dp, AccentAmber.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🔥", fontSize = 16.sp)
                        Text(
                            text = "${stats.streakDays} Day Streak",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AccentAmber
                        )
                    }
                }
            }
        }

        // Voice Accent & Speed Controls
        item {
            VoiceSettingsBar(
                currentSpeed = speed,
                isBritishAccent = isBritish,
                onSpeedChange = { viewModel.setSpeechSpeed(it) },
                onAccentChange = { viewModel.setAccent(it) }
            )
        }

        // Hero Feature 1: AI Roleplay Chat & Sinhala Grammar Corrections
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                PrimaryBlue,
                                Color(0xFF1D4ED8),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .clickable { viewModel.navigateTo(AppScreen.ROLEPLAY_CHAT) }
                    .testTag("hero_roleplay_chat_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "TWO-WAY AI ROLEPLAY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🇱🇰 Sinhala Corrections",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF6EE7B7)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Dynamic Scenario Chat & Corrections",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Select your level (Beginner / Intermediate / Advanced). Talk back-and-forth across 7 realistic scenarios with instant Sinhala grammar explanations and syllable stress guides.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Start AI Conversation",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryBlue
                        )
                    }
                }
            }
        }

        // Hero Feature 2: Native Speaker Practice (Voice Call)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .clickable { viewModel.navigateTo(AppScreen.NATIVE_CALL) }
                    .testTag("hero_native_call_card")
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(AccentGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Native Speaker Practice",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "LIVE CALL",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = AccentGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Simulated phone calls with US 🇺🇸, UK 🇬🇧 & AUS 🇦🇺 personas, audio waveform, and post-call accent coaching.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Stats Overview Row
        item {
            SpeakingStatsRow(stats = stats)
        }

        // Section Title: Powerful Practice Modes
        item {
            Text(
                text = "Interactive Practice Studios",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // 4 Studio Feature Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PracticeModuleCard(
                        title = "Tongue Twisters",
                        subtitle = "Agility lab with live green word highlighting & WPM",
                        icon = Icons.Default.Bolt,
                        accentColor = AccentRose,
                        badgeText = "Speed Demon",
                        modifier = Modifier.weight(1f),
                        testTag = "nav_tongue_twisters_card",
                        onClick = { viewModel.navigateTo(AppScreen.TONGUE_TWISTERS) }
                    )

                    PracticeModuleCard(
                        title = "AI Presentation",
                        subtitle = "Slide studio with 10-point scorecard & progress graph",
                        icon = Icons.Default.Slideshow,
                        accentColor = PrimaryBlue,
                        badgeText = "Pitch Studio",
                        modifier = Modifier.weight(1f),
                        testTag = "nav_presentation_card",
                        onClick = { viewModel.navigateTo(AppScreen.PRESENTATION) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PracticeModuleCard(
                        title = "Book Reading",
                        subtitle = "Read Steve Jobs, Gatsby & literature aloud",
                        icon = Icons.Default.Book,
                        accentColor = AccentAmber,
                        badgeText = "Classic Speech",
                        modifier = Modifier.weight(1f),
                        testTag = "nav_book_reading_card",
                        onClick = { viewModel.navigateTo(AppScreen.BOOK_READING) }
                    )

                    PracticeModuleCard(
                        title = "Pronounce Lab",
                        subtitle = "Minimal pairs (/θ/ vs /s/), stress & vocal mechanics",
                        icon = Icons.Default.RecordVoiceOver,
                        accentColor = AccentGreen,
                        badgeText = "Phonetics",
                        modifier = Modifier.weight(1f),
                        testTag = "nav_pronunciation_card",
                        onClick = { viewModel.navigateTo(AppScreen.PRONUNCIATION) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PracticeModuleCard(
                        title = "Phrasebook & Idioms",
                        subtitle = "Business phrases, chit-chat & native slang",
                        icon = Icons.Default.Translate,
                        accentColor = Color(0xFF8B5CF6),
                        badgeText = "Audio Cards",
                        modifier = Modifier.weight(1f),
                        testTag = "nav_phrasebook_card",
                        onClick = { viewModel.navigateTo(AppScreen.PHRASEBOOK) }
                    )

                    PracticeModuleCard(
                        title = "Free Speech Coach",
                        subtitle = "Speaking meter with WPM & filler word counter",
                        icon = Icons.Default.GraphicEq,
                        accentColor = Color(0xFF06B6D4),
                        badgeText = "Voice Meter",
                        modifier = Modifier.weight(1f),
                        testTag = "nav_free_speech_card",
                        onClick = { viewModel.navigateTo(AppScreen.FREE_SPEECH) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SpeakingStatsRow(stats: SpeakingStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            value = "${stats.averageAccuracy}%",
            label = "Avg Accuracy",
            icon = Icons.Default.Star,
            color = AccentGreen,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = "${stats.totalPhrasesPracticed}",
            label = "Phrases Spoken",
            icon = Icons.Default.Mic,
            color = PrimaryBlue,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = "${stats.totalSpeakingMinutes}m",
            label = "Practice Time",
            icon = Icons.Default.School,
            color = AccentAmber,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.border(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
            RoundedCornerShape(14.dp)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PracticeModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    badgeText: String,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}
