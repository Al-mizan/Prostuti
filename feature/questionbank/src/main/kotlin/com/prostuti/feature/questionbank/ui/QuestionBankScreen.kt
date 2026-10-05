package com.prostuti.feature.questionbank.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.prostuti.core.model.ModelTestDto
import com.prostuti.core.model.ModelTestStatus
import com.prostuti.feature.questionbank.presentation.QuestionBankView
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prostuti.core.designsystem.ProstutiBadge
import com.prostuti.core.designsystem.ProstutiButton
import com.prostuti.core.designsystem.ProstutiErrorView
import com.prostuti.core.designsystem.SubjectGridSkeleton
import com.prostuti.core.designsystem.parseErrorType
import com.prostuti.core.model.Option
import com.prostuti.core.model.QuestionBankItemDto
import com.prostuti.core.model.Subject
import com.prostuti.feature.questionbank.presentation.QuestionBankUiEvent
import com.prostuti.feature.questionbank.presentation.QuestionBankUiState
import com.prostuti.feature.questionbank.presentation.QuestionBankViewModel

private val EmeraldGreen = Color(0xFF017A47)
private val LightEmeraldBg = Color(0xFFE8F5E9)
private val CrimsonRed = Color(0xFFDC143C)
private val LightCrimsonBg = Color(0xFFFFEBEE)

@Composable
fun QuestionBankScreen(
    viewModel: QuestionBankViewModel,
    onStartExam: (sessionName: String?) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when (val state = uiState) {
            is QuestionBankUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = "প্রশ্ন ব্যাংক",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "বিষয়ভিত্তিক প্রশ্ন ও বিগত বছরের বিসিএস সমাধান",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    SubjectGridSkeleton(itemCount = 6)
                }
            }

            is QuestionBankUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    ProstutiErrorView(
                        errorType = parseErrorType(state.message),
                        onAction = { viewModel.onEvent(QuestionBankUiEvent.Retry) },
                    )
                }
            }

            is QuestionBankUiState.Success -> {
                BackHandler(enabled = state.view != QuestionBankView.HOME) {
                    viewModel.onEvent(QuestionBankUiEvent.NavigateView(QuestionBankView.HOME))
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when (state.view) {
                        QuestionBankView.HOME -> {
                            QuestionBankHomeScreen(
                                state = state,
                                onJoinLiveExam = { sessionName -> onStartExam(sessionName) },
                                onOpenBcsSessions = {
                                    viewModel.onEvent(QuestionBankUiEvent.NavigateView(QuestionBankView.BCS_SESSIONS))
                                },
                                onOpenModelTests = {
                                    viewModel.onEvent(QuestionBankUiEvent.NavigateView(QuestionBankView.MODEL_TESTS))
                                },
                                onOpenSubject = { subject ->
                                    viewModel.onEvent(QuestionBankUiEvent.OpenSubjectStudy(subject))
                                },
                            )
                        }

                        QuestionBankView.BCS_SESSIONS -> {
                            BcsSessionsScreen(
                                sessions = state.filteredSessions,
                                searchQuery = state.searchQuery,
                                onSearchQueryChange = {
                                    viewModel.onEvent(QuestionBankUiEvent.UpdateSearchQuery(it))
                                },
                                onSessionClick = { session ->
                                    viewModel.onEvent(QuestionBankUiEvent.OpenSessionModal(session))
                                },
                                onBack = {
                                    viewModel.onEvent(QuestionBankUiEvent.NavigateView(QuestionBankView.HOME))
                                },
                            )
                        }

                        QuestionBankView.STUDY -> {
                            QuestionBankContent(
                                state = state,
                                onEvent = viewModel::onEvent,
                                onBack = {
                                    viewModel.onEvent(QuestionBankUiEvent.NavigateView(QuestionBankView.HOME))
                                },
                            )
                        }

                        QuestionBankView.MODEL_TESTS -> {
                            ModelTestCatalogScreen(
                                state = state,
                                onEvent = viewModel::onEvent,
                                onStartExam = { sessionName -> onStartExam(sessionName) },
                                onBack = {
                                    viewModel.onEvent(QuestionBankUiEvent.NavigateView(QuestionBankView.HOME))
                                },
                            )
                        }
                    }

                    // Chorcha-style Exam Action Modal
                    if (state.activeModalSession != null) {
                        BcsExamActionModal(
                            session = state.activeModalSession,
                            onDismiss = { viewModel.onEvent(QuestionBankUiEvent.CloseSessionModal) },
                            onStartExam = { sessionName ->
                                onStartExam(sessionName)
                            },
                            onViewQuestions = { session ->
                                viewModel.viewQuestionsForSession(session)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionBankHomeScreen(
    state: QuestionBankUiState.Success,
    onJoinLiveExam: (String) -> Unit,
    onOpenBcsSessions: () -> Unit,
    onOpenModelTests: () -> Unit,
    onOpenSubject: (Subject) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // App / Section Title
        Column {
            Text(
                text = "বিসিএস প্রশ্ন ব্যাংক",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "বিষয়ভিত্তিক প্রশ্ন, লাইভ মডেল টেস্ট ও বিগত বছরের বিসিএস সমাধান",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // 1. Live Model Test Banner
        LiveModelTestBannerCard(
            liveTest = state.liveModelTest,
            onJoinExam = onJoinLiveExam,
            onBrowseAll = onOpenModelTests,
        )

        // 2. Institute BCS & Model Test Cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "প্রতিষ্ঠান ভিত্তিক প্রশ্ন ব্যাংক",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            InstituteBcsCard(onClick = onOpenBcsSessions)
            InstituteModelTestCard(onClick = onOpenModelTests)
        }

        // 3. Subject-wise Grid (9 BCS Subjects)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "বিষয় ভিত্তিক প্রশ্ন ব্যাংক",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "৯টি বিষয়",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            SubjectCardsGrid(onSubjectClick = onOpenSubject)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun QuestionBankContent(
    state: QuestionBankUiState.Success,
    onEvent: (QuestionBankUiEvent) -> Unit,
    onBack: () -> Unit,
) {
    val listState = rememberLazyListState()

    // Scroll to top when page changes
    LaunchedEffect(state.page, state.selectedSubject, state.selectedSession) {
        listState.scrollToItem(0)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Sticky Header & Filter Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = 16.dp, bottom = 8.dp),
        ) {
            // Screen Title with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                    Column {
                        Text(
                            text = state.selectedSubject?.toBanglaName() ?: state.selectedSession.ifBlank { "বিসিএস প্রশ্ন ব্যাংক" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "প্রশ্নোত্তর ও বিশদ ব্যাখ্যা",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Session Selector Button with Dropdown
                SessionSelectorDropdown(
                    sessions = state.sessions,
                    selectedSession = state.selectedSession,
                    onSelectSession = { onEvent(QuestionBankUiEvent.SelectSession(it)) },
                )
            }

            Spacer(Modifier.height(12.dp))

            // Subject Filter Bar (Horizontal Scroll)
            SubjectFilterBar(
                selectedSubject = state.selectedSubject,
                onSelectSubject = { onEvent(QuestionBankUiEvent.SelectSubject(it)) },
            )

            if (state.isRefreshingQuestions) {
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        // Questions List
        if (state.questions.isEmpty() && !state.isRefreshingQuestions) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp),
                    )
                    Text(
                        text = "কোনো প্রশ্ন পাওয়া যায়নি",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "অন্য কোনো বিষয় নির্বাচন করে দেখুন",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                itemsIndexed(
                    items = state.questions,
                    key = { _, item -> item.id },
                ) { index, item ->
                    val serialNumber = (state.page * state.pageSize) + index + 1
                    QuestionStudyCard(
                        serial = serialNumber,
                        question = item,
                        selectedOption = state.selectedOptions[item.id],
                        isExplanationExpanded = state.expandedExplanations.contains(item.id),
                        onSelectOption = { option ->
                            onEvent(QuestionBankUiEvent.SelectOption(item.id, option))
                        },
                        onToggleExplanation = {
                            onEvent(QuestionBankUiEvent.ToggleExplanation(item.id))
                        },
                    )
                }

                // Pagination Footer
                item {
                    PaginationBar(
                        currentPage = state.page,
                        totalPages = state.totalPages,
                        totalQuestions = state.totalQuestions,
                        hasPrevious = state.hasPreviousPage,
                        hasNext = state.hasNextPage,
                        onPageChange = { onEvent(QuestionBankUiEvent.ChangePage(it)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionSelectorDropdown(
    sessions: List<com.prostuti.core.model.BcsSessionSummaryDto>,
    selectedSession: String,
    onSelectSession: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
            modifier = Modifier.clickable { expanded = true },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedSession.toBanglaDigits(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select BCS Session",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        ) {
            sessions.forEach { session ->
                val isSelected = session.sessionName == selectedSession
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = session.sessionName.toBanglaDigits(),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(Modifier.width(16.dp))
                            ProstutiBadge(
                                text = "${session.totalQuestions.toBanglaDigits()} প্রশ্ন",
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        onSelectSession(session.sessionName)
                    },
                )
            }
        }
    }
}

@Composable
private fun SubjectFilterBar(
    selectedSubject: Subject?,
    onSelectSubject: (Subject?) -> Unit,
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // "সব বিষয়" (All subjects)
        FilterChip(
            selected = selectedSubject == null,
            onClick = { onSelectSubject(null) },
            label = { Text("সব বিষয়", fontWeight = if (selectedSubject == null) FontWeight.Bold else FontWeight.Normal) },
            shape = RoundedCornerShape(50),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )

        // 9 Fixed Subjects
        Subject.entries.forEach { subject ->
            val isSelected = selectedSubject == subject
            FilterChip(
                selected = isSelected,
                onClick = { onSelectSubject(subject) },
                label = {
                    Text(
                        text = subject.toBanglaName(),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    )
                },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        }
    }
}

@Composable
private fun QuestionStudyCard(
    serial: Int,
    question: QuestionBankItemDto,
    selectedOption: Option?,
    isExplanationExpanded: Boolean,
    onSelectOption: (Option) -> Unit,
    onToggleExplanation: () -> Unit,
) {
    val isAnswered = selectedOption != null

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Top Meta (Serial & Subject)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = serial.toBanglaDigits(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }

                    ProstutiBadge(
                        text = question.subject.toBanglaName(),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }

                val topic = question.topic
                if (!topic.isNullOrBlank()) {
                    Text(
                        text = topic,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Question Text
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(14.dp))

            // Options Grid (A, B, C, D)
            val options = listOf(
                Option.A to question.optionA,
                Option.B to question.optionB,
                Option.C to question.optionC,
                Option.D to question.optionD,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { (optionKey, optionText) ->
                    val isChosenByUser = selectedOption == optionKey
                    val isCorrectKey = question.correctOption == optionKey

                    val (containerColor, borderColor, contentColor, icon) = when {
                        !isAnswered -> Quad(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                            MaterialTheme.colorScheme.onSurface,
                            null,
                        )
                        isCorrectKey -> Quad(
                            LightEmeraldBg,
                            EmeraldGreen,
                            EmeraldGreen,
                            Icons.Default.Check,
                        )
                        isChosenByUser -> Quad(
                            LightCrimsonBg,
                            CrimsonRed,
                            CrimsonRed,
                            Icons.Default.Close,
                        )
                        else -> Quad(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            null,
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = containerColor,
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isAnswered) { onSelectOption(optionKey) },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Option Letter badge (ক, খ, গ, ঘ)
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(
                                        color = if (isAnswered && (isCorrectKey || isChosenByUser)) contentColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                                        shape = CircleShape,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = optionKey.toBanglaLabel(),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor,
                                )
                            }

                            Spacer(Modifier.width(10.dp))

                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = contentColor,
                                fontWeight = if (isAnswered && isCorrectKey) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f),
                            )

                            if (icon != null) {
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = contentColor,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    }
                }
            }

            // Explanation Toggle Button & Content
            val explanation = question.explanation
            if (!explanation.isNullOrBlank()) {
                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleExplanation() }
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = if (isExplanationExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isExplanationExpanded) "ব্যাখ্যা লুকান" else "ব্যাখ্যা দেখুন",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isExplanationExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.width(2.dp))
                    Icon(
                        imageVector = if (isExplanationExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (isExplanationExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }

                AnimatedVisibility(
                    visible = isExplanationExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "সঠিক উত্তর ও ব্যাখ্যা",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen,
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = explanation,
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaginationBar(
    currentPage: Int,
    totalPages: Int,
    totalQuestions: Int,
    hasPrevious: Boolean,
    hasNext: Boolean,
    onPageChange: (Int) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OutlinedButton(
                onClick = { onPageChange(currentPage - 1) },
                enabled = hasPrevious,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Page",
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text("পূর্ববর্তী", style = MaterialTheme.typography.labelMedium)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "পৃষ্ঠা ${(currentPage + 1).toBanglaDigits()} / ${totalPages.toBanglaDigits()}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "মোট ${totalQuestions.toBanglaDigits()} প্রশ্ন",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            OutlinedButton(
                onClick = { onPageChange(currentPage + 1) },
                enabled = hasNext,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text("পরবর্তী", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Page",
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun ModelTestCatalogScreen(
    state: QuestionBankUiState.Success,
    onEvent: (QuestionBankUiEvent) -> Unit,
    onStartExam: (String) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Sticky Header / Filter Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                        Column {
                            Text(
                                text = "বিসিএস মডেল টেস্ট",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "লাইভ, আসন্ন ও বিগত আর্কাইভ পরীক্ষা",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    IconButton(onClick = { onEvent(QuestionBankUiEvent.RefreshModelTests) }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onEvent(QuestionBankUiEvent.UpdateSearchQuery(it)) },
                    placeholder = { Text("মডেল টেস্ট খুঁজুন (শিরোনাম বা সেশন)...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotBlank()) {
                            IconButton(onClick = { onEvent(QuestionBankUiEvent.UpdateSearchQuery("")) }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(12.dp))

                // Filter Chips
                val filterScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(filterScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = state.selectedModelTestFilter == null,
                        onClick = { onEvent(QuestionBankUiEvent.FilterModelTests(null)) },
                        label = { Text("সব পরীক্ষা") },
                        shape = RoundedCornerShape(50),
                    )
                    FilterChip(
                        selected = state.selectedModelTestFilter == ModelTestStatus.LIVE,
                        onClick = { onEvent(QuestionBankUiEvent.FilterModelTests(ModelTestStatus.LIVE)) },
                        label = { Text("চলমান লাইভ") },
                        shape = RoundedCornerShape(50),
                    )
                    FilterChip(
                        selected = state.selectedModelTestFilter == ModelTestStatus.UPCOMING,
                        onClick = { onEvent(QuestionBankUiEvent.FilterModelTests(ModelTestStatus.UPCOMING)) },
                        label = { Text("আসন্ন পরীক্ষা") },
                        shape = RoundedCornerShape(50),
                    )
                    FilterChip(
                        selected = state.selectedModelTestFilter == ModelTestStatus.EXPIRED,
                        onClick = { onEvent(QuestionBankUiEvent.FilterModelTests(ModelTestStatus.EXPIRED)) },
                        label = { Text("আর্কাইভ / সমাপ্ত") },
                        shape = RoundedCornerShape(50),
                    )
                }
            }
        }

        // Test Cards List
        val tests = state.filteredModelTests
        if (tests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.EventNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(56.dp),
                    )
                    Text(
                        text = "কোনো মডেল টেস্ট পাওয়া যায়নি",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "ফিল্টার পরিবর্তন করে অথবা নতুন পরীক্ষার জন্য অপেক্ষা করুন।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(tests, key = { it.id }) { test ->
                    ModelTestCatalogItemCard(
                        test = test,
                        onTakeExam = { onStartExam(test.examSession) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelTestCatalogItemCard(
    test: ModelTestDto,
    onTakeExam: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLive = test.status == ModelTestStatus.LIVE
    val isUpcoming = test.status == ModelTestStatus.UPCOMING

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            if (isLive) Color(0xFFDC2626).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLive) 3.dp else 1.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Status Badge & Session
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        isLive -> Color(0xFFDC2626)
                        isUpcoming -> Color(0xFF2563EB)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                ) {
                    Text(
                        text = when {
                            isLive -> "চলমান লাইভ"
                            isUpcoming -> "আসন্ন"
                            else -> "আর্কাইভ / সমাপ্ত"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isLive || isUpcoming -> Color.White
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Text(
                        text = test.examSession,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = test.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            val description = test.description
            if (!description.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(14.dp))

            // Metadata Row: Duration, Questions, Marks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "${test.durationMinutes.toBanglaDigits()} মিনিট",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "${test.totalQuestions.toBanglaDigits()} প্রশ্ন • ${test.totalMarks.toInt().toBanglaDigits()} নম্বর",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // CTA Button
            if (isUpcoming) {
                OutlinedButton(
                    onClick = {},
                    enabled = false,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "শীঘ্রই শুরু হবে (অপেক্ষমান)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            } else {
                Button(
                    onClick = onTakeExam,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLive) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = if (isLive) "লাইভ পরীক্ষায় অংশ নিন" else "আর্কাইভ পরীক্ষা দিন",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

private fun Int.toBanglaDigits(): String {
    val banglaDigits = arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    return this.toString().map { if (it in '0'..'9') banglaDigits[it - '0'] else it }.joinToString("")
}

private fun String.toBanglaDigits(): String {
    val banglaDigits = arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    return this.map { if (it in '0'..'9') banglaDigits[it - '0'] else it }.joinToString("")
}

private fun Subject.toBanglaName(): String = when (this) {
    Subject.BENGALI -> "বাংলা"
    Subject.ENGLISH -> "ইংরেজি"
    Subject.BD_INTERNATIONAL_AFFAIRS -> "বাংলাদেশ ও আন্তর্জাতিক"
    Subject.GEOGRAPHY -> "ভূগোল"
    Subject.SCIENCE -> "সাধারণ বিজ্ঞান"
    Subject.IT -> "তথ্যপ্রযুক্তি"
    Subject.MATH -> "গাণিতিক যুক্তি"
    Subject.MENTAL_ABILITY -> "মানসিক দক্ষতা"
    Subject.ETHICS -> "নৈতিকতা ও সুশাসন"
}

private fun Option.toBanglaLabel(): String = when (this) {
    Option.A -> "ক"
    Option.B -> "খ"
    Option.C -> "গ"
    Option.D -> "ঘ"
}
