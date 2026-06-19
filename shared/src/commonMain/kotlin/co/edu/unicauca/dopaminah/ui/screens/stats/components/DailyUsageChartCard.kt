package co.edu.unicauca.dopaminah.ui.screens.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.utils.DateUtils
import kotlin.math.abs
import kotlin.math.ceil

@Composable
fun DailyUsageChartCard(
    usageData: List<Float>,
    modifier: Modifier = Modifier
) {
    val uiData = if (usageData.isEmpty()) List(7) { 0f } else usageData
    val dayLabels = remember { DateUtils.last7ShortLabels() }
    val fullDayNames = remember { DateUtils.last7FullLabels() }
    val maxValue = remember(uiData) { (uiData.maxOrNull() ?: 1f).coerceAtLeast(1f) }
    val yLabels = remember(maxValue) {
        val maxInt = ceil(maxValue.toDouble()).toInt()
        val step = (maxInt / 4).coerceAtLeast(1)
        listOf(maxInt.toFloat(), (maxInt - step).toFloat(), (maxInt - step * 2).toFloat(), 0f).filter { it >= 0 }
    }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }
    var selectedPointOffset by remember { mutableStateOf<Offset?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors =         CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Tendencia de Uso",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.height(150.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    yLabels.forEach { label ->
                        Text(
                            text = "${label.toInt()}h",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(modifier = Modifier.weight(1f).height(180.dp)) {
                    var coordinatesCache by remember { mutableStateOf<List<Offset>>(emptyList()) }
                    val lineColor = MaterialTheme.colorScheme.primary
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .pointerInput(uiData) {
                                detectTapGestures { tapOffset ->
                                    if (coordinatesCache.isNotEmpty()) {
                                        val closestIndex = coordinatesCache.withIndex().minByOrNull { (_, point) ->
                                            abs(point.x - tapOffset.x)
                                        }?.index ?: return@detectTapGestures
                                        val closestPoint = coordinatesCache[closestIndex]
                                        val step = size.width / uiData.size.toFloat()
                                        if (abs(closestPoint.x - tapOffset.x) < step) {
                                            if (selectedPointIndex == closestIndex) {
                                                selectedPointIndex = null
                                                selectedPointOffset = null
                                            } else {
                                                selectedPointIndex = closestIndex
                                                selectedPointOffset = closestPoint
                                            }
                                        } else {
                                            selectedPointIndex = null
                                            selectedPointOffset = null
                                        }
                                    }
                                }
                            }
                    ) {
                        val width = size.width
                        val height = size.height
                        val step = width / uiData.size.toFloat()
                        val coordinates = uiData.mapIndexed { index, value ->
                            val x = (index * step) + (step / 2f)
                            val y = height - (value / maxValue * height)
                            Offset(x, y)
                        }
                        coordinatesCache = coordinates
                        val yStepSpacing = height / (yLabels.size - 1).coerceAtLeast(1)
                        for (i in yLabels.indices) {
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.15f),
                                start = Offset(0f, i * yStepSpacing),
                                end = Offset(width, i * yStepSpacing),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        val path = Path().apply {
                            if (coordinates.isNotEmpty()) {
                                moveTo(coordinates.first().x, coordinates.first().y)
                                for (i in 0 until coordinates.size - 1) {
                                    val p1 = coordinates[i]
                                    val p2 = coordinates[i + 1]
                                    val controlX = (p1.x + p2.x) / 2
                                    cubicTo(controlX, p1.y, controlX, p2.y, p2.x, p2.y)
                                }
                            }
                        }
                        drawPath(
                            path = path,
                            color = lineColor,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                        val fillPath = Path().apply {
                            addPath(path)
                            lineTo(width, height)
                            lineTo(0f, height)
                            close()
                        }
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent),
                                startY = 0f,
                                endY = height
                            )
                        )
                        coordinates.forEachIndexed { index, point ->
                            val isSelected = index == selectedPointIndex
                            drawCircle(
                                color = if (isSelected) lineColor else Color.White,
                                radius = if (isSelected) 8.dp.toPx() else 6.dp.toPx(),
                                center = point
                            )
                            if (!isSelected) {
                                drawCircle(color = lineColor, radius = 4.dp.toPx(), center = point)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(top = 4.dp)
                    ) {
                        dayLabels.forEachIndexed { index, label ->
                            Text(
                                text = label,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = if (index == dayLabels.lastIndex) FontWeight.Bold else FontWeight.Medium,
                                color = if (index == dayLabels.lastIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    selectedPointIndex?.let { index ->
                        selectedPointOffset?.let { offset ->
                            val value = uiData[index]
                            val label = fullDayNames[index]
                            val hours = value.toInt()
                            val minutes = ((value - hours) * 60).toInt()
                            val timeString = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(
                                        x = (offset.x.dp - 32.dp).coerceAtLeast(0.dp),
                                        y = (offset.y.dp - 48.dp).coerceAtLeast(0.dp)
                                    )
                                    .padding(4.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = label, color = Color.Gray, fontSize = 10.sp)
                                    Text(text = timeString, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
