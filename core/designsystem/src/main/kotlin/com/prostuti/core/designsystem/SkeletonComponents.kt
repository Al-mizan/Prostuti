package com.prostuti.core.designsystem

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Skeleton placeholder for an individual question card (used in Practice, Exam, and Study screens).
 */
@Composable
fun QuestionCardSkeleton(
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 90.dp, height = 24.dp)
                        .shimmerEffect(RoundedCornerShape(12.dp))
                )
                Box(
                    modifier = Modifier
                        .size(width = 65.dp, height = 24.dp)
                        .shimmerEffect(RoundedCornerShape(12.dp))
                )
            }

            Spacer(Modifier.height(16.dp))

            // Question prompt placeholder lines
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(18.dp)
                    .shimmerEffect(RoundedCornerShape(6.dp))
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(18.dp)
                    .shimmerEffect(RoundedCornerShape(6.dp))
            )

            Spacer(Modifier.height(24.dp))

            // 4 Option items
            repeat(4) { idx ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .shimmerEffect(CircleShape)
                    )
                    Spacer(Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (idx % 2 == 0) 0.85f else 0.65f)
                            .height(16.dp)
                            .shimmerEffect(RoundedCornerShape(6.dp))
                    )
                }
            }
        }
    }
}

/**
 * Skeleton placeholder for the 9-subject grid in Question Bank and Practice screens.
 */
@Composable
fun SubjectGridSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 6,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(itemCount) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .shimmerEffect(RoundedCornerShape(10.dp))
                        )
                        Box(
                            modifier = Modifier
                                .size(width = 44.dp, height = 20.dp)
                                .shimmerEffect(RoundedCornerShape(10.dp))
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(16.dp)
                                .shimmerEffect(RoundedCornerShape(4.dp))
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(12.dp)
                                .shimmerEffect(RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }
    }
}

/**
 * Skeleton placeholder for the BCS Preliminary sessions archive list (50th down to 10th BCS).
 */
@Composable
fun BcsSessionListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 5,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(itemCount) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shimmerEffect(CircleShape)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(width = 120.dp, height = 16.dp)
                                    .shimmerEffect(RoundedCornerShape(4.dp))
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 55.dp, height = 14.dp)
                                        .shimmerEffect(RoundedCornerShape(4.dp))
                                )
                                Box(
                                    modifier = Modifier
                                        .size(width = 55.dp, height = 14.dp)
                                        .shimmerEffect(RoundedCornerShape(4.dp))
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(width = 72.dp, height = 32.dp)
                            .shimmerEffect(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

/**
 * Skeleton placeholder for the Live Model Test banner card.
 */
@Composable
fun LiveModelTestBannerSkeleton(
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 80.dp, height = 24.dp)
                        .shimmerEffect(RoundedCornerShape(12.dp))
                )
                Box(
                    modifier = Modifier
                        .size(width = 90.dp, height = 24.dp)
                        .shimmerEffect(RoundedCornerShape(12.dp))
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .shimmerEffect(RoundedCornerShape(6.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(14.dp)
                        .shimmerEffect(RoundedCornerShape(4.dp))
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .shimmerEffect(RoundedCornerShape(10.dp))
            )
        }
    }
}
