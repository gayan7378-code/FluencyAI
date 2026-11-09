package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PresentationTopic
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.PresentationProgressGraph
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.PresentationViewModel

@Composable
fun PresentationScreen(
    viewModel: PresentationViewModel,
    modifier: Modifier = Modifier
) {
    val topics = viewModel.topics
    val selectedTopic by viewModel.selectedTopic.collectAsState()
    val currentSlideIndex by viewModel.currentSlideIndex.collectAsState()
    val isPresenting by viewModel.isPresenting.collectAsState()
    val seconds by viewModel.presentationSeconds.collectAsState()
    val liveTranscript by viewModel.liveTranscript.collectAsState()
    val evaluation by viewModel.evaluation.collectAsState()
    val scoreHistory by viewModel.scoreHistory.collectAsState()
    val soundLevel by viewModel.speechManager.soundLevel.collectAsState()

    val currentSlide = selectedTopic.slides.getOrNull(currentSlideIndex) ?: selectedTopic.slides.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AI Presentation Studio 🎙️",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Present slides aloud and receive a comprehensive 10-point evaluation",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Topic Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                topics.forEach { topic ->
                    val isSelected = topic.id == selectedTopic.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) PrimaryBlue
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                1.dp,
                                if (isSelected) PrimaryBlue
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectTopic(topic) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = topic.iconEmoji, fontSize = 14.sp)
                            Text(
                                text = topic.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Live Slide Viewer Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Slide header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryBlue.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SLIDE ${currentSlide.slideNumber} OF ${selectedTopic.slides.size}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryBlue
                            )
                        }

                        // Presentation Timer
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (isPresenting) AccentRose else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            val m = seconds / 60
                            val s = seconds % 60
                            Text(
                                text = String.format("%02d:%02d", m, s),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isPresenting) AccentRose else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentSlide.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Slide Key Bullet Points
                    currentSlide.bulletPoints.forEach { point ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "•", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                            Text(
                                text = point,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Speaker Delivery Notes Box
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Speaker Delivery Notes 🗣️",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AccentAmber
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentSlide.speakerDeliveryNotes,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Suggested Key Vocabulary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Key Vocab:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        currentSlide.suggestedVocabulary.forEach { vocab ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PrimaryBlue.copy(alpha = 0.1f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = vocab,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Slide Navigation Arrows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.prevSlide() },
                            enabled = currentSlideIndex > 0
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Previous Slide", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.nextSlide() },
                            enabled = currentSlideIndex < selectedTopic.slides.size - 1
                        ) {
                            Text("Next Slide", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Audio Waveform
                    AudioWaveformVisualizer(
                        isListening = isPresenting,
                        isSpeaking = false,
                        soundLevel = soundLevel,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = if (isPresenting) AccentRose else PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Big Present / Finish Button
                    Button(
                        onClick = {
                            if (isPresenting) viewModel.finishPresentation()
                            else viewModel.startPresentation()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPresenting) AccentRose else PrimaryBlue
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("present_record_button")
                    ) {
                        Icon(
                            imageVector = if (isPresenting) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPresenting) "Finish & Evaluate Presentation" else "Start Presenting with Microphone",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }

                    // Test evaluation button for emulators
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            viewModel.finishPresentation("In today's presentation on ${selectedTopic.title}, we explored high-performance architecture, latency mitigation, and scalability.")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simulate Presentation Scoring (for emulator)", fontSize = 11.sp)
                    }
                }
            }
        }

        // Comprehensive 10-Point Evaluation Engine Card
        if (evaluation != null) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, AccentGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "10-Point Presentation Scorecard 🏆",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = evaluation?.performanceTier ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = AccentGreen
                                )
                            }

                            // Score out of 10.0
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AccentGreen.copy(alpha = 0.15f))
                                    .border(1.5.dp, AccentGreen, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${evaluation?.overallScoreOutOf10}/10",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = AccentGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 4 Fluency Pillars
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricBox(
                                value = "${evaluation?.articulationScore}%",
                                unit = "",
                                label = "Articulation",
                                color = PrimaryBlue,
                                modifier = Modifier.weight(1f)
                            )
                            MetricBox(
                                value = "${evaluation?.cadencePacingScore}%",
                                unit = "",
                                label = "Pacing",
                                color = AccentGreen,
                                modifier = Modifier.weight(1f)
                            )
                            MetricBox(
                                value = "${evaluation?.rhetoricVocabScore}%",
                                unit = "",
                                label = "Rhetoric",
                                color = AccentAmber,
                                modifier = Modifier.weight(1f)
                            )
                            MetricBox(
                                value = "${evaluation?.slideAlignmentScore}%",
                                unit = "",
                                label = "Alignment",
                                color = Color(0xFF8B5CF6),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Key Strengths
                        Text(
                            text = "Presentation Strengths:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        evaluation?.strengths?.forEach { st ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                                Text(text = st, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Concrete Improvements
                        Text(
                            text = "Targeted Improvements:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        evaluation?.concreteImprovements?.forEach { imp ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(text = "👉", fontSize = 14.sp)
                                Text(text = imp, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Difficult words with IPA
                        if (evaluation?.difficultWordsWithIpa?.isNotEmpty() == true) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Pronunciation Polish for Topic Words:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            evaluation?.difficultWordsWithIpa?.forEach { (word, ipa) ->
                                Text(
                                    text = "• $word : /$ipa/",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive Progress Graph
        item {
            PresentationProgressGraph(records = scoreHistory)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
