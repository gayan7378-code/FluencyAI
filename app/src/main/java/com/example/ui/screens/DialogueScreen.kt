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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
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
import com.example.audio.SpeechState
import com.example.data.model.DialogueLine
import com.example.data.model.DialogueScenario
import com.example.ui.SpeakFlowViewModel
import com.example.ui.components.AccuracyBadge
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.SpeakControlBar
import com.example.ui.components.VoiceSettingsBar
import com.example.ui.components.WordHighlightView
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.PrimaryBlue

@Composable
fun DialogueScreen(
    viewModel: SpeakFlowViewModel,
    modifier: Modifier = Modifier
) {
    val scenarios = viewModel.scenarios
    val selectedScenario by viewModel.selectedScenario.collectAsState()
    val currentLineIndex by viewModel.currentDialogueLineIndex.collectAsState()
    val lastEval by viewModel.lastEvaluationResult.collectAsState()
    val speechState by viewModel.speechManager.speechState.collectAsState()
    val soundLevel by viewModel.speechManager.soundLevel.collectAsState()
    val isTtsSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()
    val currentUtteranceId by viewModel.ttsManager.currentUtteranceId.collectAsState()
    val currentSpeed by viewModel.currentSpeed.collectAsState()
    val isBritish by viewModel.isBritishAccent.collectAsState()

    val currentLine = selectedScenario.lines.getOrNull(currentLineIndex)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Scenario Selector Chips
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Conversational Scenarios",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scenarios.forEach { scenario ->
                    val isSelected = scenario.id == selectedScenario.id
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
                            .clickable { viewModel.selectScenario(scenario) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("scenario_chip_${scenario.id}")
                    ) {
                        Column {
                            Text(
                                text = scenario.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${scenario.difficulty} • ${scenario.category}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Scenario Info Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Role: ${selectedScenario.userRoleName} (You)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryBlue
                            )
                            Text(
                                text = "vs ${selectedScenario.partnerRoleName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = selectedScenario.description,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryBlue.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Turn ${currentLineIndex + 1}/${selectedScenario.lines.size}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryBlue
                        )
                    }
                }
            }
        }

        // Conversation History / Dialogue Bubbles
        itemsIndexed(selectedScenario.lines) { index, line ->
            val isActive = index == currentLineIndex
            val isPassed = index < currentLineIndex
            val isSpeakingThis = isTtsSpeaking && currentUtteranceId == line.id

            DialogueBubbleCard(
                line = line,
                isActive = isActive,
                isPassed = isPassed,
                isSpeakingThis = isSpeakingThis,
                partnerName = selectedScenario.partnerRoleName,
                userName = selectedScenario.userRoleName,
                onPlayTts = { viewModel.speakText(line.text, line.id) }
            )
        }

        // Current Active Turn Speaking & Evaluation Area
        if (currentLine != null) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.5.dp,
                            if (currentLine.isUserTurn) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            RoundedCornerShape(18.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentLine.isUserTurn) "Your Turn to Speak 🎙️" else "Partner's Turn 🎧",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (currentLine.isUserTurn) PrimaryBlue else MaterialTheme.colorScheme.onSurface
                            )

                            IconButton(
                                onClick = { viewModel.speakText(currentLine.text, currentLine.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Listen to pronunciation",
                                    tint = PrimaryBlue
                                )
                            }
                        }

                        // Target sentence
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "\"${currentLine.text}\"",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 22.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Phonetic hint / Intonation tip
                        if (currentLine.phoneticNote != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = AccentAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = currentLine.phoneticNote,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Alternative phrasing
                        if (currentLine.alternativeOptions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Natural Alternatives: " + currentLine.alternativeOptions.first(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // If user's turn: Speech Control Bar
                        if (currentLine.isUserTurn) {
                            SpeakControlBar(
                                speechState = speechState,
                                soundLevel = soundLevel,
                                isSpeakingTts = isTtsSpeaking,
                                onStartListening = { viewModel.startListeningForCurrentTarget(currentLine.text) },
                                onStopListening = { viewModel.stopListening() },
                                onSimulateSpeech = { viewModel.evaluateManualSpeech(currentLine.text, it) },
                                suggestedSimulatedText = currentLine.text
                            )

                            // Evaluation Result
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
                        } else {
                            // Partner turn: tap to advance or re-listen
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = { viewModel.speakText(currentLine.text, currentLine.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Listen Partner's Line")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Step Navigation Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.previousDialogueLine() },
                                enabled = currentLineIndex > 0
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Previous")
                            }

                            Button(
                                onClick = { viewModel.nextDialogueLine() },
                                enabled = currentLineIndex < selectedScenario.lines.size - 1
                            ) {
                                Text("Next Turn")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
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
fun DialogueBubbleCard(
    line: DialogueLine,
    isActive: Boolean,
    isPassed: Boolean,
    isSpeakingThis: Boolean,
    partnerName: String,
    userName: String,
    onPlayTts: () -> Unit
) {
    val isUser = line.isUserTurn
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isUser) "$userName (You)" else partnerName,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isUser) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isPassed) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    when {
                        isActive -> if (isUser) PrimaryBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                        isUser -> PrimaryBlue.copy(alpha = 0.06f)
                        else -> MaterialTheme.colorScheme.surface
                    }
                )
                .border(
                    1.dp,
                    if (isActive) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = line.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onPlayTts,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Listen",
                        tint = if (isSpeakingThis) AccentGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
