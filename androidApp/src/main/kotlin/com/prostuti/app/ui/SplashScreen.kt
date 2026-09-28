package com.prostuti.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.prostuti.core.common.SessionStore
import com.prostuti.core.model.Role

/**
 * Splash route — checks the SessionStore once at composition and asks
 * the host NavGraph to navigate(replace) into the right next destination.
 * Never displayed for more than the single frame that readSession takes.
 */
@Composable
fun SplashScreen(
    sessionStore: SessionStore,
    onResolved: (next: SplashNext) -> Unit,
) {
    LaunchedEffect(Unit) {
        val next = if (sessionStore.hasSession()) {
            SplashNext.Main
        } else {
            SplashNext.Login
        }
        onResolved(next)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

enum class SplashNext { Login, Main }