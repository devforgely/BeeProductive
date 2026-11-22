package com.devforgely.beeproductive.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import com.devforgely.beeproductive.core.theme.Amber
import com.devforgely.beeproductive.core.theme.Blush
import com.devforgely.beeproductive.core.theme.CombLine
import com.devforgely.beeproductive.core.theme.HoneyGold
import com.devforgely.beeproductive.core.theme.Sage
import com.devforgely.beeproductive.core.theme.Sky
import com.devforgely.beeproductive.core.theme.Wax
import com.devforgely.beeproductive.core.theme.WaxCap
import com.devforgely.beeproductive.domain.model.CellVisual
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CombCluster(
    cells: List<CellVisual>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier.fillMaxWidth().height(260.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.075f
        cells.forEach { cell ->
            drawCell(center + cellOffset(cell.index, radius * 2.15f), radius, cell.fill, cell.capped)
        }
    }
}

private val CreamSoft = Color(0xFFFFF8EE)

private fun cellOffset(index: Int, step: Float): Offset {
    val (ring, slot, count) = when {
        index < 6 -> Triple(1, index, 6)
        index < 18 -> Triple(2, index - 6, 12)
        else -> Triple(3, index - 18, 18)
    }
    val angle = (Math.PI * 2 * slot / count) - Math.PI / 2
    return Offset((cos(angle) * step * ring).toFloat(), (sin(angle) * step * ring).toFloat())
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCell(
    center: Offset,
    radius: Float,
    fill: Float,
    capped: Boolean,
    queen: Boolean = false,
) {
    val path = hexagon(center, radius)
    drawPath(path, Wax.copy(alpha = if (queen) 0.55f else 0.4f))
    if (fill > 0f) {
        clipPath(path) {
            val height = radius * 2f * fill
            drawRect(
                brush = Brush.verticalGradient(listOf(HoneyGold, Amber)),
                topLeft = Offset(center.x - radius, center.y + radius - height),
                size = Size(radius * 2f, height),
            )
        }
    }
    if (capped) {
        drawCircle(WaxCap.copy(alpha = 0.9f), radius * 0.42f, center)
    }
    if (queen) {
        drawCircle(HoneyGold.copy(alpha = 0.35f), radius * 1.15f, center)
    }
    drawPath(path, CombLine, style = Stroke(width = if (queen) 3f else 2f))
}

private fun hexagon(center: Offset, radius: Float): Path {
    val path = Path()
    for (i in 0 until 6) {
        val angle = Math.toRadians(60.0 * i - 30.0)
        val x = center.x + (cos(angle) * radius).toFloat()
        val y = center.y + (sin(angle) * radius).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFlower(
    center: Offset,
    bloomed: Boolean,
    radius: Float,
) {
    val petal = if (bloomed) radius * 0.42f else radius * 0.22f
    val color = if (bloomed) Blush else Sage.copy(alpha = 0.7f)
    if (bloomed) {
        repeat(5) { petalIndex ->
            val angle = petalIndex * Math.PI * 2 / 5
            drawCircle(
                color,
                petal * 0.7f,
                center + Offset((cos(angle) * petal).toFloat(), (sin(angle) * petal).toFloat()),
            )
        }
    } else {
        drawCircle(Sage, petal, center.copy(y = center.y + petal))
        drawCircle(Sage.copy(alpha = 0.8f), petal * 0.7f, center)
    }
    drawCircle(if (bloomed) HoneyGold else Sage, petal * 0.35f, center)
}

@Composable
private fun OutfitOverlay(outfitId: String?, modifier: Modifier) {
    if (outfitId == null) return
    Canvas(modifier.size(72.dp, 28.dp)) {
        when (outfitId) {
            "outfit_flower" -> {
                drawCircle(Blush, 8f, Offset(size.width / 2f - 14f, size.height / 2f))
                drawCircle(HoneyGold, 8f, Offset(size.width / 2f, size.height / 2f - 4f))
                drawCircle(Sky, 8f, Offset(size.width / 2f + 14f, size.height / 2f))
            }
            "outfit_scarf" -> {
                drawRoundRect(
                    Amber,
                    topLeft = Offset(size.width * 0.2f, size.height * 0.45f),
                    size = Size(size.width * 0.6f, 10f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                )
            }
            "outfit_bow" -> {
                drawCircle(Sage, 10f, Offset(size.width / 2f - 12f, size.height / 2f))
                drawCircle(Sage, 10f, Offset(size.width / 2f + 12f, size.height / 2f))
                drawCircle(HoneyGold, 4f, Offset(size.width / 2f, size.height / 2f))
            }
        }
    }
}
