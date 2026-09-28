package com.prostuti.feature.history.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prostuti.core.designsystem.ProstutiBadge
import com.prostuti.core.designsystem.ProstutiButton
import com.prostuti.core.designsystem.ProstutiCard
import com.prostuti.core.model.Option
import com.prostuti.core.model.SessionType
import com.prostuti.core.model.Subject
import com.prostuti.core.model.UserAttemptSummaryDto
import com.prostuti.core.model.WrongAnswerItemDto
import com.prostuti.feature.history.presentation.AttemptTypeFilter
import com.prostuti.feature.history.presentation.HistoryTab
import com.prostuti.feature.history.presentation.HistoryUiEvent
import com.prostuti.feature.history.presentation.HistoryUiState
import com.prostuti.feature.history.presentation.HistoryViewModel

private val EmeraldGreen = Color(0xFF017A47)
private val LightEmeraldBg = Color(0xFFE8F5E9)
private val CrimsonRed = Color(0xFFDC143C)
private val LightCrimsonBg = Color(0xFFFFEBEE)
private val BlueExam = Color(0xFF2563EB)
private val LightBlueExam = Color(0xFFDBEAFE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ইতিহাস ও ভুল উত্তরসমূহ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "পেছনে")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(HistoryUiEvent.Refresh) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "রিফ্রেশ")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background,
        ) {
            when (val state = uiState) {
                is HistoryUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is HistoryUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(24.dp),
                        ) {
                            Text(text = "তথ্য লোড করা যায়নি", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            Text(text = state.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            ProstutiButton(text = "পুনরায় চেষ্টা করুন", onClick = { viewModel.loadData() })
                        }
                    }
                }
                is HistoryUiState.Content -> {
                    HistoryContent(
                        state = state,
                        onSwitchTab = { viewModel.onEvent(HistoryUiEvent.SwitchTab(it)) },
                        onFilterAttemptType = { viewModel.onEvent(HistoryUiEvent.FilterAttemptType(it)) },
                        onFilterSubject = { viewModel.onEvent(HistoryUiEvent.FilterWrongAnswerSubject(it)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryContent(
    state: HistoryUiState.Content,
    onSwitchTab: (HistoryTab) -> Unit,
    onFilterAttemptType: (AttemptTypeFilter) -> Unit,
    onFilterSubject: (Subject?) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Row: Attempts vs Wrong Answers
        PrimaryTabRow(selectedTabIndex = state.activeTab.ordinal) {
            Tab(
                selected = state.activeTab == HistoryTab.ATTEMPTS,
                onClick = { onSwitchTab(HistoryTab.ATTEMPTS) },
                text = { Text("বিগত প্রচেষ্টা (${state.allAttempts.size.toBanglaDigits()})") },
                icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp)) },
            )
            Tab(
                selected = state.activeTab == HistoryTab.WRONG_ANSWERS,
                onClick = { onSwitchTab(HistoryTab.WRONG_ANSWERS) },
                text = { Text("ভুল উত্তরসমূহ (${state.allWrongAnswers.size.toBanglaDigits()})") },
                icon = { Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(18.dp)) },
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            when (state.activeTab) {
                HistoryTab.ATTEMPTS -> {
                    AttemptsTabContent(
                        attempts = state.filteredAttempts,
                        selectedFilter = state.selectedAttemptTypeFilter,
                        onSelectFilter = onFilterAttemptType,
                    )
                }
                HistoryTab.WRONG_ANSWERS -> {
                    WrongAnswersTabContent(
                        wrongAnswers = state.filteredWrongAnswers,
                        selectedSubject = state.selectedSubjectFilter,
                        isLoading = state.isLoadingWrongAnswers,
                        onSelectSubject = onFilterSubject,
                    )
                }
            }
        }
    }
}

// ==========================================
// 1. Attempts Tab Content
// ==========================================

@Composable
private fun AttemptsTabContent(
    attempts: List<UserAttemptSummaryDto>,
    selectedFilter: AttemptTypeFilter,
    onSelectFilter: (AttemptTypeFilter) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Filter Chips row (All, Practice, Exam)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf(
                AttemptTypeFilter.ALL to "সব পরীক্ষা",
                AttemptTypeFilter.PRACTICE to "অনুশীলন",
                AttemptTypeFilter.EXAM to "মক টেস্ট",
            ).forEach { (filter, label) ->
                val isSelected = filter == selectedFilter
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectFilter(filter) },
                    label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
            }
        }

        if (attempts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(56.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "এখনো কোনো পরীক্ষার ইতিহাস নেই",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "অনুশীলন বা মক টেস্ট সম্পন্ন করলে এখানে দেখা যাবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(attempts) { _, attempt ->
                    AttemptSummaryCard(attempt = attempt)
                }
            }
        }
    }
}

@Composable
private fun AttemptSummaryCard(attempt: UserAttemptSummaryDto) {
    val isExam = attempt.sessionType == SessionType.EXAM
    val badgeColor = if (isExam) BlueExam else EmeraldGreen
    val badgeBg = if (isExam) LightBlueExam else LightEmeraldBg
    val badgeIcon = if (isExam) Icons.Default.Timer else Icons.AutoMirrored.Filled.MenuBook
    val accuracy = if (attempt.totalQuestions > 0) (attempt.score * 100) / attempt.totalQuestions else 0

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(badgeBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(badgeIcon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (isExam) "মক টেস্ট" else "অনুশীলন",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                    )
                }

                Text(
                    text = formatDate(attempt.startedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = attempt.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "স্কোর: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "${attempt.score.toBanglaDigits()} / ${attempt.totalQuestions.toBanglaDigits()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                ProstutiBadge(
                    text = "সঠিকতা: ${accuracy.toBanglaDigits()}%",
                    containerColor = if (accuracy >= 60) LightEmeraldBg else LightCrimsonBg,
                    contentColor = if (accuracy >= 60) EmeraldGreen else CrimsonRed,
                )

                val timeTaken = attempt.timeTakenSeconds
                if (timeTaken != null) {
                    Text(
                        text = "${(timeTaken / 60).toBanglaDigits()} মি. ${(timeTaken % 60).toBanglaDigits()} সে.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

// ==========================================
// 2. Wrong Answers Revision Deck
// ==========================================

@Composable
private fun WrongAnswersTabContent(
    wrongAnswers: List<WrongAnswerItemDto>,
    selectedSubject: Subject?,
    isLoading: Boolean,
    onSelectSubject: (Subject?) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Subject Filter Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = selectedSubject == null,
                onClick = { onSelectSubject(null) },
                label = { Text("সকল বিষয়") },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )

            Subject.entries.forEach { subject ->
                val isSelected = selectedSubject == subject
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectSubject(subject) },
                    label = { Text(subject.toBanglaShortName(), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (wrongAnswers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(56.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "অভিনন্দন! কোনো ভুল উত্তরের রেকর্ড নেই",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "নিয়মিত অনুশীলন ও মক টেস্ট দিয়ে আপনার অগ্রগতি বজায় রাখুন।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                itemsIndexed(wrongAnswers) { index, item ->
                    WrongAnswerCard(index = index + 1, item = item)
                }
            }
        }
    }
}

@Composable
private fun WrongAnswerCard(index: Int, item: WrongAnswerItemDto) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProstutiBadge(
                        text = item.subject.toBanglaShortName(),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    val topic = item.topic
                    if (!topic.isNullOrBlank()) {
                        Text(
                            text = topic,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                ProstutiBadge(
                    text = "ভুল উত্তর",
                    containerColor = LightCrimsonBg,
                    contentColor = CrimsonRed,
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "${index.toBanglaDigits()}. ${item.questionText}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp,
            )

            Spacer(Modifier.height(14.dp))

            // Options view
            val options = listOf(
                Option.A to item.optionA,
                Option.B to item.optionB,
                Option.C to item.optionC,
                Option.D to item.optionD,
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                options.forEach { (key, text) ->
                    val isCorrectKey = key == item.correctOption
                    val isUserWrongKey = key == item.selectedOption

                    val (rowBg, rowBorder, rowTextColor) = when {
                        isCorrectKey -> Triple(LightEmeraldBg, EmeraldGreen, EmeraldGreen)
                        isUserWrongKey -> Triple(LightCrimsonBg, CrimsonRed, CrimsonRed)
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
                                fontWeight = if (isCorrectKey || isUserWrongKey) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f),
                            )
                            if (isCorrectKey) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                            } else if (isUserWrongKey) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Explanation section
            val explanation = item.explanation
            if (!explanation.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isExpanded) "ব্যাখ্যা লুকান" else "ব্যাখ্যা দেখুন",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen,
                        )
                    }
                }

                AnimatedVisibility(visible = isExpanded) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    ) {
                        Text(
                            text = explanation,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// Helpers
// ==========================================

private fun Int.toBanglaDigits(): String {
    val banglaDigits = arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    return this.toString().map { if (it in '0'..'9') banglaDigits[it - '0'] else it }.joinToString("")
}

private fun formatDate(rawDate: String): String {
    return if (rawDate.length >= 10) rawDate.substring(0, 10) else rawDate
}

private fun Option.toBanglaLabel(): String = when (this) {
    Option.A -> "ক"
    Option.B -> "খ"
    Option.C -> "গ"
    Option.D -> "ঘ"
}

private fun Subject.toBanglaShortName(): String = when (this) {
    Subject.BENGALI -> "বাংলা"
    Subject.ENGLISH -> "ইংরেজি"
    Subject.BD_INTERNATIONAL_AFFAIRS -> "বাংলাদেশ ও আন্তর্জাতিক"
    Subject.GEOGRAPHY -> "ভূগোল"
    Subject.SCIENCE -> "বিজ্ঞান"
    Subject.IT -> "আইটি"
    Subject.MATH -> "গণিত"
    Subject.MENTAL_ABILITY -> "মানসিক দক্ষতা"
    Subject.ETHICS -> "নৈতিকতা"
}
