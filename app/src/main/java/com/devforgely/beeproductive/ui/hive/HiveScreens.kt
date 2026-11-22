package com.devforgely.beeproductive.ui.hive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.core.components.CombCluster
import com.devforgely.beeproductive.core.components.SoftCard
import com.devforgely.beeproductive.core.theme.HoneyGold
import com.devforgely.beeproductive.core.theme.Ink
import com.devforgely.beeproductive.core.theme.InkSoft
import com.devforgely.beeproductive.core.theme.SageDeep
import com.devforgely.beeproductive.domain.model.HiveProgress
import com.devforgely.beeproductive.domain.model.HoneyRules
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.HoneyBalanceChip
import com.devforgely.beeproductive.ui.common.PaperPage
import com.devforgely.beeproductive.ui.quest.GoalHex
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

@Composable
fun HiveScreen(viewModel: HiveViewModel = hiltViewModel()) {
    val snapshot by viewModel.snapshot.collectAsState()
    val prefs by viewModel.prefs.collectAsState()
    val filled = snapshot.cells.count { it.fill >= 1f }
    val level = HiveProgress.level(filled)
    val hiveTitle = prefs.userName.trim().ifBlank { "Your" }.let { name ->
        if (name == "Your") "Your hive" else "$name's hive"
    }
    val monday = snapshot.today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val week = (0..6).map { monday.plusDays(it.toLong()) }
    PaperPage {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(hiveTitle, style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Level $level · ${HiveProgress.title(level)}",
                    color = InkSoft,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            HoneyBalanceChip(snapshot.balance)
        }
        SoftCard {
            CombCluster(snapshot.cells)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                HiveStat("$filled", "cells filled")
                HiveStat("+${snapshot.weekHoney / HoneyRules.CELL_CAPACITY}", "this week", accent = true)
                HiveStat("${HiveProgress.cellsToNext(filled)}", "to level ${level + 1}")
            }
        }
        SoftCard {
            Text("Cells this week", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                week.forEach { day ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        GoalHex(filled = snapshot.honeyOn(day) > 0, color = HoneyGold)
                        Text(
                            day.dayOfWeek.name.take(1),
                            color = InkSoft,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }
        SoftCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Next unlock: ${HiveProgress.title(level + 1)}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Text("Lv ${level + 1}", color = InkSoft, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { HiveProgress.progressToNext(filled) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
            )
        }
    }
}

@Composable
private fun HiveStat(value: String, label: String, accent: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = if (accent) SageDeep else Ink,
            textAlign = TextAlign.Center,
        )
        Text(label, color = InkSoft, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
    }
}
