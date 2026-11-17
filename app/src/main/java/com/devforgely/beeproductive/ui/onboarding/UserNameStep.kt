package com.devforgely.beeproductive.ui.onboarding

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devforgely.beeproductive.core.components.NameField
import com.devforgely.beeproductive.core.components.SolidButton
import com.devforgely.beeproductive.core.components.SpeechBox

@Composable
internal fun UserNameStep(viewModel: OnboardingViewModel) {
    OnboardingPage {
        Spacer(Modifier.height(12.dp))
        SpeechBox(
            "bizz buzz, thanks for making friend with me, if my name is ${viewModel.name}, what's your name?",
        )
        Spacer(Modifier.height(12.dp))
        BeeActor()
        Spacer(Modifier.height(18.dp))
        NameField(
            value = viewModel.userName,
            placeholder = "Your name",
            onValueChange = { if (it.length <= 30) viewModel.userName = it },
            onClear = { viewModel.userName = "" },
        )
        Spacer(Modifier.height(16.dp))
        SolidButton(
            text = "Next",
            enabled = viewModel.userName.trim().length in 2..30,
            onClick = { viewModel.step = OnboardingStep.Support },
        )
    }
}
