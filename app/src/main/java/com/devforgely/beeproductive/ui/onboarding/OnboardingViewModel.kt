package com.devforgely.beeproductive.ui.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devforgely.beeproductive.R
import com.devforgely.beeproductive.data.repository.HiveRepository
import com.devforgely.beeproductive.domain.model.BeePersonality
import com.devforgely.beeproductive.domain.model.LifeArea
import com.devforgely.beeproductive.domain.model.LifeAreaTemplates
import com.devforgely.beeproductive.domain.model.OnboardingDraft
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

enum class OnboardingStep {
    Welcome,
    HiveIntro,
    Pronoun,
    BeeName,
    Trait,
    UserName,
    Support,
    Generating,
    Plan,
}

enum class BeePronoun(val label: String) {
    HE("he/him"),
    SHE("she/her"),
    THEY("they/them"),
}

data class StarterGoal(
    val icon: Int,
    val title: String,
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: HiveRepository,
) : ViewModel() {
    var step by mutableStateOf(OnboardingStep.Welcome)
    var pronoun by mutableStateOf<BeePronoun?>(BeePronoun.entries.first())
    var name by mutableStateOf(BeeNames.random())
    var userName by mutableStateOf("")
    var personality by mutableStateOf<BeePersonality?>(null)
    var supportAreas by mutableStateOf<Set<LifeArea>>(emptySet())
    var area by mutableStateOf(LifeArea.MIND_FOCUS)
    var tone by mutableStateOf("Start gently.")
    var northStar by mutableStateOf("")
    var habits by mutableStateOf<List<String>>(emptyList())
    var starterGoals by mutableStateOf<List<StarterGoal>>(emptyList())
    var saving by mutableStateOf(false)

    fun shuffleBeeName() {
        val choices = BeeNames.filter { it != name }
        name = choices[Random.nextInt(choices.size)]
    }

    fun toggleSupport(area: LifeArea) {
        supportAreas = if (area in supportAreas) supportAreas - area else supportAreas + area
    }

    fun preparePlan() {
        val chosen = SupportOrder.filter { it in supportAreas }
        if (chosen.isEmpty()) return
        area = chosen.first()
        val templates = chosen.map { LifeAreaTemplates.forArea(it) }
        northStar = templates.first().northStar
        val perArea = if (chosen.size == 1) 3 else 2
        starterGoals = chosen.flatMap { lifeArea ->
            val template = LifeAreaTemplates.forArea(lifeArea)
            val icons = SupportIcons.getValue(lifeArea)
            template.starterHabits.take(perArea).mapIndexed { index, habit ->
                StarterGoal(icons[index % icons.size], habit)
            }
        }
        habits = starterGoals.map { it.title }
        tone = "Start gently."
    }

    fun plant(onFinished: () -> Unit) {
        val chosenTrait = personality ?: return
        if (saving) return
        viewModelScope.launch {
            saving = true
            repository.plantHive(
                OnboardingDraft(
                    name = name,
                    personality = chosenTrait,
                    area = area,
                    tone = tone,
                    northStar = northStar,
                    habits = habits,
                ),
            )
            saving = false
            onFinished()
        }
    }

    private companion object {
        val BeeNames = listOf(
            "Sunny", "Clover", "Pip", "Marigold", "Nectar", "Buzz",
            "Honey", "Willow", "Poppy", "Maple", "Breeze", "Pollen",
            "Daisy", "Coco", "Amber", "Birch", "Fern", "Ginger",
            "Hazel", "Indigo", "Juniper", "Kiwi", "Luna", "Mango",
            "Olive", "Peach", "Quinn", "Rosie", "Sage", "Toffee",
        )

        val SupportOrder = listOf(
            LifeArea.HEALTH_ENERGY,
            LifeArea.MIND_FOCUS,
            LifeArea.WORK_GOALS,
            LifeArea.CREATIVITY,
            LifeArea.REST,
        )

        val SupportIcons = mapOf(
            LifeArea.HEALTH_ENERGY to listOf(
                R.drawable.ic_health_energy_apple,
                R.drawable.ic_health_energy_heart_pulse,
                R.drawable.ic_health_energy_lightning,
            ),
            LifeArea.MIND_FOCUS to listOf(
                R.drawable.ic_mind_focus_brain,
                R.drawable.ic_mind_focus_magnifier,
                R.drawable.ic_mind_focus_target,
            ),
            LifeArea.WORK_GOALS to listOf(
                R.drawable.ic_work_goals_briefcase,
                R.drawable.ic_work_goals_checklist,
                R.drawable.ic_work_goals_trophy,
            ),
            LifeArea.CREATIVITY to listOf(
                R.drawable.ic_creativity_lightbulb,
                R.drawable.ic_creativity_palette,
                R.drawable.ic_creativity_pencil,
            ),
            LifeArea.REST to listOf(
                R.drawable.ic_rest_moon_star,
                R.drawable.ic_rest_pillow,
                R.drawable.ic_rest_tea_mug,
            ),
        )
    }
}
