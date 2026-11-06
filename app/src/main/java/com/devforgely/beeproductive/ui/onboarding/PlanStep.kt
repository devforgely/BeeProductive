package com.devforgely.beeproductive.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.devforgely.beeproductive.core.components.SkyButton
import com.devforgely.beeproductive.core.components.SpeechBox
import com.devforgely.beeproductive.core.theme.RubikFontFamily

private val PlanPaper = Color(0xFFFFF6E4)
private val PlanBorder = Color(0xFFFFC48A)
private val PlanTape = Color(0xFFF6C445)

@Composable
internal fun PlanStep(viewModel: OnboardingViewModel, onFinished: () -> Unit) {
    OnboardingPage {
        Spacer(Modifier.height(8.dp))
        SpeechBox("Time to get busy, bizz buzz!")
        Spacer(Modifier.height(8.dp))
        BeeActor()
        Spacer(Modifier.height(16.dp))
        StarterPlanCard(
            userName = viewModel.userName.trim(),
            beeName = viewModel.name,
            goals = viewModel.starterGoals,
        )
        Spacer(Modifier.height(20.dp))
        SkyButton(
            text = "let's do it!",
            enabled = !viewModel.saving,
            onClick = { viewModel.plant(onFinished) },
        )
    }
}

@Composable
private fun StarterPlanCard(
    userName: String,
    beeName: String,
    goals: List<StarterGoal>,
) {
    val planShape = RoundedCornerShape(24.dp)
    Box(Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .background(PlanPaper, planShape)
                .border(2.dp, PlanBorder, planShape)
                .padding(horizontal = 18.dp, vertical = 20.dp),
        ) {
            Text(
                text = "$userName's starter plan",
                modifier = Modifier.fillMaxWidth(),
                color = WelcomeInk,
                fontFamily = RubikFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.W800,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Achieve these easy goals with $beeName",
                modifier = Modifier.fillMaxWidth(),
                color = WelcomeMuted,
                fontFamily = RubikFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.W800,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            goals.forEachIndexed { index, goal ->
                if (index > 0) {
                    HorizontalDivider(color = Color(0xFFF0E4C8))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = goal.emoji, fontSize = 20.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = goal.title,
                        color = WelcomeInk,
                        fontFamily = RubikFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .zIndex(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            repeat(6) {
                Box(
                    Modifier
                        .size(width = 16.dp, height = 16.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(PlanTape),
                )
            }
        }
    }
}
