package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.TongueTwister
import com.example.data.model.TwisterDifficulty
import com.example.ui.components.AccuracyBadge
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.TongueTwisterViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TongueTwisterScreen(
    viewModel: TongueTwisterViewModel,
    modifier: Modifier = Modifier
) {
    val difficulty by viewModel.selectedDifficulty.collectAsState()
    val twister by viewModel.selectedTwister.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val liveWpm by viewModel.liveWpm.collectAsState()
    val highlightedWords by viewModel.highlightedWords.collectAsState()
    val evaluation by viewModel.evaluation.collectAsState()
    val soundLevel by viewModel.speechManager.soundLevel.collectAsState()

    val twistersInLevel = viewModel.allTwisters.filter { it.difficulty == difficulty }
    var manualTestInput by remember { mutableStateOf(twister.targetSentence) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tongue Twisters Agility Lab ⚡",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Train speech muscles for lightning-fast phoneme agility and reading speed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4 Difficulty Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TwisterDifficulty.values().forEach { diff ->
                    val isSelected = diff == difficulty
                    val badgeColor = Color(diff.badgeColorHex)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) badgeColor else MaterialTheme.colorScheme.surface)
                            .border(
                                1.dp,
                                if (isSelected) badgeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setDifficulty(diff) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = diff.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Active Twister Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header: Title & Target WPM Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = twister.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Focus: ${twister.focusPhonemes}",
                                style = MaterialTheme.typography.labelSmall.copy(color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryBlue.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Benchmark: ${twister.targetWpm} WPM",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Target Sentence with Live Emerald Green Word Highlighting!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(16.dp)
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            highlightedWords.forEach { (word, isMatched) ->
                                Text(
                                    text = word,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = if (isMatched) FontWeight.Bold else FontWeight.SemiBold,
                                        fontSize = 17.sp
                                    ),
                                    color = if (isMatched) AccentGreen else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Articulatory Tip Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentAmber.copy(alpha = 0.1f))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "💡", fontSize = 16.sp)
                        Text(
                            text = twister.articulatoryTip,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stopwatch Timer & Live WPM Meter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (isRecording) AccentRose else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = String.format("%.1fs", elapsedSeconds),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (isRecording) AccentRose else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "$liveWpm WPM",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio Waveform
                    AudioWaveformVisualizer(
                        isListening = isRecording,
                        isSpeaking = false,
                        soundLevel = soundLevel,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = if (isRecording) AccentRose else PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Native TTS Audio Buttons (1.0x & 0.75x)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.playAudio(1.0f) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hear Native (1.0x)", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.playAudio(0.75f) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SlowMotionVideo, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Slow-Mo (0.75x)", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Big Recording Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                if (isRecording) viewModel.stopPractice()
                                else viewModel.startPractice()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecording) AccentRose else PrimaryBlue
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("twister_record_button")
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRecording) "Stop & Score Agility" else "Start Speaking Challenge",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }
                    }

                    // Test / Manual text trigger for emulators
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            viewModel.stopPractice(twister.targetSentence)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simulate & Test Speech Scoring (for emulator)", fontSize = 11.sp)
                    }
                }
            }
        }

        // Drill Evaluation Result Card
        if (evaluation != null) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, AccentGreen.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Speech Agility Scorecard 🎯",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = evaluation?.tips ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            AccuracyBadge(score = evaluation?.accuracyPercent ?: 0)
                        }

                        // Speed Beaten Celebration Badge!
                        if (evaluation?.targetSpeedBeaten == true) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AccentGreen)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White)
                                    Text(
                                        text = "⚡ SPEED BENCHMARK BEATEN! (${evaluation?.wordsPerMinute} WPM vs ${twister.targetWpm} WPM)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricBox(
                                value = "${evaluation?.wordsPerMinute}",
                                unit = "WPM",
                                label = "Reading Speed",
                                color = PrimaryBlue,
                                modifier = Modifier.weight(1f)
                            )
                            MetricBox(
                                value = String.format("%.1fs", evaluation?.elapsedSeconds ?: 0f),
                                unit = "",
                                label = "Total Time",
                                color = AccentAmber,
                                modifier = Modifier.weight(1f)
                            )
                            MetricBox(
                                value = "${evaluation?.accuracyPercent}%",
                                unit = "",
                                label = "Accuracy",
                                color = AccentGreen,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // More Twisters in this Difficulty Category
        item {
            Text(
                text = "More Drills in ${difficulty.label}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(twistersInLevel) { itemTwister ->
            val isCurrent = itemTwister.id == twister.id
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) PrimaryBlue.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isCurrent) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { viewModel.selectTwister(itemTwister) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = itemTwister.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = itemTwister.targetSentence,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = "${itemTwister.targetWpm} WPM",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
