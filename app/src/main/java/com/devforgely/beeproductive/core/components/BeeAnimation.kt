package com.devforgely.beeproductive.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.airbnb.lottie.LottieProperty
import com.devforgely.beeproductive.core.theme.BlushDeep
import com.devforgely.beeproductive.domain.model.BeeAccent
import com.devforgely.beeproductive.domain.model.BeeClip

fun BeeClip.assetName(): String = when (this) {
    BeeClip.IDLE -> "bee_idle.json"
    BeeClip.FLUTTER -> "bee_flutter.json"
    BeeClip.CELEBRATE -> "bee_celebrate.json"
    BeeClip.FORAGE -> "bee_forage.json"
    BeeClip.WELCOME -> "welcome_landing.json"
}

@Composable
fun BeeAnimation(
    clip: BeeClip,
    accent: BeeAccent,
    speed: Float,
    modifier: Modifier = Modifier.size(200.dp),
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(clip.assetName()))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = speed,
    )
    val accentColor = Color(accent.argb)
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = accentColor.toArgb(),
            "backpack",
            "**",
            "backpackFill",
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = BlushDeep.toArgb(),
            "**",
            "cheekFill",
        ),
    )
    if (composition == null) {
        FallbackBee(accentColor, modifier)
    } else {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            dynamicProperties = dynamicProperties,
            modifier = modifier,
        )
    }
}

@Composable
fun LottieLoop(
    asset: String,
    modifier: Modifier = Modifier,
    iterations: Int = LottieConstants.IterateForever,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(asset))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier,
    )
}

@Composable
private fun FallbackBee(accent: Color, modifier: Modifier) {
    Canvas(modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(Color(0x33000000), size.minDimension * 0.18f, center.copy(y = center.y + size.minDimension * 0.32f))
        rotate(-18f, center.copy(x = center.x - 40f)) {
            drawOval(Color(0xB3B9D7F2), topLeft = center.copy(x = center.x - 90f, y = center.y - 40f), size = androidx.compose.ui.geometry.Size(70f, 36f))
        }
        rotate(18f, center.copy(x = center.x + 40f)) {
            drawOval(Color(0xB3B9D7F2), topLeft = center.copy(x = center.x + 20f, y = center.y - 40f), size = androidx.compose.ui.geometry.Size(70f, 36f))
        }
        drawOval(accent, topLeft = center.copy(x = center.x + 28f, y = center.y - 10f), size = androidx.compose.ui.geometry.Size(36f, 44f))
        scale(1f, 0.86f, center) {
            drawCircle(Color(0xFFF6C23A), size.minDimension * 0.28f, center)
        }
        drawOval(Color(0xFF5A3A22), topLeft = center.copy(x = center.x - 48f, y = center.y - 8f), size = androidx.compose.ui.geometry.Size(96f, 14f))
        drawCircle(Color.White, 10f, center.copy(x = center.x - 16f, y = center.y - 12f))
        drawCircle(Color.White, 10f, center.copy(x = center.x + 16f, y = center.y - 12f))
        drawCircle(Color(0xFF3A2414), 4f, center.copy(x = center.x - 14f, y = center.y - 10f))
        drawCircle(Color(0xFF3A2414), 4f, center.copy(x = center.x + 18f, y = center.y - 10f))
        drawCircle(BlushDeep, 7f, center.copy(x = center.x - 28f, y = center.y + 10f))
        drawCircle(BlushDeep, 7f, center.copy(x = center.x + 28f, y = center.y + 10f))
    }
}
