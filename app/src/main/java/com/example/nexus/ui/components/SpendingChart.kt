package com.example.nexus.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FintechBorder
import com.example.ui.theme.FintechLime
import com.example.ui.theme.FintechLimeDark
import com.example.ui.theme.FintechLimeGlow
import com.example.ui.theme.FintechSurface2
import com.example.ui.theme.FintechTextMuted
import com.example.ui.theme.FintechTextPrimary
import com.example.ui.theme.FintechTextSecondary

data class DailySpending(
    val day: String,
    val amount: Float
)

/**
 * Reusable SpendingChart:
 * - Smooth cubic bezier line in lime green on dark card
 * - Faint gridlines
 * - Weekday labels on x-axis (Mon, Tue, Wed, Thu, Fri, Sat, Sun)
 * - Glowing dot with vertical marker line at the peak value
 * - Animated chart draw-in
 * - Tap tooltip on chart points showing exact day and dollar amount
 */
@Composable
fun SpendingChart(
    spendingData: List<DailySpending> = listOf(
        DailySpending("Mon", 65f),
        DailySpending("Tue", 120f),
        DailySpending("Wed", 85f),
        DailySpending("Thu", 195f),
        DailySpending("Fri", 420f), // Peak
        DailySpending("Sat", 240f),
        DailySpending("Sun", 110f)
    ),
    currencySymbol: String = "$",
    modifier: Modifier = Modifier
) {
    if (spendingData.isEmpty()) return

    // Find peak value
    val peakIndex = remember(spendingData) {
        spendingData.indices.maxByOrNull { spendingData[it].amount } ?: 0
    }
    var selectedIndex by remember { mutableIntStateOf(peakIndex) }

    // Animated draw-in
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
        )
    }

    val maxAmount = remember(spendingData) {
        (spendingData.maxOfOrNull { it.amount } ?: 100f) * 1.25f
    }
    val minAmount = 0f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("spending_chart_component")
    ) {
        // Tap tooltip banner
        val selectedItem = spendingData[selectedIndex]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TOTAL SPENT THIS WEEK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    color = FintechTextMuted
                )
                Text(
                    text = "$currencySymbol${spendingData.sumOf { it.amount.toDouble() }.toInt()}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = FintechTextPrimary
                )
            }

            // Interactive Tooltip Card
            Box(
                modifier = Modifier
                    .background(FintechSurface2, RoundedCornerShape(12.dp))
                    .border(1.dp, FintechLime.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(FintechLime, RoundedCornerShape(50))
                    )
                    Text(
                        text = " ${selectedItem.day}: $currencySymbol${selectedItem.amount.toInt()}",
                        color = FintechTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Custom Canvas for Line Chart with Bezier Curves, Gradient, Peak Marker and Tap Detection
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(spendingData) {
                        detectTapGestures { offset ->
                            val sectionWidth = size.width / (spendingData.size - 1)
                            val tappedIndex = ((offset.x + sectionWidth / 2) / sectionWidth)
                                .toInt()
                                .coerceIn(0, spendingData.size - 1)
                            selectedIndex = tappedIndex
                        }
                    }
            ) {
                val width = size.width
                val height = size.height - 24.dp.toPx()
                val stepX = width / (spendingData.size - 1)

                // 1. Draw faint horizontal grid lines
                val gridLines = 3
                for (i in 0..gridLines) {
                    val y = height * (i.toFloat() / gridLines)
                    drawLine(
                        color = Color(0xFF222222),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                }

                // Calculate control points
                val points = spendingData.mapIndexed { index, item ->
                    val x = index * stepX
                    val normalizedY = 1f - ((item.amount - minAmount) / (maxAmount - minAmount))
                    val animatedY = height - ((height - (normalizedY * height)) * animationProgress.value)
                    Offset(x, animatedY)
                }

                // 2. Draw smooth cubic bezier path
                val strokePath = Path()
                val fillPath = Path()

                if (points.isNotEmpty()) {
                    strokePath.moveTo(points.first().x, points.first().y)
                    fillPath.moveTo(points.first().x, height)
                    fillPath.lineTo(points.first().x, points.first().y)

                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2f, p0.y)
                        val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2f, p1.y)

                        strokePath.cubicTo(
                            controlPoint1.x, controlPoint1.y,
                            controlPoint2.x, controlPoint2.y,
                            p1.x, p1.y
                        )
                        fillPath.cubicTo(
                            controlPoint1.x, controlPoint1.y,
                            controlPoint2.x, controlPoint2.y,
                            p1.x, p1.y
                        )
                    }

                    fillPath.lineTo(points.last().x, height)
                    fillPath.close()

                    // Gradient fill underneath
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                FintechLime.copy(alpha = 0.22f),
                                FintechLime.copy(alpha = 0.04f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Line stroke
                    drawPath(
                        path = strokePath,
                        color = FintechLime,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }

                // 3. Draw vertical marker line & glowing dot at selected/peak value
                val activePoint = points.getOrNull(selectedIndex) ?: points.getOrNull(peakIndex)
                if (activePoint != null && animationProgress.value > 0.5f) {
                    // Vertical dashed indicator
                    drawLine(
                        color = FintechLime.copy(alpha = 0.45f),
                        start = Offset(activePoint.x, activePoint.y),
                        end = Offset(activePoint.x, height),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )

                    // Outer glowing halo
                    drawCircle(
                        color = FintechLimeGlow,
                        radius = 16.dp.toPx(),
                        center = activePoint
                    )

                    // Middle pulse circle
                    drawCircle(
                        color = FintechLime.copy(alpha = 0.35f),
                        radius = 10.dp.toPx(),
                        center = activePoint
                    )

                    // Inner solid lime dot
                    drawCircle(
                        color = FintechLime,
                        radius = 5.dp.toPx(),
                        center = activePoint
                    )

                    // Center dark dot
                    drawCircle(
                        color = FintechLimeDark,
                        radius = 2.dp.toPx(),
                        center = activePoint
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Weekday labels on x-axis
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            spendingData.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                Text(
                    text = item.day,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) FintechLime else FintechTextMuted
                )
            }
        }
    }
}
