package com.devforgely.beeproductive.ui.bee

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.core.components.SoftCard
import com.devforgely.beeproductive.core.theme.Amber
import com.devforgely.beeproductive.core.theme.Ink
import com.devforgely.beeproductive.core.theme.InkSoft
import com.devforgely.beeproductive.domain.model.CosmeticCatalog
import com.devforgely.beeproductive.domain.model.RepairOutcome
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.HoneyPillButton
import com.devforgely.beeproductive.ui.common.PaperPage

@Composable
fun BeeView(viewModel: HiveViewModel = hiltViewModel()) {
    val snapshot by viewModel.snapshot.collectAsState()
    val prefs by viewModel.prefs.collectAsState()
    val bee = snapshot.bee
    val beeName = bee?.name ?: "Your bee"
    var mendNote by remember { mutableStateOf<String?>(null) }
    val owned = CosmeticCatalog.all.filter { it.id in snapshot.ownedIds }
    val plan = snapshot.repairPlan
    PaperPage {
        Text("Passport", style = MaterialTheme.typography.headlineMedium)
        SoftCard {
            Text(beeName, style = MaterialTheme.typography.headlineSmall)
            if (prefs.pronoun.isNotBlank()) {
                Text(prefs.pronoun, color = InkSoft)
            }
            Spacer(Modifier.height(12.dp))
            PassportRow("Trait", bee?.personality?.label ?: "Still settling")
            PassportRow("Human", prefs.userName.ifBlank { "You" })
            bee?.personality?.let {
                Spacer(Modifier.height(8.dp))
                Text(it.blurb, color = InkSoft)
            }
        }
        SoftCard {
            val days = snapshot.streak
            Text(
                text = if (days == 1) "1-day streak" else "$days-day streak",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = when {
                    plan != null -> "Yesterday's streak stopped."
                    days == 0 -> "No streak yet. One finished day starts it."
                    else -> "The comb is still warm."
                },
                color = InkSoft,
                modifier = Modifier.padding(top = 4.dp),
            )
            if (plan != null) {
                Spacer(Modifier.height(12.dp))
                HoneyPillButton(
                    text = "Mend streak · ${plan.honey} honey",
                    onClick = {
                        viewModel.repair { outcome ->
                            mendNote = when (outcome) {
                                RepairOutcome.Repaired -> "Yesterday is mended."
                                RepairOutcome.AlreadyUsed -> "This stage already used its mend."
                                RepairOutcome.NothingToMend -> "Nothing is torn."
                                RepairOutcome.NoCycle -> "There is no active hive to mend."
                            }
                        }
                    },
                )
            }
            mendNote?.let {
                Text(it, color = Amber, modifier = Modifier.padding(top = 8.dp))
            }
        }
        Text("$beeName's collection", style = MaterialTheme.typography.titleLarge)
        if (owned.isEmpty()) {
            SoftCard {
                Text("Nothing tucked away yet.", color = Ink, fontWeight = FontWeight.SemiBold)
                Text(
                    "Bramble's shop keeps a few sweet things for honey.",
                    color = InkSoft,
                )
            }
        } else {
            owned.forEach { item ->
                SoftCard {
                    Text(item.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        item.kind.name.lowercase().replaceFirstChar { it.uppercase() },
                        color = InkSoft,
                    )
                }
            }
        }
    }
}

@Composable
private fun PassportRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(
            text = label.uppercase(),
            modifier = Modifier.weight(0.4f),
            color = InkSoft,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = value,
            modifier = Modifier.weight(0.6f),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
