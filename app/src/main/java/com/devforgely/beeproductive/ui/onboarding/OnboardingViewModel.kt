package com.devforgely.beeproductive.ui.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devforgely.beeproductive.data.repository.HiveRepository
import com.devforgely.beeproductive.domain.model.BeeAccent
import com.devforgely.beeproductive.domain.model.BeePersonality
import com.devforgely.beeproductive.domain.model.LifeArea
import com.devforgely.beeproductive.domain.model.LifeAreaTemplates
import com.devforgely.beeproductive.domain.model.OnboardingDraft
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class OnboardingStep {
    Welcome,
    Story,
    Meet,
    Area,
    State,
    Star,
    First,
    Celebration,
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: HiveRepository,
) : ViewModel() {
    var step by mutableStateOf(OnboardingStep.Welcome)
    var storyPage by mutableIntStateOf(0)
    var name by mutableStateOf("")
    var accent by mutableStateOf(BeeAccent.SAGE)
    var personality by mutableStateOf(BeePersonality.CALM)
    var area by mutableStateOf(LifeArea.MIND_FOCUS)
    var stateId by mutableStateOf<String?>(null)
    var tone by mutableStateOf("Start gently.")
    var northStar by mutableStateOf(LifeAreaTemplates.forArea(LifeArea.MIND_FOCUS).northStar)
    var habits by mutableStateOf(LifeAreaTemplates.forArea(LifeArea.MIND_FOCUS).starterHabits)
    var saving by mutableStateOf(false)

    fun chooseArea(next: LifeArea) {
        area = next
        val template = LifeAreaTemplates.forArea(next)
        northStar = template.northStar
        habits = template.starterHabits
        stateId = null
    }

    fun chooseState(id: String, nextTone: String) {
        stateId = id
        tone = nextTone
    }

    fun updateHabit(index: Int, value: String) {
        habits = habits.toMutableList().also { list ->
            if (index in list.indices) list[index] = value
        }
    }

    fun celebrateFirst(onFinished: () -> Unit) {
        if (saving) return
        viewModelScope.launch {
            saving = true
            step = OnboardingStep.Celebration
            repository.plantHive(
                OnboardingDraft(
                    name = name,
                    accent = accent,
                    personality = personality,
                    area = area,
                    tone = tone,
                    northStar = northStar,
                    habits = habits,
                ),
            )
            delay(1700)
            saving = false
            onFinished()
        }
    }
}
