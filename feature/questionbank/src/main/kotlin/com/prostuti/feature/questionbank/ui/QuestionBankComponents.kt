package com.prostuti.feature.questionbank.ui

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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
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
    val watermark: String,
    val gradientColors: List<Color>,
)

val BcsSubjectCards = listOf(
    SubjectCardDesign(
        subject = Subject.BENGALI,
        titleBangla = "বাংলা ভাষা ও সাহিত্য",
        subtitleBangla = "৩৫ নম্বর • ব্যাকরণ ও সাহিত্য",
        watermark = "অ",
        gradientColors = listOf(Color(0xFFE11D48), Color(0xFF9F1239)),
    ),
    SubjectCardDesign(
        subject = Subject.ENGLISH,
        titleBangla = "ইংরেজি ভাষা ও সাহিত্য",
        subtitleBangla = "৩৫ নম্বর • Grammar & Literature",
        watermark = "A",
        gradientColors = listOf(Color(0xFF2563EB), Color(0xFF1E40AF)),
    ),
    SubjectCardDesign(
        subject = Subject.BD_INTERNATIONAL_AFFAIRS,
        titleBangla = "বাংলাদেশ ও আন্তর্জাতিক বিষয়াবলি",
        subtitleBangla = "৫০ নম্বর • জাতীয় ও বৈশ্বিক বিষয়",
        watermark = "ব",
        gradientColors = listOf(Color(0xFF059669), Color(0xFF047857)),
    ),
    SubjectCardDesign(
        subject = Subject.GEOGRAPHY,
        titleBangla = "ভূগোল, পরিবেশ ও দুর্যোগ",
        subtitleBangla = "১০ নম্বর • বাংলাদেশ ও বিশ্ব ভূগোল",
        watermark = "ভূ",
        gradientColors = listOf(Color(0xFF0891B2), Color(0xFF0E7490)),
    ),
    SubjectCardDesign(
        subject = Subject.SCIENCE,
        titleBangla = "সাধারণ বিজ্ঞান",
        subtitleBangla = "১৫ নম্বর • ভৌত, জীব ও আধুনিক বিজ্ঞান",
        watermark = "বি",
        gradientColors = listOf(Color(0xFF7C3AED), Color(0xFF6D28D9)),
    ),
    SubjectCardDesign(
        subject = Subject.IT,
        titleBangla = "কম্পিউটার ও তথ্যপ্রযুক্তি",
        subtitleBangla = "১৫ নম্বর • হার্ডওয়্যার ও সফটওয়্যার",
        watermark = "ত",
        gradientColors = listOf(Color(0xFFEA580C), Color(0xFFC2410C)),
    ),
    SubjectCardDesign(
        subject = Subject.MATH,
        titleBangla = "গাণিতিক যুক্তি",
        subtitleBangla = "১৫ নম্বর • পাটিগণিত, বীজগণিত ও জ্যামিতি",
        watermark = "∑",
        gradientColors = listOf(Color(0xFFC026D3), Color(0xFFA21CAF)),
    ),
    SubjectCardDesign(
        subject = Subject.MENTAL_ABILITY,
        titleBangla = "মানসিক দক্ষতা",
        subtitleBangla = "১৫ নম্বর • ভাষাগত ও যুক্তি বিশ্লেষণ",
        watermark = "মা",
        gradientColors = listOf(Color(0xFF0284C7), Color(0xFF0369A1)),
    ),
    SubjectCardDesign(
        subject = Subject.ETHICS,
        titleBangla = "নৈতিকতা, মূল্যবোধ ও সুশাসন",
        subtitleBangla = "১০ নম্বর • সুশাসন ও মূল্যবোধ শিক্ষা",
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
                        imageVector = Icons.Default.HelpOutline,
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
 * 9-subject visual grid corresponding to BCS syllabus subjects.
 */
@Composable
fun SubjectCardsGrid(
    onSubjectClick: (Subject) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BcsSubjectCards.forEach { card ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(card.gradientColors))
                    .clickable { onSubjectClick(card.subject) },
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // Large watermark letter in top-right corner
                    Text(
                        text = card.watermark,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White.copy(alpha = 0.16f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp),
                    )

                    // Text content
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = card.titleBangla,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = card.subtitleBangla,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
