package com.prostuti.app.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val tabBackStack = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) {
        mutableStateListOf(0)
    }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    fun selectTab(index: Int) {
        if (selectedTabIndex != index) {
            selectedTabIndex = index
            tabBackStack.add(index)
        }
    }

    BackHandler {
        if (tabBackStack.size > 1) {
            tabBackStack.removeAt(tabBackStack.lastIndex)
            selectedTabIndex = tabBackStack.last()
        } else if (selectedTabIndex != 0) {
            selectedTabIndex = 0
            tabBackStack.clear()
            tabBackStack.add(0)
        } else {
            showExitDialog = true
        }
    }

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
                        onClick = { selectTab(index) },
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
                    onNavigateToPractice = { selectTab(1) },
                    onNavigateToQuestionBank = { selectTab(2) },
                    onNavigateToExam = onNavigateToExam,
                    onNavigateToHistory = onNavigateToHistory,
                    onNavigateToProfile = { selectTab(3) },
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

    if (showExitDialog) {
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = "অ্যাপ বন্ধ করতে চান?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "আপনি কি নিশ্চিতভাবে Prostuti অ্যাপ থেকে বের হতে চান?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        (context as? Activity)?.finish()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("হ্যাঁ, বের হন", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showExitDialog = false },
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("না", fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }
}
