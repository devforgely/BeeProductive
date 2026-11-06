package com.devforgely.beeproductive.ui.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.devforgely.beeproductive.core.theme.RubikFontFamily
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private val HivePopEasing = CubicBezierEasing(0.22f, 1.22f, 0.32f, 1f)
private val HiveExitEasing = CubicBezierEasing(0.4f, 0f, 1f, 1f)
private val HiveEnterEasing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

@Composable
internal fun HiveIntroStep(onFinished: () -> Unit) {
    val finish by rememberUpdatedState(onFinished)
    var playing by remember { mutableStateOf(false) }
    val emerge = remember { Animatable(0f) }
    val hiveAlpha = remember { Animatable(1f) }
    val hiveScale = remember { Animatable(1f) }
    val hiveLift = remember { Animatable(0f) }
    val caption = remember { Animatable(0f) }
    val density = LocalDensity.current
    val pxPerDp = with(density) { 1.dp.toPx() }
    val mouthPx = 62.dp.value * pxPerDp
    val captionRisePx = 12.dp.value * pxPerDp
    val hiveLiftPx = 18.dp.value * pxPerDp
    val hintAlpha by animateFloatAsState(
        targetValue = if (playing) 0f else 1f,
        animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
        label = "hive-hint",
    )

    LaunchedEffect(playing) {
        if (!playing) return@LaunchedEffect
        // Swing sound: app/src/main/res/raw/hive_swing.ogg
        // Uncomment when the clip is in res/raw. MediaPlayer.create needs the generated id.
        // val context = LocalContext.current
        // val player = android.media.MediaPlayer.create(context, R.raw.hive_swing)
        // player.setOnCompletionListener { it.release() }
        // player.start()
        delay(720.milliseconds)
        emerge.animateTo(1f, tween(durationMillis = 640, easing = HivePopEasing))
        delay(100.milliseconds)
        coroutineScope {
            launch { hiveAlpha.animateTo(0f, tween(durationMillis = 300, easing = HiveExitEasing)) }
            launch { hiveScale.animateTo(0.84f, tween(durationMillis = 300, easing = HiveExitEasing)) }
            launch { hiveLift.animateTo(1f, tween(durationMillis = 300, easing = HiveExitEasing)) }
        }
        caption.animateTo(1f, tween(durationMillis = 300, easing = HiveEnterEasing))
        // Welcome clip length. Advance when bee_welcome.json finishes instead of this delay.
        delay(1180.milliseconds)
        finish()
    }

    val emergeT = emerge.value
    val beeScale = lerp(0.26f, 1f, emergeT)
    val beeAlpha = (emergeT * 3.4f).coerceIn(0f, 1f)
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !playing,
                onClick = { playing = true },
            ),
    ) {
        Box(
            Modifier
                .align(Alignment.Center)
                .size(250.dp)
                .graphicsLayer {
                    scaleX = hiveScale.value
                    scaleY = hiveScale.value
                    alpha = hiveAlpha.value
                    translationY = -hiveLift.value * hiveLiftPx
                },
        ) {
            // Resting hive: app/src/main/assets/lottie/beehive.json
            // Swing clip, played once on tap: app/src/main/assets/lottie/beehive_swing.json
            // Swing sound, started with that clip from the tap effect above:
            // app/src/main/res/raw/hive_swing.ogg
            // val context = LocalContext.current
            // val swingSound = android.media.MediaPlayer.create(context, R.raw.hive_swing)
            // swingSound.setOnCompletionListener { it.release() }
            // swingSound.start()
            // val hiveAsset = if (playing) "lottie/beehive_swing.json" else "lottie/beehive.json"
            // val hiveComposition by rememberLottieComposition(LottieCompositionSpec.Asset(hiveAsset))
            // val hiveProgress by animateLottieCompositionAsState(
            //     composition = hiveComposition,
            //     iterations = if (playing) 1 else LottieConstants.IterateForever,
            // )
            // LottieAnimation(
            //     composition = hiveComposition,
            //     progress = { hiveProgress },
            //     modifier = Modifier.fillMaxSize(),
            // )
        }
        Box(
            Modifier
                .align(Alignment.Center)
                .size(176.dp)
                .graphicsLayer {
                    translationY = lerp(mouthPx, 0f, emergeT)
                    scaleX = beeScale
                    scaleY = beeScale
                    alpha = beeAlpha
                },
        ) {
            // Welcome clip, played once the bee is centered:
            // app/src/main/assets/lottie/bee_welcome.json
            // val welcomeComposition by rememberLottieComposition(
            //     LottieCompositionSpec.Asset("lottie/bee_welcome.json"),
            // )
            // val welcomeProgress by animateLottieCompositionAsState(
            //     composition = welcomeComposition,
            //     isPlaying = beeAlpha == 1f,
            //     iterations = 1,
            // )
            // LottieAnimation(
            //     composition = welcomeComposition,
            //     progress = { welcomeProgress },
            //     modifier = Modifier.fillMaxSize(),
            // )
        }
        Text(
            text = "You met a new bee!",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 128.dp)
                .graphicsLayer {
                    alpha = caption.value
                    translationY = (1f - caption.value) * captionRisePx
                },
            color = WelcomeInk,
            fontFamily = RubikFontFamily,
            fontSize = 26.sp,
            fontWeight = FontWeight.W800,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Tap anywhere",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .graphicsLayer { alpha = hintAlpha },
            color = WelcomeMuted,
            fontFamily = RubikFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}
