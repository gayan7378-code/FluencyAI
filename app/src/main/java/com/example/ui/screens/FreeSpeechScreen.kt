package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.audio.SpeechState
import com.example.ui.SpeakFlowViewModel
import com.example.ui.components.AccuracyBadge
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryBlue

@Composable
fun FreeSpeechScreen(
    viewModel: SpeakFlowViewModel,
    modifier: Modifier = Modifier
) {
    val isRecording by viewModel.freeSpeechRecording.collectAsState()
    val seconds by viewModel.freeSpeechSeconds.collectAsState()
    val result by viewModel.freeSpeechResult.collectAsState()
    val speechState by viewModel.speechManager.speechState.collectAsState()
    val soundLevel by viewModel.speechManager.soundLevel.collectAsState()

    var manualFreeSpeechText by remember {
        mutableStateOf("I love traveling to the mountains because the fresh air and quiet scenery help me relax. In the future, I plan to visit the Swiss Alps with my family.")
    }

    val prompts = listOf(
        "Describe your dream travel destination and why you want to visit.",
        "What does your typical morning routine look like from waking up to work?",
        "Share a memorable accomplishment or project you are proud of.",
        "What are the benefits and challenges of remote work?",
        "Describe your favorite food or a dish you enjoy cooking."
    )
    var currentPromptIndex by remember { mutableStateOf(0) }

    // Live partial or completed transcript
    val liveTranscript = when (val state = speechState) {
        is SpeechState.PartialResult -> state.text
        is SpeechState.Success -> state.recognizedText
        else -> ""
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Free Speech & Fluency Meter",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Speak spontaneously to measure your natural pace (WPM) and filler words",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Prompt Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "🎯", fontSize = 16.sp)
                            Text(
                                text = "Speaking Prompt",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryBlue
                            )
                        }

                        IconButton(
                            onClick = {
                                currentPromptIndex = (currentPromptIndex + 1) % prompts.size
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Next prompt",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "\"${prompts[currentPromptIndex]}\"",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Recording & Timer Dashboard Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (isRecording) AccentRose else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Timer display
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (isRecording) AccentRose else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        val mins = seconds / 60
                        val secs = seconds % 60
                        Text(
                            text = String.format("%02d:%02d", mins, secs),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = if (isRecording) AccentRose else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio Waveform
                    AudioWaveformVisualizer(
                        isListening = isRecording,
                        isSpeaking = false,
                        soundLevel = soundLevel,
                        modifier = Modifier.padding(vertical = 8.dp),
                        barCount = 13,
                        maxHeight = 44.dp,
                        color = if (isRecording) AccentRose else PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Big Record Button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) AccentRose else PrimaryBlue)
                            .clickable {
                                if (isRecording) {
                                    val textToEvaluate = liveTranscript.ifBlank { manualFreeSpeechText }
                                    viewModel.stopFreeSpeech(textToEvaluate)
                                } else {
                                    viewModel.startFreeSpeech()
                                }
                            }
                            .testTag("free_speech_record_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isRecording) "Stop speech workout" else "Start speech workout",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isRecording) "Tap to finish & analyze" else "Tap microphone to speak freely",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Live Speech Preview
                    AnimatedVisibility(visible = liveTranscript.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Transcript: \"$liveTranscript\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Test / Manual text option for emulators
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = manualFreeSpeechText,
                        onValueChange = { manualFreeSpeechText = it },
                        label = { Text("Simulate / Type speech sample") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            viewModel.stopFreeSpeech(manualFreeSpeechText)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.fillMaxWidth().testTag("analyze_manual_free_speech")
                    ) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Analyze Spoken Fluency")
                    }
                }
            }
        }

        // Analysis Results Card
        if (result != null) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.5.dp,
                            AccentGreen.copy(alpha = 0.6f),
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Fluency Analysis Report 📊",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Based on pacing, filler words & rhythm",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            AccuracyBadge(score = result?.fluencyScore ?: 0)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Key Metrics Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // WPM
                            MetricBox(
                                value = "${result?.wordsPerMinute}",
                                unit = "WPM",
                                label = "Pace (Target: 120-150)",
                                color = PrimaryBlue,
                                modifier = Modifier.weight(1f)
                            )

                            // Word count
                            MetricBox(
                                value = "${result?.totalWords}",
                                unit = "Words",
                                label = "Total Articulated",
                                color = AccentGreen,
                                modifier = Modifier.weight(1f)
                            )

                            // Fillers
                            MetricBox(
                                value = "${result?.fillerWordsDetected?.size}",
                                unit = "Fillers",
                                label = if (result?.fillerWordsDetected?.isEmpty() == true) "Zero Fillers! 🌟" else "Watch fillers",
                                color = if (result?.fillerWordsDetected?.isEmpty() == true) AccentGreen else AccentAmber,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Coaching Suggestions
                        Text(
                            text = "Fluency Insights:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        result?.suggestions?.forEach { tip ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = tip,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MetricBox(
    value: String,
    unit: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = color
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
