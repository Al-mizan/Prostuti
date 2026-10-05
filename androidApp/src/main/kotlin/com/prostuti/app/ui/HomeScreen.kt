package com.prostuti.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prostuti.core.common.SessionStore
import com.prostuti.core.common.UserStats
import com.prostuti.core.designsystem.MascotAvatar
import com.prostuti.core.designsystem.MascotPresets
import com.prostuti.core.designsystem.ProstutiBadge
import com.prostuti.core.designsystem.ProstutiCard
import com.prostuti.core.designsystem.ProstutiProgressBar
import com.prostuti.core.model.Role
import com.prostuti.feature.profile.presentation.ProfileUiState
import com.prostuti.feature.profile.presentation.ProfileViewModel
import java.time.DayOfWeek
import java.time.LocalDate

private fun toBanglaDigits(num: Int): String {
    val banglaDigits = listOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    return num.toString().map { char ->
        if (char in '0'..'9') banglaDigits[char - '0'] else char
    }.joinToString("")
}

@Composable
fun HomeScreen(
    sessionStore: SessionStore,
    profileViewModel: ProfileViewModel,
    onNavigateToPractice: () -> Unit,
    onNavigateToQuestionBank: () -> Unit,
    onNavigateToExam: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val role = sessionStore.role ?: Role.STUDENT
    val profileState by profileViewModel.uiState.collectAsState()

    val userName = when (val state = profileState) {
        is ProfileUiState.Success -> state.profile.name
        else -> if (role == Role.ADMIN) "অ্যাডমিন" else "পরীক্ষার্থী"
    }

    val avatarId = when (val state = profileState) {
        is ProfileUiState.Success -> state.profile.avatarId
        else -> "mascot_1"
    }

    val preset = MascotPresets.getById(avatarId)

    val stats = when (val state = profileState) {
        is ProfileUiState.Success -> state.stats
        else -> UserStats.Empty
    }

    val level = (stats.totalXp / 100) + 1
    val levelCurrentXp = stats.totalXp % 100
    val levelTargetXp = 100
    val xpProgress = (levelCurrentXp.toFloat() / levelTargetXp.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        // Hero Card with Mascot & Greeting
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                MaterialTheme.colorScheme.surface,
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        MascotAvatar(
                            avatarId = avatarId,
                            size = 64.dp,
                            onClick = onNavigateToProfile,
                        )

                        Spacer(Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = "স্বাগতম,",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                if (role == Role.ADMIN) {
                                    ProstutiBadge(
                                        text = "ADMIN",
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                    )
                                }
                            }

                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            Text(
                                text = "${preset.nameBangla} • ${preset.subtitle}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Mascot Inspiring Quote Pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "“${preset.quoteBangla}”",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // XP Level Progression
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "লেভেল ${toBanglaDigits(level)} (অগ্রগতি)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "${toBanglaDigits(levelCurrentXp)} / ${toBanglaDigits(levelTargetXp)} XP",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    ProstutiProgressBar(
                        progress = xpProgress,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Weekly Streak & Goals Card
        ProstutiCard(
            borderColor = Color(0xFFFED7AA),
            containerColor = Color(0xFFFFFBEB),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEDD5)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = Color(0xFFEA580C),
                                modifier = Modifier.size(22.dp),
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${toBanglaDigits(stats.streakDays)} দিনের ধারাবাহিকতা",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF431407),
                            )
                            Text(
                                text = "প্রতিদিনের অনুশীলনে সক্রিয় থাকুন",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF7C2D12),
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFFED7AA).copy(alpha = 0.6f),
                        contentColor = Color(0xFF7C2D12),
                        border = BorderStroke(1.dp, Color(0xFFFDBA74).copy(alpha = 0.6f)),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = Color(0xFFC2410C),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "+${toBanglaDigits(stats.totalXp)} XP",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C2D12),
                            )
                        }
                    }
                }

                // 7-day Week Streak Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    val todayDayOfWeek = LocalDate.now().dayOfWeek
                    val todayIndex = when (todayDayOfWeek) {
                        DayOfWeek.SATURDAY -> 0
                        DayOfWeek.SUNDAY -> 1
                        DayOfWeek.MONDAY -> 2
                        DayOfWeek.TUESDAY -> 3
                        DayOfWeek.WEDNESDAY -> 4
                        DayOfWeek.THURSDAY -> 5
                        DayOfWeek.FRIDAY -> 6
                    }
                    val days = listOf("শনি", "রবি", "সোম", "মঙ্গল", "বুধ", "বৃহঃ", "শুক্র")
                    days.forEachIndexed { index, day ->
                        val isDone = stats.weeklyActivity.getOrElse(index) { false }
                        val isToday = index == todayIndex
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isDone -> Color(0xFFEA580C)
                                            isToday -> Color(0xFFFDBA74)
                                            else -> Color(0xFFE5E7EB)
                                        }
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isDone) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isToday) Color(0xFF7C2D12) else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Main Modules Header
        Text(
            text = "অনুশীলন ও পরীক্ষা",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(12.dp))

        // BCS Question Bank Card
        DashboardActionCard(
            title = "বিসিএস প্রশ্ন ব্যাংক",
            subtitle = "বিগত বিসিএস পরীক্ষার অধ্যায়ভিত্তিক প্রশ্ন ও সমাধান",
            badgeText = "প্রশ্ন ব্যাংক",
            icon = Icons.AutoMirrored.Filled.LibraryBooks,
            iconColor = MaterialTheme.colorScheme.primary,
            iconBgColor = MaterialTheme.colorScheme.primaryContainer,
            onClick = onNavigateToQuestionBank,
        )

        Spacer(Modifier.height(10.dp))

        // Subject Practice Card
        DashboardActionCard(
            title = "বিষয়ভিত্তিক অনুশীলন",
            subtitle = "৯টি বিষয়ের অধ্যায়ভিত্তিক পূর্ণাঙ্গ প্রস্তুতি",
            badgeText = "৯টি বিষয়",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            iconColor = Color(0xFF0D9488),
            iconBgColor = Color(0xFFCCFBF1),
            onClick = onNavigateToPractice,
        )

        Spacer(Modifier.height(10.dp))

        // Timed Mock Exam Card
        DashboardActionCard(
            title = "পূর্ণাঙ্গ টাইমড মক টেস্ট",
            subtitle = "২০০ নম্বর ও ১২০ মিনিটের রিয়েল-টাইম পরীক্ষা",
            badgeText = "মক টেস্ট",
            icon = Icons.Default.Timer,
            iconColor = Color(0xFF2563EB),
            iconBgColor = Color(0xFFDBEAFE),
            onClick = onNavigateToExam,
        )

        Spacer(Modifier.height(10.dp))

        // History & Wrong Answers Card
        DashboardActionCard(
            title = "ইতিহাস ও রিভিশন",
            subtitle = "বিগত ফলাফল এবং ভুল উত্তরসমূহ রিভিশন ডেক",
            badgeText = "রিভিশন",
            icon = Icons.Default.History,
            iconColor = Color(0xFF7C3AED),
            iconBgColor = Color(0xFFEDE9FE),
            onClick = onNavigateToHistory,
        )

        // Admin Card (Visible to Role.ADMIN)
        if (role == Role.ADMIN) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = "প্রশাসনিক প্যানেল",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(10.dp))

            DashboardActionCard(
                title = "অ্যাডমিন কন্ট্রোল পোর্টাল",
                subtitle = "প্রশ্ন CSV ইমপোর্ট ও সিস্টেম ম্যানেজমেন্ট",
                badgeText = "অ্যাডমিন",
                icon = Icons.Default.AdminPanelSettings,
                iconColor = MaterialTheme.colorScheme.error,
                iconBgColor = MaterialTheme.colorScheme.errorContainer,
                onClick = onNavigateToAdmin,
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun DashboardActionCard(
    title: String,
    subtitle: String,
    badgeText: String,
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    onClick: () -> Unit,
) {
    ProstutiCard(onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    ProstutiBadge(
                        text = badgeText,
                        containerColor = iconBgColor,
                        contentColor = iconColor,
                    )
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                )
            }

            Spacer(Modifier.width(8.dp))

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}