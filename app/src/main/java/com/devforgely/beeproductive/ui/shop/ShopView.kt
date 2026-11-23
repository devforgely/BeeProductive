package com.devforgely.beeproductive.ui.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.R
import com.devforgely.beeproductive.core.components.SoftCard
import com.devforgely.beeproductive.core.theme.Amber
import com.devforgely.beeproductive.core.theme.Ink
import com.devforgely.beeproductive.core.theme.InkSoft
import com.devforgely.beeproductive.domain.model.CosmeticCatalog
import com.devforgely.beeproductive.domain.model.ShopOutcome
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.HoneyBalanceChip
import com.devforgely.beeproductive.ui.common.HoneyPillButton
import com.devforgely.beeproductive.ui.common.PaperPage

@Composable
fun ShopView(viewModel: HiveViewModel = hiltViewModel()) {
    val snapshot by viewModel.snapshot.collectAsState()
    var note by remember { mutableStateOf<String?>(null) }
    PaperPage {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Amber.copy(alpha = 0.18f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Bramble's Shop", style = MaterialTheme.typography.headlineSmall)
                Text("Trade honey for a little something sweet.", color = InkSoft)
            }
            HoneyBalanceChip(snapshot.balance)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.bear),
                contentDescription = "Bramble",
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White),
            )
            Spacer(Modifier.width(12.dp))
            SoftCard(Modifier.weight(1f)) {
                Text("Bramble", color = Amber, fontWeight = FontWeight.Bold)
                Text(
                    "Little wings, big taste. Honey spends just as sweetly as it sounds.",
                    color = Ink,
                )
            }
        }
        note?.let { Text(it, color = InkSoft) }
        CosmeticCatalog.all.forEach { item ->
            val owned = item.id in snapshot.ownedIds
            SoftCard {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${item.kind.name.lowercase().replaceFirstChar { char -> char.uppercase() }} · ${item.honeyCost} honey",
                    color = InkSoft,
                )
                Spacer(Modifier.height(10.dp))
                HoneyPillButton(
                    text = if (owned) "Wear it" else "Trade ${item.honeyCost} honey",
                    onClick = {
                        viewModel.buy(item.id) { outcome ->
                            note = when (outcome) {
                                ShopOutcome.Purchased -> "${item.name} is yours."
                                ShopOutcome.TooExpensive -> "A little more honey and it is yours."
                                ShopOutcome.Missing -> "That item has flown off."
                            }
                        }
                    },
                )
            }
        }
    }
}
