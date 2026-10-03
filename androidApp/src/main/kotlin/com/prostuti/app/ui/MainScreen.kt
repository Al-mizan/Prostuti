package com.prostuti.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.prostuti.core.common.SessionStore
import com.prostuti.feature.practice.presentation.PracticeViewModel
import com.prostuti.feature.practice.ui.PracticeScreen
import com.prostuti.feature.profile.presentation.ProfileViewModel
import com.prostuti.feature.profile.ui.ProfileScreen
import com.prostuti.feature.questionbank.presentation.QuestionBankViewModel
import com.prostuti.feature.questionbank.ui.QuestionBankScreen
import org.koin.androidx.compose.koinViewModel

enum class MainTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME("হোম", Icons.Default.Home, Icons.Outlined.Home),
    PRACTICE("অনুশীলন", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Filled.MenuBook),
    QUESTION_BANK("প্রশ্ন ব্যাংক", Icons.AutoMirrored.Filled.LibraryBooks, Icons.AutoMirrored.Outlined.LibraryBooks),
    PROFILE("প্রোফাইল", Icons.Default.Person, Icons.Outlined.Person),
}

@Composable
fun MainScreen(
    sessionStore: SessionStore,
    onLoggedOut: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToExam: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val profileViewModel: ProfileViewModel = koinViewModel()
    val questionBankViewModel: QuestionBankViewModel = koinViewModel()
    val practiceViewModel: PracticeViewModel = koinViewModel()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                MainTab.entries.forEachIndexed { index, tab ->
                    val isSelected = selectedTabIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                            )
                        },
                        label = { Text(tab.title) },
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> HomeScreen(
                    sessionStore = sessionStore,
                    profileViewModel = profileViewModel,
                    onNavigateToPractice = { selectedTabIndex = 1 },
                    onNavigateToQuestionBank = { selectedTabIndex = 2 },
                    onNavigateToExam = onNavigateToExam,
                    onNavigateToHistory = onNavigateToHistory,
                    onNavigateToProfile = { selectedTabIndex = 3 },
                    onNavigateToAdmin = onNavigateToAdmin,
                )
                1 -> PracticeScreen(viewModel = practiceViewModel)
                2 -> QuestionBankScreen(
                    viewModel = questionBankViewModel,
                    onStartExam = { sessionName -> onNavigateToExam() },
                )
                3 -> ProfileScreen(
                    viewModel = profileViewModel,
                    onLoggedOut = onLoggedOut,
                    onNavigateToHistory = onNavigateToHistory,
                )
            }
        }
    }
}
