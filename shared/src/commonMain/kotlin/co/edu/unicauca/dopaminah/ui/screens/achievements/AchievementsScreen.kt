package co.edu.unicauca.dopaminah.ui.screens.achievements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unicauca.dopaminah.ui.screens.achievements.components.BadgesGrid
import co.edu.unicauca.dopaminah.ui.screens.achievements.components.LevelCard
import co.edu.unicauca.dopaminah.ui.screens.achievements.components.StreakCard
import co.edu.unicauca.dopaminah.ui.screens.achievements.viewmodel.AchievementsViewModel
import co.edu.unicauca.dopaminah.ui.theme.extendedColors

@Composable
fun AchievementsScreen(viewModel: AchievementsViewModel? = null) {
    val vm = viewModel ?: remember { AchievementsViewModel() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            "Logros y Rachas",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Tus conquistas y progreso",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))
        StreakCard(streakDays = vm.currentStreak)
        Spacer(modifier = Modifier.height(16.dp))
        LevelCard(level = vm.currentLevel)
        Spacer(modifier = Modifier.height(24.dp))
        BadgesGrid(badges = vm.badges)
    }
}
