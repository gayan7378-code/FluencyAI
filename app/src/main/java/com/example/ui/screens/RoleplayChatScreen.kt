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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.AiScenario
import com.example.data.model.ChatMessage
import com.example.data.model.UserLevel
import com.example.ui.components.AccuracyBadge
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.RoleplayChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleplayChatScreen(
    viewModel: RoleplayChatViewModel,
    modifier: Modifier = Modifier
) {
    val scenarios = viewModel.scenarios
    val selectedScenario by viewModel.selectedScenario.collectAsState()
    val selectedLevel by viewModel.selectedLevel.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val inspectedMessage by viewModel.inspectedMessage.collectAsState()
    val speechState by viewModel.speechManager.speechState.collectAsState()
    val soundLevel by viewModel.speechManager.soundLevel.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val isListening = speechState is SpeechState.Listening || speechState is SpeechState.PartialResult

    // Auto-scroll to latest message
    LaunchedEffect(messages.size, isAiThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Top Header: English Level Selector (Beginner, Intermediate, Advanced)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Roleplay Chat & Sinhala Corrections",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${selectedLevel.label} Level",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Level Selection Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UserLevel.values().forEach { level ->
                    val isSelected = level == selectedLevel
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) PrimaryBlue
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .border(
                                1.dp,
                                if (isSelected) PrimaryBlue
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.setLevel(level) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = level.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scenario Selection Row
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
                                if (isSelected) PrimaryBlue.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .border(
                                1.dp,
                                if (isSelected) PrimaryBlue
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.startScenario(scenario) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = scenario.iconEmoji, fontSize = 14.sp)
                            Text(
                                text = scenario.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Chat Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Scenario Header Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "🎭 Scenario: ${selectedScenario.title} • Partner: ${selectedScenario.aiRole} • You: ${selectedScenario.userRole}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(messages, key = { it.id }) { message ->
                ChatMessageItem(
                    message = message,
                    aiRole = selectedScenario.aiRole,
                    onPlayTts = { viewModel.speakMessage(message) },
                    onInspectFeedback = { viewModel.inspectMessage(message) }
                )
            }

            if (isAiThinking) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = PrimaryBlue
                        )
                        Text(
                            text = "${selectedScenario.aiRole} is thinking...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Live listening visualizer bar
        AnimatedVisibility(visible = isListening) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue.copy(alpha = 0.1f))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AudioWaveformVisualizer(
                        isListening = true,
                        isSpeaking = false,
                        soundLevel = soundLevel,
                        color = PrimaryBlue
                    )
                    val hint = when (val s = speechState) {
                        is SpeechState.PartialResult -> "\"${s.text}\""
                        else -> "Listening to your spoken English..."
                    }
                    Text(
                        text = hint,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryBlue
                    )
                }
            }
        }

        // Bottom Input Dock
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Big Pulsing Mic Button
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(if (isListening) MaterialTheme.colorScheme.error else PrimaryBlue)
                    .clickable {
                        if (isListening) viewModel.stopListening() else viewModel.startListening()
                    }
                    .testTag("roleplay_mic_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Speak into mic",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Text input field
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Speak or type in English...", fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("roleplay_text_input"),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            // Send Button
            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendMessage(textInput)
                        textInput = ""
                    }
                },
                enabled = textInput.isNotBlank(),
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (textInput.isNotBlank()) PrimaryBlue else MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message",
                    tint = if (textInput.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // Pronunciation & Sinhala Grammar Feedback Modal Sheet
    if (inspectedMessage != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.inspectMessage(null) },
            sheetState = rememberModalBottomSheetState()
        ) {
            PronunciationFeedbackSheetContent(
                message = inspectedMessage!!,
                onSpeakWord = { viewModel.speakWord(it) },
                onClose = { viewModel.inspectMessage(null) }
            )
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    aiRole: String,
    onPlayTts: () -> Unit,
    onInspectFeedback: () -> Unit
) {
    val isUser = message.isUser

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isUser) "You" else aiRole,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isUser) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) PrimaryBlue.copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                )
                .border(
                    1.dp,
                    if (isUser) PrimaryBlue.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
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
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // If this is an AI response that corrected the previous user turn:
                if (message.grammarCorrection != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentRose.copy(alpha = 0.12f))
                            .border(1.dp, AccentRose.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Correction: ",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = AccentRose
                                )
                                Text(
                                    text = "\"${message.grammarCorrection.originalText}\" → \"${message.grammarCorrection.correctedText}\"",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "🇱🇰 ${message.grammarCorrection.sinhalaExplanation}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Inspection CTA Button
                if (message.scores != null || message.grammarCorrection != null || message.phoneticCorrections.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (message.scores != null) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Fluency: ${message.scores.fluencyScore}%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = AccentGreen
                                )
                                Text(
                                    text = "Grammar: ${message.scores.grammarScore}%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = PrimaryBlue
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryBlue)
                                .clickable { onInspectFeedback() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "View Phonetics & Tips",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PronunciationFeedbackSheetContent(
    message: ChatMessage,
    onSpeakWord: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI Pronunciation & Grammar Analysis",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Syllable stress, IPA phonetics, and Sinhala explanation",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Scores Dials
        if (message.scores != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ScoreDialBox(
                    score = message.scores.fluencyScore,
                    label = "Spoken Fluency",
                    color = AccentGreen,
                    modifier = Modifier.weight(1f)
                )
                ScoreDialBox(
                    score = message.scores.pronunciationScore,
                    label = "Pronunciation",
                    color = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )
                ScoreDialBox(
                    score = message.scores.grammarScore,
                    label = "Grammar Accuracy",
                    color = AccentAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Sinhala Grammar Correction Section
        if (message.grammarCorrection != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AccentRose.copy(alpha = 0.1f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AccentRose.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Sinhala Grammar Explanation 🇱🇰",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = AccentRose
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rule: ${message.grammarCorrection.ruleName}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Correction: \"${message.grammarCorrection.originalText}\" → \"${message.grammarCorrection.correctedText}\"",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = message.grammarCorrection.sinhalaExplanation,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Phonetic Corrections & Syllable Stress
        if (message.phoneticCorrections.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Pronunciation & Syllable Stress Guide 🗣️",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            message.phoneticCorrections.forEach { phonetic ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = phonetic.word,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryBlue
                                )
                                Text(
                                    text = phonetic.ipa,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Stress: ${phonetic.syllableStress}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = phonetic.mouthTip,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { onSpeakWord(phonetic.word) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen word",
                                tint = PrimaryBlue
                            )
                        }
                    }
                }
            }
        }

        // Native Phrasing Alternative
        if (message.nativeUpgradeAlternative != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryBlue.copy(alpha = 0.08f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "✨", fontSize = 18.sp)
                    Text(
                        text = message.nativeUpgradeAlternative,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreDialBox(
    score: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score%",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
