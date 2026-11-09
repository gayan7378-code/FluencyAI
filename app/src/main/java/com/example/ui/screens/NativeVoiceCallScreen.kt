package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeDown
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CallScenario
import com.example.ui.components.AccuracyBadge
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.CallUiState
import com.example.ui.viewmodel.NativeVoiceCallViewModel

@Composable
fun NativeVoiceCallScreen(
    viewModel: NativeVoiceCallViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scenario by viewModel.selectedScenario.collectAsState()
    val accent by viewModel.currentAccent.collectAsState()
    val seconds by viewModel.callSeconds.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val isSpeakerOn by viewModel.isSpeakerOn.collectAsState()
    val statusBanner by viewModel.callStatusBanner.collectAsState()
    val debrief by viewModel.debriefResult.collectAsState()
    val messages by viewModel.callMessages.collectAsState()
    val soundLevel by viewModel.speechManager.soundLevel.collectAsState()

    when (uiState) {
        CallUiState.SETUP -> {
            CallSetupView(
                scenarios = viewModel.scenarios,
                selectedScenario = scenario,
                currentAccent = accent,
                onSelectScenario = { viewModel.selectScenario(it) },
                onSelectAccent = { viewModel.setAccent(it) },
                onStartCall = { viewModel.startCall() },
                modifier = modifier
            )
        }
        CallUiState.RINGING, CallUiState.CONNECTED -> {
            ActiveCallInterface(
                scenario = scenario,
                currentAccent = accent,
                seconds = seconds,
                isMuted = isMuted,
                isSpeakerOn = isSpeakerOn,
                statusBanner = statusBanner,
                messages = messages,
                soundLevel = soundLevel,
                onToggleMute = { viewModel.toggleMute() },
                onToggleSpeaker = { viewModel.toggleSpeaker() },
                onSelectAccent = { viewModel.setAccent(it) },
                onEndCall = { viewModel.endCall() },
                modifier = modifier
            )
        }
        CallUiState.DEBRIEF -> {
            CallDebriefView(
                scenario = scenario,
                debrief = debrief,
                onCallAgain = { viewModel.returnToSetup() },
                modifier = modifier
            )
        }
    }
}

@Composable
fun CallSetupView(
    scenarios: List<CallScenario>,
    selectedScenario: CallScenario,
    currentAccent: String,
    onSelectScenario: (CallScenario) -> Unit,
    onSelectAccent: (String) -> Unit,
    onStartCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Native Speaker Practice 📞",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Simulated real-time phone calls with native AI conversational partners",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Accent Selection Bar
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Select Regional Accent",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("US", "🇺🇸 American", "General American rhotic"),
                            Triple("UK", "🇬🇧 British", "Received Pronunciation"),
                            Triple("AUS", "🇦🇺 Australian", "Authentic Aussie cadence")
                        ).forEach { (code, label, desc) ->
                            val isSelected = code == currentAccent
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) PrimaryBlue
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .clickable { onSelectAccent(code) }
                                    .padding(vertical = 10.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Caller Persona Preview Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = selectedScenario.persona.avatarEmoji, fontSize = 32.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedScenario.persona.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = selectedScenario.persona.roleTitle,
                            style = MaterialTheme.typography.bodySmall.copy(color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedScenario.persona.bio,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Call Scenarios List
        item {
            Text(
                text = "Choose a Call Scenario",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(scenarios) { sc ->
            val isSelected = sc.id == selectedScenario.id

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) PrimaryBlue.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelectScenario(sc) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = sc.persona.avatarEmoji, fontSize = 16.sp)
                            Text(
                                text = sc.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = sc.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = sc.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Green Call Button
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onStartCall,
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("start_native_call_button")
            ) {
                Icon(Icons.Default.Call, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Call ${selectedScenario.persona.name}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ActiveCallInterface(
    scenario: CallScenario,
    currentAccent: String,
    seconds: Int,
    isMuted: Boolean,
    isSpeakerOn: Boolean,
    statusBanner: String,
    messages: List<com.example.data.model.CallMessage>,
    soundLevel: Float,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onSelectAccent: (String) -> Unit,
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCaptions by remember { mutableStateOf(false) }

    // Pulsing aura animation
    val pulseAnim = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        pulseAnim.animateTo(
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar: Call Status & Timer
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = scenario.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            val mins = seconds / 60
            val secs = seconds % 60
            Text(
                text = String.format("%02d:%02d", mins, secs),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = AccentGreen
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = statusBanner,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        // Center: Glowing Caller Avatar & Audio Waveform
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .scale(pulseAnim.value)
            ) {
                // Outer glowing aura rings
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.15f))
                )
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.25f))
                )

                // Avatar Center
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = scenario.persona.avatarEmoji, fontSize = 48.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = scenario.persona.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Text(
                text = "${scenario.persona.roleTitle} • $currentAccent Accent",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Audio Waveform
            AudioWaveformVisualizer(
                isListening = !isMuted,
                isSpeaking = false,
                soundLevel = soundLevel,
                barCount = 15,
                maxHeight = 40.dp,
                color = PrimaryBlue
            )
        }

        // Captions Drawer Preview
        AnimatedVisibility(visible = showCaptions && messages.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .padding(10.dp)
            ) {
                val latest = messages.lastOrNull()
                if (latest != null) {
                    Column {
                        Text(
                            text = if (latest.isUser) "You:" else "${scenario.persona.name}:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (latest.isUser) PrimaryBlue else AccentGreen
                        )
                        Text(
                            text = latest.text,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Bottom Controls Dock
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Live Accent Switcher Pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("US" to "🇺🇸 US", "UK" to "🇬🇧 UK", "AUS" to "🇦🇺 AUS").forEach { (code, label) ->
                    val isSelected = code == currentAccent
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PrimaryBlue else Color.White.copy(alpha = 0.1f))
                            .clickable { onSelectAccent(code) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // In-Call Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mute
                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isMuted) Color.White else Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute",
                        tint = if (isMuted) Color.Black else Color.White
                    )
                }

                // End Call (Big Red Button)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(AccentRose)
                        .clickable { onEndCall() }
                        .testTag("end_call_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End call",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Captions Toggle
                IconButton(
                    onClick = { showCaptions = !showCaptions },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (showCaptions) PrimaryBlue else Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Subtitles,
                        contentDescription = "Captions",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CallDebriefView(
    scenario: CallScenario,
    debrief: com.example.data.model.CallDebrief?,
    onCallAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Call Summary & Debrief 📋",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Completed call with ${scenario.persona.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                AccuracyBadge(score = debrief?.fluencyScore ?: 88)
            }
        }

        // Metrics Row
        if (debrief != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox(
                        value = "${debrief.durationSeconds}s",
                        unit = "",
                        label = "Call Duration",
                        color = PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        value = "${debrief.turnsCount}",
                        unit = "turns",
                        label = "Spoken Exchanges",
                        color = AccentGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        value = "${debrief.clarityScore}%",
                        unit = "",
                        label = "Pronunciation",
                        color = AccentAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Accent-Specific Coaching
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${scenario.persona.accentLabel} Accent Coaching 🎙️",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        debrief.accentTips.forEach { tip ->
                            Text(
                                text = "• $tip",
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Recommended Idioms & Phrasing
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Recommended Native Expressions ✨",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = AccentGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        debrief.recommendedIdioms.forEach { (idiom, meaning) ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = "\"$idiom\"",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = meaning,
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
            Button(
                onClick = onCallAgain,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Make Another Call")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
