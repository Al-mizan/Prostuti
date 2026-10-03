package com.prostuti.feature.exam.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prostuti.core.designsystem.ProstutiBadge
import com.prostuti.core.designsystem.ProstutiButton
import com.prostuti.core.designsystem.ProstutiCard
import com.prostuti.core.designsystem.ProstutiProgressBar
import com.prostuti.core.model.ExamQuestionResultDto
import com.prostuti.core.model.LeaderboardEntryDto
import com.prostuti.core.model.Option
import com.prostuti.core.model.Subject
import com.prostuti.feature.exam.presentation.ExamUiEvent
import com.prostuti.feature.exam.presentation.ExamUiState
import com.prostuti.feature.exam.presentation.ExamViewModel
import com.prostuti.feature.exam.presentation.ResultTab

private val EmeraldGreen = Color(0xFF017A47)
private val LightEmeraldBg = Color(0xFFE8F5E9)
private val CrimsonRed = Color(0xFFDC143C)
private val LightCrimsonBg = Color(0xFFFFEBEE)
private val AmberFlag = Color(0xFFD97706)
private val LightAmberBg = Color(0xFFFEF3C7)

@Composable
fun ExamScreen(
    viewModel: ExamViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when (val state = uiState) {
            is ExamUiState.Setup -> {
                ExamSetupContent(
                    state = state,
                    onSelectSession = { viewModel.onEvent(ExamUiEvent.SelectSession(it)) },
                    onSelectQuestionCount = { viewModel.onEvent(ExamUiEvent.SelectQuestionCount(it)) },
                    onSelectDuration = { viewModel.onEvent(ExamUiEvent.SelectDuration(it)) },
                    onStartExam = { viewModel.onEvent(ExamUiEvent.StartExam) },
                    onBack = onNavigateBack,
                )
            }

            is ExamUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            is ExamUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            text = "একটি ত্রুটি ঘটেছে",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        ProstutiButton(
                            text = "পুনরায় চেষ্টা করুন",
                            onClick = { viewModel.onEvent(ExamUiEvent.ResetToSetup) },
                        )
                    }
                }
            }

            is ExamUiState.ActiveExam -> {
                ActiveExamContent(
                    state = state,
                    onSelectOption = { viewModel.onEvent(ExamUiEvent.SelectOption(it)) },
                    onClearOption = { viewModel.onEvent(ExamUiEvent.ClearOption) },
                    onToggleFlag = { viewModel.onEvent(ExamUiEvent.ToggleFlag) },
                    onJumpToQuestion = { viewModel.onEvent(ExamUiEvent.JumpToQuestion(it)) },
                    onNext = { viewModel.onEvent(ExamUiEvent.NextQuestion) },
                    onPrevious = { viewModel.onEvent(ExamUiEvent.PreviousQuestion) },
                    onTogglePalette = { viewModel.onEvent(ExamUiEvent.TogglePalette) },
                    onShowSubmitDialog = { viewModel.onEvent(ExamUiEvent.ShowConfirmSubmitDialog) },
                    onDismissSubmitDialog = { viewModel.onEvent(ExamUiEvent.DismissConfirmSubmitDialog) },
                    onConfirmSubmit = { viewModel.onEvent(ExamUiEvent.ConfirmSubmit) },
                )
            }

            is ExamUiState.ResultSummary -> {
                ExamResultContent(
                    state = state,
                    onSwitchTab = { viewModel.onEvent(ExamUiEvent.SwitchResultTab(it)) },
                    onRetake = { viewModel.onEvent(ExamUiEvent.RetakeExam) },
                    onClose = {
                        viewModel.onEvent(ExamUiEvent.ResetToSetup)
                        onNavigateBack()
                    },
                )
            }
        }
    }
}

// ==========================================
// 1. Exam Setup View
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamSetupContent(
    state: ExamUiState.Setup,
    onSelectSession: (String) -> Unit,
    onSelectQuestionCount: (Int) -> Unit,
    onSelectDuration: (Int) -> Unit,
    onStartExam: () -> Unit,
    onBack: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("টাইমড বিসিএস মক টেস্ট", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Header Banner
            ProstutiCard(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "বাস্তব পরীক্ষার অভিজ্ঞতা",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "টাইমার ও নেগেটিভ মার্কিংসহ পূর্ণাঙ্গ প্রস্তুতি",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // BCS Session Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "বিসিএস পরীক্ষার সেশন নির্বাচন করুন:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value = state.selectedSession.ifBlank { "সেশন লোড হচ্ছে..." },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("সর্বশেষ বিসিএস সমন্বিত মক টেস্ট (সব সেশন)") },
                            onClick = {
                                onSelectSession("ALL")
                                expanded = false
                            },
                        )
                        state.availableSessions.forEach { session ->
                            DropdownMenuItem(
                                text = { Text("${session.sessionName} (${session.totalQuestions.toBanglaDigits()}টি প্রশ্ন)") },
                                onClick = {
                                    onSelectSession(session.sessionName)
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }

            // Question Count Filter Bar
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "প্রশ্নের সংখ্যা নির্বাচন করুন:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(25, 50, 100, 200).forEach { count ->
                        val isSelected = count == state.selectedQuestionCount
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectQuestionCount(count) },
                            label = { Text("${count.toBanglaDigits()}টি", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        )
                    }
                }
            }

            // Duration Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "সময় নির্ধারণ করুন:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(15, 30, 60, 120).forEach { minutes ->
                        val isSelected = minutes == state.selectedDurationMinutes
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectDuration(minutes) },
                            label = { Text("${minutes.toBanglaDigits()} মিনিট", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        )
                    }
                }
            }

            // Rules & Marking Policy Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "পরীক্ষার নিয়মাবলি ও মূল্যায়ন পদ্ধতি",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    RuleBulletPoint("প্রতি সঠিক উত্তরের জন্য পাবেন: +১.০০ নম্বর")
                    RuleBulletPoint("প্রতি ভুল উত্তরের জন্য কাটা যাবে: -০.৫০ নম্বর (নেগেটিভ মার্কিং)")
                    RuleBulletPoint("অনুত্তরিত প্রশ্নের জন্য কোনো নম্বর কাটা যাবে না")
                    RuleBulletPoint("পরীক্ষার সময় শেষ হওয়ার সাথে সাথে স্বয়ংক্রিয়ভাবে উত্তরপত্র জমা হবে")
                    RuleBulletPoint("প্যালেট ব্যবহার করে যেকোনো প্রশ্নে দ্রুত জাম্প করতে পারবেন এবং রিভিউয়ের জন্য ফ্ল্যাগ করতে পারবেন")
                }
            }

            Spacer(Modifier.height(10.dp))

            ProstutiButton(
                text = "মক টেস্ট শুরু করুন",
                onClick = onStartExam,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun RuleBulletPoint(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp,
        )
    }
}

// ==========================================
// 2. Active Exam Runner View
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActiveExamContent(
    state: ExamUiState.ActiveExam,
    onSelectOption: (Option) -> Unit,
    onClearOption: () -> Unit,
    onToggleFlag: () -> Unit,
    onJumpToQuestion: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onTogglePalette: () -> Unit,
    onShowSubmitDialog: () -> Unit,
    onDismissSubmitDialog: () -> Unit,
    onConfirmSubmit: () -> Unit,
) {
    val question = state.currentQuestion
    val selectedOption = state.answers[question.id]
    val isFlagged = state.flaggedQuestions.contains(question.id)
    val progress = (state.currentIndex + 1).toFloat() / state.totalQuestions.toFloat()
    val isTimeLow = state.remainingSeconds < 300 // under 5 minutes

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                shadowElevation = 2.dp,
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        // Timer Display Pill
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isTimeLow) LightCrimsonBg else MaterialTheme.colorScheme.primaryContainer,
                            border = BorderStroke(1.dp, if (isTimeLow) CrimsonRed else MaterialTheme.colorScheme.primary),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            ) {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = "Timer",
                                    tint = if (isTimeLow) CrimsonRed else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = formatSecondsToTime(state.remainingSeconds),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTimeLow) CrimsonRed else MaterialTheme.colorScheme.primary,
                                )
                            }
                        }

                        // Question Palette Button
                        OutlinedButton(
                            onClick = onTogglePalette,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp),
                        ) {
                            Icon(
                                Icons.Default.GridView,
                                contentDescription = "Palette",
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "${state.answeredCount.toBanglaDigits()}/${state.totalQuestions.toBanglaDigits()}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        // Finish / Submit Button
                        Button(
                            onClick = onShowSubmitDialog,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp),
                        ) {
                            Text("জমা দিন", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    ProstutiProgressBar(progress = progress)
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                shadowElevation = 8.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = onPrevious,
                        enabled = state.hasPrevious,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("পূর্ববর্তী")
                    }

                    if (selectedOption != null) {
                        TextButton(
                            onClick = onClearOption,
                            modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                        ) {
                            Text("উত্তর বাতিল", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    Button(
                        onClick = onNext,
                        enabled = state.hasNext,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                    ) {
                        Text("পরবর্তী")
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            // Question Sub-header with Subject and Flag toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ProstutiBadge(
                        text = "প্রশ্ন ${(state.currentIndex + 1).toBanglaDigits()} / ${state.totalQuestions.toBanglaDigits()}",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    ProstutiBadge(
                        text = question.subject.toBanglaName(),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Flag / Review toggle
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isFlagged) LightAmberBg else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (isFlagged) AmberFlag else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.clickable { onToggleFlag() },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Icon(
                            if (isFlagged) Icons.Default.Flag else Icons.Default.OutlinedFlag,
                            contentDescription = "Flag",
                            tint = if (isFlagged) AmberFlag else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isFlagged) "চিহ্নিত" else "রিভিউ",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isFlagged) AmberFlag else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isFlagged) FontWeight.Bold else FontWeight.Normal,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Topic text if available
            val topic = question.topic
            if (!topic.isNullOrBlank()) {
                Text(
                    text = topic,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(4.dp))
            }

            // Question Text
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 26.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(Modifier.height(24.dp))

            // Options list (ক, খ, গ, ঘ) — No immediate feedback during exam!
            val options = listOf(
                Option.A to question.optionA,
                Option.B to question.optionB,
                Option.C to question.optionC,
                Option.D to question.optionD,
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { (optionKey, optionText) ->
                    val isSelected = selectedOption == optionKey

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f),
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectOption(optionKey) },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = CircleShape,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = optionKey.toBanglaLabel(),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                )
                            }

                            Spacer(Modifier.width(14.dp))

                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                modifier = Modifier.weight(1f),
                            )

                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Question Palette Bottom Sheet
    if (state.isPaletteVisible) {
        ModalBottomSheet(
            onDismissRequest = onTogglePalette,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "প্রশ্ন প্যালেট",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(onClick = onTogglePalette) {
                        Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন")
                    }
                }

                // Palette Legend
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    LegendItem(color = EmeraldGreen, label = "উত্তর: ${state.answeredCount.toBanglaDigits()}")
                    LegendItem(color = AmberFlag, label = "চিহ্নিত: ${state.flaggedCount.toBanglaDigits()}")
                    LegendItem(color = MaterialTheme.colorScheme.outlineVariant, label = "বাকি: ${state.unansweredCount.toBanglaDigits()}")
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Grid of 1..N bubbles
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                ) {
                    itemsIndexed(state.questions) { index, q ->
                        val isAnswered = state.answers.containsKey(q.id)
                        val isFlaggedQ = state.flaggedQuestions.contains(q.id)
                        val isCurrent = index == state.currentIndex

                        val (bgColor, textColor) = when {
                            isFlaggedQ -> AmberFlag to Color.White
                            isAnswered -> EmeraldGreen to Color.White
                            else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurface
                        }

                        Surface(
                            shape = CircleShape,
                            color = bgColor,
                            border = if (isCurrent) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .size(44.dp)
                                .clickable { onJumpToQuestion(index) },
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (index + 1).toBanglaDigits(),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // Submit Confirmation Dialog
    if (state.isConfirmSubmitDialogOpen) {
        AlertDialog(
            onDismissRequest = onDismissSubmitDialog,
            title = { Text("মক টেস্ট জমা দেবেন?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("আপনি কি নিশ্চিতভাবে এই মক টেস্টটি সম্পন্ন করে জমা দিতে চান?")
                    Spacer(Modifier.height(4.dp))
                    Text("• মোট প্রশ্ন: ${state.totalQuestions.toBanglaDigits()}টি")
                    Text("• উত্তর দেওয়া হয়েছে: ${state.answeredCount.toBanglaDigits()}টি", color = EmeraldGreen, fontWeight = FontWeight.SemiBold)
                    Text("• অনুত্তরিত রয়েছে: ${state.unansweredCount.toBanglaDigits()}টি", color = CrimsonRed, fontWeight = FontWeight.SemiBold)
                    if (state.flaggedCount > 0) {
                        Text("• পর্যালোচনার জন্য চিহ্নিত: ${state.flaggedCount.toBanglaDigits()}টি", color = AmberFlag, fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onConfirmSubmit,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Text("হ্যাঁ, জমা দিন")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismissSubmitDialog) {
                    Text("পরীক্ষা চালিয়ে যান")
                }
            }
        )
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ==========================================
// 3. Exam Result & Leaderboard View
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamResultContent(
    state: ExamUiState.ResultSummary,
    onSwitchTab: (ResultTab) -> Unit,
    onRetake: () -> Unit,
    onClose: () -> Unit,
) {
    val result = state.result
    val accuracy = if (result.totalQuestions > 0) {
        (result.correctCount * 100) / result.totalQuestions
    } else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("মক টেস্ট ফলাফল ও পর্যালোচনা", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Summary Header Card
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = result.examSession,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = result.score.toBanglaDigits(),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = " / ${result.totalQuestions.toBanglaDigits()}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp),
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Detailed breakdown stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        ResultStatItem(label = "সঠিক (+১)", value = result.correctCount.toBanglaDigits(), color = EmeraldGreen)
                        ResultStatItem(label = "ভুল (-০.৫০)", value = result.incorrectCount.toBanglaDigits(), color = CrimsonRed)
                        ResultStatItem(label = "ছেড়েছেন", value = result.skippedCount.toBanglaDigits(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        ResultStatItem(label = "নির্ভুলতা", value = "${accuracy.toBanglaDigits()}%", color = MaterialTheme.colorScheme.primary)
                        ResultStatItem(label = "সময়", value = formatSecondsToTime(result.timeTakenSeconds), color = AmberFlag)
                    }
                }
            }

            // Tab Row: Question Review vs Leaderboard
            PrimaryTabRow(selectedTabIndex = state.activeTab.ordinal) {
                Tab(
                    selected = state.activeTab == ResultTab.ANALYSIS,
                    onClick = { onSwitchTab(ResultTab.ANALYSIS) },
                    text = { Text("প্রশ্ন সমাধান ও ব্যাখ্যা") },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp)) },
                )
                Tab(
                    selected = state.activeTab == ResultTab.LEADERBOARD,
                    onClick = { onSwitchTab(ResultTab.LEADERBOARD) },
                    text = { Text("মেধাতালিকা / র‍্যাংক") },
                    icon = { Icon(Icons.Default.Leaderboard, contentDescription = null, modifier = Modifier.size(18.dp)) },
                )
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                when (state.activeTab) {
                    ResultTab.ANALYSIS -> {
                        QuestionReviewList(questions = result.questions)
                    }
                    ResultTab.LEADERBOARD -> {
                        LeaderboardList(
                            entries = state.leaderboard,
                            isLoading = state.isLoadingLeaderboard,
                        )
                    }
                }
            }

            // Bottom Actions
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onRetake,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("আবার পরীক্ষা দিন")
                    }

                    Button(
                        onClick = onClose,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                    ) {
                        Text("হোমে ফিরে যান")
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun QuestionReviewList(questions: List<ExamQuestionResultDto>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        itemsIndexed(questions) { index, item ->
            ReviewQuestionCard(index = index + 1, item = item)
        }
    }
}

@Composable
private fun ReviewQuestionCard(index: Int, item: ExamQuestionResultDto) {
    val statusColor = when {
        item.isCorrect -> EmeraldGreen
        item.selectedOption != null -> CrimsonRed
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    val statusBg = when {
        item.isCorrect -> LightEmeraldBg
        item.selectedOption != null -> LightCrimsonBg
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProstutiBadge(
                    text = "প্রশ্ন ${index.toBanglaDigits()}",
                    containerColor = statusBg,
                    contentColor = statusColor,
                )

                Text(
                    text = when {
                        item.isCorrect -> "সঠিক উত্তর (+১.০০)"
                        item.selectedOption != null -> "ভুল উত্তর (-০.৫০)"
                        else -> "অনুত্তরিত (০.০০)"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = item.questionText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp,
            )

            Spacer(Modifier.height(14.dp))

            // 4 options review
            val options = listOf(
                Option.A to item.optionA,
                Option.B to item.optionB,
                Option.C to item.optionC,
                Option.D to item.optionD,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { (key, text) ->
                    val isCorrectKey = key == item.correctOption
                    val isUserKey = key == item.selectedOption

                    val (rowBg, rowBorder, rowTextColor) = when {
                        isCorrectKey -> Triple(LightEmeraldBg, EmeraldGreen, EmeraldGreen)
                        isUserKey && !item.isCorrect -> Triple(LightCrimsonBg, CrimsonRed, CrimsonRed)
                        else -> Triple(Color.Transparent, Color.Transparent, MaterialTheme.colorScheme.onSurface)
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = rowBg,
                        border = if (rowBorder != Color.Transparent) BorderStroke(1.dp, rowBorder) else null,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${key.toBanglaLabel()})",
                                fontWeight = FontWeight.Bold,
                                color = rowTextColor,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = rowTextColor,
                                fontWeight = if (isCorrectKey || isUserKey) FontWeight.SemiBold else FontWeight.Normal,
                                modifier = Modifier.weight(1f),
                            )
                            if (isCorrectKey) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                            } else if (isUserKey) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Explanation
            val explanation = item.explanation
            if (!explanation.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("ব্যাখ্যা:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = explanation,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardList(
    entries: List<LeaderboardEntryDto>,
    isLoading: Boolean,
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (entries.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "এখনো কোনো র‍্যাংকিং রেকর্ড নেই।",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(entries) { _, entry ->
                LeaderboardRow(entry = entry)
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntryDto) {
    val medalColor = when (entry.rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(medalColor.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center,
            ) {
                if (entry.rank in 1..3) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = medalColor,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    Text(
                        text = entry.rank.toBanglaDigits(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.userName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "সময়: ${formatSecondsToTime(entry.timeTakenSeconds)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = "${entry.score.toBanglaDigits()} নম্বর",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

// ==========================================
// Helper Extension Functions
// ==========================================

private fun Int.toBanglaDigits(): String {
    val banglaDigits = arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    return this.toString().map { if (it in '0'..'9') banglaDigits[it - '0'] else it }.joinToString("")
}

private fun Double.toBanglaDigits(): String {
    val banglaDigits = arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    val formatted = if (this % 1.0 == 0.0) this.toInt().toString() else String.format(java.util.Locale.US, "%.1f", this)
    return formatted.map {
        when (it) {
            in '0'..'9' -> banglaDigits[it - '0']
            '.' -> '.'
            else -> it
        }
    }.joinToString("")
}

private fun formatSecondsToTime(totalSeconds: Int): String {
    val mins = totalSeconds / 60
    val secs = totalSeconds % 60
    val banglaMins = mins.toBanglaDigits().padStart(2, '০')
    val banglaSecs = secs.toBanglaDigits().padStart(2, '০')
    return "$banglaMins:$banglaSecs"
}

private fun Option.toBanglaLabel(): String = when (this) {
    Option.A -> "ক"
    Option.B -> "খ"
    Option.C -> "গ"
    Option.D -> "ঘ"
}

private fun Subject.toBanglaName(): String = when (this) {
    Subject.BENGALI -> "বাংলা"
    Subject.ENGLISH -> "ইংরেজি"
    Subject.BD_INTERNATIONAL_AFFAIRS -> "বাংলাদেশ ও আন্তর্জাতিক"
    Subject.GEOGRAPHY -> "ভূগোল"
    Subject.SCIENCE -> "সাধারণ বিজ্ঞান"
    Subject.IT -> "তথ্যপ্রযুক্তি"
    Subject.MATH -> "গণিত"
    Subject.MENTAL_ABILITY -> "মানসিক দক্ষতা"
    Subject.ETHICS -> "নৈতিকতা"
}
