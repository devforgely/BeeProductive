package com.devforgely.beeproductive.ui.focus

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.R
import com.devforgely.beeproductive.core.theme.RubikFontFamily
import com.devforgely.beeproductive.core.theme.isDuskTheme
import com.devforgely.beeproductive.domain.model.HoneyRules
import com.devforgely.beeproductive.domain.model.LifeArea
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.areaVisual
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.seconds

private const val FocusSeconds = 25 * 60

private val JarInk = Color(0xFF4A3218)
private val BeeYellow = Color(0xFFFFD34A)
private val BeeStripe = Color(0xFF5A3A1C)
private val HoneyLight = Color(0xFFFFE08A)
private val Honey = Color(0xFFF6B423)
private val HoneyDeep = Color(0xFFE89412)
private val Grass = Color(0xFFB7DC62)
private val GrassDeep = Color(0xFF8FCB45)
private val Stem = Color(0xFF6EAF3A)
private val Leaf = Color(0xFF8BC34A)
private val PetalPink = Color(0xFFF7A8C8)
private val WarmShadow = Color(0xFF8A4E10)

private data class FocusColors(
    val top: Color,
    val bottom: Color,
    val comb: Color,
    val title: Color,
    val subtitle: Color,
    val timer: Color,
    val footer: Color,
    val chip: Color,
    val chipInk: Color,
    val button: Color,
    val buttonInk: Color,
)

private fun focusColors(night: Boolean) = if (night) {
    FocusColors(
        top = Color(0xFF4A341C),
        bottom = Color(0xFF2A1E12),
        comb = Color.White.copy(alpha = 0.07f),
        title = Color(0xFFFFF6E8),
        subtitle = Color(0xFFFFF6E8).copy(alpha = 0.72f),
        timer = Color(0xFFFFF6E8),
        footer = Color(0xFFFFF6E8).copy(alpha = 0.82f),
        chip = Color(0xFF3A3128),
        chipInk = Color(0xFFFFF3E2),
        button = Color(0xFFFFF6E8),
        buttonInk = Color(0xFFC9842A),
    )
} else {
    FocusColors(
        top = Color(0xFFFFB133),
        bottom = Color(0xFFF39C14),
        comb = Color.White.copy(alpha = 0.20f),
        title = Color.White,
        subtitle = Color.White.copy(alpha = 0.84f),
        timer = Color.White,
        footer = Color.White.copy(alpha = 0.94f),
        chip = Color.White,
        chipInk = Color(0xFF6A3E10),
        button = Color(0xFFFFF8EE),
        buttonInk = Color(0xFFE09416),
    )
}

@Composable
fun FocusView(
    onBack: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    val areas = snapshot.questAreas
    var selected by remember(areas) { mutableStateOf(areas.firstOrNull()) }
    var remaining by remember { mutableIntStateOf(FocusSeconds) }
    var running by remember { mutableStateOf(false) }
    var focusedSeconds by remember { mutableIntStateOf(0) }
    val beeName = snapshot.bee?.name?.takeIf { it.isNotBlank() } ?: "Bumble"
    val fill by animateFloatAsState(
        targetValue = 0.46f + 0.38f * (1f - remaining / FocusSeconds.toFloat()),
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "honey-level",
    )

    LaunchedEffect(running) {
        val block = HoneyRules.FOCUS_BLOCK_MINUTES * 60
        while (running && remaining > 0) {
            delay(1.seconds)
            remaining -= 1
            focusedSeconds += 1
            if (focusedSeconds % block == 0) {
                viewModel.grantFocusBlock()
            }
            if (remaining <= 0) running = false
        }
    }

    FocusScreen(
        beeName = beeName,
        area = selected,
        areas = areas,
        remaining = remaining,
        running = running,
        fill = fill,
        onBack = onBack,
        onSelect = { selected = it },
        onStart = {
            remaining = FocusSeconds
            focusedSeconds = 0
            running = true
        },
    )
}

@Composable
private fun FocusScreen(
    beeName: String,
    area: LifeArea?,
    areas: List<LifeArea>,
    remaining: Int,
    running: Boolean,
    fill: Float,
    onBack: () -> Unit,
    onSelect: (LifeArea) -> Unit,
    onStart: () -> Unit,
) {
    val colors = focusColors(isDuskTheme())
    val motion = rememberInfiniteTransition(label = "focus-motion")
    val hover by motion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "hover",
    )
    val flap by motion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 320, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "flap",
    )
    val drift by motion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drift",
    )
    val drip by motion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "drip",
    )

    Box(Modifier.fillMaxSize()) {
        FocusBackdrop(colors, drift)
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 6.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.fillMaxWidth()) {
                BackChevron(
                    onBack = onBack,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
            }
            Text(
                text = "Start collecting honey!",
                modifier = Modifier.fillMaxWidth(),
                color = colors.title,
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
            )
            Text(
                text = "Stay focused and $beeName fills the jar",
                modifier = Modifier.padding(top = 4.dp, start = 12.dp, end = 12.dp),
                color = colors.subtitle,
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
            )
            BoxWithConstraints(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                val side = minOf(maxWidth, maxHeight, 320.dp)
                CollectingBadge(
                    fill = fill,
                    hover = hover,
                    flap = flap,
                    drip = drip,
                    pouring = running,
                    beeName = beeName,
                    modifier = Modifier
                        .size(side)
                        .align(Alignment.Center),
                )
            }
            AreaChip(
                area = area,
                areas = areas,
                colors = colors,
                onSelect = onSelect,
            )
            Text(
                text = "%02d:%02d".format(remaining / 60, remaining % 60),
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 4.dp)
                    .semantics {
                        contentDescription = "%d minutes and %d seconds remaining".format(
                            remaining / 60,
                            remaining % 60,
                        )
                    },
                color = colors.timer,
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 76.sp,
                lineHeight = 80.sp,
                letterSpacing = (-1.5).sp,
            )
            CollectButton(
                running = running,
                colors = colors,
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
            HoneyPace(
                colors = colors,
                modifier = Modifier.padding(top = 14.dp),
            )
        }
    }
}

@Composable
private fun FocusBackdrop(colors: FocusColors, drift: Float) {
    Canvas(Modifier.fillMaxSize()) {
        drawRect(Brush.verticalGradient(listOf(colors.top, colors.bottom)))
        drawRect(
            Brush.verticalGradient(
                listOf(Color.White.copy(alpha = 0.16f), Color.Transparent),
                endY = size.height * 0.42f,
            ),
        )
        val shift = (drift - 0.5f) * 18.dp.toPx()
        drawHoneycomb(
            radius = 34.dp.toPx(),
            color = colors.comb,
            stroke = 1.6.dp.toPx(),
            offset = Offset(shift, shift * 0.45f),
        )
    }
}

@Composable
private fun CollectingBadge(
    fill: Float,
    hover: Float,
    flap: Float,
    drip: Float,
    pouring: Boolean,
    beeName: String,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier.semantics { contentDescription = "$beeName filling a honey jar" },
    ) {
        drawCollectingScene(fill, hover, flap, drip, pouring)
    }
}

private fun DrawScope.drawCollectingScene(
    fill: Float,
    hover: Float,
    flap: Float,
    drip: Float,
    pouring: Boolean,
) {
    val scene = size.minDimension
    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = scene * 0.46f
    val hoverPx = (hover - 0.5f) * 8.dp.toPx()

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(WarmShadow.copy(alpha = 0.22f), Color.Transparent),
            center = center + Offset(0f, 10.dp.toPx()),
            radius = radius * 1.08f,
        ),
        radius = radius * 1.08f,
        center = center + Offset(0f, 10.dp.toPx()),
    )
    drawCircle(Color(0xFFFFF8EE), radius, center)
    val innerRadius = radius * 0.90f
    val inner = Path().apply { addOval(Rect(center, innerRadius)) }
    drawPath(inner, Color(0xFFFFF1D4))
    clipPath(inner) {
        drawHoneycomb(
            radius = innerRadius / 4.6f,
            color = Color(0xFFE7BE74).copy(alpha = 0.95f),
            stroke = 1.5.dp.toPx(),
            fill = Color(0xFFFFE3A8).copy(alpha = 0.45f),
        )
        drawOval(
            GrassDeep,
            topLeft = Offset(center.x - radius * 0.78f, center.y + radius * 0.42f),
            size = Size(radius * 1.56f, radius * 0.40f),
        )
        drawOval(
            Grass,
            topLeft = Offset(center.x - radius * 0.70f, center.y + radius * 0.40f),
            size = Size(radius * 1.40f, radius * 0.32f),
        )
    }

    val jarW = scene * 0.36f
    val jarH = scene * 0.33f
    val jarLeft = center.x - jarW / 2f
    val jarTop = center.y - jarH * 0.16f
    drawJar(jarLeft, jarTop, jarW, jarH, fill, pouring, drip)

    flower(
        stemBase = Offset(center.x - radius * 0.62f, center.y + radius * 0.62f),
        petal = PetalPink,
        petals = 5,
        height = scene * 0.10f,
        outlined = false,
    )
    flower(
        stemBase = Offset(center.x + radius * 0.58f, center.y + radius * 0.64f),
        petal = Color.White,
        petals = 6,
        height = scene * 0.09f,
        outlined = true,
    )

    val beeSize = scene * 0.28f
    val beeCenter = Offset(jarLeft + jarW * 0.78f, jarTop - scene * 0.145f + hoverPx)
    drawBee(beeCenter, beeSize, flap)
    val wand = Offset(beeCenter.x - beeSize * 0.28f, beeCenter.y + beeSize * 0.34f)
    val spout = Offset(jarLeft + jarW * 0.46f, jarTop + jarH * 0.04f)
    drawHoneyStream(wand, spout, drip, pouring)
    drawHexBadge(
        center + Offset(radius * 0.84f, -radius * 0.02f + -hoverPx * 0.45f),
        scene * 0.055f,
    )
}

private fun DrawScope.drawJar(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    fill: Float,
    pouring: Boolean,
    drip: Float,
) {
    val corner = width * 0.18f
    val body = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(Offset(left, top), Size(width, height)),
                cornerRadius = CornerRadius(corner, corner),
            ),
        )
    }
    drawOval(
        Color(0x22000000),
        topLeft = Offset(left + width * 0.08f, top + height - width * 0.04f),
        size = Size(width * 0.84f, height * 0.14f),
    )
    drawPath(body, Color(0xFFFFFBF6))
    val surface = top + height * (1f - fill.coerceIn(0.08f, 0.92f))
    val wobble = if (pouring) sin(drip * PI * 2.0).toFloat() * 2.5.dp.toPx() else 0f
    clipPath(body) {
        val honeyPath = Path().apply {
            moveTo(left - 4f, top + height + 8f)
            lineTo(left - 4f, surface)
            val segments = 3
            val seg = (width + 8f) / segments
            for (index in 0 until segments) {
                val x0 = left - 4f + seg * index
                quadraticTo(
                    x0 + seg * 0.5f,
                    surface + (if (index % 2 == 0) -5.dp.toPx() else 4.dp.toPx()) + wobble,
                    x0 + seg,
                    surface,
                )
            }
            lineTo(left + width + 4f, top + height + 8f)
            close()
        }
        drawPath(
            honeyPath,
            Brush.verticalGradient(
                listOf(HoneyLight, Honey, HoneyDeep),
                startY = surface,
                endY = top + height,
            ),
        )
        drawRoundRect(
            Color.White.copy(alpha = 0.40f),
            topLeft = Offset(left + width * 0.12f, top + height * 0.14f),
            size = Size(width * 0.065f, height * 0.34f),
            cornerRadius = CornerRadius(width * 0.04f, width * 0.04f),
        )
    }
    val emblem = width * 0.15f
    drawPath(hexagon(Offset(left + width / 2f, top + height * 0.56f), emblem), Color(0xFFFFF8EE))
    drawPath(
        hexagon(Offset(left + width / 2f, top + height * 0.56f), emblem),
        Color(0xFFC9842A),
        style = Stroke(width = width * 0.018f, join = StrokeJoin.Round),
    )
    drawPath(body, JarInk, style = Stroke(width = width * 0.038f, join = StrokeJoin.Round))

    val lidH = height * 0.15f
    val lidTop = top - lidH * 0.55f
    val lidLeft = left - width * 0.045f
    val lidSize = Size(width * 1.09f, lidH)
    val lidRadius = CornerRadius(lidH * 0.42f, lidH * 0.42f)
    drawRoundRect(
        brush = Brush.verticalGradient(
            listOf(Color(0xFFF0B14E), Color(0xFFC47A28), Color(0xFF8E5420)),
            startY = lidTop,
            endY = lidTop + lidH,
        ),
        topLeft = Offset(lidLeft, lidTop),
        size = lidSize,
        cornerRadius = lidRadius,
    )
    drawRoundRect(
        JarInk,
        topLeft = Offset(lidLeft, lidTop),
        size = lidSize,
        cornerRadius = lidRadius,
        style = Stroke(width = width * 0.032f),
    )
    val knobW = width * 0.26f
    val knobH = lidH * 0.62f
    val knobLeft = left + width / 2f - knobW / 2f
    val knobTop = lidTop - knobH * 0.62f
    drawRoundRect(
        Color(0xFFE2A44C),
        topLeft = Offset(knobLeft, knobTop),
        size = Size(knobW, knobH),
        cornerRadius = CornerRadius(knobH * 0.4f, knobH * 0.4f),
    )
    drawRoundRect(
        JarInk,
        topLeft = Offset(knobLeft, knobTop),
        size = Size(knobW, knobH),
        cornerRadius = CornerRadius(knobH * 0.4f, knobH * 0.4f),
        style = Stroke(width = width * 0.026f),
    )
    val lip = lidTop + lidH * 0.78f
    honeyDrip(Offset(left + width * 0.30f, lip), width * 0.034f)
    honeyDrip(Offset(left + width * 0.50f, lip + width * 0.012f), width * 0.042f)
    honeyDrip(Offset(left + width * 0.70f, lip), width * 0.030f)
}

private fun DrawScope.honeyDrip(center: Offset, radius: Float) {
    val path = Path().apply {
        moveTo(center.x, center.y - radius * 1.15f)
        quadraticTo(center.x + radius * 1.35f, center.y + radius * 0.15f, center.x, center.y + radius)
        quadraticTo(center.x - radius * 1.35f, center.y + radius * 0.15f, center.x, center.y - radius * 1.15f)
        close()
    }
    drawPath(path, Honey)
    drawPath(path, JarInk, style = Stroke(width = radius * 0.32f, join = StrokeJoin.Round))
}

private fun DrawScope.drawHoneyStream(
    from: Offset,
    to: Offset,
    drip: Float,
    pouring: Boolean,
) {
    val tip = from + (to - from) * 0.38f
    drawLine(Color(0xFF8C5A24), from, tip, strokeWidth = 3.5.dp.toPx(), cap = StrokeCap.Round)
    drawCircle(HoneyDeep, 4.5.dp.toPx(), tip)
    drawCircle(JarInk, 4.5.dp.toPx(), tip, style = Stroke(width = 1.4.dp.toPx()))
    val phases = if (pouring) {
        listOf(drip, (drip + 0.25f) % 1f, (drip + 0.5f) % 1f, (drip + 0.75f) % 1f)
    } else {
        listOf(0.08f, 0.32f, 0.56f, 0.80f)
    }
    phases.forEach { phase ->
        val spot = curvePoint(phase, tip, to)
        val radius = 4.6.dp.toPx() * (1.15f - phase * 0.35f)
        drawCircle(HoneyLight, radius, spot)
        drawCircle(HoneyDeep, radius * 0.72f, spot)
    }
}

private fun DrawScope.drawBee(center: Offset, sizePx: Float, flap: Float) {
    val unit = sizePx / 100f
    withTransform({
        translate(left = center.x, top = center.y)
        rotate(degrees = -18f, pivot = Offset.Zero)
        scale(scaleX = unit, scaleY = unit, pivot = Offset.Zero)
    }) {
        wing(Offset(-46f, -34f), Size(40f, 26f), 0.58f + flap * 0.42f, -16f)
        wing(Offset(4f, -40f), Size(44f, 28f), 0.58f + (1f - flap) * 0.42f, 14f)
        val body = Rect(Offset(-24f, -6f), Size(48f, 58f))
        val bodyPath = Path().apply { addOval(body) }
        drawOval(BeeYellow, body.topLeft, body.size)
        clipPath(bodyPath) {
            drawOval(Color.White.copy(alpha = 0.35f), Offset(-18f, -2f), Size(16f, 24f))
            drawRect(BeeStripe, Offset(-24f, 12f), Size(48f, 8f))
            drawRect(BeeStripe, Offset(-24f, 28f), Size(48f, 8f))
        }
        drawOval(JarInk, body.topLeft, body.size, style = Stroke(width = 3.2f))
        drawCircle(BeeYellow, 18f, Offset(0f, -16f))
        drawCircle(JarInk, 18f, Offset(0f, -16f), style = Stroke(width = 3.2f))
        drawCircle(PetalPink.copy(alpha = 0.9f), 4.2f, Offset(-11f, -12f))
        drawCircle(PetalPink.copy(alpha = 0.9f), 4.2f, Offset(11f, -12f))
        drawCircle(JarInk, 3.1f, Offset(-6.5f, -18f))
        drawCircle(JarInk, 3.1f, Offset(6.5f, -18f))
        drawCircle(Color.White, 1.15f, Offset(-7.4f, -19f))
        drawCircle(Color.White, 1.15f, Offset(5.6f, -19f))
        drawArc(
            color = JarInk,
            startAngle = 18f,
            sweepAngle = 144f,
            useCenter = false,
            topLeft = Offset(-7f, -16f),
            size = Size(14f, 11f),
            style = Stroke(width = 1.8f, cap = StrokeCap.Round),
        )
        drawLine(JarInk, Offset(-5f, -32f), Offset(-14f, -46f), strokeWidth = 2.2f, cap = StrokeCap.Round)
        drawLine(JarInk, Offset(5f, -32f), Offset(15f, -44f), strokeWidth = 2.2f, cap = StrokeCap.Round)
        drawCircle(JarInk, 2.8f, Offset(-14f, -46f))
        drawCircle(JarInk, 2.8f, Offset(15f, -44f))
    }
}

private fun DrawScope.wing(topLeft: Offset, size: Size, flapScale: Float, tilt: Float) {
    val pivot = Offset(topLeft.x + size.width * 0.25f, topLeft.y + size.height * 0.8f)
    rotate(tilt, pivot) {
        scale(1f, flapScale, pivot) {
            drawOval(Color(0xFFF7FBFF).copy(alpha = 0.95f), topLeft, size)
            drawOval(
                Color(0xFFD5E4F2),
                topLeft,
                size,
                style = Stroke(width = 1.6f),
            )
        }
    }
}

private fun DrawScope.flower(
    stemBase: Offset,
    petal: Color,
    petals: Int,
    height: Float,
    outlined: Boolean,
) {
    val bloom = stemBase + Offset(0f, -height)
    drawLine(Stem, stemBase, bloom, strokeWidth = height * 0.08f, cap = StrokeCap.Round)
    val leafCenter = stemBase + Offset(height * 0.02f, -height * 0.38f)
    rotate(degrees = -52f, pivot = leafCenter) {
        drawOval(
            Leaf,
            topLeft = leafCenter,
            size = Size(height * 0.34f, height * 0.16f),
        )
    }
    val petalLength = height * 0.46f
    val petalWidth = height * 0.24f
    repeat(petals) { index ->
        val angle = -90f + index * (360f / petals)
        rotate(degrees = angle, pivot = bloom) {
            val topLeft = Offset(bloom.x - petalWidth / 2f, bloom.y - petalLength)
            val petalSize = Size(petalWidth, petalLength * 0.62f)
            drawOval(petal, topLeft, petalSize)
            if (outlined) {
                drawOval(
                    Color(0xFFE4D0AE),
                    topLeft,
                    petalSize,
                    style = Stroke(width = 1.1.dp.toPx()),
                )
            }
        }
    }
    drawCircle(BeeYellow, height * 0.16f, bloom)
    drawCircle(Color.White.copy(alpha = 0.8f), height * 0.05f, bloom + Offset(-height * 0.04f, -height * 0.04f))
}

private fun DrawScope.drawHexBadge(center: Offset, radius: Float) {
    drawCircle(WarmShadow.copy(alpha = 0.16f), radius * 1.15f, center + Offset(0f, 3.dp.toPx()))
    drawPath(hexagon(center, radius), Color(0xFFFFF6E4))
    drawPath(hexagon(center, radius * 0.58f), Color(0xFFFFD56A))
    drawPath(
        hexagon(center, radius),
        Color(0xFFE0A83A),
        style = Stroke(width = radius * 0.12f, join = StrokeJoin.Round),
    )
}

private fun DrawScope.drawHoneycomb(
    radius: Float,
    color: Color,
    stroke: Float,
    offset: Offset = Offset.Zero,
    fill: Color? = null,
) {
    val width = 1.7320508f * radius
    val rowHeight = 1.5f * radius
    var row = 0
    var y = -radius + offset.y
    val join = Stroke(width = stroke, join = StrokeJoin.Round)
    while (y < size.height + radius) {
        val rowShift = if (row % 2 == 0) 0f else width / 2f
        var x = -radius + rowShift + offset.x
        while (x < size.width + radius) {
            val cell = hexagon(Offset(x, y), radius)
            if (fill != null) drawPath(cell, fill)
            drawPath(cell, color, style = join)
            x += width
        }
        y += rowHeight
        row += 1
    }
}

private fun curvePoint(t: Float, start: Offset, end: Offset): Offset {
    val control = Offset(
        (start.x + end.x) / 2f - (end.y - start.y) * 0.18f,
        (start.y + end.y) / 2f,
    )
    val u = 1f - t
    return Offset(
        u * u * start.x + 2f * u * t * control.x + t * t * end.x,
        u * u * start.y + 2f * u * t * control.y + t * t * end.y,
    )
}

private fun hexagon(center: Offset, radius: Float): Path {
    val path = Path()
    for (i in 0 until 6) {
        val angle = Math.toRadians((60 * i - 90).toDouble())
        val x = center.x + cos(angle).toFloat() * radius
        val y = center.y + sin(angle).toFloat() * radius
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

@Composable
private fun AreaChip(
    area: LifeArea?,
    areas: List<LifeArea>,
    colors: FocusColors,
    onSelect: (LifeArea) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val turn by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "chevron",
    )
    val dot = area?.let { areaVisual(it).color.pastel() } ?: Color(0xFFD7E4F8)
    Box {
        Row(
            Modifier
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    clip = false,
                    ambientColor = WarmShadow.copy(alpha = 0.18f),
                    spotColor = WarmShadow.copy(alpha = 0.22f),
                )
                .clip(CircleShape)
                .background(colors.chip)
                .clickable(
                    enabled = areas.isNotEmpty(),
                    role = Role.Button,
                    onClick = { expanded = true },
                )
                .padding(start = 8.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(dot),
            )
            Text(
                text = area?.label ?: "Focus",
                modifier = Modifier.padding(start = 8.dp, end = 4.dp),
                color = colors.chipInk,
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
            Image(
                painter = painterResource(R.drawable.ic_chevron_down),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .graphicsLayer { rotationZ = turn },
                colorFilter = ColorFilter.tint(colors.chipInk),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            areas.forEach { choice ->
                DropdownMenuItem(
                    text = { Text(choice.label) },
                    onClick = {
                        onSelect(choice)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun CollectButton(
    running: Boolean,
    colors: FocusColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && !running) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessMedium),
        label = "press",
    )
    val shape = RoundedCornerShape(50)
    Box(
        modifier
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (pressed) 2.dp else 10.dp,
                shape = shape,
                clip = false,
                ambientColor = WarmShadow.copy(alpha = 0.26f),
                spotColor = WarmShadow.copy(alpha = 0.32f),
            )
            .clip(shape)
            .background(colors.button)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = !running,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!running) {
                PlayMark(colors.buttonInk, Modifier.size(16.dp))
                Spacer(Modifier.size(10.dp))
            }
            Text(
                text = if (running) "Collecting…" else "Start Collecting",
                color = colors.buttonInk,
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
        }
    }
}

@Composable
private fun PlayMark(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.18f, size.height * 0.06f)
            lineTo(size.width * 0.90f, size.height * 0.50f)
            lineTo(size.width * 0.18f, size.height * 0.94f)
            close()
        }
        drawPath(path, color)
    }
}

@Composable
private fun HoneyPace(colors: FocusColors, modifier: Modifier = Modifier) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.honey),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = "+${HoneyRules.FOCUS_HONEY} honey every ${HoneyRules.FOCUS_BLOCK_MINUTES} minutes focused",
            modifier = Modifier.padding(start = 6.dp),
            color = colors.footer,
            fontFamily = RubikFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun BackChevron(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(
                role = Role.Button,
                onClick = onBack,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White.copy(alpha = 0.35f), bounded = true),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(18.dp)) {
            val stroke = 2.4.dp.toPx()
            drawLine(
                Color.White,
                Offset(size.width * 0.72f, size.height * 0.16f),
                Offset(size.width * 0.32f, size.height * 0.50f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                Color.White,
                Offset(size.width * 0.32f, size.height * 0.50f),
                Offset(size.width * 0.72f, size.height * 0.84f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

private fun Color.pastel(): Color = copy(
    red = red + (1f - red) * 0.28f,
    green = green + (1f - green) * 0.28f,
    blue = blue + (1f - blue) * 0.28f,
)
