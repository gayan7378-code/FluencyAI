package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WordAccuracyResult
import com.example.data.model.WordMatchStatus
import com.example.ui.theme.ScoreExcellent
import com.example.ui.theme.ScoreFair
import com.example.ui.theme.ScoreNeedsWork

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordHighlightView(
    words: List<WordAccuracyResult>,
    onWordClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            words.forEach { wordResult ->
                val (bgTint, borderColor, textColor, statusIcon) = when (wordResult.status) {
                    WordMatchStatus.PERFECT -> listOf(
                        ScoreExcellent.copy(alpha = 0.15f),
                        ScoreExcellent.copy(alpha = 0.6f),
                        ScoreExcellent,
                        Icons.Default.Check
                    )
                    WordMatchStatus.CLOSE -> listOf(
                        ScoreFair.copy(alpha = 0.15f),
                        ScoreFair.copy(alpha = 0.6f),
                        ScoreFair,
                        null
                    )
                    WordMatchStatus.MISSED -> listOf(
                        ScoreNeedsWork.copy(alpha = 0.15f),
                        ScoreNeedsWork.copy(alpha = 0.6f),
                        ScoreNeedsWork,
                        Icons.Default.Close
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgTint as Color)
                        .border(1.dp, borderColor as Color, RoundedCornerShape(8.dp))
                        .clickable { onWordClick(wordResult.targetWord) }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = wordResult.targetWord,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            ),
                            color = textColor as Color
                        )
                        if (statusIcon != null) {
                            Icon(
                                imageVector = statusIcon as androidx.compose.ui.graphics.vector.ImageVector,
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Tap any word to hear its native pronunciation",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
