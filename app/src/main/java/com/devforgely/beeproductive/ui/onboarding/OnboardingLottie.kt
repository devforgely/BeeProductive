package com.devforgely.beeproductive.ui.onboarding

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.devforgely.beeproductive.domain.model.BeePersonality
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream

internal const val HiveSwingLottieAsset = "lottie/hive_swing.lottie"
internal const val BeeTraitsLottieAsset = "lottie/bee_traits.lottie"
internal const val BeeIdleAnimationId = "bee_idle"
internal const val BeeWelcomeAnimationId = "bee_welcome"

internal fun beeTraitAnimationId(personality: BeePersonality): String = when (personality) {
    BeePersonality.COMPASSIONATE -> "bee_conpassionate"
    else -> "bee_${personality.name.lowercase()}"
}

@Composable
internal fun rememberHiveSwingComposition(): LottieCompositionResult =
    rememberLottieComposition(LottieCompositionSpec.Asset(HiveSwingLottieAsset))

@Composable
internal fun rememberBeeTraitsComposition(animationId: String): LottieCompositionResult {
    val context = LocalContext.current
    val spec = remember(BeeTraitsLottieAsset, animationId) {
        LottieCompositionSpec.JsonString(
            readDotLottieAnimation(context, BeeTraitsLottieAsset, animationId),
        )
    }
    return rememberLottieComposition(spec)
}

internal fun readDotLottieAnimation(
    context: Context,
    assetPath: String,
    animationId: String,
): String {
    val bytes = context.assets.open(assetPath).use { it.readBytes() }
    ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
        var entry = zip.nextEntry
        while (entry != null) {
            val id = entry.name.substringAfterLast('/').removeSuffix(".json")
            if (!entry.isDirectory && id == animationId) {
                return zip.bufferedReader().readText()
            }
            zip.closeEntry()
            entry = zip.nextEntry
        }
    }
    error("Animation $animationId not found in $assetPath")
}