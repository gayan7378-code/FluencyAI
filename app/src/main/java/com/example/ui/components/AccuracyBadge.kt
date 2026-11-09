package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ScoreExcellent
import com.example.ui.theme.ScoreFair
import com.example.ui.theme.ScoreGood
import com.example.ui.theme.ScoreNeedsWork

@Composable
fun AccuracyBadge(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    val (badgeColor, label) = when {
        score >= 90 -> Pair(ScoreExcellent, "Native")
        score >= 75 -> Pair(ScoreGood, "Clear")
        score >= 50 -> Pair(ScoreFair, "Fair")
        else -> Pair(ScoreNeedsWork, "Practice")
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(badgeColor.copy(alpha = 0.15f))
            .border(2.5.dp, badgeColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (size > 60.dp) 18.sp else 14.sp
                ),
                color = badgeColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                ),
                color = badgeColor
            )
        }
    }
}

@Composable
fun AccuracyPill(
    score: Int,
    modifier: Modifier = Modifier
) {
    val badgeColor = when {
        score >= 90 -> ScoreExcellent
        score >= 75 -> ScoreGood
        score >= 50 -> ScoreFair
        else -> ScoreNeedsWork
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(badgeColor.copy(alpha = 0.15f))
            .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$score% Accuracy",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = badgeColor
        )
    }
}
