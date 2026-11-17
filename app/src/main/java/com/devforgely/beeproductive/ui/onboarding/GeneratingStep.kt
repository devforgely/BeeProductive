package com.devforgely.beeproductive.ui.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devforgely.beeproductive.core.theme.RubikFontFamily

@Composable
internal fun GeneratingStep(viewModel: OnboardingViewModel) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        viewModel.preparePlan()
        progress.animateTo(1f, tween(durationMillis = 1700, easing = FastOutSlowInEasing))
        viewModel.step = OnboardingStep.Plan
    }
    val ringSize = 240.dp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(ringSize),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                progress = { progress.value },
                modifier = Modifier.fillMaxSize(),
                color = WelcomeSkyBlue,
                trackColor = ChoiceIdle,
                strokeWidth = 10.dp,
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
            )
            BeeActor()
        }
        Spacer(Modifier.height(28.dp))
        Text(
            text = "Generating your north stars with ${viewModel.name}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            style = TextStyle(
                color = WelcomeInk,
                fontFamily = RubikFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.W800,
                lineHeight = 26.sp,
                textAlign = TextAlign.Center,
                lineBreak = LineBreak.Simple,
                hyphens = Hyphens.None,
            ),
        )
    }
}
