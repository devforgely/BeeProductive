package com.devforgely.beeproductive.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.R
import kotlin.math.roundToInt
import com.devforgely.beeproductive.core.components.SoftCard
import com.devforgely.beeproductive.core.theme.Amber
import com.devforgely.beeproductive.core.theme.HoneyGold
import com.devforgely.beeproductive.core.theme.Ink
import com.devforgely.beeproductive.core.theme.InkSoft
import com.devforgely.beeproductive.core.theme.RubikFontFamily
import com.devforgely.beeproductive.core.theme.SageDeep
import com.devforgely.beeproductive.core.theme.Wax
import com.devforgely.beeproductive.domain.model.HabitItem
import com.devforgely.beeproductive.domain.model.HoneyRules
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.HoneyPillButton
import com.devforgely.beeproductive.ui.common.areaVisual

private val GoalShape = RoundedCornerShape(24.dp)
private val GoalShadow = Color.Black.copy(alpha = 0.08f)

@Composable
fun HomeScreen(
    onSettings: () -> Unit,
    onFocus: () -> Unit,
    onCelebrate: (honey: Int, cells: Int) -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    val goals = snapshot.activeHabits
    val done = goals.count { it.doneToday }
    var editing by remember { mutableStateOf<HabitItem?>(null) }
    val ordered = remember { goals.toMutableStateList() }
    var draggingId by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(goals) {
        val fresh = goals.associateBy { it.id }
        val sameMembers = ordered.map { it.id }.toSet() == fresh.keys
        if (!sameMembers) {
            ordered.clear()
            ordered.addAll(goals)
        } else {
            ordered.forEachIndexed { index, item ->
                fresh[item.id]?.let { if (it != item) ordered[index] = it }
            }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState(), enabled = draggingId == null)
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            Spacer(Modifier.height(188.dp))
            EnergyCard(
                current = snapshot.energyShown,
                cap = HoneyRules.ENERGY_CAP,
            )
            Spacer(Modifier.height(22.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Today's goals",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "$done of ${goals.size} done",
                    color = InkSoft,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(Modifier.height(12.dp))
            if (goals.isEmpty()) {
                SoftCard {
                    Text(
                        "Your goals will land here once the hive is built.",
                        color = InkSoft,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                GoalList(
                    goals = ordered,
                    draggingId = draggingId,
                    onDragStart = { draggingId = it },
                    onDragEnd = {
                        draggingId = null
                        viewModel.reorderHabits(ordered.map { it.id })
                    },
                    onEdit = { editing = it },
                    onComplete = { habit ->
                        viewModel.completeHabit(habit.id) { result ->
                            if (result.celebrate) onCelebrate(result.honey, result.cells)
                        }
                    },
                )
            }
            Spacer(Modifier.height(18.dp))
            HoneyPillButton(text = "Start collecting", onClick = onFocus)
        }
        Image(
            painter = painterResource(R.drawable.menu),
            contentDescription = "Settings",
            modifier = Modifier
                .padding(start = 16.dp, top = 12.dp)
                .size(42.dp)
                .align(Alignment.TopStart)
                .clickable(onClick = onSettings),
        )
    }
    editing?.let { habit ->
        EditGoalDialog(
            title = habit.title,
            onDismiss = { editing = null },
            onSave = { next ->
                viewModel.renameHabit(habit.id, next)
                editing = null
            },
        )
    }
}

@Composable
private fun GoalList(
    goals: SnapshotStateList<HabitItem>,
    draggingId: Long?,
    onDragStart: (Long) -> Unit,
    onDragEnd: () -> Unit,
    onEdit: (HabitItem) -> Unit,
    onComplete: (HabitItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        goals.forEach { habit ->
            key(habit.id) {
                GoalRow(
                habit = habit,
                dragging = draggingId == habit.id,
                onDragStart = { onDragStart(habit.id) },
                onDrag = { delta -> moveGoal(goals, habit.id, delta) },
                onDragEnd = onDragEnd,
                onEdit = { onEdit(habit) },
                onComplete = { onComplete(habit) },
                )
            }
        }
    }
}

private fun moveGoal(goals: SnapshotStateList<HabitItem>, id: Long, deltaRows: Int) {
    if (deltaRows == 0) return
    val from = goals.indexOfFirst { it.id == id }
    if (from < 0) return
    val to = (from + deltaRows).coerceIn(0, goals.lastIndex)
    if (to == from) return
    val item = goals.removeAt(from)
    goals.add(to, item)
}

@Composable
private fun EnergyCard(current: Int, cap: Int) {
    val fraction = if (cap == 0) 0f else (current.toFloat() / cap).coerceIn(0f, 1f)
    val segments = 18
    val filled = (fraction * segments).toInt()
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.energy),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Energy",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "$current / $cap",
                color = InkSoft,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.fillMaxWidth()) {
            repeat(segments) { index ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (index < filled) HoneyGold else Wax),
                )
            }
        }
    }
}

@Composable
private fun GoalRow(
    habit: HabitItem,
    dragging: Boolean,
    onDragStart: () -> Unit,
    onDrag: (deltaRows: Int) -> Unit,
    onDragEnd: () -> Unit,
    onEdit: () -> Unit,
    onComplete: () -> Unit,
) {
    val visual = areaVisual(habit.area)
    var accumulated by remember(habit.id) { mutableFloatStateOf(0f) }
    Row(
        Modifier
            .fillMaxWidth()
            .zIndex(if (dragging) 1f else 0f)
            .offset { IntOffset(0, if (dragging) accumulated.roundToInt() else 0) }
            .shadow(
                elevation = if (dragging) 8.dp else 3.dp,
                shape = GoalShape,
                ambientColor = GoalShadow,
                spotColor = GoalShadow,
            )
            .clip(GoalShape)
            .background(Color.White)
            .pointerInput(habit.id) {
                val rowHeight = size.height.toFloat().coerceAtLeast(1f) + 12.dp.toPx()
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        accumulated = 0f
                        onDragStart()
                    },
                    onDragEnd = {
                        accumulated = 0f
                        onDragEnd()
                    },
                    onDragCancel = {
                        accumulated = 0f
                        onDragEnd()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        accumulated += dragAmount.y
                        val steps = (accumulated / rowHeight).toInt()
                        if (steps != 0) {
                            onDrag(steps)
                            accumulated -= steps * rowHeight
                        }
                    },
                )
            }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(visual.color),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(visual.icon),
                contentDescription = null,
                modifier = Modifier.size(26.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(
            Modifier
                .weight(1f)
                .clickable(onClick = onEdit),
        ) {
            Text(
                text = habit.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (habit.doneToday) InkSoft else Ink,
                textDecoration = if (habit.doneToday) TextDecoration.LineThrough else null,
            )
            Text(
                text = habit.area.label,
                color = InkSoft,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Image(
            painter = painterResource(R.drawable.energy),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "+${HoneyRules.HABIT_HONEY}",
            color = Amber,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.width(10.dp))
        GoalCheck(done = habit.doneToday, onComplete = onComplete)
    }
}

@Composable
private fun GoalCheck(done: Boolean, onComplete: () -> Unit) {
    Box(
        Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(if (done) SageDeep else Color.Transparent)
            .border(2.dp, if (done) SageDeep else Amber.copy(alpha = 0.7f), CircleShape)
            .clickable(enabled = !done, onClick = onComplete),
        contentAlignment = Alignment.Center,
    ) {
        if (done) {
            Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)
        }
    }
}

@Composable
private fun EditGoalDialog(
    title: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var draft by remember(title) { mutableStateOf(title) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit goal", fontFamily = RubikFontFamily, fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                singleLine = true,
                textStyle = TextStyle(fontFamily = RubikFontFamily, color = Ink),
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(draft.trim()) },
                enabled = draft.trim().isNotEmpty(),
            ) {
                Text("Save", fontFamily = RubikFontFamily, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = RubikFontFamily)
            }
        },
    )
}
