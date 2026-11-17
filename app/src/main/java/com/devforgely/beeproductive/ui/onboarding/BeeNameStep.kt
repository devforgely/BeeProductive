package com.devforgely.beeproductive.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devforgely.beeproductive.core.components.NameField
import com.devforgely.beeproductive.core.components.SolidButton
import com.devforgely.beeproductive.core.theme.RubikFontFamily

private val ShuffleGray = Color(0xFFE4E4E8)
private val ShuffleGrayShadow = Color(0xFFC2C2C8)

@Composable
internal fun BeeNameStep(viewModel: OnboardingViewModel) {
    OnboardingPage {
        Spacer(Modifier.height(12.dp))
        BeeActor()
        Spacer(Modifier.height(16.dp))
        Text(
            text = "What do you want to name your bee?",
            color = WelcomeInk,
            fontFamily = RubikFontFamily,
            fontSize = 26.sp,
            fontWeight = FontWeight.W800,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "you can change this later",
            color = WelcomeMuted,
            fontFamily = RubikFontFamily,
            fontSize = 15.sp,
            fontWeight = FontWeight.W800,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(22.dp))
        NameField(
            value = viewModel.name,
            placeholder = "Bee name",
            onValueChange = { if (it.length <= 10) viewModel.name = it },
            onClear = { viewModel.name = "" },
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SolidButton(
                text = "Shuffle",
                enabled = true,
                onClick = viewModel::shuffleBeeName,
                modifier = Modifier.weight(1f),
                containerColor = ShuffleGray,
                shadowColor = ShuffleGrayShadow,
                contentColor = WelcomeInk,
            )
            SolidButton(
                text = "Next",
                enabled = viewModel.name.trim().length in 2..10,
                onClick = { viewModel.step = OnboardingStep.Trait },
                modifier = Modifier.weight(1f),
            )
        }
    }
}
