package com.devforgely.beeproductive.ui.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.core.theme.InkSoft
import com.devforgely.beeproductive.core.theme.RubikFontFamily
import com.devforgely.beeproductive.domain.model.HoneyRules
import com.devforgely.beeproductive.domain.model.LifeArea
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.HiveBackButton
import com.devforgely.beeproductive.ui.common.HoneyBalanceChip
import com.devforgely.beeproductive.ui.common.HoneyPillButton
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

private const val FocusSeconds = 25 * 60

@Composable
fun FocusScreen(
    onBack: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    val areas = snapshot.questAreas
    var selected by remember(areas) { mutableStateOf(areas.firstOrNull()) }
    var menuOpen by remember { mutableStateOf(false) }
    var remaining by remember { mutableIntStateOf(FocusSeconds) }
    var running by remember { mutableStateOf(false) }
    var focusedSeconds by remember { mutableIntStateOf(0) }

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

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HiveBackButton(onBack)
            Spacer(Modifier.weight(1f))
            HoneyBalanceChip(snapshot.balance)
        }
        Spacer(Modifier.weight(1f))
        AreaChip(
            area = selected,
            expanded = menuOpen,
            areas = areas,
            onOpen = { menuOpen = true },
            onDismiss = { menuOpen = false },
            onSelect = {
                selected = it
                menuOpen = false
            },
        )
        Text(
            text = "%02d:%02d".format(remaining / 60, remaining % 60),
            modifier = Modifier.padding(top = 18.dp, bottom = 22.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = RubikFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 72.sp,
        )
        HoneyPillButton(
            text = if (running) "Collecting…" else "Start collecting",
            enabled = !running,
            onClick = {
                remaining = FocusSeconds
                focusedSeconds = 0
                running = true
            },
        )
        Text(
            text = "+${HoneyRules.FOCUS_HONEY} honey every ${HoneyRules.FOCUS_BLOCK_MINUTES} minutes focused",
            modifier = Modifier.padding(top = 14.dp, bottom = 8.dp),
            color = InkSoft,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun AreaChip(
    area: LifeArea?,
    expanded: Boolean,
    areas: List<LifeArea>,
    onOpen: () -> Unit,
    onDismiss: () -> Unit,
    onSelect: (LifeArea) -> Unit,
) {
    Box {
        Text(
            text = area?.label ?: "Focus",
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .clickable(enabled = areas.isNotEmpty(), onClick = onOpen)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
        )
        DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
            areas.forEach { choice ->
                DropdownMenuItem(
                    text = { Text(choice.label) },
                    onClick = { onSelect(choice) },
                )
            }
        }
    }
}
