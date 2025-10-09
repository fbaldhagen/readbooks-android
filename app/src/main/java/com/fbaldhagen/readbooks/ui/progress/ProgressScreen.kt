package com.fbaldhagen.readbooks.ui.progress

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fbaldhagen.readbooks.domain.model.AchievementDetails
import com.fbaldhagen.readbooks.domain.model.ReadingGoalProgress
import com.fbaldhagen.readbooks.domain.model.ReadingStreakInfo
import com.fbaldhagen.readbooks.domain.model.StreakStatus
import com.fbaldhagen.readbooks.ui.common.TopBarBackground
import com.fbaldhagen.readbooks.ui.common.TopBarState
import com.fbaldhagen.readbooks.ui.components.TieredBadgeIcon
import com.fbaldhagen.readbooks.ui.profile.formatDuration
import com.fbaldhagen.readbooks.ui.theme.BadgeColors

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = hiltViewModel(),
    contentPadding: PaddingValues,
    onConfigureTopBar: (TopBarState) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        onConfigureTopBar(
            TopBarState.Standard(
                title = "",
                background = TopBarBackground.Scrim
            )
        )
    }

    if (state.isGoalDialogVisible) {
        SetGoalDialog(
            currentGoal = state.readingGoalProgress.goal,
            onDismiss = viewModel::onDismissGoalDialog,
            onSave = viewModel::onSaveGoal
        )
    }

    state.selectedAchievement?.let { achievement ->
        AchievementDetailsSheet(
            achievement = achievement,
            onDismiss = viewModel::onDismissAchievementDetails
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ProgressContent(
            state = state,
            contentPadding = contentPadding,
            onGoalClicked = viewModel::onGoalClicked,
            onAchievementClicked = viewModel::onAchievementClicked
        )
    }
}

@Composable
fun ProgressContent(
    state: ProgressState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onGoalClicked: () -> Unit,
    onAchievementClicked: (AchievementDetails) -> Unit
) {
    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding(),
            bottom = 16.dp + navBarPadding.calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            ReadingGoalCard(
                progress = state.readingGoalProgress,
                onClick = onGoalClicked
            )
        }

        item {
            StatsSnapshotCard(
                streakInfo = state.readingStreakInfo,
                finishedBookCount = state.finishedBookCount,
                totalReadingTime = state.readingAnalytics.totalReadingTime,
                longestStreakInDays = state.readingAnalytics.longestStreakInDays
            )
        }

        item {
            AchievementsCard(
                achievements = state.achievements,
                onAchievementClicked = onAchievementClicked
            )
        }
    }
}

@Composable
fun StatsSnapshotCard(
    streakInfo: ReadingStreakInfo,
    finishedBookCount: Int,
    totalReadingTime: kotlin.time.Duration,
    longestStreakInDays: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(text = "Your Stats", style = MaterialTheme.typography.titleLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val (streakIcon, streakTint) = when (streakInfo.status) {
                    StreakStatus.COMPLETED_TODAY -> Icons.Default.LocalFireDepartment to MaterialTheme.colorScheme.primary
                    StreakStatus.IN_PROGRESS -> Icons.Outlined.LocalFireDepartment to MaterialTheme.colorScheme.onSurfaceVariant
                    StreakStatus.INACTIVE -> Icons.Outlined.LocalFireDepartment to MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                }

                StatItem(
                    value = streakInfo.count.toString(),
                    label = "Current Streak",
                    icon = streakIcon,
                    tint = streakTint
                )

                StatItem(
                    value = longestStreakInDays.toString(),
                    label = "Longest Streak",
                    icon = Icons.Default.MilitaryTech,
                    tint = if (longestStreakInDays > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(
                    value = formatDuration(totalReadingTime),
                    label = "Time Read",
                    icon = Icons.Default.AccessTime,
                    tint = if (totalReadingTime > kotlin.time.Duration.ZERO) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatItem(
                    value = finishedBookCount.toString(),
                    label = "Books Read",
                    icon = Icons.Default.WorkspacePremium,
                    tint = if (finishedBookCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AchievementsCard(
    achievements: List<AchievementDetails>,
    onAchievementClicked: (AchievementDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Achievements",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                achievements.forEachIndexed { index, achievement ->
                    AchievementItem(
                        achievement = achievement,
                        onClick = { onAchievementClicked(achievement) }
                    )
                    if (index < achievements.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementItem(
    achievement: AchievementDetails,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val definition = achievement.definition
    val userProgress = achievement.userProgress

    val currentTierIndex = userProgress.unlockedTier - 1
    val isUnlocked = userProgress.unlockedTier > 0

    val nextTierIndex = userProgress.unlockedTier
    val nextTier = definition.tiers.getOrNull(nextTierIndex)

    val isMaxTierUnlocked = nextTier == null
    val currentTier = definition.tiers.getOrNull(userProgress.unlockedTier - 1)

    val progressPercent = if (isMaxTierUnlocked) {
        1f
    } else {
        (userProgress.currentProgress.toFloat() / nextTier!!.threshold.toFloat()).coerceIn(0f, 1f)
    }

    val descriptionText = if (isMaxTierUnlocked) {
        "Completed!"
    } else {
        definition.description.format(nextTier!!.threshold)
    }

    val (primaryBadgeColor, accentBadgeColor) = if (isUnlocked) {
        BadgeColors.tierPairs.getOrElse(currentTierIndex) { BadgeColors.tierPairs.last() }
    } else {
        BadgeColors.LockedPrimary to BadgeColors.LockedAccent
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                TieredBadgeIcon(
                    primaryColor = primaryBadgeColor,
                    accentColor = accentBadgeColor,
                    contentDescription = "Achievement Badge",
                    modifier = Modifier.fillMaxSize()
                )

                Image(
                    painter = painterResource(id = definition.iconRes),
                    contentDescription = "${definition.name} icon",
                    modifier = Modifier.size(42.dp),
                    colorFilter = if (isUnlocked) null else ColorFilter.colorMatrix(
                        ColorMatrix().apply { setToSaturation(0f) }
                    ),
                    alpha = if (isUnlocked) 1.0f else 0.7f
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = definition.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = descriptionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isMaxTierUnlocked) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LinearProgressIndicator(
                progress = { progressPercent },
                modifier = Modifier.weight(1f),
                strokeCap = StrokeCap.Round
            )
            val progressText = if (isMaxTierUnlocked) {
                "${currentTier?.threshold ?: userProgress.currentProgress}"
            } else {
                "${userProgress.currentProgress}/${nextTier?.threshold}"
            }
            Text(
                text = progressText,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementDetailsSheet(
    achievement: AchievementDetails,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    val definition = achievement.definition
    val userProgress = achievement.userProgress

    val currentTierIndex = userProgress.unlockedTier - 1
    val isUnlocked = userProgress.unlockedTier > 0

    val (primaryBadgeColor, accentBadgeColor) = if (isUnlocked) {
        BadgeColors.tierPairs.getOrElse(currentTierIndex) { BadgeColors.tierPairs.last() }
    } else {
        BadgeColors.LockedPrimary to BadgeColors.LockedAccent
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(96.dp),
                contentAlignment = Alignment.Center
            ) {
                TieredBadgeIcon(
                    primaryColor = primaryBadgeColor,
                    accentColor = accentBadgeColor,
                    contentDescription = "Achievement Badge",
                    modifier = Modifier.fillMaxSize()
                )
                Image(
                    painter = painterResource(id = definition.iconRes),
                    contentDescription = "${definition.name} icon",
                    modifier = Modifier.size(84.dp),
                    colorFilter = if (isUnlocked) null else ColorFilter.colorMatrix(
                        ColorMatrix().apply { setToSaturation(0f) }
                    ),
                    alpha = if (isUnlocked) 1.0f else 0.7f
                )
            }

            Text(
                text = achievement.definition.name,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Unlocked on: -",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            Text(
                text = "Tiers",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            achievement.definition.tiers.forEachIndexed { _, tier ->
                TierItem(
                    tierDescription = achievement.definition.description.format(tier.threshold),
                    isUnlocked = isUnlocked
                )
            }
        }
    }
}

@Composable
private fun TierItem(
    tierDescription: String,
    isUnlocked: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = if (isUnlocked) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
            contentDescription = if (isUnlocked) "Unlocked" else "Locked",
            tint = if (isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column {
            Text(
                text = tierDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SetGoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onSave: (goal: Int) -> Unit
) {
    var text by remember { mutableStateOf(currentGoal.toString()) }

    val goalValue = text.toIntOrNull()
    val isValid = goalValue != null && goalValue > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Your Goal") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    if (newText.all { it.isDigit() }) {
                        text = newText
                    }
                },
                label = { Text("Books per year") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = text.isNotEmpty() && !isValid,
                supportingText = {
                    if (text.isNotEmpty() && !isValid) {
                        Text("Please enter a number greater than 0")
                    }
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { goalValue?.let(onSave) },
                enabled = isValid
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ReadingGoalCard(
    progress: ReadingGoalProgress,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Annual Reading Goal",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            CircularGoalIndicator(progress = progress)
        }
    }
}

@Composable
private fun CircularGoalIndicator(
    progress: ReadingGoalProgress,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 10.dp
) {
    val progressValue = (progress.currentCount.toFloat() / progress.goal.toFloat()).coerceIn(0f, 1f)
    val isGoalMet = progress.currentCount >= progress.goal

    val progressColor = if (isGoalMet) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = strokeWidth
        )

        CircularProgressIndicator(
            progress = { progressValue },
            modifier = Modifier.fillMaxSize(),
            color = progressColor,
            strokeWidth = strokeWidth,
            strokeCap = StrokeCap.Round
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = progress.currentCount.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = progressColor
            )
            Text(
                text = "of ${progress.goal} books",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatItem(
    value: String,
    label: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = tint
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = tint
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}