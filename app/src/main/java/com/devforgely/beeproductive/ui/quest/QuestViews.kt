package com.devforgely.beeproductive.ui.quest

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devforgely.beeproductive.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.core.components.SoftCard
import com.devforgely.beeproductive.core.theme.CombLine
import com.devforgely.beeproductive.core.theme.Cream
import com.devforgely.beeproductive.core.theme.HiveInk
import com.devforgely.beeproductive.core.theme.isDuskTheme
import com.devforgely.beeproductive.core.theme.HoneyGold
import com.devforgely.beeproductive.core.theme.Ink
import com.devforgely.beeproductive.core.theme.InkSoft
import com.devforgely.beeproductive.core.theme.SageDeep
import com.devforgely.beeproductive.core.theme.Wax
import com.devforgely.beeproductive.core.theme.WaxShadow
import com.devforgely.beeproductive.domain.model.HiveSnapshot
import com.devforgely.beeproductive.domain.model.LifeArea
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.HiveBackButton
import com.devforgely.beeproductive.ui.common.PaperPage
import com.devforgely.beeproductive.ui.common.areaVisual
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import kotlin.math.cos
import kotlin.math.sin

private val milestoneDays = listOf(1, 3, 5, 7)

@Composable
fun QuestListView(
    onArea: (LifeArea) -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    PaperPage {
        if (snapshot.questAreas.isEmpty()) {
            SoftCard {
                Text("Goals from onboarding will gather here.", color = InkSoft)
            }
        }
        snapshot.questAreas.forEach { area ->
            QuestCard(snapshot, area, onClick = { onArea(area) })
        }
    }
}

@Composable
private fun QuestCard(snapshot: HiveSnapshot, area: LifeArea, onClick: () -> Unit) {
    val goals = snapshot.habitsIn(area)
    val done = goals.count { it.doneToday }
    val visual = areaVisual(area)
    val shape = RoundedCornerShape(20.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 5.dp,
                shape = shape,
                ambientColor = WaxShadow.copy(alpha = 0.12f),
                spotColor = WaxShadow.copy(alpha = 0.16f),
            )
            .clip(shape)
            .background(Color.White)
            .border(1.dp, CombLine.copy(alpha = 0.28f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(visual.color),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(visual.icon),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                area.label,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = if (done == goals.size && goals.isNotEmpty()) {
                    "$done of ${goals.size} goals · Full comb!"
                } else {
                    "$done of ${goals.size} goals today"
                },
                color = if (done == goals.size && goals.isNotEmpty()) SageDeep else InkSoft,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                goals.forEach { goal ->
                    GoalHex(filled = goal.doneToday, color = visual.color, modifier = Modifier.size(14.dp))
                }
            }
        }
        Image(
            painter = painterResource(R.drawable.ic_chevron_down),
            contentDescription = null,
            colorFilter = ColorFilter.tint(InkSoft),
            modifier = Modifier
                .size(32.dp)
                .graphicsLayer { rotationZ = -90f },
        )
    }
}

@Composable
fun QuestDetailView(
    areaName: String,
    onBack: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val area = runCatching { LifeArea.valueOf(areaName) }.getOrNull()
    val snapshot by viewModel.snapshot.collectAsState()
    val navigationBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HiveBackButton(onBack)
            if (area != null) {
                Text(
                    text = area.label,
                    modifier = Modifier.padding(start = 12.dp),
                    color = if (isDuskTheme()) Color.White else HiveInk,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        }
        val sheet = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .shadow(
                    elevation = 16.dp,
                    shape = sheet,
                    ambientColor = WaxShadow.copy(alpha = 0.12f),
                    spotColor = WaxShadow.copy(alpha = 0.18f),
                )
                .clip(sheet)
                .background(Cream)
                .verticalScroll(rememberScrollState())
                .padding(start = 18.dp, top = 22.dp, end = 18.dp, bottom = 22.dp + navigationBottom),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (area == null) {
                Text("That life area has flown off.", color = InkSoft)
            } else {
                WeekChart(snapshot, area)
                Milestones(snapshot.areaRun(area))
            }
        }
    }
}

@Composable
private fun WeekChart(snapshot: HiveSnapshot, area: LifeArea) {
    val monday = snapshot.today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val days = (0..6).map { monday.plusDays(it.toLong()) }
    val counts = days.map { snapshot.completionsOn(area, it) }
    val peak = counts.maxOrNull()?.coerceAtLeast(1) ?: 1
    val visual = areaVisual(area)
    val softBar = lerp(Color.White, visual.color, 0.42f)
    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "This Week",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Ink, fontWeight = FontWeight.Bold)) {
                        append(counts.sum().toString())
                    }
                    withStyle(SpanStyle(color = InkSoft)) {
                        append(" goals done")
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(Modifier.height(18.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(176.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
        ) {
            days.forEachIndexed { index, day ->
                val today = day == snapshot.today
                val upcoming = day.isAfter(snapshot.today)
                val fraction = counts[index].toFloat() / peak
                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Text(
                        text = if (upcoming) " " else counts[index].toString(),
                        color = if (today) visual.color else Ink,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        Modifier
                            .width(32.dp)
                            .height((if (upcoming) 8f else 14f + 86f * fraction).dp)
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                            .background(
                                when {
                                    upcoming -> Color(0xFFE6E0D6)
                                    today -> visual.color
                                    else -> softBar
                                },
                            ),
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(
                        Modifier.size(32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (today) {
                            Box(
                                Modifier
                                    .matchParentSize()
                                    .clip(CircleShape)
                                    .background(visual.color),
                            )
                        }
                        Text(
                            text = day.dayOfWeek.name.take(1),
                            color = when {
                                today -> Color.White
                                upcoming -> InkSoft.copy(alpha = 0.55f)
                                else -> InkSoft
                            },
                            fontWeight = if (today) FontWeight.ExtraBold else FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Milestones(run: Int) {
    val reached = milestoneDays.lastOrNull { run >= it }
    val cellWidth = 60.dp
    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Weekly Milestones",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
            if (reached != null) {
                ReachedBadge()
            }
        }
        Spacer(Modifier.height(18.dp))
        Box(Modifier.fillMaxWidth()) {
            Canvas(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = cellWidth / 2)
                    .height(51.dp),
            ) {
                val y = size.height / 2f
                val reachedIndex = milestoneDays.indexOfLast { run >= it }
                val fraction = if (reachedIndex <= 0) 0f else reachedIndex.toFloat() / milestoneDays.lastIndex
                drawLine(
                    color = Color(0xFFE4DDD4),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                if (fraction > 0f) {
                    drawLine(
                        color = HoneyGold,
                        start = Offset(0f, y),
                        end = Offset(size.width * fraction, y),
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                milestoneDays.forEach { day ->
                    Box(Modifier.width(cellWidth), contentAlignment = Alignment.Center) {
                        MilestoneMark(reached = run >= day)
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            milestoneDays.forEach { day ->
                Text(
                    text = if (day == 1) "1 day" else "$day days",
                    modifier = Modifier.width(cellWidth),
                    color = Ink,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(HoneyGold.copy(alpha = 0.28f))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.honey),
                contentDescription = null,
                modifier = Modifier.size(34.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = buildAnnotatedString {
                    val body = SpanStyle(fontWeight = FontWeight.Bold, color = Ink)
                    val stronger = SpanStyle(fontWeight = FontWeight.ExtraBold, color = Ink)
                    val royal = SpanStyle(fontWeight = FontWeight.ExtraBold, color = Color(0xFFC98115))
                    if (run >= milestoneDays.last()) {
                        withStyle(body) { append("A ") }
                        withStyle(royal) { append("Royal Jelly") }
                        withStyle(body) { append(" cell is yours (+50 honey)") }
                    } else {
                        withStyle(body) { append("Reach ") }
                        withStyle(stronger) { append("7 days") }
                        withStyle(body) { append(" to unlock a ") }
                        withStyle(royal) { append("Royal Jelly") }
                        withStyle(body) { append(" cell (+50 honey)") }
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private val ReachedGreen = Color(0xFF7BC67E)

@Composable
private fun DetailCard(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RoundedCornerShape(24.dp),
                shadow = Shadow(
                    radius = 14.dp,
                    color = Color(0xFF3E3428),
                    spread = 1.dp,
                    offset = DpOffset.Zero,
                    alpha = 0.18f,
                ),
            )
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(16.dp),
    ) {
        content()
    }
}

@Composable
private fun ReachedBadge() {
    Row(
        Modifier
            .clip(CircleShape)
            .background(ReachedGreen)
            .padding(start = 6.dp, top = 6.dp, end = 12.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.size(12.dp)) {
                val stroke = Stroke(
                    width = size.minDimension * 0.18f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                )
                val path = Path().apply {
                    moveTo(size.width * 0.22f, size.height * 0.52f)
                    lineTo(size.width * 0.42f, size.height * 0.72f)
                    lineTo(size.width * 0.78f, size.height * 0.32f)
                }
                drawPath(path, ReachedGreen, style = stroke)
            }
        }
        Spacer(Modifier.width(6.dp))
        Text(
            text = "Milestone Reached!",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
        )
    }
}

@Composable
private fun MilestoneMark(reached: Boolean) {
    Image(
        painter = painterResource(R.drawable.star_badge),
        contentDescription = null,
        colorFilter = if (reached) {
            null
        } else {
            ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
        },
        modifier = Modifier.size(width = 44.dp, height = 51.dp),
    )
}

@Composable
internal fun GoalHex(
    filled: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier.size(18.dp)) {
        val path = hexPath(Offset(size.width / 2f, size.height / 2f), size.minDimension / 2f)
        drawPath(
            path = path,
            color = if (filled) color else Wax,
            style = Fill,
        )
    }
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

