package com.devforgely.beeproductive.ui.onboarding

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.audiofx.LoudnessEnhancer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.devforgely.beeproductive.core.theme.RubikFontFamily
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

private const val HiveSwingAudioAsset = "audio/hive_swing.mp3"
private const val HiveSwingLoudnessGainMilliBel = 1400
private const val BeeEmergeDurationMillis = 3_000
private const val HiveFadeOutDurationMillis = 2_000
private val WelcomeIntroDuration = 3.seconds

private val HivePopEasing = CubicBezierEasing(0.22f, 1.22f, 0.32f, 1f)
private val HiveEnterEasing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

@Composable
internal fun HiveIntroStep(onFinished: () -> Unit) {
    val finish by rememberUpdatedState(onFinished)
    val context = LocalContext.current
    var playing by remember { mutableStateOf(false) }
    var hiveSwinging by remember { mutableStateOf(false) }
    var welcoming by remember { mutableStateOf(false) }
    val emerge = remember { Animatable(0f) }
    val hiveAlpha = remember { Animatable(1f) }
    val hiveScale = remember { Animatable(1f) }
    val hiveLift = remember { Animatable(0f) }
    val caption = remember { Animatable(0f) }
    val density = LocalDensity.current
    val pxPerDp = with(density) { 1.dp.toPx() }
    val mouthPx = 100.dp.value * pxPerDp
    val captionRisePx = 12.dp.value * pxPerDp
    val hiveLiftPx = 18.dp.value * pxPerDp
    val hintAlpha by animateFloatAsState(
        targetValue = if (playing) 0f else 1f,
        animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
        label = "hive-hint",
    )

    val hiveComposition by rememberHiveSwingComposition()
    val idleComposition by rememberBeeTraitsComposition(BeeIdleAnimationId)
    val welcomeComposition by rememberBeeTraitsComposition(BeeWelcomeAnimationId)

    val hiveProgress by animateLottieCompositionAsState(
        composition = hiveComposition,
        isPlaying = hiveSwinging,
        iterations = LottieConstants.IterateForever,
        restartOnPlay = true,
    )
    val idleProgress by animateLottieCompositionAsState(
        composition = idleComposition,
        isPlaying = playing && !welcoming,
        iterations = LottieConstants.IterateForever,
    )
    val welcomeProgress by animateLottieCompositionAsState(
        composition = welcomeComposition,
        isPlaying = welcoming,
        iterations = LottieConstants.IterateForever,
        restartOnPlay = true,
    )

    LaunchedEffect(playing) {
        if (!playing) return@LaunchedEffect
        hiveSwinging = true
        var loudnessEnhancer: LoudnessEnhancer? = null
        var player: MediaPlayer? = runCatching {
            MediaPlayer().also { mediaPlayer ->
                mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                )
                context.assets.openFd(HiveSwingAudioAsset).use { afd ->
                    mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    mediaPlayer.prepare()
                }
                mediaPlayer.setVolume(1f, 1f)
                loudnessEnhancer = LoudnessEnhancer(mediaPlayer.audioSessionId).apply {
                    setTargetGain(HiveSwingLoudnessGainMilliBel)
                    enabled = true
                }
                mediaPlayer.start()
            }
        }.getOrNull()
        try {
            delay(6.seconds)
            releaseHiveSwingAudio(player, loudnessEnhancer)
            player = null
            loudnessEnhancer = null
            hiveSwinging = false
            coroutineScope {
                launch {
                    emerge.animateTo(
                        1f,
                        tween(durationMillis = BeeEmergeDurationMillis, easing = HivePopEasing),
                    )
                }
                launch {
                    hiveAlpha.animateTo(
                        0f,
                        tween(durationMillis = HiveFadeOutDurationMillis, easing = FastOutSlowInEasing),
                    )
                }
                launch {
                    hiveScale.animateTo(
                        0.92f,
                        tween(durationMillis = HiveFadeOutDurationMillis, easing = FastOutSlowInEasing),
                    )
                }
                launch {
                    hiveLift.animateTo(
                        1f,
                        tween(durationMillis = HiveFadeOutDurationMillis, easing = FastOutSlowInEasing),
                    )
                }
            }
            welcoming = true
            coroutineScope {
                launch {
                    caption.animateTo(1f, tween(durationMillis = 300, easing = HiveEnterEasing))
                }
                launch { delay(WelcomeIntroDuration) }
            }
            finish()
        } finally {
            releaseHiveSwingAudio(player, loudnessEnhancer)
        }
    }

    val emergeT = emerge.value
    val beeScale = lerp(0.22f, 1f, emergeT)
    val beeAlpha = emergeT.coerceIn(0f, 1f)
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !playing,
                onClick = {
                    playing = true
                    hiveSwinging = true
                },
            ),
    ) {
        Box(
            Modifier
                .align(Alignment.Center)
                .size(400.dp)
                .graphicsLayer {
                    scaleX = hiveScale.value
                    scaleY = hiveScale.value
                    alpha = hiveAlpha.value
                    translationY = -hiveLift.value * hiveLiftPx
                },
        ) {
            LottieAnimation(
                composition = hiveComposition,
                progress = { hiveProgress },
                modifier = Modifier.fillMaxSize(),
            )
        }
        Box(
            Modifier
                .align(Alignment.Center)
                .size(280.dp)
                .graphicsLayer {
                    translationY = lerp(mouthPx, 0f, emergeT.coerceIn(0f, 1f))
                    scaleX = beeScale
                    scaleY = beeScale
                    alpha = beeAlpha
                },
        ) {
            if (welcoming) {
                LottieAnimation(
                    composition = welcomeComposition,
                    progress = { welcomeProgress },
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LottieAnimation(
                    composition = idleComposition,
                    progress = { idleProgress },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Text(
            text = "You met a new bee!",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 176.dp)
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

private fun releaseHiveSwingAudio(
    player: MediaPlayer?,
    loudnessEnhancer: LoudnessEnhancer?,
) {
    runCatching { loudnessEnhancer?.release() }
    player?.let { mediaPlayer ->
        runCatching {
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}

