package com.devforgely.beeproductive.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.R
import com.devforgely.beeproductive.core.theme.RubikFontFamily
import com.devforgely.beeproductive.core.components.BeeAnimation
import com.devforgely.beeproductive.core.components.ChoiceCard
import com.devforgely.beeproductive.core.components.HiveScene
import com.devforgely.beeproductive.core.components.MeadowBackground
import com.devforgely.beeproductive.core.components.PrimaryBeeButton
import com.devforgely.beeproductive.core.components.SoftCard
import com.devforgely.beeproductive.domain.model.BeeAccent
import com.devforgely.beeproductive.domain.model.BeeClip
import com.devforgely.beeproductive.domain.model.BeePersonality
import com.devforgely.beeproductive.domain.model.CombModel
import com.devforgely.beeproductive.domain.model.CycleStage
import com.devforgely.beeproductive.domain.model.HiveCopy
import com.devforgely.beeproductive.domain.model.LifeArea
import com.devforgely.beeproductive.domain.model.LifeAreaTemplates

private val WelcomeSkyBlue = Color(0xFF52B4E8)
private val WelcomeSkyBlueShadow = Color(0xFF3A8FC4)
private val WelcomeInk = Color(0xFF252525)
private val WelcomeMuted = Color(0xFF8E8E93)
private val WelcomeButtonShape = RoundedCornerShape(12.dp)
private val WelcomeButtonShadowDrop = 4.dp

private val story = listOf(
    "A tired bee sat in a quiet meadow, wings too heavy for a long flight.",
    "Then one small visit. And another. The comb began to glow.",
    "Ninety gentle days can turn a tiny nest into a garden.",
)

private val nameIdeas = listOf("Sunny", "Clover", "Pip", "Marigold", "Nectar", "Buzz")

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    if (viewModel.step == OnboardingStep.Welcome) {
        WelcomeStep(onNext = { viewModel.step = OnboardingStep.Story })
        return
    }
    Box(Modifier.fillMaxSize()) {
        MeadowBackground(themeId = null)
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (viewModel.step) {
                OnboardingStep.Welcome -> Unit
                OnboardingStep.Story -> StoryStep(viewModel.storyPage) {
                    if (viewModel.storyPage < story.lastIndex) {
                        viewModel.storyPage += 1
                    } else {
                        viewModel.step = OnboardingStep.Meet
                    }
                }
                OnboardingStep.Meet -> MeetStep(viewModel) { viewModel.step = OnboardingStep.Area }
                OnboardingStep.Area -> AreaStep(viewModel) { viewModel.step = OnboardingStep.State }
                OnboardingStep.State -> StateStep(viewModel) { viewModel.step = OnboardingStep.Star }
                OnboardingStep.Star -> StarStep(viewModel) { viewModel.step = OnboardingStep.First }
                OnboardingStep.First -> FirstStep(viewModel, onFinished)
                OnboardingStep.Celebration -> CelebrationStep(viewModel)
            }
        }
    }
}

@Composable
private fun WelcomeStep(onNext: () -> Unit) {
    val agreement = buildAnnotatedString {
        append("By continuing, you agree to our ")
        withStyle(SpanStyle(color = WelcomeSkyBlue)) { append("Terms of Service") }
        append("\nand ")
        withStyle(SpanStyle(color = WelcomeSkyBlue)) { append("Privacy Policy") }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Image(
                painter = painterResource(R.drawable.bee_avatar),
                contentDescription = "Bee",
                modifier = Modifier.size(128.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = "Bee Productive",
                color = WelcomeInk,
                fontFamily = RubikFontFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Your new buzz-buddy for sweet success.",
                color = WelcomeMuted,
                fontFamily = RubikFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(Modifier.weight(0.75f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp + WelcomeButtonShadowDrop),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(0.85f)
                    .height(52.dp)
                    .align(Alignment.BottomCenter)
                    .background(WelcomeSkyBlueShadow, WelcomeButtonShape),
            )
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(52.dp)
                    .align(Alignment.TopCenter),
                shape = WelcomeButtonShape,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WelcomeSkyBlue,
                    contentColor = Color.White,
                )
            ) {
                Text(
                    text = "Meet a new bee",
                    fontFamily = RubikFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = agreement,
            color = WelcomeMuted,
            fontFamily = RubikFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(0.5f))
    }
}

@Composable
private fun StoryStep(page: Int, onNext: () -> Unit) {
    BeeAnimation(BeeClip.IDLE, BeeAccent.HONEY, 0.8f, Modifier.size(180.dp).alignCenter())
    SoftCard {
        Text(story[page], style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Text(
            "Panel ${page + 1} of ${story.size}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    PrimaryBeeButton(
        if (page == story.lastIndex) "Meet your bee" else "Continue",
        onClick = onNext,
    )
}

@Composable
private fun MeetStep(viewModel: OnboardingViewModel, onNext: () -> Unit) {
    val speed = HiveCopy.speed(viewModel.personality, null)
    val clip = HiveCopy.clip(viewModel.personality, null, celebrating = false, foraging = false)
    BeeAnimation(clip, viewModel.accent, speed, Modifier.size(190.dp).alignCenter())
    Text("What should we call you?", style = MaterialTheme.typography.headlineSmall)
    OutlinedTextField(
        value = viewModel.name,
        onValueChange = { viewModel.name = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Bee name") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        shape = MaterialTheme.shapes.large,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        nameIdeas.take(3).forEach { idea ->
            QuietName(idea) { viewModel.name = idea }
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        nameIdeas.drop(3).forEach { idea ->
            QuietName(idea) { viewModel.name = idea }
        }
    }
    Text("Accent", style = MaterialTheme.typography.titleMedium)
    BeeAccent.entries.forEach { accent ->
        ChoiceCard(
            title = accent.label,
            body = "Tints the backpack and the warm parts of the hive.",
            selected = viewModel.accent == accent,
            onClick = { viewModel.accent = accent },
        )
    }
    Text("Personality", style = MaterialTheme.typography.titleMedium)
    BeePersonality.entries.forEach { personality ->
        ChoiceCard(
            title = personality.label,
            body = personality.blurb,
            selected = viewModel.personality == personality,
            onClick = { viewModel.personality = personality },
        )
    }
    PrimaryBeeButton("This feels right", enabled = viewModel.name.isNotBlank(), onClick = onNext)
}

@Composable
private fun AreaStep(viewModel: OnboardingViewModel, onNext: () -> Unit) {
    Text("Where should the hive grow first?", style = MaterialTheme.typography.headlineSmall)
    LifeArea.entries.forEach { area ->
        ChoiceCard(
            title = area.label,
            body = LifeAreaTemplates.forArea(area).northStar,
            selected = viewModel.area == area,
            onClick = { viewModel.chooseArea(area) },
        )
    }
    PrimaryBeeButton("Continue", onClick = onNext)
}

@Composable
private fun StateStep(viewModel: OnboardingViewModel, onNext: () -> Unit) {
    val template = LifeAreaTemplates.forArea(viewModel.area)
    Text("How does this area feel right now?", style = MaterialTheme.typography.headlineSmall)
    template.stateOptions.forEach { option ->
        ChoiceCard(
            title = option.label,
            body = option.tone,
            selected = viewModel.stateId == option.id,
            onClick = { viewModel.chooseState(option.id, option.tone) },
        )
    }
    PrimaryBeeButton("Continue", enabled = viewModel.stateId != null, onClick = onNext)
}

@Composable
private fun StarStep(viewModel: OnboardingViewModel, onNext: () -> Unit) {
    Text("Your North Star", style = MaterialTheme.typography.headlineSmall)
    Text(
        "This becomes the glowing queen cell. Edit it until it sounds like you.",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    OutlinedTextField(
        value = viewModel.northStar,
        onValueChange = { viewModel.northStar = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("90-day goal") },
        shape = MaterialTheme.shapes.large,
    )
    viewModel.habits.forEachIndexed { index, habit ->
        OutlinedTextField(
            value = habit,
            onValueChange = { viewModel.updateHabit(index, it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Starter habit ${index + 1}") },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
        )
    }
    PrimaryBeeButton(
        "Plant the hive",
        enabled = viewModel.northStar.isNotBlank() && viewModel.habits.any { it.isNotBlank() },
        onClick = onNext,
    )
}

@Composable
private fun FirstStep(viewModel: OnboardingViewModel, onFinished: () -> Unit) {
    val first = viewModel.habits.firstOrNull { it.isNotBlank() } ?: "One small action"
    Text("One small action starts the comb.", style = MaterialTheme.typography.headlineSmall)
    SoftCard {
        Text(first, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text("When you have done it, even imperfectly, seal the first cell.")
    }
    PrimaryBeeButton(
        text = "I did this",
        onClick = { viewModel.celebrateFirst(onFinished) },
        enabled = !viewModel.saving,
    )
}

@Composable
private fun CelebrationStep(viewModel: OnboardingViewModel) {
    Text("Your hive has begun.", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    HiveScene(
        northStar = viewModel.northStar,
        queenGlow = CombModel.queenGlow(1),
        cells = CombModel.cells(10, CycleStage.POLLINATING),
        goals = emptyList(),
        accent = viewModel.accent,
        clip = BeeClip.CELEBRATE,
        speed = 1.1f,
        outfitId = null,
        decorationId = null,
    )
}

private fun Modifier.alignCenter(): Modifier = this.fillMaxWidth()

@Composable
private fun RowScope.QuietName(label: String, onClick: () -> Unit) {
    com.devforgely.beeproductive.core.components.QuietButton(label, Modifier.weight(1f), onClick)
}
