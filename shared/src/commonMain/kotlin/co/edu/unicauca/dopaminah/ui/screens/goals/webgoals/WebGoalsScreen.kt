package co.edu.unicauca.dopaminah.ui.screens.goals.webgoals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.icons.LucideGlobe
import co.edu.unicauca.dopaminah.ui.icons.LucidePlus
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.components.CreateWebGoalDialog
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.components.WebGoalCard
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.components.WebGoalsHeader
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurple
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurpleDark
import co.edu.unicauca.dopaminah.ui.theme.DopaminahOrange
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurpleLight

private val MIN_CARD_WIDTH = 240.dp
private val GRID_GAP = 12.dp

@Composable
fun WebGoalsScreen(viewModel: WebGoalsViewModel? = null) {
    val vm = viewModel ?: remember { WebGoalsViewModel() }
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        WebGoalsHeader(
            totalGoals = state.totalGoals,
            activeBlocks = state.activeBlocks
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(28.dp))

                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 640.dp)
                                .fillMaxWidth()
                                .height(240.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = DopaminahPurple,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    state.webGoals.isEmpty() -> {
                        EmptyState(
                            modifier = Modifier.widthIn(max = 640.dp),
                            onAddClick = { vm.showCreateDialog() }
                        )
                    }

                    else -> {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val isDesktop = maxWidth >= 768.dp
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                            ) {
                                Spacer(modifier = Modifier.height(4.dp))

                                GoalGrid(
                                    goals = state.webGoals,
                                    onAddClick = { vm.showCreateDialog() },
                                    onDelete = { vm.deleteGoal(it) },
                                    onEdit = { vm.showCreateDialog() }
                                )

                                if (!isDesktop) {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    TipCard()
                                }

                                Spacer(modifier = Modifier.height(40.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showCreateDialog) {
        CreateWebGoalDialog(
            onDismiss = { vm.hideCreateDialog() },
            onSave = { url, minutes ->
                vm.addGoal(url, minutes)
                vm.hideCreateDialog()
            }
        )
    }
}

@Composable
private fun GoalGrid(
    goals: List<WebGoalUiModel>,
    onAddClick: () -> Unit,
    onDelete: (String) -> Unit,
    onEdit: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val columns = ((maxWidth + GRID_GAP) / (MIN_CARD_WIDTH + GRID_GAP)).toInt().coerceAtLeast(1)

        if (columns > 1) {
            val gridItems = buildList<Any?> { add(null); addAll(goals) }
            val rows = gridItems.chunked(columns)

            Column(verticalArrangement = Arrangement.spacedBy(GRID_GAP)) {
                rows.forEach { chunk ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(GRID_GAP)
                    ) {
                        chunk.forEach { item ->
                            Box(modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                            ) {
                                if (item == null) {
                                    AddGoalGridCard(onClick = onAddClick)
                                } else {
                                    WebGoalCard(
                                        goal = item as WebGoalUiModel,
                                        onDelete = { onDelete(item.id) },
                                        onEdit = onEdit,
                                        onToggle = {}
                                    )
                                }
                            }
                        }
                        repeat(columns - chunk.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(GRID_GAP)) {
                AddGoalRow(onClick = onAddClick)
                goals.forEach { goal ->
                    WebGoalCard(
                        goal = goal,
                        onDelete = { onDelete(goal.id) },
                        onEdit = onEdit,
                        onToggle = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun TipCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DopaminahOrange.copy(alpha = 0.07f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "\uD83D\uDCA1",
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 1.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                Text(
                    text = "Consejo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DopaminahOrange
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Comienza con limites de 15-30 minutos en tus sitios mas visitados. " +
                            "Al alcanzar el limite, el acceso se bloqueara hasta el siguiente dia.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun AddGoalGridCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(DopaminahPurpleLight.copy(alpha = 0.12f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DopaminahPurple.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = LucidePlus,
                        contentDescription = "Agregar limite",
                        tint = DopaminahPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Agregar limite",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DopaminahPurple
                )
                Text(
                    text = "Nuevo sitio web",
                    fontSize = 11.sp,
                    color = DopaminahPurple.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun AddGoalRow(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(DopaminahPurple, DopaminahPurpleDark)
                )
            )
            .clickable { onClick() }
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = LucidePlus,
                contentDescription = "Agregar Limite",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Agregar limite web",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(DopaminahPurpleLight.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = LucideGlobe,
                contentDescription = null,
                tint = DopaminahPurple.copy(alpha = 0.5f),
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Aun no tienes limites web",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Define cuanto tiempo quieres pasar en cada\nsitio y deja que la app lo controle por ti.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(
            onClick = onAddClick,
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(DopaminahPurple, DopaminahPurpleDark)
                    )
                )
                .padding(horizontal = 36.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = LucidePlus,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Crear mi primer limite",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


