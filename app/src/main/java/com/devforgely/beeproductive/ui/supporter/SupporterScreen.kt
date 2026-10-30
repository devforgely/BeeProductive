package com.devforgely.beeproductive.ui.supporter

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.core.components.BeeAnimation
import com.devforgely.beeproductive.core.components.ChoiceCard
import com.devforgely.beeproductive.core.components.LottieLoop
import com.devforgely.beeproductive.core.components.MeadowBackground
import com.devforgely.beeproductive.core.components.PrimaryBeeButton
import com.devforgely.beeproductive.core.components.QuietButton
import com.devforgely.beeproductive.data.repository.BillingEvent
import com.devforgely.beeproductive.domain.model.BeeAccent
import com.devforgely.beeproductive.domain.model.BeeClip
import com.devforgely.beeproductive.domain.model.BeePersonality
import com.devforgely.beeproductive.domain.model.HiveCopy
import com.devforgely.beeproductive.domain.model.OfferLoad
import com.devforgely.beeproductive.ui.HiveViewModel

@Composable
fun SupporterScreen(
    onClose: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    val prefs by viewModel.prefs.collectAsState()
    val offers by viewModel.offers.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var selected by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.billingEvents.collect { event ->
            when (event) {
                BillingEvent.Purchased -> snackbar.showSnackbar("Thank you. The keeper felt that.")
                BillingEvent.Canceled -> snackbar.showSnackbar("No gift sent. The hive is unchanged.")
                is BillingEvent.Failed -> snackbar.showSnackbar(event.message)
            }
        }
    }
    val bee = snapshot.bee
    Box(Modifier.fillMaxSize()) {
        MeadowBackground(bee?.themeId)
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            LottieLoop("honey_drip.json", Modifier.height(90.dp).fillMaxWidth())
            BeeAnimation(
                clip = BeeClip.IDLE,
                accent = bee?.accent ?: BeeAccent.HONEY,
                speed = HiveCopy.speed(bee?.personality ?: BeePersonality.CALM, snapshot.mood),
                modifier = Modifier.size(180.dp),
            )
            Text(
                "Bee Supporter",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Every feature in the hive is free. If it has been good to you, you can leave a gift for the developer. A gift does not unlock anything extra.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (prefs.supporter) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "You have already supported this hive. Another gift is welcome, and still optional.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(16.dp))
            when (val load = offers) {
                OfferLoad.Loading -> Text("Asking the Play Store for gifts…")
                is OfferLoad.Unavailable -> {
                    Text(load.message, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    QuietButton("Try again") { viewModel.retryOffers() }
                }
                is OfferLoad.Ready -> {
                    load.tiers.forEach { tier ->
                        ChoiceCard(
                            title = "${tier.title}  ${tier.formattedPrice}",
                            body = tier.blurb,
                            selected = selected == tier.productId,
                            onClick = { selected = tier.productId },
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    PrimaryBeeButton(
                        text = "Send this gift",
                        enabled = selected != null,
                        onClick = {
                            val productId = selected
                            val activity = context as? Activity
                            if (productId != null && activity != null && !viewModel.launchPurchase(activity, productId)) {
                                viewModel.retryOffers()
                            }
                        },
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            QuietButton("Not today", onClick = onClose)
            Spacer(Modifier.height(24.dp))
        }
        SnackbarHost(snackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
