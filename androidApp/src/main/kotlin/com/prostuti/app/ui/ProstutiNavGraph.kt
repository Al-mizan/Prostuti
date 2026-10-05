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
import android.net.Uri
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
    const val Exam = "exam?session={session}"
    const val History = "history"

    fun exam(session: String? = null): String {
        return if (!session.isNullOrBlank()) {
            "exam?session=${Uri.encode(session)}"
        } else {
            "exam"
        }
    }
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
                serverClientId = com.prostuti.app.BuildConfig.WEB_GOOGLE_CLIENT_ID,
            )
        }

        composable(Routes.Register) {
            val viewModel: AuthViewModel = koinViewModel { parametersOf(AuthUiState.Mode.REGISTER) }
            AuthScreen(
                mode = AuthUiState.Mode.REGISTER,
                viewModel = viewModel,
                onAuthenticated = { goAfterLogin(navController) },
                onSwitchMode = { navController.popBackStack() },
                serverClientId = com.prostuti.app.BuildConfig.WEB_GOOGLE_CLIENT_ID,
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
                onNavigateToExam = { session ->
                    navController.navigate(Routes.exam(session))
                },
                onNavigateToHistory = {
                    navController.navigate(Routes.History)
                },
            )
        }

        composable(Routes.Admin) {
            val viewModel: com.prostuti.feature.admin.presentation.AdminViewModel = koinViewModel()
            com.prostuti.feature.admin.ui.AdminScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.Exam,
            arguments = listOf(
                navArgument("session") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val rawSession = backStackEntry.arguments?.getString("session")
            val sessionName = rawSession?.takeIf { it.isNotBlank() && it != "{session}" }
            val viewModel: com.prostuti.feature.exam.presentation.ExamViewModel = koinViewModel {
                parametersOf(sessionName)
            }
            com.prostuti.feature.exam.ui.ExamScreen(
                viewModel = viewModel,
                initialSessionName = sessionName,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.History) {
            val viewModel: com.prostuti.feature.history.presentation.HistoryViewModel = koinViewModel()
            com.prostuti.feature.history.ui.HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

private fun goAfterLogin(navController: NavHostController) {
    navController.navigate(Routes.Main) {
        popUpTo(Routes.Login) { inclusive = true }
    }
}