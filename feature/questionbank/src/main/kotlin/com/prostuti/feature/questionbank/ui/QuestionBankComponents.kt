package com.prostuti.feature.questionbank.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prostuti.core.model.Subject

/**
 * Visual metadata for the 9 fixed BCS subjects.
 * Matches reference styling: vibrant two-tone gradients, Bangla typography watermark, and subtitle.
 */
data class SubjectCardDesign(
    val subject: Subject,
    val titleBangla: String,
    val subtitleBangla: String,
    val marksBangla: String,
    val icon: ImageVector,
    val accentColor: Color,
    val containerColor: Color,
    val watermark: String = "",
    val gradientColors: List<Color> = listOf(accentColor, accentColor),
)

val BcsSubjectCards = listOf(
    SubjectCardDesign(
        subject = Subject.BENGALI,
        titleBangla = "বাংলা ভাষা ও সাহিত্য",
        subtitleBangla = "ব্যাকরণ ও সাহিত্য",
        marksBangla = "৩৫ নম্বর",
        icon = Icons.Default.Translate,
        accentColor = Color(0xFFE11D48),
        containerColor = Color(0xFFFFF1F2),
        watermark = "অ",
        gradientColors = listOf(Color(0xFFE11D48), Color(0xFF9F1239)),
    ),
    SubjectCardDesign(
        subject = Subject.ENGLISH,
        titleBangla = "ইংরেজি ভাষা ও সাহিত্য",
        subtitleBangla = "Grammar & Literature",
        marksBangla = "৩৫ নম্বর",
        icon = Icons.Default.Language,
        accentColor = Color(0xFF2563EB),
        containerColor = Color(0xFFEFF6FF),
        watermark = "A",
        gradientColors = listOf(Color(0xFF2563EB), Color(0xFF1E40AF)),
    ),
    SubjectCardDesign(
        subject = Subject.BD_INTERNATIONAL_AFFAIRS,
        titleBangla = "বাংলাদেশ ও আন্তর্জাতিক বিষয়",
        subtitleBangla = "জাতীয় ও বৈশ্বিক বিষয়",
        marksBangla = "৫০ নম্বর",
        icon = Icons.Default.Public,
        accentColor = Color(0xFF059669),
        containerColor = Color(0xFFECFDF5),
        watermark = "ব",
        gradientColors = listOf(Color(0xFF059669), Color(0xFF047857)),
    ),
    SubjectCardDesign(
        subject = Subject.GEOGRAPHY,
        titleBangla = "ভূগোল ও পরিবেশ",
        subtitleBangla = "বাংলাদেশ ও বিশ্ব ভূগোল",
        marksBangla = "১০ নম্বর",
        icon = Icons.AutoMirrored.Filled.MenuBook,
        accentColor = Color(0xFF0891B2),
        containerColor = Color(0xFFECFEFF),
        watermark = "ভূ",
        gradientColors = listOf(Color(0xFF0891B2), Color(0xFF0E7490)),
    ),
    SubjectCardDesign(
        subject = Subject.SCIENCE,
        titleBangla = "সাধারণ বিজ্ঞান",
        subtitleBangla = "ভৌত ও আধুনিক বিজ্ঞান",
        marksBangla = "১৫ নম্বর",
        icon = Icons.Default.Science,
        accentColor = Color(0xFF7C3AED),
        containerColor = Color(0xFFF5F3FF),
        watermark = "বি",
        gradientColors = listOf(Color(0xFF7C3AED), Color(0xFF6D28D9)),
    ),
    SubjectCardDesign(
        subject = Subject.IT,
        titleBangla = "কম্পিউটার ও তথ্যপ্রযুক্তি",
        subtitleBangla = "হার্ডওয়্যার ও তথ্যপ্রযুক্তি",
        marksBangla = "১৫ নম্বর",
        icon = Icons.Default.Computer,
        accentColor = Color(0xFFEA580C),
        containerColor = Color(0xFFFFF7ED),
        watermark = "ত",
        gradientColors = listOf(Color(0xFFEA580C), Color(0xFFC2410C)),
    ),
    SubjectCardDesign(
        subject = Subject.MATH,
        titleBangla = "গাণিতিক যুক্তি",
        subtitleBangla = "পাটিগণিত ও বীজগণিত",
        marksBangla = "১৫ নম্বর",
        icon = Icons.Default.Calculate,
        accentColor = Color(0xFFC026D3),
        containerColor = Color(0xFFFDF4FF),
        watermark = "∑",
        gradientColors = listOf(Color(0xFFC026D3), Color(0xFFA21CAF)),
    ),
    SubjectCardDesign(
        subject = Subject.MENTAL_ABILITY,
        titleBangla = "মানসিক দক্ষতা",
        subtitleBangla = "যুক্তিমূলক বিশ্লেষণ",
        marksBangla = "১৫ নম্বর",
        icon = Icons.Default.Psychology,
        accentColor = Color(0xFF0284C7),
        containerColor = Color(0xFFF0F9FF),
        watermark = "মা",
        gradientColors = listOf(Color(0xFF0284C7), Color(0xFF0369A1)),
    ),
    SubjectCardDesign(
        subject = Subject.ETHICS,
        titleBangla = "নৈতিকতা ও সুশাসন",
        subtitleBangla = "মূল্যবোধ ও সুশাসন",
        marksBangla = "১০ নম্বর",
        icon = Icons.Default.Gavel,
        accentColor = Color(0xFF0D9488),
        containerColor = Color(0xFFF0FDFA),
        watermark = "নৌ",
        gradientColors = listOf(Color(0xFF0D9488), Color(0xFF115E59)),
    ),
)

/**
 * 15–30 day featured Live Model Test card with countdown and 1-attempt badge.
 */
@Composable
fun LiveModelTestBannerCard(
    onJoinExam: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF881337), // Crimson deep
                        Color(0xFF4C0519),
                        Color(0xFF1E0108),
                    )
                )
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFDC2626),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = "লাইভ মডেল টেস্ট • চলমান",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.15f),
                ) {
                    Text(
                        text = "১ বার অংশগ্রহণ যোগ্য",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "৪৭তম বিসিএস বিশেষ লাইভ মডেল টেস্ট",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "১৫ দিনব্যাপী চলবে • পূর্ণাঙ্গ সিলেবাস • রিয়েল-টাইম ফলাফল ও ব্যাখ্যা",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
            )

            Spacer(Modifier.height(16.dp))

            // Metadata row
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
                        tint = Color(0xFFFDE047),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "১২০ মিনিট",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = null,
                        tint = Color(0xFF93C5FD),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "২০০ প্রশ্ন • ২০০ নম্বর",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onJoinExam,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE11D48),
                    contentColor = Color.White,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "পরীক্ষায় অংশগ্রহণ করুন",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

/**
 * Institute BCS Preliminary card (taking student to 50th down to 10th BCS archive).
 */
@Composable
fun InstituteBcsCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f),
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(52.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "বিসিএস প্রিলিমিনারি",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        ) {
                            Text(
                                text = "৪০+ পরীক্ষা",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "১০ম থেকে ৫০তম বিসিএস • সকল প্রশ্ন ও ব্যাখ্যা",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open archive",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

/**
 * Modern 2-column visual grid for the 9 BCS subjects with soft pastel cards,
 * clear mark badges, and high-contrast typography.
 */
@Composable
fun SubjectCardsGrid(
    onSubjectClick: (Subject) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = BcsSubjectCards.chunked(2)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        rows.forEach { rowCards ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowCards.forEach { card ->
                    ModernSubjectCard(
                        card = card,
                        onClick = { onSubjectClick(card.subject) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowCards.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ModernSubjectCard(
    card: SubjectCardDesign,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = card.containerColor,
        border = BorderStroke(1.dp, card.accentColor.copy(alpha = 0.22f)),
        shadowElevation = 1.dp,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = card.accentColor.copy(alpha = 0.14f),
                    modifier = Modifier.size(36.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = card.icon,
                            contentDescription = null,
                            tint = card.accentColor,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = card.accentColor.copy(alpha = 0.12f),
                ) {
                    Text(
                        text = card.marksBangla,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = card.accentColor,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = card.titleBangla,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = card.subtitleBangla,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
