package com.devforgely.beeproductive.ui.celebrate

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.devforgely.beeproductive.core.theme.Blush
import com.devforgely.beeproductive.core.theme.Cream
import com.devforgely.beeproductive.core.theme.Ink
import com.devforgely.beeproductive.core.theme.InkSoft
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.HoneyPillButton

private const val CelebrateAsset = "lottie/bee_celebrate.lottie"

@Composable
fun CelebrationView(
    honey: Int,
    cells: Int,
    onDone: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    val beeName = snapshot.bee?.name ?: "Your bee"
    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Blush.copy(alpha = 0.55f), Cream)))
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "ALL GOALS COMPLETE",
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.7f))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            color = Color(0xFFD46A8C),
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Sweet success!",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "$beeName is so proud of you, honey.",
            color = InkSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp),
        )
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            CelebrateBee()
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .padding(vertical = 18.dp),
        ) {
            Reward("+$honey", "Honey", Modifier.weight(1f))
            Reward("+$cells", "Hive cells", Modifier.weight(1f))
        }
        Text(
            text = "Honey and hive cells, tucked into the comb.",
            color = InkSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 14.dp, bottom = 18.dp),
        )
        HoneyPillButton(text = "Bee-autiful!", onClick = onDone)
    }
}

@Composable
private fun Reward(value: String, label: String, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Ink)
        Text(label, color = InkSoft, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun CelebrateBee() {
    val context = LocalContext.current
    val available = remember { assetExists(context, CelebrateAsset) }
    if (!available) {
        Spacer(Modifier.height(180.dp))
        return
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(CelebrateAsset))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(240.dp),
    )
}

private fun assetExists(context: Context, path: String): Boolean {
    return runCatching {
        context.assets.open(path).close()
        true
    }.getOrDefault(false)
}
