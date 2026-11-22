package com.devforgely.beeproductive.ui.quest

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.core.components.SoftCard
import com.devforgely.beeproductive.core.theme.HoneyGold
import com.devforgely.beeproductive.core.theme.Ink
import com.devforgely.beeproductive.core.theme.InkSoft
import com.devforgely.beeproductive.core.theme.SageDeep
import com.devforgely.beeproductive.core.theme.Wax
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
fun QuestListScreen(
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
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(visual.color),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(visual.icon),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(area.label, style = MaterialTheme.typography.titleMedium)
            Text(
                text = if (done == goals.size && goals.isNotEmpty()) {
                    "$done of ${goals.size} goals · Full comb!"
                } else {
                    "$done of ${goals.size} goals today"
                },
                color = if (done == goals.size && goals.isNotEmpty()) SageDeep else InkSoft,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                goals.forEach { goal ->
                    GoalHex(filled = goal.doneToday, color = visual.color)
                }
            }
        }
        Text("›", color = InkSoft, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun QuestDetailScreen(
    areaName: String,
    onBack: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val area = runCatching { LifeArea.valueOf(areaName) }.getOrNull()
    val snapshot by viewModel.snapshot.collectAsState()
    PaperPage {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HiveBackButton(onBack)
            if (area != null) {
                Text(
                    text = area.label,
                    modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        }
        if (area == null) {
            Text("That life area has flown off.", color = InkSoft)
        } else {
            WeekChart(snapshot, area)
            Milestones(snapshot.areaRun(area))
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
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("This week", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text(
                "${counts.sum()} goals done",
                color = InkSoft,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(140.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
        ) {
            days.forEachIndexed { index, day ->
                val fraction = counts[index].toFloat() / peak
                val today = day == snapshot.today
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .width(22.dp)
                            .height((18 + 96 * fraction).dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(if (today) visual.color else visual.color.copy(alpha = 0.45f)),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = day.dayOfWeek.name.take(1),
                        color = if (today) visual.color else InkSoft,
                        fontWeight = if (today) FontWeight.Bold else FontWeight.Medium,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun Milestones(run: Int) {
    val reached = milestoneDays.lastOrNull { run >= it }
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Weekly milestones", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            if (reached != null) {
                Text(
                    text = "Milestone reached",
                    color = SageDeep,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            milestoneDays.forEach { day ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GoalHex(filled = run >= day, color = HoneyGold, modifier = Modifier.size(36.dp))
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "$day day",
                        color = Ink,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
internal fun GoalHex(
    filled: Boolean,
    color: Color,
    modifier: Modifier = Modifier.size(18.dp),
) {
    Canvas(modifier) {
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
