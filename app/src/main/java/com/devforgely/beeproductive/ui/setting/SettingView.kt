package com.devforgely.beeproductive.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.core.components.SoftCard
import com.devforgely.beeproductive.core.theme.InkSoft
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.HiveBackButton
import com.devforgely.beeproductive.ui.common.HoneyPillButton
import com.devforgely.beeproductive.ui.common.PaperPage

@Composable
fun SettingView(
    onBack: () -> Unit,
    onSupport: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val prefs by viewModel.prefs.collectAsState()
    PaperPage {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HiveBackButton(onBack)
            Text(
                text = "Settings",
                modifier = Modifier.padding(start = 12.dp),
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        SettingToggle(
            title = "Dusk garden",
            body = "A warmer dark meadow for late flights.",
            checked = prefs.dusk,
            onChecked = viewModel::setDusk,
        )
        SettingToggle(
            title = "Morning whisper",
            body = "A gentle invitation around 8.",
            checked = prefs.morningNotes,
            onChecked = viewModel::setMorning,
        )
        SettingToggle(
            title = "Evening tuck-in",
            body = "An optional note around 8 in the evening.",
            checked = prefs.eveningNotes,
            onChecked = viewModel::setEvening,
        )
        SoftCard {
            Text("Bee Supporter", style = MaterialTheme.typography.titleMedium)
            Text(
                if (prefs.supporter) {
                    "Thank you for keeping the garden growing. Another gift is welcome, and still optional."
                } else {
                    "Every feature is free. A gift here supports the developer and unlocks nothing extra."
                },
                color = InkSoft,
            )
            Spacer(Modifier.height(12.dp))
            HoneyPillButton(
                text = if (prefs.supporter) "Send another gift" else "Support the hive",
                onClick = onSupport,
            )
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    body: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
) {
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(body, color = InkSoft)
            }
            Switch(checked = checked, onCheckedChange = onChecked)
        }
    }
}
