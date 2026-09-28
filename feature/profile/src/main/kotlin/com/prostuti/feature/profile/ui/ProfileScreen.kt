package com.prostuti.feature.profile.ui

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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.prostuti.core.designsystem.EditNameDialog
import com.prostuti.core.designsystem.MascotAvatar
import com.prostuti.core.designsystem.MascotPickerBottomSheet
import com.prostuti.core.designsystem.MascotPresets
import com.prostuti.core.designsystem.ProstutiBadge
import com.prostuti.core.designsystem.ProstutiCard
import com.prostuti.core.model.Role
import com.prostuti.core.model.UserProfileDto
import com.prostuti.feature.profile.presentation.ProfileUiState
import com.prostuti.feature.profile.presentation.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLoggedOut: () -> Unit,
    onNavigateToHistory: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showMascotPickerSheet by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is ProfileUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadProfile() }) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("পুনরায় লোড করুন")
                    }
                }
            }
            is ProfileUiState.Success -> {
                val profile = state.profile
                val preset = MascotPresets.getById(profile.avatarId)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "ব্যবহারকারী প্রোফাইল",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    Spacer(Modifier.height(20.dp))

                    // Hero Profile Card
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
                                    Brush.verticalGradient(
                                        listOf(
                                            preset.backgroundColor.copy(alpha = 0.6f),
                                            MaterialTheme.colorScheme.surface,
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                // Avatar with quick edit badge
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    MascotAvatar(
                                        avatarId = profile.avatarId,
                                        size = 96.dp,
                                        onClick = { showMascotPickerSheet = true },
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = preset.primaryColor,
                                        contentColor = Color.White,
                                        shadowElevation = 4.dp,
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .clickable { showMascotPickerSheet = true },
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "ম্যাসকট পরিবর্তন",
                                                modifier = Modifier.size(15.dp),
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(14.dp))

                                // Name with Edit Icon
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        text = profile.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    IconButton(
                                        onClick = { showEditNameDialog = true },
                                        modifier = Modifier.size(32.dp),
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "নাম সম্পাদনা",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(17.dp),
                                        )
                                    }
                                }

                                // Mascot & Role Subtitle
                                Text(
                                    text = "${preset.nameBangla} (${preset.titleEnglish}) • ${preset.subtitle}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = preset.primaryColor,
                                    fontWeight = FontWeight.SemiBold,
                                )

                                Spacer(Modifier.height(8.dp))

                                val roleLabel = if (profile.role == Role.ADMIN) "অ্যাডমিন (Admin)" else "শিক্ষার্থী (Student)"
                                val roleColor = if (profile.role == Role.ADMIN) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                                val roleTextColor = if (profile.role == Role.ADMIN) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer

                                ProstutiBadge(
                                    text = roleLabel,
                                    containerColor = roleColor,
                                    contentColor = roleTextColor,
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // 3-Column Performance Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        ProfileStatBox(
                            title = "স্ট্রিক",
                            value = "৩ দিন",
                            icon = Icons.Default.LocalFireDepartment,
                            iconColor = Color(0xFFEA580C),
                            bgColor = Color(0xFFFFF7ED),
                            modifier = Modifier.weight(1f),
                        )
                        ProfileStatBox(
                            title = "মোট পয়েন্ট",
                            value = "১২০ XP",
                            icon = Icons.Default.Star,
                            iconColor = Color(0xFFD97706),
                            bgColor = Color(0xFFFEF3C7),
                            modifier = Modifier.weight(1f),
                        )
                        ProfileStatBox(
                            title = "সঠিকতার হার",
                            value = "৮৫%",
                            icon = Icons.Default.TrackChanges,
                            iconColor = Color(0xFF0D9488),
                            bgColor = Color(0xFFCCFBF1),
                            modifier = Modifier.weight(1f),
                        )
                    }

                    if (state.errorMessage != null) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = state.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    if (state.isUpdating) {
                        Spacer(Modifier.height(12.dp))
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }

                    Spacer(Modifier.height(20.dp))

                    // Mascot Persona Quote Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = preset.backgroundColor,
                        border = BorderStroke(1.dp, preset.primaryColor.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = preset.primaryColor,
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = preset.quoteBangla,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Account Information Card
                    ProstutiCard {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(
                                text = "অ্যাকাউন্ট বিবরণ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )

                            ProfileInfoRow(
                                icon = Icons.Default.Email,
                                label = "ইমেইল ঠিকানা",
                                value = profile.email,
                            )

                            ProfileInfoRow(
                                icon = Icons.Default.Security,
                                label = "ভূমিকা ও অনুমোদন",
                                value = profile.role.name,
                            )

                            ProfileInfoRow(
                                icon = Icons.Default.CalendarToday,
                                label = "যোগদানের তারিখ",
                                value = formatDate(profile.createdAt),
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Profile Actions Card
                    ProstutiCard {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            ProfileActionItem(
                                icon = Icons.Default.History,
                                title = "পরীক্ষার ইতিহাস ও ভুল উত্তর",
                                subtitle = "বিগত প্রচেষ্টা এবং ভুল উত্তরসমূহ পর্যালোচনা করুন",
                                onClick = onNavigateToHistory,
                            )
                            ProfileActionItem(
                                icon = Icons.Default.Face,
                                title = "ম্যাসকট পরিবর্তন করুন",
                                subtitle = "৬টি অনন্য ম্যাসকট থেকে আপনার পছন্দেরটি বেছে নিন",
                                onClick = { showMascotPickerSheet = true },
                            )
                            ProfileActionItem(
                                icon = Icons.Default.Edit,
                                title = "নাম সম্পাদনা করুন",
                                subtitle = "আপনার ডিসপ্লে নাম আপডেট করুন",
                                onClick = { showEditNameDialog = true },
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Logout Button
                    OutlinedButton(
                        onClick = {
                            viewModel.logout()
                            onLoggedOut()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "লগআউট করুন",
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    // Dialogs
                    if (showEditNameDialog) {
                        EditNameDialog(
                            currentName = profile.name,
                            onDismiss = { showEditNameDialog = false },
                            onConfirm = { newName ->
                                showEditNameDialog = false
                                viewModel.updateName(newName)
                            },
                        )
                    }

                    if (showMascotPickerSheet) {
                        MascotPickerBottomSheet(
                            selectedAvatarId = profile.avatarId,
                            onDismiss = { showMascotPickerSheet = false },
                            onSelectAvatar = { newAvatarId ->
                                showMascotPickerSheet = false
                                viewModel.updateAvatar(newAvatarId)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatBox(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(1.dp, iconColor.copy(alpha = 0.2f)),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = iconColor,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ProfileActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun formatDate(rawDate: String): String {
    return if (rawDate.length >= 10) {
        rawDate.substring(0, 10)
    } else {
        rawDate
    }
}
