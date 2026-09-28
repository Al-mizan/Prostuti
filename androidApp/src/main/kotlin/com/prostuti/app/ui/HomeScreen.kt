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
import com.prostuti.core.designsystem.MascotAvatar
import com.prostuti.core.designsystem.MascotPresets
import com.prostuti.core.designsystem.ProstutiBadge
import com.prostuti.core.designsystem.ProstutiCard
import com.prostuti.core.designsystem.ProstutiProgressBar
import com.prostuti.core.model.Role
import com.prostuti.feature.profile.presentation.ProfileUiState
import com.prostuti.feature.profile.presentation.ProfileViewModel

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        // Hero Card with Mascot & Greeting
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.surface,
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        MascotAvatar(
                            avatarId = avatarId,
                            size = 72.dp,
                            onClick = onNavigateToProfile,
                        )

                        Spacer(Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = "স্বাগতম,",
                                    style = MaterialTheme.typography.labelLarge,
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
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            Text(
                                text = "${preset.nameBangla} • ${preset.subtitle}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Mascot Inspiring Quote Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "“${preset.quoteBangla}”",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // XP Level Progression
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "লেভেল ২ (অগ্রগতি)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "১২০ / ২০০ XP",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    ProstutiProgressBar(
                        progress = 0.6f,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Weekly Streak & Goals Card
        ProstutiCard(
            borderColor = Color(0xFFFED7AA),
            containerColor = Color(0xFFFFFBEB),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEDD5)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = Color(0xFFEA580C),
                                modifier = Modifier.size(26.dp),
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "৩ দিনের ধারাবাহিকতা",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C2D12),
                            )
                            Text(
                                text = "প্রতিদিনের অনুশীলনে স্ট্রিক ধরে রাখুন",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF9A3412),
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFFEF3C7),
                        contentColor = Color(0xFFB45309),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "+১৫ XP",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                // 7-day Week Streak Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    val days = listOf("শনি", "রবি", "সোম", "মঙ্গল", "বুধ", "বৃহঃ", "শুক্র")
                    days.forEachIndexed { index, day ->
                        val isDone = index in 0..2
                        val isToday = index == 2
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
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
                                        modifier = Modifier.size(18.dp),
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

        Spacer(Modifier.height(24.dp))

        // Main Modules Header
        Text(
            text = "অনুশীলন ও পরীক্ষা",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(14.dp))

        // BCS Question Bank Card
        DashboardActionCard(
            title = "বিসিএস প্রশ্ন ব্যাংক",
            subtitle = "বিগত ৪৫তম থেকে ৩৫তম বিসিএস পরীক্ষার অধ্যায়ভিত্তিক প্রশ্ন ও সমাধান",
            badgeText = "BCS Question Bank",
            icon = Icons.AutoMirrored.Filled.LibraryBooks,
            iconColor = MaterialTheme.colorScheme.primary,
            iconBgColor = MaterialTheme.colorScheme.primaryContainer,
            onClick = onNavigateToQuestionBank,
        )

        Spacer(Modifier.height(12.dp))

        // Subject Practice Card
        DashboardActionCard(
            title = "বিষয়ভিত্তিক অনুশীলন",
            subtitle = "বাংলা, ইংরেজি, সাধারণ জ্ঞান ও গণিত সহ ৯টি বিষয়ের পূর্ণাঙ্গ প্রস্তুতি",
            badgeText = "9 Subjects",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            iconColor = Color(0xFF0D9488),
            iconBgColor = Color(0xFFCCFBF1),
            onClick = onNavigateToPractice,
        )

        Spacer(Modifier.height(12.dp))

        // Timed Mock Exam Card
        DashboardActionCard(
            title = "পূর্ণাঙ্গ টাইমড মক টেস্ট",
            subtitle = "বাস্তব পরীক্ষার অভিজ্ঞতা: ২০০ নম্বর, সময় ১২০ মিনিট, তাৎক্ষণিক ফলাফল ও বিশ্লেষণ",
            badgeText = "Mock Exam",
            icon = Icons.Default.Timer,
            iconColor = Color(0xFF2563EB),
            iconBgColor = Color(0xFFDBEAFE),
            onClick = onNavigateToExam,
        )

        Spacer(Modifier.height(12.dp))

        // History & Wrong Answers Card
        DashboardActionCard(
            title = "পরীক্ষার ইতিহাস ও ভুল উত্তর",
            subtitle = "বিগত সব প্রচেষ্টার ফলাফল এবং ভুল উত্তরসমূহ রিভিশন ডেক",
            badgeText = "History",
            icon = Icons.Default.History,
            iconColor = Color(0xFF7C3AED),
            iconBgColor = Color(0xFFEDE9FE),
            onClick = onNavigateToHistory,
        )

        // Admin Card (Visible to Role.ADMIN)
        if (role == Role.ADMIN) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "প্রশাসনিক প্যানেল",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(12.dp))

            DashboardActionCard(
                title = "অ্যাডমিন কন্ট্রোল পোর্টাল",
                subtitle = "নতুন প্রশ্ন CSV ইমপোর্ট, প্রশ্ন ব্যাংক আপডেট এবং ইউজার ম্যানেজমেন্ট",
                badgeText = "ADMIN ONLY",
                icon = Icons.Default.AdminPanelSettings,
                iconColor = MaterialTheme.colorScheme.error,
                iconBgColor = MaterialTheme.colorScheme.errorContainer,
                onClick = onNavigateToAdmin,
            )
        }

        Spacer(Modifier.height(32.dp))
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
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp),
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    ProstutiBadge(
                        text = badgeText,
                        containerColor = iconBgColor,
                        contentColor = iconColor,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp,
                )
            }

            Spacer(Modifier.width(8.dp))

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}