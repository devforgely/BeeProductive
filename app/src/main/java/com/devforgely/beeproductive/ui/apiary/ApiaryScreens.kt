package com.devforgely.beeproductive.ui.apiary

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.core.components.BeeAnimation
import com.devforgely.beeproductive.core.components.GardenScaffold
import com.devforgely.beeproductive.core.components.PrimaryBeeButton
import com.devforgely.beeproductive.core.components.QuietButton
import com.devforgely.beeproductive.core.components.SoftCard
import com.devforgely.beeproductive.domain.model.BeeAccent
import com.devforgely.beeproductive.domain.model.BeeClip
import com.devforgely.beeproductive.domain.model.CosmeticCatalog
import com.devforgely.beeproductive.domain.model.HarvestChoice
import com.devforgely.beeproductive.domain.model.HiveCopy
import com.devforgely.beeproductive.domain.model.JournalKind
import com.devforgely.beeproductive.domain.model.LifeArea
import com.devforgely.beeproductive.domain.model.LifeAreaTemplates
import com.devforgely.beeproductive.domain.model.RepairOutcome
import com.devforgely.beeproductive.domain.model.ShopOutcome
import com.devforgely.beeproductive.ui.HiveViewModel
import androidx.compose.foundation.layout.size

@Composable
fun ApiaryScreen(
    onShop: () -> Unit,
    onInsights: () -> Unit,
    onSettings: () -> Unit,
    onSupport: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    var repairNote by remember { mutableStateOf<String?>(null) }
    val bee = snapshot.bee
    GardenScaffold(bee?.themeId) {
        item {
            Text("Apiary", style = MaterialTheme.typography.headlineMedium)
            Text("Jars you have already bottled, and the bee who made them.")
        }
        item {
            SoftCard {
                if (bee != null) {
                    BeeAnimation(
                        clip = BeeClip.IDLE,
                        accent = bee.accent,
                        speed = HiveCopy.speed(bee.personality, snapshot.mood),
                        modifier = Modifier.size(140.dp),
                    )
                    Text(bee.name, style = MaterialTheme.typography.headlineSmall)
                    Text("${bee.personality.label} · ${bee.accent.label}")
                }
                Text("${snapshot.balance} nectar on the shelf")
                Text("Streak ${snapshot.streak}")
            }
        }
        item {
            Column {
                QuietButton("Nectar shop", onClick = onShop)
                Spacer(Modifier.height(8.dp))
                QuietButton("Insights", onClick = onInsights)
                Spacer(Modifier.height(8.dp))
                QuietButton("Settings", onClick = onSettings)
                Spacer(Modifier.height(8.dp))
                QuietButton("Bee Supporter", onClick = onSupport)
            }
        }
        item {
            PrimaryBeeButton("Mend yesterday's streak") {
                viewModel.repair { outcome ->
                    repairNote = when (outcome) {
                        RepairOutcome.Repaired -> "Yesterday is mended. The habits stay as they were."
                        RepairOutcome.AlreadyUsed -> "This stage already used its mend."
                        RepairOutcome.NothingToMend -> "Nothing is torn. The comb is fine."
                        RepairOutcome.NoCycle -> "There is no active hive to mend."
                    }
                }
            }
            repairNote?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }
        }
        if (snapshot.jars.isEmpty()) {
            item { Text("The shelf is clear. The first jar arrives at the end of a stage.") }
        }
        items(snapshot.jars.size) { index ->
            val jar = snapshot.jars[index]
            SoftCard {
                Text(jar.title, style = MaterialTheme.typography.titleLarge)
                Text(jar.subtitle)
                Text(if (jar.kind == "HARVEST") "Harvest jar" else "Phase jar · day ${jar.dayMark}")
            }
        }
    }
}

@Composable
fun ShopScreen(
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    var note by remember { mutableStateOf<String?>(null) }
    GardenScaffold(snapshot.bee?.themeId) {
        item {
            Text("Nectar shop", style = MaterialTheme.typography.headlineMedium)
            Text("${snapshot.balance} nectar to spend")
            note?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }
        }
        items(CosmeticCatalog.all.size) { index ->
            val item = CosmeticCatalog.all[index]
            val owned = item.id in snapshot.ownedIds
            SoftCard {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text("${item.kind.name.lowercase().replaceFirstChar { it.uppercase() }} · ${item.nectarCost} nectar")
                Spacer(Modifier.height(8.dp))
                PrimaryBeeButton(
                    text = if (owned) "Wear it" else "Trade nectar",
                    onClick = {
                        viewModel.buy(item.id) { outcome ->
                            note = when (outcome) {
                                ShopOutcome.Purchased -> "${item.name} is yours."
                                ShopOutcome.TooExpensive -> "A little more nectar and it is yours."
                                ShopOutcome.Missing -> "That item has flown off."
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun InsightsScreen(
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    GardenScaffold(snapshot.bee?.themeId) {
        item { Text("Insights", style = MaterialTheme.typography.headlineMedium) }
        val day = snapshot.cycle?.dayIndex ?: 1
        val possible = (snapshot.activeHabits.size * day).coerceAtLeast(1)
        val done = snapshot.logs.count { it.completed }
        val rate = done.toFloat() / possible
        item {
            SoftCard {
                Text("Completion", style = MaterialTheme.typography.titleMedium)
                Text("${(rate * 100).toInt()}% of possible checks in this cycle")
                LinearProgressIndicator(progress = { rate.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            }
        }
        item {
            SoftCard {
                Text("Nectar earned", style = MaterialTheme.typography.titleMedium)
                Text("${snapshot.earnedThisCycle} in this cycle")
                Text("Today ${snapshot.todayNectar}")
            }
        }
        item {
            val moods = snapshot.checks.groupingBy { it.mood }.eachCount()
            SoftCard {
                Text("Mood", style = MaterialTheme.typography.titleMedium)
                if (moods.isEmpty()) {
                    Text("Check in a few times and this will fill.")
                } else {
                    moods.forEach { (mood, count) ->
                        Text("${mood.label} · $count")
                    }
                }
            }
        }
    }
}

@Composable
fun HarvestScreen(
    onDone: (HarvestChoice) -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    var pivot by remember { mutableStateOf(snapshot.cycle?.northStar.orEmpty()) }
    GardenScaffold(snapshot.bee?.themeId) {
        item {
            Text("The honey harvest", style = MaterialTheme.typography.headlineMedium)
            Text("Your colony is thriving. What's next?")
            Spacer(Modifier.height(8.dp))
            Text(snapshot.cycle?.northStar.orEmpty(), style = MaterialTheme.typography.titleLarge)
        }
        item {
            SoftCard {
                Text("Complete & Archive", style = MaterialTheme.typography.titleMedium)
                Text("Set this honeycomb and jar on the permanent shelf, then open the hive for a new goal.")
                Spacer(Modifier.height(8.dp))
                PrimaryBeeButton("Archive") {
                    viewModel.harvest(HarvestChoice.ARCHIVE, "") { onDone(HarvestChoice.ARCHIVE) }
                }
            }
        }
        item {
            SoftCard {
                Text("Loop & Elevate", style = MaterialTheme.typography.titleMedium)
                Text("Keep the same North Star and raise the habits for another 90 days.")
                Spacer(Modifier.height(8.dp))
                PrimaryBeeButton("Elevate") {
                    viewModel.harvest(HarvestChoice.LOOP, snapshot.cycle?.northStar.orEmpty()) {
                        onDone(HarvestChoice.LOOP)
                    }
                }
            }
        }
        item {
            SoftCard {
                Text("Pivot direction", style = MaterialTheme.typography.titleMedium)
                Text("Carry the theme forward, and adjust the focus.")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = pivot,
                    onValueChange = { pivot = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Adjusted North Star") },
                )
                Spacer(Modifier.height(8.dp))
                PrimaryBeeButton("Pivot") {
                    viewModel.harvest(HarvestChoice.PIVOT, pivot) { onDone(HarvestChoice.PIVOT) }
                }
            }
        }
    }
}

@Composable
fun PhaseScreen(
    dayMark: Int,
    onDone: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    var note by remember { mutableStateOf("") }
    val title = if (dayMark == 30) "The first comb is set" else "Nectar is flowing"
    GardenScaffold(snapshot.bee?.themeId) {
        item {
            Text(title, style = MaterialTheme.typography.headlineMedium)
            Text(HiveCopy.phaseReflection(dayMark))
        }
        item {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("A short reflection") },
            )
        }
        item {
            PrimaryBeeButton("Seal this jar") {
                viewModel.acknowledgePhase(dayMark, note, onDone)
            }
        }
    }
}

@Composable
fun NewCycleScreen(
    onDone: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    var area by remember { mutableStateOf(LifeArea.MIND_FOCUS) }
    var star by remember { mutableStateOf(LifeAreaTemplates.forArea(area).northStar) }
    var habits by remember { mutableStateOf(LifeAreaTemplates.forArea(area).starterHabits) }
    GardenScaffold(null) {
        item { Text("A new queen cell", style = MaterialTheme.typography.headlineMedium) }
        items(LifeArea.entries.size) { index ->
            val option = LifeArea.entries[index]
            QuietButton(option.label) {
                area = option
                star = LifeAreaTemplates.forArea(option).northStar
                habits = LifeAreaTemplates.forArea(option).starterHabits
            }
        }
        item {
            OutlinedTextField(star, { star = it }, modifier = Modifier.fillMaxWidth(), label = { Text("North Star") })
        }
        items(habits.size) { index ->
            OutlinedTextField(
                value = habits[index],
                onValueChange = { value ->
                    habits = habits.toMutableList().also { it[index] = value }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Habit ${index + 1}") },
            )
        }
        item {
            PrimaryBeeButton("Begin the next 90") {
                viewModel.plantFresh(star, area, habits, "A fresh season.", onDone)
            }
        }
    }
}

@Composable
fun WeeklyScreen(viewModel: HiveViewModel = hiltViewModel()) {
    val snapshot by viewModel.snapshot.collectAsState()
    var note by remember(snapshot.weeklyNote) { mutableStateOf(snapshot.weeklyNote.orEmpty()) }
    var goal by remember { mutableStateOf("") }
    val weekLogs = snapshot.logs.count { log ->
        val day = snapshot.cycle?.let { cycle ->
            java.time.temporal.ChronoUnit.DAYS.between(cycle.startDate, log.day).toInt() + 1
        } ?: 0
        val week = snapshot.cycle?.let { com.devforgely.beeproductive.domain.model.CycleCalendar.weekIndex(it.dayIndex) }
        week != null && day > 0 && com.devforgely.beeproductive.domain.model.CycleCalendar.weekIndex(day) == week && log.completed
    }
    GardenScaffold(snapshot.bee?.themeId) {
        item {
            Text("This week", style = MaterialTheme.typography.headlineMedium)
            Text("$weekLogs habits sealed · ${snapshot.todayNectar} nectar today · comb ${snapshot.cells.count { it.fill >= 1f }} full cells")
        }
        item {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("What should change?") },
            )
        }
        item {
            OutlinedTextField(
                value = goal,
                onValueChange = { goal = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("One weekly flower") },
            )
        }
        items(snapshot.habits.size) { index ->
            val habit = snapshot.habits[index]
            if (!habit.active) return@items
            SoftCard {
                Text(habit.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                QuietButton("Retire") { viewModel.retireHabit(habit.id) }
            }
        }
        item {
            PrimaryBeeButton("Save the week") {
                viewModel.saveWeekly(note, goal)
                goal = ""
            }
        }
    }
}
