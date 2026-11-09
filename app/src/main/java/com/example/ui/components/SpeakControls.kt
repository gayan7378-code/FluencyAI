package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechState

@Composable
fun SpeakControlBar(
    speechState: SpeechState,
    soundLevel: Float,
    isSpeakingTts: Boolean,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onSimulateSpeech: (String) -> Unit,
    modifier: Modifier = Modifier,
    suggestedSimulatedText: String = ""
) {
    var showSimulateDialog by remember { mutableStateOf(false) }
    var manualInputText by remember { mutableStateOf(suggestedSimulatedText) }

    val isListening = speechState is SpeechState.Listening || speechState is SpeechState.PartialResult

    // Pulsing animation for listening state
    val pulseScale = remember { Animatable(1f) }
    LaunchedEffect(isListening) {
        if (isListening) {
            pulseScale.animateTo(
                targetValue = 1.18f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            pulseScale.animateTo(1f)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Live feedback text or partial transcript
        AnimatedVisibility(visible = isListening) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                AudioWaveformVisualizer(
                    isListening = true,
                    isSpeaking = false,
                    soundLevel = soundLevel,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                val text = when (val state = speechState) {
                    is SpeechState.PartialResult -> "\"${state.text}\""
                    else -> "Listening... speak clearly"
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Test / manual input option (useful for emulators & testing)
            IconButton(
                onClick = {
                    manualInputText = suggestedSimulatedText
                    showSimulateDialog = true
                },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("simulate_speech_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Test speech by typing",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Main Microphone / Stop Action
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(76.dp)
                    .scale(pulseScale.value)
            ) {
                if (isListening) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    )
                }

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (isListening) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary
                        )
                        .clickable {
                            if (isListening) onStopListening() else onStartListening()
                        }
                        .testTag("main_mic_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isListening) "Stop listening" else "Start speaking",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(64.dp))
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isListening) "Tap square to stop" else "Tap microphone to speak",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showSimulateDialog) {
        AlertDialog(
            onDismissRequest = { showSimulateDialog = false },
            title = { Text("Practice / Type Spoken Words") },
            text = {
                Column {
                    Text(
                        "You can test pronunciation scoring by speaking into the microphone, or test sentences directly here:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = manualInputText,
                        onValueChange = { manualInputText = it },
                        label = { Text("Spoken English text") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_speech_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSimulateDialog = false
                        onSimulateSpeech(manualInputText)
                    },
                    modifier = Modifier.testTag("submit_simulated_speech")
                ) {
                    Text("Evaluate")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSimulateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun VoiceSettingsBar(
    currentSpeed: Float,
    isBritishAccent: Boolean,
    onSpeedChange: (Float) -> Unit,
    onAccentChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Speed selectors
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Speed:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            listOf(0.75f to "0.75x", 1.0f to "1.0x", 1.25f to "1.25x").forEach { (speed, label) ->
                val selected = currentSpeed == speed
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .border(
                            1.dp,
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onSpeedChange(speed) }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Accent selector
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Accent:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (!isBritishAccent) MaterialTheme.colorScheme.secondary
                        else Color.Transparent
                    )
                    .clickable { onAccentChange(false) }
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "🇺🇸 US",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (!isBritishAccent) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (!isBritishAccent) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isBritishAccent) MaterialTheme.colorScheme.secondary
                        else Color.Transparent
                    )
                    .clickable { onAccentChange(true) }
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "🇬🇧 UK",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isBritishAccent) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isBritishAccent) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
