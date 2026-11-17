package com.devforgely.beeproductive.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.devforgely.beeproductive.domain.model.BeePersonality

internal val WelcomeSkyBlue = Color(0xFF52B4E8)
internal val WelcomeInk = Color(0xFF252525)
internal val WelcomeMuted = Color(0xFF8E8E93)
internal val ChoiceIdle = Color(0xFFE6E6EA)
internal val WelcomeButtonShape = RoundedCornerShape(12.dp)

@Composable
internal fun OnboardingPage(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}

@Composable
internal fun BeeActor(trait: BeePersonality? = null) {
    val animationId = trait?.let(::beeTraitAnimationId) ?: BeeIdleAnimationId
    val composition by rememberBeeTraitsComposition(animationId)
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        restartOnPlay = true,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .size(180.dp)
            .semantics { contentDescription = trait?.let { "${it.label} bee" } ?: "Bee" },
    )
}