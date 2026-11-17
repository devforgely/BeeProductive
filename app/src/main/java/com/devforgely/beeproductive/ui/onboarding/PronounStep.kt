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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devforgely.beeproductive.R
import com.devforgely.beeproductive.core.components.SolidButton
import com.devforgely.beeproductive.core.theme.RubikFontFamily

private val PronounHe = Color(0xFF3D9BE9)
private val PronounShe = Color(0xFFE56A9A)
private val PronounThey = Color(0xFF8D74D6)
private val PronounHeartIdle = Color(0xFFC4C4CC)

@Composable
internal fun PronounStep(viewModel: OnboardingViewModel) {
    OnboardingPage {
        Spacer(Modifier.height(12.dp))
        BeeActor()
        Spacer(Modifier.height(16.dp))
        Text(
            text = "You met a new bee!",
            color = WelcomeInk,
            fontFamily = RubikFontFamily,
            fontSize = 28.sp,
            fontWeight = FontWeight.W800,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "your bee's pronouns",
            color = WelcomeMuted,
            fontFamily = RubikFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.W800,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(22.dp))
        PronounChoice("he/him", PronounHe, viewModel.pronoun == BeePronoun.HE) {
            viewModel.pronoun = BeePronoun.HE
        }
        Spacer(Modifier.height(12.dp))
        PronounChoice("she/her", PronounShe, viewModel.pronoun == BeePronoun.SHE) {
            viewModel.pronoun = BeePronoun.SHE
        }
        Spacer(Modifier.height(12.dp))
        PronounChoice("they/them", PronounThey, viewModel.pronoun == BeePronoun.THEY) {
            viewModel.pronoun = BeePronoun.THEY
        }
        Spacer(Modifier.height(22.dp))
        SolidButton(
            text = "Next",
            enabled = viewModel.pronoun != null,
            onClick = { viewModel.step = OnboardingStep.BeeName },
        )
    }
}

@Composable
private fun PronounChoice(
    label: String,
    tint: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, if (selected) tint else ChoiceIdle, RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_heart),
            contentDescription = null,
            tint = if (selected) tint else PronounHeartIdle,
            modifier = Modifier.size(32.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = if (selected) WelcomeInk else Color(0xFFB0B4C0),
            fontFamily = RubikFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        if (selected) {
            Icon(
                painter = painterResource(R.drawable.ic_circle_tick),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
