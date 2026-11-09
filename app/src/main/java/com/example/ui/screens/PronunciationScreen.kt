package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PronunciationDrill
import com.example.data.model.SoundCategory
import com.example.ui.SpeakFlowViewModel
import com.example.ui.components.AccuracyBadge
import com.example.ui.components.SpeakControlBar
import com.example.ui.components.WordHighlightView
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryBlue

@Composable
fun PronunciationScreen(
    viewModel: SpeakFlowViewModel,
    modifier: Modifier = Modifier
) {
    val drills = viewModel.pronunciationDrills
    val selectedCategory by viewModel.selectedSoundCategory.collectAsState()
    val selectedDrill by viewModel.selectedDrill.collectAsState()
    val lastEval by viewModel.lastEvaluationResult.collectAsState()
    val speechState by viewModel.speechManager.speechState.collectAsState()
    val soundLevel by viewModel.speechManager.soundLevel.collectAsState()
    val isTtsSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()

    val filteredDrills = if (selectedCategory != null) {
        drills.filter { it.category == selectedCategory }
    } else {
        drills
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
                text = "Pronunciation & Phonetics Lab",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Train your vocal muscles to articulate English phonemes clearly",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Sound Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All" chip
                CategoryFilterChip(
                    title = "All Sounds",
                    isSelected = selectedCategory == null,
                    onClick = { viewModel.selectSoundCategory(null) }
                )

                SoundCategory.values().forEach { category ->
                    CategoryFilterChip(
                        title = category.title,
                        isSelected = selectedCategory == category,
                        onClick = { viewModel.selectSoundCategory(category) }
                    )
                }
            }
        }

        // Active Drill Hero Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentRose.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = selectedDrill.category.title,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AccentRose
                            )
                        }

                        // IPA Phonetic Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryBlue.copy(alpha = 0.12f))
                                .border(1.dp, PrimaryBlue.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = selectedDrill.phoneticSymbol,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = PrimaryBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = selectedDrill.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Target Drill Sentence Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        PrimaryBlue.copy(alpha = 0.08f),
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "\"${selectedDrill.targetSentence}\"",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 24.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Focus Words:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                selectedDrill.focusWords.forEach { word ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(PrimaryBlue)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = word,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mouth & Tongue Positioning Guide
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "👄", fontSize = 18.sp)
                            Column {
                                Text(
                                    text = "Vocal Mechanics & Mouth Position",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = selectedDrill.mouthGuide,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio Listen Buttons (Normal Speed & Slow Speed)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.setSpeechSpeed(1.0f)
                                viewModel.speakText(selectedDrill.targetSentence)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Normal")
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.setSpeechSpeed(0.75f)
                                viewModel.speakText(selectedDrill.targetSentence)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SlowMotionVideo, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Slow (0.75x)")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Speech Input & Microphone Bar
                    SpeakControlBar(
                        speechState = speechState,
                        soundLevel = soundLevel,
                        isSpeakingTts = isTtsSpeaking,
                        onStartListening = { viewModel.startListeningForCurrentTarget(selectedDrill.targetSentence) },
                        onStopListening = { viewModel.stopListening() },
                        onSimulateSpeech = { viewModel.evaluateManualSpeech(selectedDrill.targetSentence, it) },
                        suggestedSimulatedText = selectedDrill.targetSentence
                    )

                    // Evaluation Card
                    if (lastEval != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lastEval?.feedback ?: "",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = lastEval?.tips ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            AccuracyBadge(score = lastEval?.overallScore ?: 0)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        WordHighlightView(
                            words = lastEval?.words ?: emptyList(),
                            onWordClick = { word -> viewModel.speakText(word) }
                        )
                    }
                }
            }
        }

        // List of other drills in this category
        item {
            Text(
                text = "More Drills in this Category",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(filteredDrills.size) { index ->
            val drill = filteredDrills[index]
            val isCurrent = drill.id == selectedDrill.id

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
                    .clickable { viewModel.selectDrill(drill) }
                    .testTag("drill_card_${drill.id}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = drill.phoneticSymbol,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            )
                            Text(
                                text = "• ${drill.title}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = drill.targetSentence,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    IconButton(
                        onClick = { viewModel.speakText(drill.targetSentence) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Listen drill",
                            tint = PrimaryBlue
                        )
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
fun CategoryFilterChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) PrimaryBlue
                else MaterialTheme.colorScheme.surface
            )
            .border(
                1.dp,
                if (isSelected) PrimaryBlue
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}
