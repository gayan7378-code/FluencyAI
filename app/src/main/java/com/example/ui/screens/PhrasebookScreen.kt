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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
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
import com.example.data.model.PhraseItem
import com.example.ui.SpeakFlowViewModel
import com.example.ui.components.AccuracyBadge
import com.example.ui.components.SpeakControlBar
import com.example.ui.components.WordHighlightView
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryBlue

@Composable
fun PhrasebookScreen(
    viewModel: SpeakFlowViewModel,
    modifier: Modifier = Modifier
) {
    val phrases by viewModel.phrasebook.collectAsState()
    val activeTestedPhrase by viewModel.activeTestedPhrase.collectAsState()
    val lastEval by viewModel.lastEvaluationResult.collectAsState()
    val speechState by viewModel.speechManager.speechState.collectAsState()
    val soundLevel by viewModel.speechManager.soundLevel.collectAsState()
    val isTtsSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showOnlyBookmarked by remember { mutableStateOf(false) }

    val categories = listOf(
        "Business & Professional",
        "Everyday Chit-Chat",
        "Idioms & Native Slang",
        "Travel & Emergencies"
    )

    val filteredPhrases = phrases.filter { phrase ->
        val matchesCategory = selectedCategory == null || phrase.category == selectedCategory
        val matchesBookmark = !showOnlyBookmarked || phrase.isBookmarked
        val matchesSearch = searchQuery.isBlank() ||
                phrase.englishText.contains(searchQuery, ignoreCase = true) ||
                phrase.meaning.contains(searchQuery, ignoreCase = true) ||
                phrase.category.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesBookmark && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Situational Phrasebook & Idioms",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Natural spoken expressions used daily by native English speakers",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search phrases, idioms, meanings...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("phrase_search_field"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All Chip
                CategoryFilterChip(
                    title = "All",
                    isSelected = selectedCategory == null && !showOnlyBookmarked,
                    onClick = {
                        selectedCategory = null
                        showOnlyBookmarked = false
                    }
                )

                // Bookmarks Chip
                CategoryFilterChip(
                    title = "⭐ Saved",
                    isSelected = showOnlyBookmarked,
                    onClick = {
                        showOnlyBookmarked = true
                        selectedCategory = null
                    }
                )

                categories.forEach { category ->
                    CategoryFilterChip(
                        title = category,
                        isSelected = selectedCategory == category && !showOnlyBookmarked,
                        onClick = {
                            selectedCategory = category
                            showOnlyBookmarked = false
                        }
                    )
                }
            }
        }

        if (filteredPhrases.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔍", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No phrases found matching your search",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // List of Phrase Cards
        items(filteredPhrases, key = { it.id }) { phrase ->
            val isBeingTested = activeTestedPhrase?.id == phrase.id

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isBeingTested) PrimaryBlue.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isBeingTested) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header: Category tag & bookmark button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = phrase.category,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleBookmark(phrase.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (phrase.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (phrase.isBookmarked) AccentAmber else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // English phrase with listen action
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = phrase.englishText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { viewModel.speakText(phrase.englishText) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen phrase",
                                tint = PrimaryBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Phonetic hint
                    Text(
                        text = "Phonetics: /${phrase.phoneticHint}/",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Meaning
                    Text(
                        text = "Meaning: ${phrase.meaning}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Example
                    Text(
                        text = "Example: \"${phrase.exampleUsage}\"",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action: Test my speaking
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isBeingTested) PrimaryBlue
                                    else PrimaryBlue.copy(alpha = 0.12f)
                                )
                                .clickable {
                                    if (isBeingTested) {
                                        viewModel.setTestPhrase(null)
                                    } else {
                                        viewModel.setTestPhrase(phrase)
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = if (isBeingTested) Color.White else PrimaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isBeingTested) "Close Test" else "Test Pronunciation",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isBeingTested) Color.White else PrimaryBlue
                                )
                            }
                        }
                    }

                    // Expanded Test Area
                    AnimatedVisibility(visible = isBeingTested) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            SpeakControlBar(
                                speechState = speechState,
                                soundLevel = soundLevel,
                                isSpeakingTts = isTtsSpeaking,
                                onStartListening = { viewModel.startListeningForCurrentTarget(phrase.englishText) },
                                onStopListening = { viewModel.stopListening() },
                                onSimulateSpeech = { viewModel.evaluateManualSpeech(phrase.englishText, it) },
                                suggestedSimulatedText = phrase.englishText
                            )

                            if (lastEval != null) {
                                Spacer(modifier = Modifier.height(14.dp))
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
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
