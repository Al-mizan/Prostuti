package com.prostuti.feature.questionbank.ui

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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prostuti.core.designsystem.ProstutiBadge
import com.prostuti.core.designsystem.ProstutiButton
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
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when (val state = uiState) {
            is QuestionBankUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "প্রশ্ন ব্যাংক লোড হচ্ছে...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            is QuestionBankUiState.Error -> {
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
                        )
                        ProstutiButton(
                            text = "আবার চেষ্টা করুন",
                            onClick = { viewModel.onEvent(QuestionBankUiEvent.Retry) },
                        )
                    }
                }
            }

            is QuestionBankUiState.Success -> {
                QuestionBankContent(
                    state = state,
                    onEvent = viewModel::onEvent,
                )
            }
        }
    }
}

@Composable
private fun QuestionBankContent(
    state: QuestionBankUiState.Success,
    onEvent: (QuestionBankUiEvent) -> Unit,
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
            // Screen Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "বিসিএস প্রশ্ন ব্যাংক",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "১০ম থেকে ৪৭তম বিসিএস প্রিলিমিনারি",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
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
