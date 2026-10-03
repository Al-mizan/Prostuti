package com.prostuti.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prostuti.core.common.SessionStore
import com.prostuti.core.designsystem.MascotPresets
import com.prostuti.core.designsystem.ProstutiCrimson
import com.prostuti.core.designsystem.ProstutiCrimsonDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Animated Splash Screen.
 * Displays brand Crimson gradient, animated Prostuti mascot, and Bengali branding
 * with an 800ms grace window to eliminate jarring transitions.
 */
@Composable
fun SplashScreen(
    sessionStore: SessionStore,
    onResolved: (next: SplashNext) -> Unit,
) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            )
        }

        // Enforce smooth minimum display duration (800ms)
        delay(800L)

        val next = if (sessionStore.hasSession()) {
            SplashNext.Main
        } else {
            SplashNext.Login
        }
        onResolved(next)
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            ProstutiCrimson,
            ProstutiCrimsonDark,
            Color(0xFF2E020A),
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(32.dp)
                .scale(scale.value)
                .alpha(alpha.value),
        ) {
            // Mascot Emblem
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.12f),
                modifier = Modifier.size(108.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text(
                        text = MascotPresets.list.first().emoji,
                        fontSize = 52.sp,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // App Name (Bengali)
            Text(
                text = "প্রস্তুতি",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 2.sp,
            )

            Spacer(Modifier.height(8.dp))

            // Tagline / Subtitle
            Text(
                text = "বিসিএস প্রিলিমিনারি পূর্ণাঙ্গ প্রস্তুতি",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
            )
        }

        // Bottom Brand / Version Subtext
        Text(
            text = "ভার্সন ১.০ • বিসিএস স্পেশাল",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
        )
    }
}

enum class SplashNext { Login, Main }