package com.devforgely.beeproductive.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
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
import com.devforgely.beeproductive.domain.model.HabitItem
import com.devforgely.beeproductive.domain.model.HoneyRules
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.HoneyPillButton
import com.devforgely.beeproductive.ui.common.areaVisual

private val GoalShape = RoundedCornerShape(18.dp)
private val GoalShadow = Color.Black.copy(alpha = 0.08f)
private val GoalRowHeight = 80.dp
private val GoalRowGap = 12.dp
private val EnergyCardShape = RoundedCornerShape(22.dp)
private val EnergyBarShape = RoundedCornerShape(50)

@Composable
fun HomeView(
    onSettings: () -> Unit,
    onFocus: () -> Unit,
    onCelebrate: (honey: Int, cells: Int) -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    val goals = snapshot.activeHabits
    val done = goals.count { it.doneToday }
    var editing by remember { mutableStateOf<HabitItem?>(null) }
    var adding by remember { mutableStateOf(false) }
    var goalsExpanded by remember { mutableStateOf(true) }
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
            userScrollEnabled = draggingId == null,
        ) {
            item(key = "energy-space") { Spacer(Modifier.height(188.dp)) }
            item(key = "energy") {
                EnergyCard(
                    current = snapshot.energyShown,
                    cap = HoneyRules.ENERGY_CAP,
                    forageNumber = snapshot.forages.size.coerceAtLeast(1),
                )
            }
            item(key = "collect-space") { Spacer(Modifier.height(22.dp)) }
            item(key = "collect") {
                HoneyPillButton(text = "Start collecting", onClick = onFocus)
            }
            item(key = "goals-header-space") { Spacer(Modifier.height(18.dp)) }
            item(key = "goals-header") {
                GoalsHeader(
                    remaining = goals.size - done,
                    expanded = goalsExpanded,
                    onToggle = {
                        goalsExpanded = !goalsExpanded
                        draggingId = null
                    },
                    onAdd = { adding = true },
                )
            }
            item(key = "goals-list") {
                AnimatedVisibility(
                    visible = goalsExpanded,
                    enter = expandVertically(
                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
                    ) + fadeIn(animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)),
                    exit = shrinkVertically(
                        animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing),
                    ) + fadeOut(animationSpec = tween(durationMillis = 140, easing = FastOutLinearInEasing)),
                ) {
                    Column(Modifier.padding(top = 8.dp)) {
                        if (goals.isEmpty()) {
                            SoftCard {
                                Text(
                                    "Your goals will land here once the hive is built.",
                                    color = InkSoft,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        } else {
                            ordered.forEach { habit ->
                                GoalRow(
                                    modifier = Modifier.padding(bottom = GoalRowGap),
                                    habit = habit,
                                    dragging = draggingId == habit.id,
                                    onDragStart = { draggingId = habit.id },
                                    onDrag = { delta -> moveGoal(ordered, habit.id, delta) },
                                    onDragEnd = {
                                        draggingId = null
                                        viewModel.reorderHabits(ordered.map { it.id })
                                    },
                                    onEdit = { editing = habit },
                                    onComplete = {
                                        viewModel.completeHabit(habit.id) { result ->
                                            if (result.celebrate) onCelebrate(result.honey, result.cells)
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
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
        GoalTitleDialog(
            heading = "Edit goal",
            initial = habit.title,
            onDismiss = { editing = null },
            onSave = { next ->
                viewModel.renameHabit(habit.id, next)
                editing = null
            },
        )
    }
    if (adding) {
        GoalTitleDialog(
            heading = "New goal",
            initial = "",
            onDismiss = { adding = false },
            onSave = { next ->
                viewModel.addHabit(next)
                adding = false
            },
        )
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
private fun GoalsHeader(
    remaining: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    onAdd: () -> Unit,
) {
    val remainingLabel = when (remaining) {
        1 -> "1 goal left for today!"
        else -> "$remaining goals left for today!"
    }
    val chevronTurn by animateFloatAsState(
        targetValue = if (expanded) 0f else -90f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "goals-chevron",
    )
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_calendar),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = remainingLabel,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Image(
                painter = painterResource(R.drawable.ic_filter),
                contentDescription = "Filter goals",
                modifier = Modifier.size(26.dp),
            )
            Spacer(Modifier.width(14.dp))
            Image(
                painter = painterResource(R.drawable.ic_grid_plus),
                contentDescription = "Add from grid",
                modifier = Modifier.size(26.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier.clickable(onClick = onToggle),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Start the day",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.width(6.dp))
                Image(
                    painter = painterResource(R.drawable.ic_chevron_down),
                    contentDescription = if (expanded) "Collapse goals" else "Expand goals",
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer { rotationZ = chevronTurn },
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.55f)),
            )
            Spacer(Modifier.width(8.dp))
            Image(
                painter = painterResource(R.drawable.ic_add_circle),
                contentDescription = "Add goal",
                modifier = Modifier
                    .size(26.dp)
                    .clickable(onClick = onAdd),
            )
        }
    }
}

@Composable
private fun EnergyCard(current: Int, cap: Int, forageNumber: Int) {
    val fraction = if (cap == 0) 0f else (current.toFloat() / cap).coerceIn(0f, 1f)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(EnergyCardShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(start = 10.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.energy),
            contentDescription = null,
            modifier = Modifier.size(56.dp),
        )
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = "${ordinal(forageNumber)} forage",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .clip(EnergyBarShape)
                    .background(Color.White.copy(alpha = 0.28f)),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(fraction)
                        .fillMaxHeight()
                        .background(HoneyGold),
                )
                Text(
                    text = "$current / $cap",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp),
                )
            }
        }
    }
}

private fun ordinal(n: Int): String {
    val suffix = when {
        n % 100 in 11..13 -> "th"
        n % 10 == 1 -> "st"
        n % 10 == 2 -> "nd"
        n % 10 == 3 -> "rd"
        else -> "th"
    }
    return "$n$suffix"
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
    modifier: Modifier = Modifier,
) {
    val visual = areaVisual(habit.area)
    var accumulated by remember(habit.id) { mutableFloatStateOf(0f) }
    Row(
        modifier
            .fillMaxWidth()
            .height(GoalRowHeight)
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
            .padding(start = 8.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.drag),
            contentDescription = "Reorder goal",
            modifier = Modifier
                .size(22.dp)
                .pointerInput(habit.id) {
                    val rowHeight = GoalRowHeight.toPx() + GoalRowGap.toPx()
                    detectDragGestures(
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
                },
        )
        Spacer(Modifier.width(6.dp))
        Box(
            Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(visual.color),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(visual.icon),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(
            Modifier
                .weight(1f)
                .clickable(onClick = onEdit),
        ) {
            Text(
                text = habit.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (habit.doneToday) InkSoft else Ink,
                textDecoration = if (habit.doneToday) TextDecoration.LineThrough else null,
                maxLines = 1,
                overflow = TextOverflow.Clip,
            )
            if (habit.description.isNotBlank()) {
                Text(
                    text = habit.description,
                    color = InkSoft,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                )
            }
        }
        Text(
            text = "+${HoneyRules.HABIT_HONEY}",
            color = Amber,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.width(4.dp))
        Image(
            painter = painterResource(R.drawable.energy),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
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
private fun GoalTitleDialog(
    heading: String,
    initial: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var draft by remember(initial) { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(heading, fontFamily = RubikFontFamily, fontWeight = FontWeight.Bold)
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
