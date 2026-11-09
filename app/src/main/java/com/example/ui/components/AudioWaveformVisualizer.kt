package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AudioWaveformVisualizer(
    isListening: Boolean,
    isSpeaking: Boolean,
    soundLevel: Float, // 0f to 1f
    modifier: Modifier = Modifier,
    barCount: Int = 9,
    baseHeight: Dp = 12.dp,
    maxHeight: Dp = 48.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val animHeight = remember { Animatable(baseHeight.value) }

            LaunchedEffect(isListening, isSpeaking, soundLevel) {
                if (isListening) {
                    val multiplier = (soundLevel * 2f).coerceIn(0.2f, 1.0f)
                    val variance = kotlin.math.sin(i.toDouble() + System.currentTimeMillis() / 200.0).toFloat()
                    val target = baseHeight.value + (maxHeight.value - baseHeight.value) * multiplier * (0.6f + 0.4f * variance.coerceAtLeast(0f))
                    animHeight.animateTo(
                        targetValue = target.coerceIn(baseHeight.value, maxHeight.value),
                        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)
                    )
                } else if (isSpeaking) {
                    val target = baseHeight.value + (maxHeight.value - baseHeight.value) * (0.4f + (i % 3) * 0.25f)
                    animHeight.animateTo(
                        targetValue = target,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 250 + i * 40, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                } else {
                    animHeight.animateTo(
                        targetValue = baseHeight.value,
                        animationSpec = tween(durationMillis = 200)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(animHeight.value.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}
