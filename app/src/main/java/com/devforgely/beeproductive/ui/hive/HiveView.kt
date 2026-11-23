package com.devforgely.beeproductive.ui.hive

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.devforgely.beeproductive.R
import com.devforgely.beeproductive.core.components.SoftCard
import com.devforgely.beeproductive.core.theme.Amber
import com.devforgely.beeproductive.core.theme.CombLine
import com.devforgely.beeproductive.core.theme.HoneyGold
import com.devforgely.beeproductive.core.theme.Ink
import com.devforgely.beeproductive.core.theme.InkSoft
import com.devforgely.beeproductive.core.theme.Sage
import com.devforgely.beeproductive.core.theme.SupporterPurple
import com.devforgely.beeproductive.core.theme.WaxShadow
import com.devforgely.beeproductive.core.theme.isDuskTheme
import com.devforgely.beeproductive.domain.model.HiveProgress
import com.devforgely.beeproductive.domain.model.HiveSnapshot
import com.devforgely.beeproductive.domain.model.HoneyRules
import com.devforgely.beeproductive.domain.model.LifeArea
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.HoneyBalanceChip
import com.devforgely.beeproductive.ui.common.PaperPage
import com.devforgely.beeproductive.ui.common.areaVisual
import com.devforgely.beeproductive.ui.onboarding.BeeIdleAnimationId
import com.devforgely.beeproductive.ui.onboarding.rememberBeeTraitsComposition
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlinx.coroutines.flow.first

private enum class HiveSpan(val label: String) {
    Week("Week"),
    Month("Month"),
    AllTime("All time"),
}

private data class HiveStory(
    val paints: List<Color?>,
    val outlines: List<Color>,
    val partial: Float,
)

private data class Axial(val q: Int, val r: Int)

private val GhostDisk: List<Axial> = buildList {
    val radius = 4
    for (q in -radius..radius) {
        for (r in -radius..radius) {
            val s = -q - r
            if (maxOf(abs(q), abs(r), abs(s)) <= radius) add(Axial(q, r))
        }
    }
}

@Composable
fun HiveView(viewModel: HiveViewModel = hiltViewModel()) {
    val snapshot by viewModel.snapshot.collectAsState()
    val prefs by viewModel.prefs.collectAsState()
    var span by remember { mutableStateOf(HiveSpan.Month) }
    val story = remember(snapshot, span) { hiveStory(snapshot, span) }
    val filled = story.paints.size
    val level = HiveProgress.level(filled)
    val hiveTitle = prefs.userName.trim().ifBlank { "Your" }.let { name ->
        if (name == "Your") "Your hive" else "$name's hive"
    }
    val monday = snapshot.today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val week = (0..6).map { monday.plusDays(it.toLong()) }
    val weekHoney = week.map { snapshot.honeyOn(it) }
    val night = isDuskTheme()
    val ink = MaterialTheme.colorScheme.onBackground
    val soft = MaterialTheme.colorScheme.onSurfaceVariant
    val gain = if (night) Sage else Color(0xFF2C9A55)

    PaperPage {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    hiveTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "Level $level · ${HiveProgress.title(level)}",
                    color = soft,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            HoneyBalanceChip(snapshot.balance)
        }
        HiveSpanTabs(selected = span, night = night, onSelect = { span = it })
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HiveScene(story = story, span = span, night = night)
            HiveStats(
                filled = filled,
                weekCells = weekHoney.sum() / HoneyRules.CELL_CAPACITY,
                toNext = HiveProgress.cellsToNext(filled),
                nextLevel = level + 1,
                ink = ink,
                soft = soft,
                gain = gain,
            )
            AreaLegend(markNew = span != HiveSpan.AllTime, soft = soft)
        }
        WeekCellsCard(days = week, honey = weekHoney)
        NextUnlockCard(level = level, filled = filled)
    }
}

@Composable
private fun HiveSpanTabs(
    selected: HiveSpan,
    night: Boolean,
    onSelect: (HiveSpan) -> Unit,
) {
    val options = HiveSpan.entries
    val shape = RoundedCornerShape(24.dp)
    val pill = RoundedCornerShape(20.dp)
    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(if (night) MaterialTheme.colorScheme.surfaceVariant else Color.White.copy(alpha = 0.55f))
            .padding(4.dp),
    ) {
        val index by animateFloatAsState(
            targetValue = selected.ordinal.toFloat(),
            animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow),
            label = "hive-span",
        )
        val tabWidth = maxWidth / options.size
        Box(
            Modifier
                .offset(x = tabWidth * index)
                .width(tabWidth)
                .fillMaxHeight()
                .shadow(
                    elevation = if (night) 0.dp else 6.dp,
                    shape = pill,
                    ambientColor = WaxShadow.copy(alpha = 0.12f),
                    spotColor = WaxShadow.copy(alpha = 0.18f),
                )
                .clip(pill)
                .background(if (night) MaterialTheme.colorScheme.surface else Color.White),
        )
        Row(Modifier.fillMaxSize()) {
            options.forEach { span ->
                val on = span == selected
                val color by animateColorAsState(
                    targetValue = if (on) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                    label = "hive-span-ink",
                )
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .selectable(
                            selected = on,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Tab,
                            onClick = { onSelect(span) },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        span.label,
                        color = color,
                        fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun HiveScene(story: HiveStory, span: HiveSpan, night: Boolean) {
    val appear = remember { Animatable(0f) }
    val tint = remember { Animatable(1f) }
    val beeIn = remember { Animatable(0f) }
    val tintGate = remember { booleanArrayOf(true) }
    val latestStory by rememberUpdatedState(story)
    LaunchedEffect(Unit) {
        snapshotFlow {
            val current = latestStory
            current.paints.isNotEmpty() || current.partial > 0f || current.outlines.isNotEmpty()
        }.first { it }
        appear.snapTo(0f)
        appear.animateTo(1f, tween(durationMillis = 480, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(span) {
        if (tintGate[0]) {
            tintGate[0] = false
            return@LaunchedEffect
        }
        tint.snapTo(0.22f)
        tint.animateTo(1f, tween(durationMillis = 280, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        beeIn.animateTo(1f, tween(durationMillis = 340, delayMillis = 90, easing = FastOutSlowInEasing))
    }
    val drift = rememberInfiniteTransition(label = "hive-drift")
    val hover by drift.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "bee-hover",
    )
    val gloss by drift.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "cell-gloss",
    )
    val appeared = appear.value
    val tinted = tint.value
    val arrived = beeIn.value
    Box(
        Modifier
            .fillMaxWidth()
            .height(300.dp)
            .semantics {
                contentDescription = "${story.paints.size} honeycomb cells"
            },
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawHive(
                story = story,
                appear = appeared,
                gloss = gloss,
                tint = tinted,
                night = night,
            )
        }
        HiveBee(
            hover = hover,
            arrived = arrived,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-112).dp, y = (-104).dp)
                .size(92.dp),
        )
    }
}

@Composable
private fun HiveBee(hover: Float, arrived: Float, modifier: Modifier = Modifier) {
    val idle by rememberBeeTraitsComposition(BeeIdleAnimationId)
    val progress by animateLottieCompositionAsState(
        composition = idle,
        iterations = LottieConstants.IterateForever,
    )
    Box(
        modifier
            .graphicsLayer {
                alpha = arrived
                translationY = sin(hover * 2.0 * PI).toFloat() * 6.dp.toPx() + (1f - arrived) * 12.dp.toPx()
            }
            .semantics { contentDescription = "Bee" },
    ) {
        if (idle == null) {
            Image(
                painter = painterResource(R.drawable.bee),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            LottieAnimation(
                composition = idle,
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun HiveStats(
    filled: Int,
    weekCells: Int,
    toNext: Int,
    nextLevel: Int,
    ink: Color,
    soft: Color,
    gain: Color,
) {
    Row(Modifier.fillMaxWidth()) {
        HiveStat("$filled", "cells filled", ink, soft)
        HiveStat("+$weekCells", "this week", gain, soft)
        HiveStat("$toNext", "to Level $nextLevel", ink, soft)
    }
}

@Composable
private fun RowScope.HiveStat(value: String, label: String, valueColor: Color, labelColor: Color) {
    Column(
        Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            color = valueColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = label,
            color = labelColor,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AreaLegend(markNew: Boolean, soft: Color) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        if (markNew) {
            Text(
                "New:",
                color = soft,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 6.dp, top = 1.dp),
            )
        }
        LifeArea.entries.forEach { area ->
            val visual = areaVisual(area)
            Row(
                Modifier
                    .padding(horizontal = 5.dp)
                    .semantics { contentDescription = area.label },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(visual.color),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = area.shortLabel(),
                    color = soft,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun WeekCellsCard(days: List<LocalDate>, honey: List<Int>) {
    val peak = honey.maxOrNull()?.coerceAtLeast(1) ?: 1
    val bestIndex = honey.indices.maxByOrNull { honey[it] } ?: 0
    val bestName = if (honey.getOrElse(bestIndex) { 0 } <= 0) {
        "—"
    } else {
        days[bestIndex].dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    }
    val grown = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        grown.animateTo(1f, tween(durationMillis = 280, delayMillis = 180, easing = FastOutSlowInEasing))
    }
    SoftCard {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val slot = ((maxWidth - 148.dp) / 7).coerceIn(14.dp, 26.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "Cells this week",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        "Best day: $bestName",
                        color = InkSoft,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    days.forEachIndexed { index, day ->
                        val amount = honey[index]
                        val local = ((grown.value - index * 0.08f) / 0.55f).coerceIn(0f, 1f)
                        val scale = lerp(0.72f, 1f, easeOutBack(local).coerceIn(0f, 1.1f))
                        val fraction = if (amount <= 0) 0.52f else 0.66f + 0.34f * amount / peak.toFloat()
                        WeekHex(
                            letter = day.dayOfWeek.name.take(1),
                            filled = amount > 0,
                            best = index == bestIndex && amount > 0,
                            size = slot * fraction * scale,
                            slot = slot,
                            alpha = local,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekHex(
    letter: String,
    filled: Boolean,
    best: Boolean,
    size: Dp,
    slot: Dp,
    alpha: Float,
) {
    Column(
        Modifier
            .width(slot)
            .graphicsLayer { this.alpha = alpha },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Canvas(
            Modifier
                .size(size.coerceAtMost(slot))
                .semantics { contentDescription = if (filled) "$letter filled" else "$letter empty" },
        ) {
            val radius = this.size.minDimension / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            drawPath(
                hexPath(center, radius * 0.92f),
                color = if (filled) HoneyGold else Color(0xFFF3E4C4),
            )
            if (best) {
                drawPath(
                    hexPath(center, radius * 0.92f),
                    color = Color.White.copy(alpha = 0.28f),
                    style = Stroke(width = radius * 0.1f),
                )
            }
        }
        Text(
            text = letter,
            color = if (best) Ink else InkSoft,
            fontWeight = if (best) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 3.dp),
        )
    }
}

@Composable
private fun NextUnlockCard(level: Int, filled: Int) {
    val progress = HiveProgress.progressToNext(filled)
    val meter = remember { Animatable(0f) }
    LaunchedEffect(progress) {
        meter.animateTo(progress, tween(durationMillis = 520, delayMillis = 160, easing = FastOutSlowInEasing))
    }
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CrownBadge()
            Spacer(Modifier.width(12.dp))
            Text(
                "Next unlock: ${HiveProgress.title(level + 1)}",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "Lv ${level + 1}",
                color = InkSoft,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Spacer(Modifier.height(12.dp))
        UnlockMeter(meter.value)
    }
}

@Composable
private fun CrownBadge() {
    Box(
        Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SupporterPurple.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_hive_crown),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun UnlockMeter(progress: Float) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(11.dp)
            .clip(CircleShape)
            .background(SupporterPurple.copy(alpha = 0.16f)),
    ) {
        if (progress > 0f) {
            Box(
                Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFD4C4FA), SupporterPurple),
                        ),
                    ),
            )
        }
    }
}

private fun DrawScope.drawHive(
    story: HiveStory,
    appear: Float,
    gloss: Float,
    tint: Float,
    night: Boolean,
) {
    val radius = minOf(size.width * 0.064f, size.height * 0.072f)
    val origin = Offset(size.width / 2f, size.height * 0.52f)
    val placed = GhostDisk.map { it to axialPoint(origin, it, radius) }
    val anchor = origin + Offset(0f, radius * 1.15f)
    val nearest = placed.sortedBy { (_, center) ->
        val dx = center.x - anchor.x
        val dy = center.y - anchor.y
        dx * dx + dy * dy
    }
    val chosen = nearest.take(story.paints.size).sortedByDescending { it.second.y }
    val used = chosen.map { it.first }.toSet()
    val open = placed.filter { it.first !in used }
    val crown = chosen.minOfOrNull { it.second.y } ?: origin.y
    val partialSpot = if (story.partial > 0f) {
        open.minByOrNull { (_, center) ->
            val dx = center.x - origin.x
            val dy = center.y - (crown - radius * 1.65f)
            dx * dx * 0.45f + dy * dy
        }
    } else {
        null
    }
    val outlinePool = open.filter { it.first != partialSpot?.first }
    val outlineSpots = outlinePool.sortedBy { (_, center) ->
        val dx = center.x - origin.x
        val dy = center.y - (crown - radius * 1.85f)
        dx * dx * 0.3f + dy * dy
    }.take(story.outlines.size)
    val hidden = used + outlineSpots.map { it.first }.toSet() + listOfNotNull(partialSpot?.first)
    val ghostFill = if (night) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.42f)
    val ghostStroke = if (night) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.9f)
    val shadow = if (night) Color.Black.copy(alpha = 0.35f) else Amber.copy(alpha = 0.22f)

    placed.forEach { (hex, center) ->
        if (hex in hidden) return@forEach
        val path = hexPath(center, radius * 0.86f)
        drawPath(path, ghostFill)
        drawPath(path, ghostStroke, style = Stroke(width = radius * 0.045f))
    }

    val count = chosen.size.coerceAtLeast(1)
    chosen.forEachIndexed { index, (_, center) ->
        val start = index / count.toFloat() * 0.42f
        val local = ((appear - start) / 0.58f).coerceIn(0f, 1f)
        val pop = easeOutBack(local).coerceIn(0f, 1.12f)
        val paint = story.paints[index]
        val color = paint?.let { lerpColor(HoneyGold, it, tint) }
        val breathe = if (paint != null && index >= chosen.lastIndex - 5) gloss else 0.62f
        drawHoneyCell(
            center = center + Offset(0f, (1f - pop) * radius * 0.55f),
            radius = radius * 0.9f * lerp(0.62f, 1f, pop),
            color = color,
            gloss = breathe,
            alpha = local,
            shadow = shadow,
        )
    }

    partialSpot?.let { (_, center) ->
        val local = ((appear - 0.5f) / 0.5f).coerceIn(0f, 1f)
        drawPartialCell(center, radius * 0.9f, story.partial, local)
    }

    outlineSpots.forEachIndexed { index, (_, center) ->
        val local = ((appear - 0.48f) / 0.52f).coerceIn(0f, 1f) * tint
        drawOutlineCell(
            center = center,
            radius = radius * 0.9f,
            color = story.outlines[index],
            alpha = local,
        )
    }
}

private fun DrawScope.drawHoneyCell(
    center: Offset,
    radius: Float,
    color: Color?,
    gloss: Float,
    alpha: Float,
    shadow: Color,
) {
    if (radius <= 0f || alpha <= 0f) return
    val path = hexPath(center, radius)
    drawPath(hexPath(center + Offset(0f, radius * 0.12f), radius), shadow, alpha = alpha)
    val top: Color
    val bottom: Color
    if (color == null) {
        top = Color(0xFFFFE7A2)
        bottom = Amber
    } else {
        top = lerpColor(Color.White, color, 0.58f)
        bottom = color
    }
    drawPath(
        path,
        Brush.verticalGradient(listOf(top, bottom), startY = center.y - radius, endY = center.y + radius),
        alpha = alpha,
    )
    clipPath(path) {
        drawCircle(
            color = Color.White,
            radius = radius * 0.36f,
            center = center + Offset(-radius * 0.06f, -radius * 0.3f),
            alpha = 0.32f * gloss * alpha,
        )
    }
    drawPath(
        path,
        Color.White.copy(alpha = 0.72f),
        alpha = alpha,
        style = Stroke(width = max(1.5f, radius * 0.06f)),
    )
}

private fun DrawScope.drawPartialCell(center: Offset, radius: Float, fill: Float, alpha: Float) {
    val path = hexPath(center, radius)
    drawPath(path, Color.White.copy(alpha = 0.55f), alpha = alpha)
    if (fill > 0f) {
        clipPath(path) {
            val height = radius * 2f * fill.coerceIn(0f, 1f)
            drawRect(
                brush = Brush.verticalGradient(listOf(HoneyGold, Amber)),
                topLeft = Offset(center.x - radius, center.y + radius - height),
                size = Size(radius * 2f, height),
                alpha = alpha,
            )
        }
    }
    drawPath(path, CombLine, alpha = alpha, style = Stroke(width = max(1.5f, radius * 0.07f)))
}

private fun DrawScope.drawOutlineCell(center: Offset, radius: Float, color: Color, alpha: Float) {
    if (alpha <= 0f) return
    val path = hexPath(center, radius * 0.92f)
    drawPath(path, color.copy(alpha = 0.16f), alpha = alpha)
    drawPath(path, color, alpha = alpha, style = Stroke(width = max(2f, radius * 0.1f)))
}

private fun axialPoint(origin: Offset, hex: Axial, radius: Float): Offset {
    val x = radius * 1.7320508f * (hex.q + hex.r / 2f)
    val y = radius * 1.5f * hex.r
    return Offset(origin.x + x, origin.y + y)
}

private fun hexPath(center: Offset, radius: Float): Path {
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

private fun easeOutBack(t: Float): Float {
    val c1 = 1.70158f
    val c3 = c1 + 1f
    val p = t - 1f
    return 1f + c3 * p * p * p + c1 * p * p
}

private fun hiveStory(snapshot: HiveSnapshot, span: HiveSpan): HiveStory {
    val filled = snapshot.cells.count { it.fill >= 1f }
    val start = when (span) {
        HiveSpan.Week -> snapshot.today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        HiveSpan.Month -> snapshot.today.withDayOfMonth(1)
        HiveSpan.AllTime -> null
    }
    val shares = areaShares(snapshot, start)
    val slots = if (span == HiveSpan.AllTime) {
        filled
    } else {
        val honey = honeySince(snapshot, start)
        (honey / HoneyRules.CELL_CAPACITY).coerceAtMost(filled)
    }
    val painted = areaPaint(slots, shares, outlineGaps = span != HiveSpan.AllTime)
    val gold = if (span == HiveSpan.AllTime && painted.colors.isNotEmpty()) {
        0
    } else {
        (filled - painted.colors.size).coerceAtLeast(0)
    }
    val paints = List(gold) { null } + painted.colors
    val partial = snapshot.cells.firstOrNull { it.fill > 0f && it.fill < 1f }?.fill ?: 0f
    return HiveStory(paints = paints, outlines = painted.outlined, partial = partial)
}

private data class AreaPaint(val colors: List<Color>, val outlined: List<Color>)

private fun areaPaint(slots: Int, shares: Map<LifeArea, Int>, outlineGaps: Boolean): AreaPaint {
    val active = LifeArea.entries.map { it to (shares[it] ?: 0) }.filter { it.second > 0 }
    if (slots <= 0 || active.isEmpty()) {
        val outlined = if (outlineGaps) active.map { areaVisual(it.first).color } else emptyList()
        return AreaPaint(emptyList(), outlined)
    }
    val total = active.sumOf { it.second }.toFloat()
    val exact = active.map { (area, count) -> area to (slots * count / total) }
    val counts = exact.associate { it.first to it.second.toInt() }.toMutableMap()
    var left = slots - counts.values.sum()
    exact.sortedByDescending { it.second - it.second.toInt() }.forEach { (area, _) ->
        if (left <= 0) return@forEach
        counts[area] = counts.getValue(area) + 1
        left -= 1
    }
    val buckets = LifeArea.entries.mapNotNull { area ->
        val count = counts[area] ?: return@mapNotNull null
        if (count <= 0) null else MutableList(count) { areaVisual(area).color }
    }
    val colors = buildList {
        while (size < slots && buckets.any { it.isNotEmpty() }) {
            buckets.forEach { bucket ->
                if (bucket.isNotEmpty() && size < slots) add(bucket.removeAt(0))
            }
        }
    }
    val outlined = if (outlineGaps) {
        active.map { it.first }.filter { counts[it] == null || counts.getValue(it) == 0 }.map { areaVisual(it).color }
    } else {
        emptyList()
    }
    return AreaPaint(colors, outlined)
}

private fun areaShares(snapshot: HiveSnapshot, start: LocalDate?): Map<LifeArea, Int> {
    val areas = snapshot.habits.associate { it.id to it.area }
    if (areas.isEmpty()) return emptyMap()
    return snapshot.logs
        .asSequence()
        .filter { log ->
            log.completed &&
                !log.day.isAfter(snapshot.today) &&
                (start == null || !log.day.isBefore(start))
        }
        .mapNotNull { areas[it.habitId] }
        .groupingBy { it }
        .eachCount()
}

private fun honeySince(snapshot: HiveSnapshot, start: LocalDate?): Int {
    val cycleId = snapshot.cycle?.id ?: return 0
    return snapshot.honeyLedger.sumOf { item ->
        if (item.amount <= 0 || item.cycleId != cycleId) return@sumOf 0
        if (item.day.isAfter(snapshot.today)) return@sumOf 0
        if (start != null && item.day.isBefore(start)) return@sumOf 0
        item.amount
    }
}

private fun LifeArea.shortLabel(): String = when (this) {
    LifeArea.HEALTH_ENERGY -> "Health"
    LifeArea.MIND_FOCUS -> "Mind"
    LifeArea.WORK_GOALS -> "Work"
    LifeArea.CREATIVITY -> "Create"
    LifeArea.REST -> "Rest"
}
