package com.prostuti.feature.questionbank.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Computer
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prostuti.core.designsystem.ProstutiCrimson
import com.prostuti.core.designsystem.ProstutiCrimsonContainer
import com.prostuti.core.designsystem.ProstutiCrimsonDark
import com.prostuti.core.designsystem.ProstutiCrimsonLight
import com.prostuti.core.designsystem.UnifiedSubjectCard
import com.prostuti.core.model.ModelTestDto
import com.prostuti.core.model.ModelTestStatus
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
 * Dynamic Live Model Test Banner with live pulsing indicator dot,
 * countdown, high-contrast WCAG AA text, and single-click CTA.
 * If liveTest == null, renders a sleek fallback banner.
 */
@Composable
fun LiveModelTestBannerCard(
    liveTest: ModelTestDto?,
    onJoinExam: (sessionName: String) -> Unit,
    onBrowseAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (liveTest == null) {
        // Fallback banner when no live test is active
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E293B), // Slate deep
                            Color(0xFF0F172A),
                            Color(0xFF020617),
                        )
                    )
                )
                .clickable(onClick = onBrowseAll),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.15f),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.EventNote,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = "মডেল টেস্ট আপডেট",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE2E8F0),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "কোনো লাইভ মডেল টেস্ট চলমান নেই",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "আসন্ন পরীক্ষার সময়সূচী বা পূর্বের আর্কাইভ মডেল টেস্টগুলোতে অংশ নিতে ক্যাটালগ ব্রাউজ করুন।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCBD5E1),
                )

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = onBrowseAll,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "সকল মডেল টেস্ট ব্রাউজ করুন",
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
        return
    }

    // Active live or upcoming model test banner
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseScale",
    )

    val isLive = liveTest.status == ModelTestStatus.LIVE
    val isUpcoming = liveTest.status == ModelTestStatus.UPCOMING

    val gradientColors = when {
        isLive -> listOf(Color(0xFFFFF1F2), Color(0xFFFFE4E6))
        isUpcoming -> listOf(Color(0xFF1E3A8A), Color(0xFF172554), Color(0xFF0F172A))
        else -> listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A))
    }

    val countdownText = if (isLive) {
        formatCountdown(liveTest.endTime, isLive = true)
    } else if (isUpcoming) {
        formatCountdown(liveTest.startTime, isLive = false)
    } else {
        "সমাপ্ত"
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLive) 6.dp else 4.dp),
        border = if (isLive) BorderStroke(1.5.dp, ProstutiCrimson) else null,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(colors = gradientColors))
            .clickable {
                if (isLive) {
                    onJoinExam(liveTest.examSession.ifBlank { liveTest.title })
                } else {
                    onBrowseAll()
                }
            },
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
                    color = when {
                        isLive -> ProstutiCrimson
                        isUpcoming -> Color(0xFF2563EB)
                        else -> Color(0xFF475569)
                    },
                    shadowElevation = if (isLive) 3.dp else 0.dp,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        if (isLive) {
                            // Pulsing dot with glowing halo
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(12.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .graphicsLayer {
                                            scaleX = pulseScale
                                            scaleY = pulseScale
                                            alpha = pulseAlpha * 0.5f
                                        }
                                        .background(Color.White.copy(alpha = 0.5f), CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(Color.White, CircleShape)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (isUpcoming) Icons.Default.AccessTime else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp),
                            )
                        }
                        Text(
                            text = when {
                                isLive -> "লাইভ চলছে"
                                isUpcoming -> "আসন্ন পরীক্ষা"
                                else -> "আর্কাইভ পরীক্ষা"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isLive) ProstutiCrimsonContainer else Color.White.copy(alpha = 0.18f),
                    border = if (isLive) BorderStroke(1.dp, ProstutiCrimson.copy(alpha = 0.25f)) else null,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (isLive) ProstutiCrimson else Color(0xFFFDE047),
                            modifier = Modifier.size(13.dp),
                        )
                        Text(
                            text = countdownText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isLive) ProstutiCrimsonDark else Color.White,
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = liveTest.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isLive) ProstutiCrimsonDark else Color.White,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = liveTest.description?.takeIf { it.isNotBlank() }
                    ?: "${liveTest.examSession} • পূর্ণাঙ্গ সিলেবাস • রিয়েল-টাইম ফলাফল ও ব্যাখ্যা",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isLive) Color(0xFF475569) else Color(0xFFF1F5F9),
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
                        tint = if (isLive) ProstutiCrimson else Color(0xFFFDE047),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "${liveTest.durationMinutes.toBanglaDigits()} মিনিট",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isLive) Color(0xFF1E293B) else Color.White,
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
                        tint = if (isLive) ProstutiCrimson else Color(0xFF93C5FD),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "${liveTest.totalQuestions.toBanglaDigits()} প্রশ্ন • ${liveTest.totalMarks.toInt().toBanglaDigits()} নম্বর",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isLive) Color(0xFF1E293B) else Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = {
                    if (isLive) {
                        onJoinExam(liveTest.examSession.ifBlank { liveTest.title })
                    } else {
                        onBrowseAll()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLive) ProstutiCrimson else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (isLive) "পরীক্ষায় অংশ নিন" else "বিস্তারিত ও সূচি দেখুন",
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
 * Institute Model Test Card taking student to the BCS Model Test catalog/archive.
 */
@Composable
fun InstituteModelTestCard(
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
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.size(52.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FactCheck,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
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
                            text = "বিসিএস মডেল টেস্ট",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFDC2626).copy(alpha = 0.12f),
                        ) {
                            Text(
                                text = "লাইভ ও আর্কাইভ",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "সকল লাইভ, আসন্ন ও বিগত আর্কাইভ মডেল টেস্ট",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open model tests",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

private fun formatCountdown(isoTime: String, isLive: Boolean): String {
    return try {
        val targetMillis = java.time.Instant.parse(isoTime).toEpochMilli()
        val nowMillis = System.currentTimeMillis()
        val diffMillis = targetMillis - nowMillis
        if (diffMillis <= 0) return if (isLive) "সময় সমাপ্ত" else "চলমান"
        val hours = diffMillis / (1000 * 60 * 60)
        val minutes = (diffMillis / (1000 * 60)) % 60
        val days = hours / 24
        if (days > 0) {
            val remHours = hours % 24
            if (isLive) "${days.toInt().toBanglaDigits()} দিন ${remHours.toInt().toBanglaDigits()} ঘণ্টা বাকি"
            else "${days.toInt().toBanglaDigits()} দিন পর শুরু"
        } else if (hours > 0) {
            if (isLive) "${hours.toInt().toBanglaDigits()} ঘণ্টা ${minutes.toInt().toBanglaDigits()} মিনিট বাকি"
            else "${hours.toInt().toBanglaDigits()} ঘণ্টা পর শুরু"
        } else {
            if (isLive) "${minutes.toInt().toBanglaDigits()} মিনিট বাকি"
            else "${minutes.toInt().toBanglaDigits()} মিনিট পর শুরু"
        }
    } catch (_: Exception) {
        if (isLive) "চলমান" else "আসন্ন"
    }
}

private fun Int.toBanglaDigits(): String {
    val banglaDigits = arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    return this.toString().map { if (it in '0'..'9') banglaDigits[it - '0'] else it }.joinToString("")
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        rows.forEach { rowCards ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowCards.forEach { card ->
                    UnifiedSubjectCard(
                        title = card.titleBangla,
                        marks = card.marksBangla,
                        icon = card.icon,
                        accentColor = card.accentColor,
                        containerColor = card.containerColor,
                        subtitle = card.subtitleBangla,
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
    UnifiedSubjectCard(
        title = card.titleBangla,
        marks = card.marksBangla,
        icon = card.icon,
        accentColor = card.accentColor,
        containerColor = card.containerColor,
        subtitle = card.subtitleBangla,
        onClick = onClick,
        modifier = modifier,
    )
}
