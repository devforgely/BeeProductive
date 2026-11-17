package com.devforgely.beeproductive.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devforgely.beeproductive.R
import com.devforgely.beeproductive.core.components.SolidButton
import com.devforgely.beeproductive.core.theme.RubikFontFamily
import com.devforgely.beeproductive.domain.model.LifeArea

private val supportChoices = listOf(
    LifeArea.HEALTH_ENERGY to "Health & energy",
    LifeArea.MIND_FOCUS to "Mind & focus",
    LifeArea.WORK_GOALS to "Work & goals",
    LifeArea.CREATIVITY to "Creativity",
    LifeArea.REST to "Reset",
)

@Composable
internal fun SupportStep(viewModel: OnboardingViewModel) {
    OnboardingPage {
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Where do you want support?",
            color = WelcomeInk,
            fontFamily = RubikFontFamily,
            fontSize = 26.sp,
            fontWeight = FontWeight.W800,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Choose every area that fits.",
            color = WelcomeMuted,
            fontFamily = RubikFontFamily,
            fontSize = 15.sp,
            fontWeight = FontWeight.W800,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        supportChoices.forEach { (area, label) ->
            SupportChoice(
                label = label,
                selected = area in viewModel.supportAreas,
                onClick = { viewModel.toggleSupport(area) },
            )
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(8.dp))
        SolidButton(
            text = "Next",
            enabled = viewModel.supportAreas.isNotEmpty(),
            onClick = { viewModel.step = OnboardingStep.Generating },
        )
    }
}

@Composable
private fun SupportChoice(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, if (selected) WelcomeSkyBlue else ChoiceIdle, RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = if (selected) WelcomeInk else Color(0xFF8E8E93),
            fontFamily = RubikFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        if (selected) {
            Icon(
                painter = painterResource(R.drawable.ic_circle_tick),
                contentDescription = null,
                tint = WelcomeSkyBlue,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
