package co.edu.unicauca.dopaminah.ui.screens.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun DailyUsageChartCard(
    usageData: List<Float>,
    modifier: Modifier = Modifier
) {
    val uiData = if (usageData.isEmpty()) List(7) { 0f } else usageData

    // Get the last 7 days initials (e.g., M, T, W, T, F, S, S)
    val dayLabels = remember {
        buildList {
            val calendar = Calendar.getInstance()
            val formatShort = SimpleDateFormat("EEE", Locale.getDefault())
            for (i in 0 until 7) {
                // Take abbreviation and lowercase
                val label = formatShort.format(calendar.time).lowercase()
                add(0, label)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }
        }
    }

    val fullDayNames = remember {
        buildList {
            val calendar = Calendar.getInstance()
            val formatFull = SimpleDateFormat("EEEE", Locale.getDefault())
            for (i in 0 until 7) {
                val label = formatFull.format(calendar.time).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                add(0, label)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }
        }
    }

    val maxValue = remember(uiData) { (uiData.maxOrNull() ?: 1f).coerceAtLeast(1f) }
    // Generate Y-axis labels
    val yLabels = remember(maxValue) {
        val maxInt = Math.ceil(maxValue.toDouble()).toInt()
        val step = (maxInt / 4).coerceAtLeast(1)
        listOf(maxInt.toFloat(), (maxInt - step).toFloat(), (maxInt - step * 2).toFloat(), 0f).filter { it >= 0 }
    }

    // Touch interaction state
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }
    var selectedPointOffset by remember { mutableStateOf<Offset?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Tendencia de Uso",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                // Y-Axis
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

                Box(modifier = Modifier
                    .weight(1f)
                    .height(180.dp)) {

                    var coordinatesCache by remember { mutableStateOf<List<Offset>>(emptyList()) }

                    // Chart Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .pointerInput(uiData) {
                                detectTapGestures { tapOffset ->
                                    if (coordinatesCache.isNotEmpty()) {
                                        // Find closest point by horizontal X distance only
                                        val closestIndex = coordinatesCache.indexOfMinBy { point ->
                                            kotlin.math.abs(point.x - tapOffset.x)
                                        }
                                        val closestPoint = coordinatesCache[closestIndex]
                                        
                                        // Allow selection if tap is in the column zone
                                        val step = size.width / uiData.size.toFloat()
                                        
                                        if (kotlin.math.abs(closestPoint.x - tapOffset.x) < step) {
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
                        
                        // Calculate step to match the Row with weight(1f) modifiers
                        val step = width / uiData.size.toFloat()

                        val coordinates = uiData.mapIndexed { index, value ->
                            // Center point inside its column
                            val x = (index * step) + (step / 2f)
                            val y = height - (value / maxValue * height)
                            Offset(x, y)
                        }
                        coordinatesCache = coordinates

                        // Draw horizontal grid lines
                        val yStepSpacing = height / (yLabels.size - 1)
                        for (i in yLabels.indices) {
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.15f),
                                start = Offset(0f, i * yStepSpacing),
                                end = Offset(width, i * yStepSpacing),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Create smoothed path
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

                        // Stroke Path
                        val lineColor = Color(0xFF8B5CF6) // Purple
                        drawPath(
                            path = path,
                            color = lineColor,
                            style = Stroke(
                                width = 4.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        // Fill Gradient Path
                        val fillPath = Path().apply {
                            addPath(path)
                            lineTo(width, height)
                            lineTo(0f, height)
                            close()
                        }
                        
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    lineColor.copy(alpha = 0.3f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = height
                            )
                        )

                        // Draw points on top
                        coordinates.forEachIndexed { index, point ->
                            val isSelected = index == selectedPointIndex
                            drawCircle(
                                color = if (isSelected) lineColor else Color.White,
                                radius = if (isSelected) 8.dp.toPx() else 6.dp.toPx(),
                                center = point
                            )
                            if (!isSelected) {
                                drawCircle(
                                    color = lineColor,
                                    radius = 4.dp.toPx(),
                                    center = point
                                )
                            }
                        }
                    } // Canvas

                    // X-Axis Labels
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(top = 16.dp),
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

                    // Tooltip Overlay
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
                                    // Adjust popup position (above tap point, centered horizontally)
                                    .padding(
                                        start = (offset.x.dp - 32.dp).coerceAtLeast(0.dp), 
                                        top = (offset.y.dp - 48.dp).coerceAtLeast(0.dp)
                                    )
                                    .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = label, color = Color.Gray, fontSize = 10.sp)
                                    Text(text = timeString, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } // Chart Box
            } // Row
        }
    }
}

private fun <T> Iterable<T>.indexOfMinBy(selector: (T) -> Float): Int {
    var minIndex = -1
    var minValue = Float.MAX_VALUE
    forEachIndexed { index, element ->
        val value = selector(element)
        if (value < minValue) {
            minValue = value
            minIndex = index
        }
    }
    return minIndex
}
