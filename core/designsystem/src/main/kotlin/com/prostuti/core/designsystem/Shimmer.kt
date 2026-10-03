package com.prostuti.core.designsystem

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Modifier extension that applies a smooth shimmering gradient effect across a clipped shape.
 * Ideal for skeleton loading screens in place of generic spinners.
 */
fun Modifier.shimmerEffect(
    shape: Shape = RoundedCornerShape(8.dp),
    baseColor: Color? = null,
    highlightColor: Color? = null,
    durationMillis: Int = 1200,
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerTranslate",
    )

    val actualBaseColor = baseColor ?: MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
    val actualHighlightColor = highlightColor ?: MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)

    val brush = Brush.linearGradient(
        colors = listOf(
            actualBaseColor,
            actualHighlightColor,
            actualBaseColor,
        ),
        start = Offset(translateAnim - 450f, translateAnim - 450f),
        end = Offset(translateAnim, translateAnim),
    )

    this
        .clip(shape)
        .background(brush)
}
