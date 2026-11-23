package com.devforgely.beeproductive.ui.celebrate

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.R
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.devforgely.beeproductive.core.theme.RubikFontFamily
import com.devforgely.beeproductive.domain.model.HiveProgress
import com.devforgely.beeproductive.domain.model.HiveSnapshot
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.onboarding.BeeCelebrateAnimationId
import com.devforgely.beeproductive.ui.onboarding.rememberBeeTraitsComposition
import kotlin.math.PI
import kotlin.math.sin

private val SkyPink = Color(0xFFFFD0E4)
private val MidPink = Color(0xFFFFD6E6)
private val Peach = Color(0xFFFFE4D4)
private val FloorCream = Color(0xFFFFF2E8)
private val BadgePink = Color(0xFFE94B84)
private val ButtonPink = Color(0xFFFF4D8A)
private val TitleBrown = Color(0xFF4A3028)
private val SubtitleBrown = Color(0xFF8D7468)
private val CardShadow = Color(0xFFE7A0B8)

private val PopEasing = { t: Float -> easeOutBack(t) }

@Composable
fun CelebrationView(
    honey: Int,
    cells: Int,
    onDone: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    val beeName = snapshot.bee?.name ?: "Your bee"
    val goalCount = completedGoalCount(snapshot)
    val filled = snapshot.cells.count { it.fill >= 1f }
    val level = HiveProgress.level(filled)
    val remaining = HiveProgress.cellsToNext(filled)
    val reduceMotion = rememberReduceMotion()
    val clock = remember { Animatable(if (reduceMotion) 1f else 0f) }
    LaunchedEffect(reduceMotion) {
        if (!reduceMotion) {
            clock.animateTo(1f, tween(durationMillis = 520, easing = LinearEasing))
        }
    }
    val ambient = rememberInfiniteTransition(label = "celebrate-ambient")
    val phase by ambient.animateFloat(
        initialValue = 0f,
        targetValue = (PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(tween(durationMillis = 4200, easing = LinearEasing)),
        label = "confetti-phase",
    )
    val drift = if (reduceMotion) 0f else phase
    val lift = with(LocalDensity.current) { 16.dp.toPx() }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to SkyPink,
                    0.62f to MidPink,
                    0.84f to Peach,
                    1f to FloorCream,
                ),
            ),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp)
                .padding(top = 12.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Reveal(
                clock = clock.value,
                start = 0f,
                span = 0.42f,
                easing = PopEasing,
                lift = lift * 0.4f,
            ) {
                Text(
                    text = celebrationBadge(goalCount),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    color = BadgePink,
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.6.sp,
                )
            }
            Spacer(Modifier.height(18.dp))
            Reveal(clock.value, start = 0.08f, span = 0.4f, lift = lift) {
                Text(
                    text = "Sweet Success!",
                    color = TitleBrown,
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 36.sp,
                    lineHeight = 42.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(6.dp))
            Reveal(clock.value, start = 0.16f, span = 0.36f, lift = lift * 0.6f) {
                Text(
                    text = "$beeName is so proud of you, honey.",
                    color = SubtitleBrown,
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                val beeShown = reveal(clock.value, start = 0.02f, span = 0.5f, easing = ::easeOutCubic)
                CelebrateGlow(drift, Modifier.graphicsLayer { alpha = beeShown })
                CelebrateBee(
                    Modifier.graphicsLayer {
                        alpha = beeShown.coerceIn(0f, 1f)
                        val scale = 0.9f + 0.1f * beeShown
                        scaleX = scale
                        scaleY = scale
                    },
                )
            }
            Reveal(clock.value, start = 0.28f, span = 0.42f, lift = lift) {
                RewardCard(honey = honey, cells = cells)
            }
            Spacer(Modifier.height(14.dp))
            Reveal(clock.value, start = 0.4f, span = 0.36f, lift = lift * 0.45f) {
                Text(
                    text = hiveGrewLine(filled, remaining, level + 1),
                    color = SubtitleBrown,
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(18.dp))
            Reveal(clock.value, start = 0.48f, span = 0.4f, easing = PopEasing, lift = lift * 0.7f) {
                DoneButton(onClick = onDone)
            }
        }
    }
}

@Composable
private fun Reveal(
    clock: Float,
    start: Float,
    span: Float,
    lift: Float,
    easing: (Float) -> Float = ::easeOutCubic,
    content: @Composable () -> Unit,
) {
    val shown = reveal(clock, start, span, easing)
    Box(
        Modifier.graphicsLayer {
            alpha = shown.coerceIn(0f, 1f)
            translationY = (1f - shown.coerceIn(0f, 1f)) * lift
            val scale = 0.96f + 0.04f * shown.coerceIn(0f, 1.06f)
            scaleX = scale
            scaleY = scale
        },
    ) {
        content()
    }
}

@Composable
private fun RewardCard(honey: Int, cells: Int) {
    val shape = RoundedCornerShape(32.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = CardShadow.copy(alpha = 0.22f),
                spotColor = CardShadow.copy(alpha = 0.28f),
            )
            .clip(shape)
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Reward(
            value = "+$honey",
            label = "Honey",
            icon = R.drawable.ic_reward_honey,
        )
        Reward(
            value = "+$cells",
            label = "Hive cells",
            icon = R.drawable.ic_reward_hive_cell,
        )
    }
}

@Composable
private fun Reward(
    value: String,
    label: String,
    icon: Int,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = value,
            color = TitleBrown,
            fontFamily = RubikFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            lineHeight = 26.sp,
        )
        Text(
            text = label,
            color = SubtitleBrown,
            fontFamily = RubikFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun DoneButton(onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessMedium),
        label = "done-press",
    )
    val shape = RoundedCornerShape(50)
    Box(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = if (pressed) 8.dp else 16.dp,
                    color = ButtonPink,
                    spread = 0.dp,
                    offset = DpOffset(0.dp, if (pressed) 2.dp else 8.dp),
                    alpha = if (pressed) 0.28f else 0.55f,
                ),
            )
            .clip(shape)
            .background(Color.White)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Bee-autiful!",
            color = ButtonPink,
            fontFamily = RubikFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 17.sp,
        )
    }
}

@Composable
private fun CelebrateGlow(phase: Float, modifier: Modifier = Modifier) {
    val breathe = 1f + sin(phase) * 0.035f
    Canvas(
        modifier
            .fillMaxWidth(0.68f)
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = breathe
                scaleY = breathe
            },
    ) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.72f),
                    Color(0xFFFFF0F6).copy(alpha = 0.4f),
                    Color.Transparent,
                ),
            ),
        )
    }
}

@Composable
private fun CelebrateBee(modifier: Modifier = Modifier) {
    val composition by rememberBeeTraitsComposition(BeeCelebrateAnimationId)
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Celebrating bee" },
    )
}

@Composable
private fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}

private fun reveal(clock: Float, start: Float, span: Float, easing: (Float) -> Float): Float {
    val t = ((clock - start) / span).coerceIn(0f, 1f)
    return easing(t)
}

private fun easeOutCubic(t: Float): Float {
    val remaining = 1f - t
    return 1f - remaining * remaining * remaining
}

private fun easeOutBack(t: Float): Float {
    val overshoot = 1.35f
    val c3 = overshoot + 1f
    val x = t - 1f
    return 1f + c3 * x * x * x + overshoot * x * x
}

private fun celebrationBadge(count: Int): String = when (count) {
    0 -> "ALL GOALS COMPLETE"
    1 -> "ALL 1 GOAL COMPLETE"
    else -> "ALL $count GOALS COMPLETE"
}

private fun completedGoalCount(snapshot: HiveSnapshot): Int {
    val active = snapshot.activeHabits
    if (active.isEmpty()) return 0
    if (active.all { it.doneToday }) return active.size
    return active
        .groupBy { it.area }
        .values
        .filter { group -> group.isNotEmpty() && group.all { it.doneToday } }
        .maxOfOrNull { it.size }
        ?: active.count { it.doneToday }
}

private fun hiveGrewLine(filled: Int, remaining: Int, nextLevel: Int) = buildAnnotatedString {
    val cellsLabel = if (filled == 1) "1 cell" else "$filled cells"
    val emphasis = SpanStyle(fontWeight = FontWeight.Bold, color = TitleBrown)
    append("Your hive grew to ")
    withStyle(emphasis) { append(cellsLabel) }
    append(" · ")
    withStyle(emphasis) { append(remaining.toString()) }
    append(" more to ")
    withStyle(emphasis) { append("Level $nextLevel") }
}
