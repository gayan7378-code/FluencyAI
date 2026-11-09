package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PresentationScoreRecord
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.PrimaryBlue

@Composable
fun PresentationProgressGraph(
    records: List<PresentationScoreRecord>,
    modifier: Modifier = Modifier
) {
    if (records.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val maxScore = 10.0f
    val minScore = 5.0f

    val average = records.map { it.scoreOutOf10 }.average().toFloat()
    val personalBest = records.maxOfOrNull { it.scoreOutOf10 } ?: 0f

    CardContainer(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Presentation Score History",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Score progression out of 10.0",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ScoreStatPill(label = "Avg", value = String.format("%.1f", average), color = PrimaryBlue)
                    ScoreStatPill(label = "Best", value = String.format("%.1f", personalBest), color = AccentGreen)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Graph
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .pointerInput(records) {
                            detectTapGestures { offset ->
                                val stepX = size.width / (records.size.coerceAtLeast(2) - 1)
                                val tappedIndex = ((offset.x + stepX / 2) / stepX).toInt().coerceIn(0, records.size - 1)
                                selectedIndex = tappedIndex
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val n = records.size

                    val stepX = if (n > 1) width / (n - 1) else width
                    val points = records.mapIndexed { i, rec ->
                        val x = if (n > 1) i * stepX else width / 2
                        val ratio = (rec.scoreOutOf10 - minScore) / (maxScore - minScore)
                        val y = height - (ratio * (height - 30.dp.toPx()) + 15.dp.toPx())
                        Offset(x, y)
                    }

                    // Draw reference grid dashed lines (at 7.0 and 9.0)
                    val y7 = height - (((7f - minScore) / (maxScore - minScore)) * (height - 30.dp.toPx()) + 15.dp.toPx())
                    val y9 = height - (((9f - minScore) / (maxScore - minScore)) * (height - 30.dp.toPx()) + 15.dp.toPx())
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.2f),
                        start = Offset(0f, y7),
                        end = Offset(width, y7),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.2f),
                        start = Offset(0f, y9),
                        end = Offset(width, y9),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    // Draw Gradient Fill under line
                    val fillPath = Path().apply {
                        moveTo(points.first().x, height)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, height)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2563EB).copy(alpha = 0.25f),
                                Color(0xFF2563EB).copy(alpha = 0.02f)
                            )
                        )
                    )

                    // Draw Connecting Line
                    val strokePath = Path().apply {
                        points.forEachIndexed { i, p ->
                            if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                        }
                    }
                    drawPath(
                        path = strokePath,
                        color = Color(0xFF2563EB),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Draw Points
                    points.forEachIndexed { i, p ->
                        val isSelected = selectedIndex == i
                        drawCircle(
                            color = Color.White,
                            radius = if (isSelected) 7.dp.toPx() else 5.dp.toPx(),
                            center = p
                        )
                        drawCircle(
                            color = if (isSelected) Color(0xFF10B981) else Color(0xFF2563EB),
                            radius = if (isSelected) 5.dp.toPx() else 3.5.dp.toPx(),
                            center = p
                        )
                    }
                }
            }

            // X-Axis Labels Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                records.forEachIndexed { i, r ->
                    val isSelected = selectedIndex == i
                    Text(
                        text = r.dateLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        ),
                        color = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Interactive Tooltip if tapped
            if (selectedIndex != null) {
                val record = records[selectedIndex!!]
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = record.topic,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${record.scoreOutOf10}/10",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = AccentGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
    ) {
        content()
    }
}

@Composable
fun ScoreStatPill(
    label: String,
    value: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = color)
            Text(text = value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = color)
        }
    }
}
