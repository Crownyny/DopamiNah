package co.edu.unicauca.dopaminah.ui.screens.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.unicauca.dopaminah.ui.screens.achievements.components.AchievementStatsCard
import co.edu.unicauca.dopaminah.ui.screens.achievements.components.AchievementsHeader
import co.edu.unicauca.dopaminah.ui.screens.achievements.components.BadgesGrid
import co.edu.unicauca.dopaminah.ui.screens.achievements.components.LevelCard
import co.edu.unicauca.dopaminah.ui.screens.achievements.components.NextAchievementCard
import co.edu.unicauca.dopaminah.ui.screens.achievements.components.RewardsSystemCard
import co.edu.unicauca.dopaminah.ui.screens.achievements.components.StreakCard
import co.edu.unicauca.dopaminah.ui.screens.achievements.viewmodel.AchievementsViewModel

@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        AchievementsHeader()

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StreakCard(streakDays = state.streakDays)

            LevelCard(level = state.level, streakDays = state.streakDays)

            Text(
                text = "Insignias",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            BadgesGrid(badges = state.badges)

            NextAchievementCard(
                emoji = state.nextBadgeEmoji,
                title = state.nextBadgeTitle,
                description = state.nextBadgeDescription
            )

            RewardsSystemCard()

            AchievementStatsCard(
                unlockedCount = state.unlockedCount,
                totalCount = state.totalCount,
                level = state.level,
                bestStreak = state.bestStreak
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
