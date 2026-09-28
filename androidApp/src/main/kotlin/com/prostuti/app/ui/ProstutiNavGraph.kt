package com.prostuti.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prostuti.core.common.SessionStore
import com.prostuti.core.designsystem.ProstutiBadge
import com.prostuti.core.designsystem.ProstutiButton
import com.prostuti.feature.auth.presentation.AuthUiState
import com.prostuti.feature.auth.presentation.AuthViewModel
import com.prostuti.feature.auth.ui.AuthScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

object Routes {
    const val Splash = "splash"
    const val Login = "auth/login"
    const val Register = "auth/register"
    const val Main = "main"
    const val Admin = "admin"
}

@Composable
fun ProstutiNavGraph() {
    val navController = rememberNavController()
    val sessionStore: SessionStore = koinInject()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash,
    ) {
        composable(Routes.Splash) {
            SplashScreen(
                sessionStore = sessionStore,
                onResolved = { next ->
                    val target = when (next) {
                        SplashNext.Login -> Routes.Login
                        SplashNext.Main -> Routes.Main
                    }
                    navController.navigate(target) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.Login) {
            val viewModel: AuthViewModel = koinViewModel { parametersOf(AuthUiState.Mode.LOGIN) }
            AuthScreen(
                mode = AuthUiState.Mode.LOGIN,
                viewModel = viewModel,
                onAuthenticated = { goAfterLogin(navController) },
                onSwitchMode = { navController.navigate(Routes.Register) },
            )
        }

        composable(Routes.Register) {
            val viewModel: AuthViewModel = koinViewModel { parametersOf(AuthUiState.Mode.REGISTER) }
            AuthScreen(
                mode = AuthUiState.Mode.REGISTER,
                viewModel = viewModel,
                onAuthenticated = { goAfterLogin(navController) },
                onSwitchMode = { navController.popBackStack() },
            )
        }

        composable(Routes.Main) {
            MainScreen(
                sessionStore = sessionStore,
                onLoggedOut = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Main) { inclusive = true }
                    }
                },
                onNavigateToAdmin = {
                    navController.navigate(Routes.Admin)
                },
            )
        }

        composable(Routes.Admin) {
            AdminPlaceholderScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private fun goAfterLogin(navController: NavHostController) {
    navController.navigate(Routes.Main) {
        popUpTo(Routes.Login) { inclusive = true }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminPlaceholderScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("অ্যাডমিন প্যানেল", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "পেছনে")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProstutiBadge(
                text = "ROLE.ADMIN REQUIRED",
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "অ্যাডমিন পোর্টাল",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "CSV ইমপোর্ট এবং প্রশ্ন ব্যবস্থাপনা ফিচারটি পরবর্তী ধাপে সংযুক্ত করা হবে।",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(24.dp))
            ProstutiButton(
                text = "হোমে ফিরে যান",
                onClick = onBack,
            )
        }
    }
}