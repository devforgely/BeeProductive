package com.devforgely.beeproductive.ui.reflect

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.devforgely.beeproductive.domain.model.BeeClip
import com.devforgely.beeproductive.domain.model.CycleStage
import com.devforgely.beeproductive.domain.model.HiveCopy
import com.devforgely.beeproductive.domain.model.JournalKind
import com.devforgely.beeproductive.ui.HiveViewModel
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.size

@Composable
fun ReflectScreen(
    onReflection: () -> Unit,
    onGratitude: () -> Unit,
    onEvening: () -> Unit,
    onFocus: () -> Unit,
    onFocusForage: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    GardenScaffold(snapshot.bee?.themeId) {
        item { Text("A softer part of the day", style = MaterialTheme.typography.headlineMedium) }
        item { QuietButton("Reflection", onClick = onReflection) }
        item { QuietButton("Gratitude", onClick = onGratitude) }
        item { QuietButton("Evening review", onClick = onEvening) }
        item { QuietButton("Focus timer", onClick = onFocus) }
        item { QuietButton("Focus forage", onClick = onFocusForage) }
    }
}

@Composable
fun ReflectionScreen(viewModel: HiveViewModel = hiltViewModel()) {
    val snapshot by viewModel.snapshot.collectAsState()
    val prompts = HiveCopy.reflectionPrompts(snapshot.cycle?.stage ?: CycleStage.POLLINATING)
    JournalEditor(
        title = "Reflection",
        prompts = prompts,
        kind = JournalKind.REFLECTION,
        themeId = snapshot.bee?.themeId,
        viewModel = viewModel,
    )
}

@Composable
fun GratitudeScreen(viewModel: HiveViewModel = hiltViewModel()) {
    val snapshot by viewModel.snapshot.collectAsState()
    JournalEditor(
        title = "Gratitude",
        prompts = listOf(HiveCopy.gratitudePrompt()),
        kind = JournalKind.GRATITUDE,
        themeId = snapshot.bee?.themeId,
        viewModel = viewModel,
    )
}

@Composable
fun EveningScreen(viewModel: HiveViewModel = hiltViewModel()) {
    val snapshot by viewModel.snapshot.collectAsState()
    JournalEditor(
        title = "Tuck the day in",
        prompts = listOf("One light intention for tomorrow"),
        kind = JournalKind.EVENING,
        themeId = snapshot.bee?.themeId,
        viewModel = viewModel,
        preface = snapshot.activeHabits.filter { it.doneToday }.joinToString { it.title }
            .ifBlank { "Nothing was checked yet, and that is allowed." },
    )
}

@Composable
fun FocusForageScreen(
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    val steps = HiveCopy.focusForageSteps()
    var index by remember { mutableIntStateOf(0) }
    var body by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }
    GardenScaffold(snapshot.bee?.themeId) {
        item { Text("Focus forage", style = MaterialTheme.typography.headlineMedium) }
        item { Text(steps[index], style = MaterialTheme.typography.titleLarge) }
        item {
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("A few words") },
            )
        }
        item {
            PrimaryBeeButton(if (index == steps.lastIndex) "Bring it home" else "Next flower") {
                viewModel.saveJournal(JournalKind.FOCUS_FORAGE, steps[index], body)
                body = ""
                if (index == steps.lastIndex) saved = true else index += 1
            }
            if (saved) Text("The forage is written. The hive can rest.", modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun FocusScreen(viewModel: HiveViewModel = hiltViewModel()) {
    val snapshot by viewModel.snapshot.collectAsState()
    var minutes by remember { mutableIntStateOf(15) }
    var remaining by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(running, remaining) {
        if (running && remaining > 0) {
            delay(1000)
            remaining -= 1
            if (remaining == 0) {
                running = false
                viewModel.grantFocus { granted ->
                    note = if (granted) "A little nectar for the flight." else "Today's focus nectar is already in the comb."
                }
            }
        }
    }
    GardenScaffold(snapshot.bee?.themeId) {
        item {
            Text("Focus flight", style = MaterialTheme.typography.headlineMedium)
            BeeAnimation(
                clip = if (running) BeeClip.FLUTTER else BeeClip.IDLE,
                accent = snapshot.bee?.accent ?: com.devforgely.beeproductive.domain.model.BeeAccent.HONEY,
                speed = if (running) 1.3f else 0.8f,
                modifier = Modifier.size(180.dp),
            )
        }
        item {
            val shown = if (running || remaining > 0) remaining else minutes * 60
            Text("${shown / 60}:${(shown % 60).toString().padStart(2, '0')}", style = MaterialTheme.typography.headlineLarge)
            note?.let { Text(it) }
        }
        item {
            Row {
                listOf(5, 15, 25).forEach { choice ->
                    QuietButton(
                        "$choice min",
                        Modifier.weight(1f).padding(horizontal = 4.dp),
                    ) { minutes = choice }
                }
            }
        }
        item {
            PrimaryBeeButton(if (running) "Flying…" else "Take off", onClick = {
                if (!running) {
                    remaining = minutes * 60
                    running = true
                }
            }, enabled = !running)
        }
    }
}

@Composable
private fun JournalEditor(
    title: String,
    prompts: List<String>,
    kind: JournalKind,
    themeId: String?,
    viewModel: HiveViewModel,
    preface: String? = null,
) {
    var promptIndex by remember { mutableIntStateOf(0) }
    var body by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }
    GardenScaffold(themeId) {
        item { Text(title, style = MaterialTheme.typography.headlineMedium) }
        preface?.let { item { SoftCard { Text(it) } } }
        item { Text(prompts[promptIndex], style = MaterialTheme.typography.titleLarge) }
        item {
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Write a little") },
            )
        }
        item {
            if (prompts.size > 1) {
                QuietButton("Another prompt") {
                    promptIndex = (promptIndex + 1) % prompts.size
                }
                Spacer(Modifier.height(8.dp))
            }
            PrimaryBeeButton("Keep this") {
                viewModel.saveJournal(kind, prompts[promptIndex], body)
                saved = true
                body = ""
            }
            if (saved) Text("Tucked into the hive.", modifier = Modifier.padding(top = 8.dp))
        }
    }
}
