package com.prostuti.core.designsystem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable M3 OutlinedTextField wrapper used across all feature modules.
 */
@Composable
fun ProstutiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
    )
}

/**
 * Official visual emblem icon for the Prostuti brand.
 */
@Composable
fun ProstutiEmblem(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 64.dp,
) {
    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(size * 0.28f),
        color = MaterialTheme.colorScheme.primary,
        border = BorderStroke(1.5.dp, Color(0xFFFBBF24).copy(alpha = 0.6f)),
        shadowElevation = 4.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF018750),
                            Color(0xFF004D2C),
                        ),
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "Prostuti Crest",
                tint = Color(0xFFFDE68A),
                modifier = Modifier.size(size * 0.58f),
            )
        }
    }
}

/**
 * Official brand logo pairing the Prostuti emblem with typography and tagline.
 */
@Composable
fun ProstutiLogo(
    modifier: Modifier = Modifier,
    showTagline: Boolean = true,
) {
    Column(
        modifier = modifier.padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ProstutiEmblem(size = 68.dp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Prostuti",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = (-0.5).sp,
        )
        if (showTagline) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "বিসিএস প্রিলিমিনারি পূর্ণাঙ্গ প্রস্তুতি",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

/**
 * Authentic official Google 4-color 'G' vector graphic
 * (Red #EA4335, Yellow #FBBC05, Green #34A853, Blue #4285F4).
 */
@Composable
fun GoogleLogo(modifier: Modifier = Modifier) {
    val bluePath = remember {
        Path().apply {
            moveTo(22.56f, 12.25f)
            cubicTo(22.56f, 11.47f, 22.49f, 10.72f, 22.36f, 10.0f)
            lineTo(12f, 10.0f)
            lineTo(12f, 14.26f)
            lineTo(17.92f, 14.26f)
            cubicTo(17.66f, 15.63f, 16.88f, 16.79f, 15.71f, 17.57f)
            lineTo(15.71f, 20.34f)
            lineTo(19.28f, 20.34f)
            cubicTo(21.36f, 18.42f, 22.56f, 15.60f, 22.56f, 12.25f)
            close()
        }
    }
    val greenPath = remember {
        Path().apply {
            moveTo(12f, 23f)
            cubicTo(14.97f, 23f, 17.46f, 22.02f, 19.28f, 20.34f)
            lineTo(15.71f, 17.57f)
            cubicTo(14.73f, 18.23f, 13.48f, 18.63f, 12f, 18.63f)
            cubicTo(9.14f, 18.63f, 6.71f, 16.70f, 5.84f, 14.10f)
            lineTo(2.18f, 14.10f)
            lineTo(2.18f, 16.94f)
            cubicTo(3.99f, 20.53f, 7.7f, 23f, 12f, 23f)
            close()
        }
    }
    val yellowPath = remember {
        Path().apply {
            moveTo(5.84f, 14.09f)
            cubicTo(5.62f, 13.43f, 5.49f, 12.73f, 5.49f, 12f)
            cubicTo(5.49f, 11.27f, 5.62f, 10.57f, 5.84f, 9.91f)
            lineTo(5.84f, 7.07f)
            lineTo(2.18f, 7.07f)
            cubicTo(1.43f, 8.55f, 1f, 10.22f, 1f, 12f)
            cubicTo(1f, 13.78f, 1.43f, 15.45f, 2.18f, 16.93f)
            lineTo(5.03f, 14.71f)
            lineTo(5.84f, 14.09f)
            close()
        }
    }
    val redPath = remember {
        Path().apply {
            moveTo(12f, 5.38f)
            cubicTo(13.62f, 5.38f, 15.06f, 5.94f, 16.21f, 7.02f)
            lineTo(19.36f, 3.87f)
            cubicTo(17.45f, 2.09f, 14.97f, 1f, 12f, 1f)
            cubicTo(7.7f, 1f, 3.99f, 3.47f, 2.18f, 7.07f)
            lineTo(5.84f, 9.91f)
            cubicTo(6.71f, 7.31f, 9.14f, 5.38f, 12f, 5.38f)
            close()
        }
    }

    Box(
        modifier = modifier.size(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            scale(
                scaleX = size.width / 24f,
                scaleY = size.height / 24f,
                pivot = androidx.compose.ui.geometry.Offset.Zero,
            ) {
                drawPath(path = redPath, color = Color(0xFFEA4335))
                drawPath(path = yellowPath, color = Color(0xFFFBBC05))
                drawPath(path = greenPath, color = Color(0xFF34A853))
                drawPath(path = bluePath, color = Color(0xFF4285F4))
            }
        }
    }
}

/**
 * Elevated, bordered Card component.
 */
@Composable
fun ProstutiCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
    contentPadding: androidx.compose.ui.unit.Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    val border = BorderStroke(1.dp, borderColor)

    if (onClick != null) {
        Card(
            onClick = onClick,
            shape = shape,
            border = border,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 4.dp),
            modifier = modifier.fillMaxWidth(),
        ) {
            Box(modifier = Modifier.padding(contentPadding)) {
                content()
            }
        }
    } else {
        Card(
            shape = shape,
            border = border,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = modifier.fillMaxWidth(),
        ) {
            Box(modifier = Modifier.padding(contentPadding)) {
                content()
            }
        }
    }
}

/**
 * Pill-shaped badge for Role, Session, Difficulty labels.
 */
@Composable
fun ProstutiBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = containerColor,
        contentColor = contentColor,
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

/**
 * Unified 2-column subject card ensuring standard 152.dp height, pastel container,
 * rounded icon container, marks pill badge, and bold 2-line Bengali typography.
 */
@Composable
fun UnifiedSubjectCard(
    title: String,
    marks: String,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionBadgeText: String? = null,
    bottomContent: (@Composable () -> Unit)? = null,
) {
    val isDark = isSystemInDarkTheme()
    val effectiveContainerColor = if (isDark) {
        accentColor.copy(alpha = 0.12f).compositeOver(MaterialTheme.colorScheme.surface)
    } else {
        containerColor
    }

    val isLightContainer = effectiveContainerColor.luminance() > 0.45f
    val titleColor = if (isLightContainer) {
        Color(0xFF0F172A)
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val subtitleColor = if (isLightContainer) {
        Color(0xFF475569)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = effectiveContainerColor,
        border = BorderStroke(1.dp, accentColor.copy(alpha = if (isDark) 0.30f else 0.20f)),
        shadowElevation = if (isDark) 0.dp else 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(152.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Top row: rounded icon container (36.dp) with 10.dp corner radius + Marks pill badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accentColor.copy(alpha = if (isDark) 0.22f else 0.14f),
                    modifier = Modifier.size(36.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = accentColor.copy(alpha = if (isDark) 0.20f else 0.12f),
                ) {
                    Text(
                        text = marks,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }

            // Middle: 2-line title in bold Bengali typography
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                minLines = 2,
                maxLines = 2,
                lineHeight = 18.sp,
                overflow = TextOverflow.Ellipsis,
            )

            // Bottom: Subtitle or action badge
            Box(modifier = Modifier.fillMaxWidth()) {
                if (bottomContent != null) {
                    bottomContent()
                } else if (actionBadgeText != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = subtitleColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = accentColor.copy(alpha = if (isDark) 0.20f else 0.12f),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = actionBadgeText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = accentColor,
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(12.dp),
                                )
                            }
                        }
                    }
                } else if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = subtitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/**
 * Styled Progress Bar with smooth rounded caps.
 */
@Composable
fun ProstutiProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50)),
        color = color,
        trackColor = trackColor,
        strokeCap = StrokeCap.Round,
    )
}

/**
 * Dialog for editing user name.
 */
@Composable
fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var name by remember { mutableStateOf(currentName) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "নাম পরিবর্তন করুন",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProstutiTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) error = null
                    },
                    label = "পূর্ণ নাম",
                )
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        error = "নাম ফাঁকা রাখা যাবে না"
                    } else {
                        onConfirm(name.trim())
                    }
                }
            ) {
                Text("সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

/**
 * Deluxe Modal Bottom Sheet to pick from the 6 Mascot Presets.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MascotPickerBottomSheet(
    selectedAvatarId: String,
    onDismiss: () -> Unit,
    onSelectAvatar: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var highlightedAvatarId by remember { mutableStateOf(selectedAvatarId) }
    val highlightedPreset = MascotPresets.getById(highlightedAvatarId)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "আপনার ম্যাসকট বেছে নিন",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "আপনার প্রতিদিনের অনুশীলনের সঙ্গী ম্যাসকট নির্বাচন করুন",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(20.dp))

            // Highlighted Quote Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = highlightedPreset.backgroundColor,
                border = BorderStroke(1.dp, highlightedPreset.primaryColor.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = highlightedPreset.primaryColor,
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${highlightedPreset.nameBangla} (${highlightedPreset.titleEnglish})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = highlightedPreset.primaryColor,
                        )
                        Text(
                            text = highlightedPreset.quoteBangla,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(MascotPresets.list) { preset ->
                    val isSelected = preset.id == selectedAvatarId
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                highlightedAvatarId = preset.id
                                onSelectAvatar(preset.id)
                            }
                            .padding(8.dp),
                    ) {
                        MascotAvatar(
                            avatarId = preset.id,
                            size = 72.dp,
                            isSelected = isSelected,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = preset.nameBangla,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = preset.subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}
