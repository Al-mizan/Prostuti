package com.prostuti.feature.practice.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prostuti.core.designsystem.ProstutiBadge
import com.prostuti.core.designsystem.ProstutiButton
import com.prostuti.core.designsystem.ProstutiProgressBar
import com.prostuti.core.model.Option
import com.prostuti.core.model.PracticeSessionQuestionDto
import com.prostuti.core.model.Subject
import com.prostuti.feature.practice.presentation.PracticeUiEvent
import com.prostuti.feature.practice.presentation.PracticeUiState
import com.prostuti.feature.practice.presentation.PracticeViewModel

private val EmeraldGreen = Color(0xFF017A47)
private val LightEmeraldBg = Color(0xFFE8F5E9)
private val CrimsonRed = Color(0xFFDC143C)
private val LightCrimsonBg = Color(0xFFFFEBEE)

data class SubjectGridMeta(
    val subject: Subject,
    val banglaName: String,
    val marks: String,
    val icon: ImageVector,
    val color: Color,
    val bgColor: Color,
)

private val SubjectMetaList = listOf(
    SubjectGridMeta(
        subject = Subject.BENGALI,
        banglaName = "বাংলা ভাষা ও সাহিত্য",
        marks = "৩৫ নম্বর",
        icon = Icons.Default.Translate,
        color = Color(0xFF9E1B32),
        bgColor = Color(0xFFFFEBEE),
    ),
    SubjectGridMeta(
        subject = Subject.ENGLISH,
        banglaName = "ইংরেজি ভাষা ও সাহিত্য",
        marks = "৩৫ নম্বর",
        icon = Icons.Default.Language,
        color = Color(0xFF1E40AF),
        bgColor = Color(0xFFDBEAFE),
    ),
    SubjectGridMeta(
        subject = Subject.BD_INTERNATIONAL_AFFAIRS,
        banglaName = "বাংলাদেশ ও আন্তর্জাতিক বিষয়াবলি",
        marks = "৫০ নম্বর",
        icon = Icons.Default.Public,
        color = Color(0xFF0D9488),
        bgColor = Color(0xFFCCFBF1),
    ),
    SubjectGridMeta(
        subject = Subject.GEOGRAPHY,
        banglaName = "ভূগোল, পরিবেশ ও দুর্যোগ",
        marks = "১০ নম্বর",
        icon = Icons.AutoMirrored.Filled.MenuBook,
        color = Color(0xFFD97706),
        bgColor = Color(0xFFFEF3C7),
    ),
    SubjectGridMeta(
        subject = Subject.SCIENCE,
        banglaName = "সাধারণ বিজ্ঞান",
        marks = "১৫ নম্বর",
        icon = Icons.Default.Science,
        color = Color(0xFF059669),
        bgColor = Color(0xFFD1FAE5),
    ),
    SubjectGridMeta(
        subject = Subject.IT,
        banglaName = "কম্পিউটার ও তথ্যপ্রযুক্তি",
        marks = "১৫ নম্বর",
        icon = Icons.Default.Computer,
        color = Color(0xFF7C3AED),
        bgColor = Color(0xFFEDE9FE),
    ),
    SubjectGridMeta(
        subject = Subject.MATH,
        banglaName = "গাণিতিক যুক্তি",
        marks = "১৫ নম্বর",
        icon = Icons.Default.Calculate,
        color = Color(0xFFDC2626),
        bgColor = Color(0xFFFEE2E2),
    ),
    SubjectGridMeta(
        subject = Subject.MENTAL_ABILITY,
        banglaName = "মানসিক দক্ষতা",
        marks = "১৫ নম্বর",
        icon = Icons.Default.Psychology,
        color = Color(0xFF4F46E5),
        bgColor = Color(0xFFE0E7FF),
    ),
    SubjectGridMeta(
        subject = Subject.ETHICS,
        banglaName = "নৈতিকতা, মূল্যবোধ ও সুশাসন",
        marks = "১০ নম্বর",
        icon = Icons.Default.Security,
        color = Color(0xFF0284C7),
        bgColor = Color(0xFFE0F2FE),
    ),
)

@Composable
fun PracticeScreen(
    viewModel: PracticeViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when (val state = uiState) {
            is PracticeUiState.SubjectSelect -> {
                SubjectSelectContent(
                    selectedCount = state.selectedCount,
                    onCountChange = { viewModel.onEvent(PracticeUiEvent.ChangeQuestionCount(it)) },
                    onStartPractice = { subject, count ->
                        viewModel.onEvent(PracticeUiEvent.StartSession(subject, count))
                    },
                )
            }

            is PracticeUiState.Loading -> {
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
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            is PracticeUiState.Error -> {
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
                            onClick = { viewModel.onEvent(PracticeUiEvent.ResetToSubjectSelect) },
                        )
                    }
                }
            }

            is PracticeUiState.ActiveSession -> {
                ActiveSessionContent(
                    state = state,
                    onSelectOption = { viewModel.onEvent(PracticeUiEvent.SubmitAnswer(it)) },
                    onNext = { viewModel.onEvent(PracticeUiEvent.NextQuestion) },
                    onFinish = { viewModel.onEvent(PracticeUiEvent.FinishSession) },
                )
            }

            is PracticeUiState.SessionSummary -> {
                SessionSummaryContent(
                    state = state,
                    onPracticeAgain = { viewModel.onEvent(PracticeUiEvent.ResetToSubjectSelect) },
                )
            }
        }
    }
}

@Composable
private fun SubjectSelectContent(
    selectedCount: Int,
    onCountChange: (Int) -> Unit,
    onStartPractice: (Subject, Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Text(
            text = "বিষয়ভিত্তিক অনুশীলন",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "বিসিএস প্রিলিমিনারির ৯টি নির্ধারিত বিষয় থেকে অনুশীলন করুন",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(16.dp))

        // Question Count Filter Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "প্রশ্নের সংখ্যা:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            listOf(10, 15, 20).forEach { count ->
                val isSelected = count == selectedCount
                FilterChip(
                    selected = isSelected,
                    onClick = { onCountChange(count) },
                    label = { Text("${count.toBanglaDigits()}টি", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(SubjectMetaList) { meta ->
                Card(
                    onClick = { onStartPractice(meta.subject, selectedCount) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, meta.color.copy(alpha = 0.25f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(meta.bgColor),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    meta.icon,
                                    contentDescription = null,
                                    tint = meta.color,
                                    modifier = Modifier.size(22.dp),
                                )
                            }

                            ProstutiBadge(
                                text = meta.marks,
                                containerColor = meta.bgColor,
                                contentColor = meta.color,
                            )
                        }

                        Text(
                            text = meta.banglaName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            minLines = 2,
                            lineHeight = 18.sp,
                        )

                        Text(
                            text = "${selectedCount.toBanglaDigits()}টি প্রশ্নের কুইজ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "অনুশীলন করুন",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = meta.color,
                            )
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = meta.color,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveSessionContent(
    state: PracticeUiState.ActiveSession,
    onSelectOption: (Option) -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val question = state.currentQuestion
    val answerResult = state.currentAnswerResult
    val isAnswered = answerResult != null
    val progress = (state.currentIndex + 1).toFloat() / state.totalQuestions.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        // Sticky Header Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ProstutiBadge(
                        text = state.subject.toBanglaName(),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "প্রশ্ন ${(state.currentIndex + 1).toBanglaDigits()} / ${state.totalQuestions.toBanglaDigits()}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.width(12.dp))
                        OutlinedButton(
                            onClick = onFinish,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp),
                        ) {
                            Text("শেষ করুন", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                ProstutiProgressBar(progress = progress)
            }
        }

        // Question Viewport
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
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

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 26.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(Modifier.height(20.dp))

            // Options List (ক, খ, গ, ঘ)
            val options = listOf(
                Option.A to question.optionA,
                Option.B to question.optionB,
                Option.C to question.optionC,
                Option.D to question.optionD,
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                options.forEach { (optionKey, optionText) ->
                    val isChosenByUser = answerResult?.selectedOption == optionKey
                    val isCorrectKey = answerResult?.correctOption == optionKey

                    val (containerColor, borderColor, contentColor, icon) = when {
                        !isAnswered -> TripleQuad(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.onSurface,
                            null,
                        )
                        isCorrectKey -> TripleQuad(
                            LightEmeraldBg,
                            EmeraldGreen,
                            EmeraldGreen,
                            Icons.Default.Check,
                        )
                        isChosenByUser -> TripleQuad(
                            LightCrimsonBg,
                            CrimsonRed,
                            CrimsonRed,
                            Icons.Default.Close,
                        )
                        else -> TripleQuad(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            null,
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = containerColor,
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isAnswered && !state.isSubmittingAnswer) {
                                onSelectOption(optionKey)
                            },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        color = if (isAnswered && (isCorrectKey || isChosenByUser)) contentColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
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

                            Spacer(Modifier.width(12.dp))

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
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }
            }

            // Explanation Display (revealed automatically on answer)
            val explanation = answerResult?.explanation
            AnimatedVisibility(
                visible = isAnswered && !explanation.isNullOrBlank(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = if (answerResult?.isCorrect == true) EmeraldGreen else CrimsonRed,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (answerResult?.isCorrect == true) "সঠিক উত্তর! ব্যাখ্যা দেখুন:" else "ভুল উত্তর! সঠিক ব্যাখ্যা:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (answerResult?.isCorrect == true) EmeraldGreen else CrimsonRed,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = explanation.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Next / Finish Action Button
            if (isAnswered) {
                Button(
                    onClick = onNext,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                ) {
                    Text(
                        text = if (state.hasNextQuestion) "পরবর্তী প্রশ্ন" else "ফলাফল দেখুন",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionSummaryContent(
    state: PracticeUiState.SessionSummary,
    onPracticeAgain: () -> Unit,
) {
    val summary = state.summary
    val accuracy = if (summary.totalQuestions > 0) (summary.correctCount * 100) / summary.totalQuestions else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Trophy / Result Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = if (accuracy >= 60) LightEmeraldBg else LightCrimsonBg,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = if (accuracy >= 60) EmeraldGreen else CrimsonRed,
                modifier = Modifier.size(44.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "অনুশীলন সম্পন্ন!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "${summary.subject.toBanglaName()} বিষয়ের ফলাফল",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(24.dp))

        // Score Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "${summary.score.toBanglaDigits()} / ${summary.totalQuestions.toBanglaDigits()}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "সঠিকতার হার: ${accuracy.toBanglaDigits()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (accuracy >= 60) EmeraldGreen else CrimsonRed,
                )

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = summary.correctCount.toBanglaDigits(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen,
                        )
                        Text(
                            text = "সঠিক উত্তর",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = summary.incorrectCount.toBanglaDigits(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = CrimsonRed,
                        )
                        Text(
                            text = "ভুল উত্তর",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        ProstutiButton(
            text = "আবার অনুশীলন করুন",
            onClick = onPracticeAgain,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private data class TripleQuad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

private fun Int.toBanglaDigits(): String {
    val banglaDigits = arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    return this.toString().map { if (it in '0'..'9') banglaDigits[it - '0'] else it }.joinToString("")
}

private fun Subject.toBanglaName(): String = when (this) {
    Subject.BENGALI -> "বাংলা ভাষা ও সাহিত্য"
    Subject.ENGLISH -> "ইংরেজি ভাষা ও সাহিত্য"
    Subject.BD_INTERNATIONAL_AFFAIRS -> "বাংলাদেশ ও আন্তর্জাতিক"
    Subject.GEOGRAPHY -> "ভূগোল, পরিবেশ ও দুর্যোগ"
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
