package com.devforgely.beeproductive.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    if (viewModel.step == OnboardingStep.Welcome) {
        WelcomeStep(onNext = { viewModel.step = OnboardingStep.HiveIntro })
        return
    }
    if (viewModel.step == OnboardingStep.HiveIntro) {
        HiveIntroStep(onFinished = { viewModel.step = OnboardingStep.Pronoun })
        return
    }
    QuestionFlow(viewModel, onFinished)
}

@Composable
private fun QuestionFlow(viewModel: OnboardingViewModel, onFinished: () -> Unit) {
    when (viewModel.step) {
        OnboardingStep.Welcome, OnboardingStep.HiveIntro -> Unit
        OnboardingStep.Pronoun -> PronounStep(viewModel)
        OnboardingStep.BeeName -> BeeNameStep(viewModel)
        OnboardingStep.Trait -> TraitStep(viewModel)
        OnboardingStep.UserName -> UserNameStep(viewModel)
        OnboardingStep.Support -> SupportStep(viewModel)
        OnboardingStep.Generating -> GeneratingStep(viewModel)
        OnboardingStep.Plan -> PlanStep(viewModel, onFinished)
    }
}
