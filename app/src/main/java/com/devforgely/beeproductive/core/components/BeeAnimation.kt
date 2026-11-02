package com.devforgely.beeproductive.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.devforgely.beeproductive.core.theme.BlushDeep
import com.devforgely.beeproductive.domain.model.BeeAccent
import com.devforgely.beeproductive.domain.model.BeeClip

@Composable
fun BeeAnimation(
    clip: BeeClip,
    accent: BeeAccent,
    speed: Float,
    modifier: Modifier = Modifier.size(200.dp),
) {
    FallbackBee(Color(accent.argb), modifier)
}

@Composable
private fun FallbackBee(accent: Color, modifier: Modifier) {
    Canvas(modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(Color(0x33000000), size.minDimension * 0.18f, center.copy(y = center.y + size.minDimension * 0.32f))
        rotate(-18f, center.copy(x = center.x - 40f)) {
            drawOval(Color(0xB3B9D7F2), topLeft = center.copy(x = center.x - 90f, y = center.y - 40f), size = androidx.compose.ui.geometry.Size(70f, 36f))
        }
        rotate(18f, center.copy(x = center.x + 40f)) {
            drawOval(Color(0xB3B9D7F2), topLeft = center.copy(x = center.x + 20f, y = center.y - 40f), size = androidx.compose.ui.geometry.Size(70f, 36f))
        }
        drawOval(accent, topLeft = center.copy(x = center.x + 28f, y = center.y - 10f), size = androidx.compose.ui.geometry.Size(36f, 44f))
        scale(1f, 0.86f, center) {
            drawCircle(Color(0xFFF6C23A), size.minDimension * 0.28f, center)
        }
        drawOval(Color(0xFF5A3A22), topLeft = center.copy(x = center.x - 48f, y = center.y - 8f), size = androidx.compose.ui.geometry.Size(96f, 14f))
        drawCircle(Color.White, 10f, center.copy(x = center.x - 16f, y = center.y - 12f))
        drawCircle(Color.White, 10f, center.copy(x = center.x + 16f, y = center.y - 12f))
        drawCircle(Color(0xFF3A2414), 4f, center.copy(x = center.x - 14f, y = center.y - 10f))
        drawCircle(Color(0xFF3A2414), 4f, center.copy(x = center.x + 18f, y = center.y - 10f))
        drawCircle(BlushDeep, 7f, center.copy(x = center.x - 28f, y = center.y + 10f))
        drawCircle(BlushDeep, 7f, center.copy(x = center.x + 28f, y = center.y + 10f))
    }
}
