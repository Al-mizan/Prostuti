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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prostuti.core.designsystem.ProstutiBadge
import com.prostuti.core.designsystem.ProstutiCard
import com.prostuti.core.designsystem.ProstutiProgressBar
import com.prostuti.core.model.Subject

data class SubjectCardItem(
    val subject: Subject,
    val banglaName: String,
    val marks: String,
    val questionCount: Int,
    val icon: ImageVector,
    val color: Color,
    val bgColor: Color,
)

@Composable
fun PracticeScreen(
    modifier: Modifier = Modifier,
) {
    val subjects = listOf(
        SubjectCardItem(
            subject = Subject.BENGALI,
            banglaName = "বাংলা ভাষা ও সাহিত্য",
            marks = "৩৫ নম্বর",
            questionCount = 350,
            icon = Icons.Default.Translate,
            color = Color(0xFF9E1B32),
            bgColor = Color(0xFFFFEBEE),
        ),
        SubjectCardItem(
            subject = Subject.ENGLISH,
            banglaName = "ইংরেজি ভাষা ও সাহিত্য",
            marks = "৩৫ নম্বর",
            questionCount = 350,
            icon = Icons.Default.Language,
            color = Color(0xFF1E40AF),
            bgColor = Color(0xFFDBEAFE),
        ),
        SubjectCardItem(
            subject = Subject.BD_INTERNATIONAL_AFFAIRS,
            banglaName = "বাংলাদেশ ও আন্তর্জাতিক বিষয়াবলি",
            marks = "৫০ নম্বর",
            questionCount = 500,
            icon = Icons.Default.Public,
            color = Color(0xFF0D9488),
            bgColor = Color(0xFFCCFBF1),
        ),
        SubjectCardItem(
            subject = Subject.GEOGRAPHY,
            banglaName = "ভূগোল ও পরিবেশ",
            marks = "১০ নম্বর",
            questionCount = 100,
            icon = Icons.AutoMirrored.Filled.MenuBook,
            color = Color(0xFFD97706),
            bgColor = Color(0xFFFEF3C7),
        ),
        SubjectCardItem(
            subject = Subject.SCIENCE,
            banglaName = "সাধারণ বিজ্ঞান",
            marks = "১৫ নম্বর",
            questionCount = 150,
            icon = Icons.Default.Science,
            color = Color(0xFF059669),
            bgColor = Color(0xFFD1FAE5),
        ),
        SubjectCardItem(
            subject = Subject.IT,
            banglaName = "কম্পিউটার ও তথ্যপ্রযুক্তি",
            marks = "১৫ নম্বর",
            questionCount = 150,
            icon = Icons.Default.Computer,
            color = Color(0xFF7C3AED),
            bgColor = Color(0xFFEDE9FE),
        ),
        SubjectCardItem(
            subject = Subject.MATH,
            banglaName = "গাণিতিক যুক্তি",
            marks = "১৫ নম্বর",
            questionCount = 150,
            icon = Icons.Default.Calculate,
            color = Color(0xFFDC2626),
            bgColor = Color(0xFFFEE2E2),
        ),
        SubjectCardItem(
            subject = Subject.MENTAL_ABILITY,
            banglaName = "মানসিক দক্ষতা",
            marks = "১৫ নম্বর",
            questionCount = 150,
            icon = Icons.Default.Psychology,
            color = Color(0xFF4F46E5),
            bgColor = Color(0xFFE0E7FF),
        ),
        SubjectCardItem(
            subject = Subject.ETHICS,
            banglaName = "নৈতিকতা, মূল্যবোধ ও সুশাসন",
            marks = "১০ নম্বর",
            questionCount = 100,
            icon = Icons.Default.Security,
            color = Color(0xFF0284C7),
            bgColor = Color(0xFFE0F2FE),
        ),
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
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
                text = "বিসিএস প্রিলিমিনারির ৯টি নির্ধারিত বিষয় বেছে নিন",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(18.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(subjects) { item ->
                    Card(
                        onClick = { /* Start practice */ },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, item.color.copy(alpha = 0.25f)),
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
                                        .background(item.bgColor),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        item.icon,
                                        contentDescription = null,
                                        tint = item.color,
                                        modifier = Modifier.size(22.dp),
                                    )
                                }

                                ProstutiBadge(
                                    text = item.marks,
                                    containerColor = item.bgColor,
                                    contentColor = item.color,
                                )
                            }

                            Text(
                                text = item.banglaName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                minLines = 2,
                                lineHeight = 18.sp,
                            )

                            Text(
                                text = "${item.questionCount}+ টি প্রশ্ন",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            ProstutiProgressBar(
                                progress = 0.25f,
                                color = item.color,
                                trackColor = item.bgColor,
                            )
                        }
                    }
                }
            }
        }
    }
}
