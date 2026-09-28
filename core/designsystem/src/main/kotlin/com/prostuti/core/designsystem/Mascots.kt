package com.prostuti.core.designsystem

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MascotPreset(
    val id: String,
    val nameBangla: String,
    val titleEnglish: String,
    val subtitle: String,
    val emoji: String,
    val primaryColor: Color,
    val backgroundColor: Color,
    val quoteBangla: String,
)

object MascotPresets {
    val list = listOf(
        MascotPreset(
            id = "mascot_1",
            nameBangla = "বিজ্ঞ পেঁচা",
            titleEnglish = "Wise Owl",
            subtitle = "জ্ঞানের প্রতীক",
            emoji = "🦉",
            primaryColor = Color(0xFF9E1B32),
            backgroundColor = Color(0xFFFFF1F2),
            quoteBangla = "নিয়মিত প্রস্তুতিই নিশ্চিত বিজয়ের মূল ভিত্তি।",
        ),
        MascotPreset(
            id = "mascot_2",
            nameBangla = "বিজয়ী সিংহ",
            titleEnglish = "Champion Lion",
            subtitle = "আত্মবিশ্বাসের প্রতীক",
            emoji = "🦁",
            primaryColor = Color(0xFFD97706),
            backgroundColor = Color(0xFFFEF3C7),
            quoteBangla = "দৃঢ় আত্মবিশ্বাস ও সাহসিকতার সাথে লক্ষ্য অর্জন করুন।",
        ),
        MascotPreset(
            id = "mascot_3",
            nameBangla = "চতুর শিয়াল",
            titleEnglish = "Sharp Fox",
            subtitle = "কৌশলের প্রতীক",
            emoji = "🦊",
            primaryColor = Color(0xFF0D9488),
            backgroundColor = Color(0xFFCCFBF1),
            quoteBangla = "সঠিক কৌশল ও সময়ানুবর্তিতা কঠিন পথকে সহজ করে।",
        ),
        MascotPreset(
            id = "mascot_4",
            nameBangla = "লক্ষ্যভেদী বাঘ",
            titleEnglish = "Focused Tiger",
            subtitle = "দৃঢ় সংকল্পের প্রতীক",
            emoji = "🐯",
            primaryColor = Color(0xFF2563EB),
            backgroundColor = Color(0xFFDBEAFE),
            quoteBangla = "নিখুঁত একাগ্রতায় প্রতিটি প্রশ্নের সঠিক সমাধান আনুন।",
        ),
        MascotPreset(
            id = "mascot_5",
            nameBangla = "জ্ঞানী হাতি",
            titleEnglish = "Patient Elephant",
            subtitle = "ধৈর্য্যের প্রতীক",
            emoji = "🐘",
            primaryColor = Color(0xFF059669),
            backgroundColor = Color(0xFFD1FAE5),
            quoteBangla = "ধৈর্য ও অবিচল অধ্যবসায়ই দীর্ঘ লড়াইয়ে বিজয়ী করে।",
        ),
        MascotPreset(
            id = "mascot_6",
            nameBangla = "বিদ্যোৎসাহী হরিণ",
            titleEnglish = "Agile Deer",
            subtitle = "চঞ্চল ও মনোযোগী",
            emoji = "🦌",
            primaryColor = Color(0xFF7C3AED),
            backgroundColor = Color(0xFFEDE9FE),
            quoteBangla = "ক্ষিপ্র চিন্তা ও গতিশীল সমাধান নিশ্চিত করে এগিয়ে থাকা।",
        ),
    )

    fun getById(id: String?): MascotPreset =
        list.firstOrNull { it.id == id } ?: list.first()
}

@Composable
fun MascotAvatar(
    avatarId: String?,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    isSelected: Boolean = false,
    showRing: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    val preset = MascotPresets.getById(avatarId)
    val fontSize = (size.value * 0.5f).sp
    val elevation by animateDpAsState(if (isSelected) 8.dp else 2.dp, label = "elevation")

    val clickableModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    val borderModifier = if (isSelected) {
        Modifier.border(3.dp, Brush.linearGradient(listOf(preset.primaryColor, MaterialTheme.colorScheme.primary)), CircleShape)
    } else if (showRing) {
        Modifier.border(2.dp, preset.primaryColor.copy(alpha = 0.25f), CircleShape)
    } else {
        Modifier
    }

    Box(
        modifier = clickableModifier
            .size(size)
            .shadow(elevation, CircleShape)
            .then(borderModifier)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color.White, preset.backgroundColor)
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = preset.emoji,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
        )

        if (isSelected) {
            Surface(
                shape = CircleShape,
                color = preset.primaryColor,
                contentColor = Color.White,
                modifier = Modifier
                    .size(size * 0.32f)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-2).dp, y = (-2).dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "নির্বাচিত",
                        modifier = Modifier.size(size * 0.2f),
                    )
                }
            }
        }
    }
}
