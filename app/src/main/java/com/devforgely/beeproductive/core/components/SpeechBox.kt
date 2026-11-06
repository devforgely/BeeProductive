package com.devforgely.beeproductive.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devforgely.beeproductive.core.theme.RubikFontFamily

private val SpeechFill = Color(0xFFF7F7F8)
private val SpeechOutline = Color(0xFFE6E6EB)
private val SpeechInk = Color(0xFF252525)

private class SpeechBubbleShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val tailHeight = with(density) { 12.dp.toPx() }
        val tailHalf = with(density) { 11.dp.toPx() }
        val bodyBottom = (size.height - tailHeight).coerceAtLeast(0f)
        val radius = with(density) { 12.dp.toPx() }
            .coerceAtMost(minOf(size.width, bodyBottom) / 2f)
        val center = size.width / 2f
        val path = Path().apply {
            moveTo(radius, 0f)
            lineTo(size.width - radius, 0f)
            arcTo(
                rect = Rect(size.width - 2 * radius, 0f, size.width, 2 * radius),
                startAngleDegrees = -90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false,
            )
            lineTo(size.width, bodyBottom - radius)
            arcTo(
                rect = Rect(
                    size.width - 2 * radius,
                    bodyBottom - 2 * radius,
                    size.width,
                    bodyBottom,
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false,
            )
            lineTo(center + tailHalf, bodyBottom)
            quadraticTo(
                center + tailHalf * 0.2f,
                bodyBottom + tailHeight * 0.45f,
                center,
                size.height,
            )
            quadraticTo(
                center - tailHalf * 0.2f,
                bodyBottom + tailHeight * 0.45f,
                center - tailHalf,
                bodyBottom,
            )
            lineTo(radius, bodyBottom)
            arcTo(
                rect = Rect(0f, bodyBottom - 2 * radius, 2 * radius, bodyBottom),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false,
            )
            lineTo(0f, radius)
            arcTo(
                rect = Rect(0f, 0f, 2 * radius, 2 * radius),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false,
            )
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun SpeechBox(text: String) {
    val shape = remember { SpeechBubbleShape() }
    val tailHeight = 12.dp
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .background(SpeechFill, shape)
            .border(1.5.dp, SpeechOutline, shape)
            .padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 14.dp + tailHeight),
        color = SpeechInk,
        fontFamily = RubikFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        lineHeight = 22.sp,
    )
}
