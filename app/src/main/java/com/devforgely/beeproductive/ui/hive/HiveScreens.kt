package com.devforgely.beeproductive.ui.hive

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.devforgely.beeproductive.core.components.GardenScaffold
import com.devforgely.beeproductive.core.components.HabitCard
import com.devforgely.beeproductive.core.components.HiveScene
import com.devforgely.beeproductive.core.components.MoodRow
import com.devforgely.beeproductive.core.components.PrimaryBeeButton
import com.devforgely.beeproductive.core.components.QuietButton
import com.devforgely.beeproductive.core.components.SoftCard
import com.devforgely.beeproductive.core.components.SpeechBubble
import com.devforgely.beeproductive.core.components.StageChip
import com.devforgely.beeproductive.domain.model.BeeClip
import com.devforgely.beeproductive.domain.model.ForageOutcome
import com.devforgely.beeproductive.domain.model.HabitAddResult
import com.devforgely.beeproductive.domain.model.HiveCopy
import com.devforgely.beeproductive.domain.model.Ritual
import com.devforgely.beeproductive.ui.HiveViewModel

@Composable
fun HiveScreen(
    onForage: () -> Unit,
    onFocus: () -> Unit,
    onReflect: () -> Unit,
    onEvening: () -> Unit,
    onWeekly: () -> Unit,
    onPhase: (Int) -> Unit,
    onHarvest: () -> Unit,
    onNewCycle: () -> Unit,
    onSettings: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    var offered by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(snapshot.ritual) {
        val ritual = snapshot.ritual
        if (!offered && ritual != null) {
            offered = true
            when (ritual) {
                Ritual.PHASE_30 -> onPhase(30)
                Ritual.PHASE_60 -> onPhase(60)
                Ritual.HARVEST -> onHarvest()
            }
        }
    }
    val bee = snapshot.bee
    val cycle = snapshot.cycle
    GardenScaffold(bee?.themeId) {
        item { SpeechBubble(snapshot.greeting) }
        item { MoodRow(snapshot.mood) { viewModel.checkIn(it) } }
        if (cycle == null || bee == null) {
            item {
                SoftCard {
                    Text("The queen cell is waiting.", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("Plant a new 90-day North Star whenever you are ready.")
                    Spacer(Modifier.height(12.dp))
                    PrimaryBeeButton("Plant a new goal", onClick = onNewCycle)
                }
            }
        } else {
            item { StageChip(cycle.stage.label, cycle.stage.dayRange) }
            item {
                HiveScene(
                    northStar = cycle.northStar,
                    queenGlow = snapshot.queenGlow,
                    cells = snapshot.cells,
                    goals = snapshot.goals,
                    accent = bee.accent,
                    clip = HiveCopy.clip(bee.personality, snapshot.mood, celebrating = false, foraging = false),
                    speed = HiveCopy.speed(bee.personality, snapshot.mood),
                    outfitId = bee.outfitId,
                    decorationId = bee.decorationId,
                )
            }
            item {
                SoftCard {
                    Text(cycle.stage.summary, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("Day ${cycle.dayIndex} of 90", style = MaterialTheme.typography.titleMedium)
                    Text("${snapshot.completedToday} of ${snapshot.dailyGoal} habits today")
                }
            }
            if (snapshot.goalMet && !snapshot.foragedToday) {
                item {
                    PrimaryBeeButton("Send ${bee.name} to forage", onClick = onForage)
                }
            }
        }
        item {
            Column {
                QuietButton("Focus timer", onClick = onFocus)
                Spacer(Modifier.height(8.dp))
                QuietButton("Reflect", onClick = onReflect)
                Spacer(Modifier.height(8.dp))
                QuietButton("Evening review", onClick = onEvening)
                Spacer(Modifier.height(8.dp))
                QuietButton("This week", onClick = onWeekly)
                Spacer(Modifier.height(8.dp))
                QuietButton("Settings", onClick = onSettings)
            }
        }
    }
}

@Composable
fun TodayScreen(
    onForage: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    var adding by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    GardenScaffold(snapshot.bee?.themeId) {
        item {
            Text("Today", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Small checks fill the comb. ${snapshot.completedToday} of ${snapshot.dailyGoal} so far.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item { MoodRow(snapshot.mood) { viewModel.checkIn(it) } }
        items(snapshot.activeHabits.size) { index ->
            val habit = snapshot.activeHabits[index]
            HabitCard(habit.title, habit.doneToday) {
                viewModel.completeHabit(habit.id)
            }
        }
        item {
            message?.let {
                Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
            }
            QuietButton("Add a habit") { adding = true }
        }
        if (snapshot.goalMet) {
            item {
                PrimaryBeeButton(
                    text = if (snapshot.foragedToday) "The meadow visit is done" else "Send ${snapshot.bee?.name ?: "your bee"} to forage",
                    onClick = onForage,
                    enabled = !snapshot.foragedToday,
                )
            }
        }
    }
    if (adding) {
        AlertDialog(
            onDismissRequest = { adding = false },
            title = { Text("A new habit") },
            text = {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    label = { Text("What will you repeat?") },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addHabit(draft) { result ->
                        when (result) {
                            HabitAddResult.Added -> {
                                draft = ""
                                adding = false
                            }
                            HabitAddResult.NoCycle -> message = "Plant a hive before adding habits."
                        }
                    }
                }) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { adding = false }) { Text("Close") } },
        )
    }
}

@Composable
fun MeadowScreen(viewModel: HiveViewModel = hiltViewModel()) {
    val snapshot by viewModel.snapshot.collectAsState()
    var flight by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<ForageOutcome?>(null) }
    GardenScaffold(snapshot.bee?.themeId) {
        item {
            Text("Meadow", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Flower patches are your mini-goals. Bloom them when they are real.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (flight) {
            item {
                HiveScene(
                    northStar = snapshot.cycle?.northStar ?: "",
                    queenGlow = snapshot.queenGlow,
                    cells = snapshot.cells,
                    goals = snapshot.goals,
                    accent = snapshot.bee?.accent ?: com.devforgely.beeproductive.domain.model.BeeAccent.HONEY,
                    clip = BeeClip.FORAGE,
                    speed = 1.2f,
                    outfitId = snapshot.bee?.outfitId,
                    decorationId = snapshot.bee?.decorationId,
                )
            }
        }
        items(snapshot.goals.size) { index ->
            val goal = snapshot.goals[index]
            SoftCard {
                Text(goal.title, style = MaterialTheme.typography.titleMedium)
                Text(goal.stage.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (goal.weekly) Text("This week's flower")
                Spacer(Modifier.height(8.dp))
                if (!goal.bloomed) {
                    PrimaryBeeButton(
                        text = "Bloom",
                        onClick = { viewModel.bloomGoal(goal.id) },
                    )
                } else {
                    Text("In bloom")
                }
            }
        }
        item {
            PrimaryBeeButton(
                text = if (snapshot.foragedToday) "Back in the hive" else "Forage",
                onClick = {
                    flight = true
                    viewModel.forage { outcome ->
                        result = outcome
                        flight = outcome is ForageOutcome.Found
                    }
                },
                enabled = snapshot.goalMet && !snapshot.foragedToday,
            )
            result?.let { outcome ->
                Spacer(Modifier.height(12.dp))
                SoftCard {
                    when (outcome) {
                        is ForageOutcome.Found -> {
                            Text(outcome.title, style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(6.dp))
                            Text(outcome.body)
                        }
                        ForageOutcome.NotReady -> Text("A few more habits and the wings are ready.")
                        ForageOutcome.AlreadyWent -> Text("Today's flight already came home.")
                        ForageOutcome.NoCycle -> Text("Plant a hive to open the meadow.")
                    }
                }
            }
        }
    }
}
