package com.devforgely.beeproductive.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devforgely.beeproductive.R
import com.devforgely.beeproductive.core.components.SkyButton
import com.devforgely.beeproductive.core.theme.RubikFontFamily
import com.devforgely.beeproductive.domain.model.BeePersonality

private data class TraitChoice(
    val personality: BeePersonality,
    val icon: Int,
)

private val tightText = TextStyle(
    platformStyle = PlatformTextStyle(includeFontPadding = false),
)

private val traitChoices = listOf(
    TraitChoice(BeePersonality.DILIGENT, R.drawable.ic_target),
    TraitChoice(BeePersonality.PLAYFUL, R.drawable.ic_sunny),
    TraitChoice(BeePersonality.CALM, R.drawable.ic_moon_stars),
    TraitChoice(BeePersonality.CURIOUS, R.drawable.ic_quiz),
    TraitChoice(BeePersonality.COMPASSIONATE, R.drawable.ic_honey_drop),
    TraitChoice(BeePersonality.CONFIDENT, R.drawable.ic_trophy),
)

@Composable
internal fun TraitStep(viewModel: OnboardingViewModel) {
    val beeName = viewModel.name.ifBlank { "your bee" }
    val chosen = viewModel.personality?.label?.lowercase() ?: "..."
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(8.dp))
        BeeActor(trait = viewModel.personality)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Choose a trait for $beeName",
            color = WelcomeInk,
            fontFamily = RubikFontFamily,
            fontSize = 26.sp,
            fontWeight = FontWeight.W800,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "$beeName is $chosen",
            color = WelcomeMuted,
            fontFamily = RubikFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.W800,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(14.dp))
        traitChoices.forEach { trait ->
            TraitChoiceRow(
                label = trait.personality.label,
                description = trait.personality.blurb,
                icon = trait.icon,
                selected = viewModel.personality == trait.personality,
                onClick = { viewModel.personality = trait.personality },
            )
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(8.dp))
        SkyButton(
            text = "Next",
            enabled = viewModel.personality != null,
            onClick = { viewModel.step = OnboardingStep.UserName },
        )
    }
}

@Composable
private fun TraitChoiceRow(
    label: String,
    description: String,
    icon: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (selected) WelcomeSkyBlue else WelcomeMuted
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
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = label,
                color = if (selected) WelcomeInk else Color(0xFF8E8E93),
                fontFamily = RubikFontFamily,
                fontSize = 16.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold,
                style = tightText,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = description,
                color = if (selected) WelcomeMuted else Color(0xFFB0B0B5),
                fontFamily = RubikFontFamily,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold,
                style = tightText,
            )
        }
    }
}
