package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Mic
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AccuracyBadge
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.BookReadingViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyReadingScreen(
    viewModel: BookReadingViewModel,
    modifier: Modifier = Modifier
) {
    val excerpts = viewModel.excerpts
    val selectedExcerpt by viewModel.selectedExcerpt.collectAsState()
    val isReading by viewModel.isReading.collectAsState()
    val seconds by viewModel.readingSeconds.collectAsState()
    val wordsStatus by viewModel.wordsStatus.collectAsState()
    val evaluation by viewModel.evaluation.collectAsState()
    val soundLevel by viewModel.speechManager.soundLevel.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Daily Book Reading Mode 📖",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Read famous literature and speeches aloud with real-time text tracking and phonetic guidance",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Excerpts Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                excerpts.forEach { item ->
                    val isSelected = item.id == selectedExcerpt.id
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
                            .clickable { viewModel.selectExcerpt(item) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text(
                                text = item.bookTitle,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = item.author,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Active Excerpt Reader Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedExcerpt.bookTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "By ${selectedExcerpt.author} • ${selectedExcerpt.category}",
                                style = MaterialTheme.typography.bodySmall.copy(color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                            )
                        }

                        // Timer
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (isReading) AccentRose else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            val m = seconds / 60
                            val s = seconds % 60
                            Text(
                                text = String.format("%02d:%02d", m, s),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isReading) AccentRose else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Reading Passage Box with Real-Time Word Tracking
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
                            wordsStatus.forEach { (word, isRead) ->
                                Text(
                                    text = word,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 17.sp,
                                        lineHeight = 26.sp,
                                        fontWeight = if (isRead) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isRead) AccentGreen else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Audio Model Playback (1.0x & 0.8x)
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
                            Text("Author Model (1.0x)", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.playAudio(0.8f) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SlowMotionVideo, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Slow Practice (0.8x)", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio Waveform
                    AudioWaveformVisualizer(
                        isListening = isReading,
                        isSpeaking = false,
                        soundLevel = soundLevel,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = if (isReading) AccentRose else PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Big Read Aloud Button
                    Button(
                        onClick = {
                            if (isReading) viewModel.stopReading()
                            else viewModel.startReading()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isReading) AccentRose else PrimaryBlue
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("book_reading_record_button")
                    ) {
                        Icon(
                            imageVector = if (isReading) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isReading) "Stop Reading & Evaluate" else "Start Reading Aloud",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }

                    // Test trigger for emulators
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            viewModel.stopReading(selectedExcerpt.passageText)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simulate Reading Aloud (for emulator)", fontSize = 11.sp)
                    }
                }
            }
        }

        // Reading Evaluation Card
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
                                    text = "Reading Performance 📖",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = evaluation?.feedback ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            AccuracyBadge(score = evaluation?.accuracyPercent ?: 0)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricBox(
                                value = "${evaluation?.readingSpeedWpm}",
                                unit = "WPM",
                                label = "Pacing (Target: ${selectedExcerpt.targetWpm})",
                                color = PrimaryBlue,
                                modifier = Modifier.weight(1f)
                            )
                            MetricBox(
                                value = "${evaluation?.totalWordsRead}",
                                unit = "words",
                                label = "Words Read",
                                color = AccentGreen,
                                modifier = Modifier.weight(1f)
                            )
                            MetricBox(
                                value = "${evaluation?.accuracyPercent}%",
                                unit = "",
                                label = "Accuracy",
                                color = AccentAmber,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Challenging Words Phonetic Cards
                        if (evaluation?.mispronouncedWords?.isNotEmpty() == true) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Phonetic Corrections for Literature Words:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            evaluation?.mispronouncedWords?.forEach { (word, note) ->
                                Text(
                                    text = "• $word : $note",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrimaryBlue
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
