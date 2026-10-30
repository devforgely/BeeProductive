package com.devforgely.beeproductive.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devforgely.beeproductive.data.repository.BillingEvent
import com.devforgely.beeproductive.data.repository.BillingRepository
import com.devforgely.beeproductive.data.repository.HiveRepository
import com.devforgely.beeproductive.data.repository.NotificationRepository
import com.devforgely.beeproductive.data.repository.UserPreferencesRepository
import com.devforgely.beeproductive.domain.model.ForageOutcome
import com.devforgely.beeproductive.domain.model.HabitAddResult
import com.devforgely.beeproductive.domain.model.HarvestChoice
import com.devforgely.beeproductive.domain.model.HiveSnapshot
import com.devforgely.beeproductive.domain.model.JournalKind
import com.devforgely.beeproductive.domain.model.LifeArea
import com.devforgely.beeproductive.domain.model.Mood
import com.devforgely.beeproductive.domain.model.OfferLoad
import com.devforgely.beeproductive.domain.model.RepairOutcome
import com.devforgely.beeproductive.domain.model.ShopOutcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HiveViewModel @Inject constructor(
    private val repository: HiveRepository,
    private val preferences: UserPreferencesRepository,
    private val billing: BillingRepository,
    private val notifications: NotificationRepository,
) : ViewModel() {
    val snapshot: StateFlow<HiveSnapshot> = repository.observe().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HiveSnapshot(),
    )
    val prefs = preferences.prefs.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        com.devforgely.beeproductive.data.repository.HivePrefs(),
    )
    val offers: StateFlow<OfferLoad> = billing.offers
    val billingEvents = billing.events

    fun checkIn(mood: Mood) = launch { repository.checkIn(mood) }

    fun completeHabit(id: Long, onDone: (Boolean) -> Unit = {}) = launch {
        onDone(repository.completeHabit(id))
    }

    fun addHabit(title: String, onResult: (HabitAddResult) -> Unit) = launch {
        onResult(repository.addHabit(title))
    }

    fun renameHabit(id: Long, title: String) = launch { repository.renameHabit(id, title) }

    fun retireHabit(id: Long) = launch { repository.retireHabit(id) }

    fun bloomGoal(id: Long) = launch { repository.bloomGoal(id) }

    fun forage(onResult: (ForageOutcome) -> Unit) = launch { onResult(repository.forage()) }

    fun saveJournal(kind: JournalKind, prompt: String, body: String) = launch {
        repository.saveJournal(kind, prompt, body)
    }

    fun grantFocus(onGranted: (Boolean) -> Unit) = launch { onGranted(repository.grantFocus()) }

    fun repair(onResult: (RepairOutcome) -> Unit) = launch { onResult(repository.repairStreak()) }

    fun buy(id: String, onResult: (ShopOutcome) -> Unit) = launch { onResult(repository.buy(id)) }

    fun equip(id: String) = launch { repository.equip(id) }

    fun acknowledgePhase(dayMark: Int, reflection: String, onDone: () -> Unit) = launch {
        repository.acknowledgePhase(dayMark, reflection)
        onDone()
    }

    fun harvest(choice: HarvestChoice, northStar: String, onDone: () -> Unit) = launch {
        repository.harvest(choice, northStar)
        onDone()
    }

    fun plantFresh(northStar: String, area: LifeArea, habits: List<String>, tone: String, onDone: () -> Unit) = launch {
        repository.plantFreshCycle(northStar, area, habits, tone)
        onDone()
    }

    fun saveWeekly(note: String, goal: String) = launch {
        repository.saveWeeklyNote(note)
        if (goal.isNotBlank()) repository.addWeeklyGoal(goal)
    }

    fun setDusk(enabled: Boolean) = launch { preferences.setDusk(enabled) }

    fun setMorning(enabled: Boolean) = launch {
        preferences.setMorningNotes(enabled)
        notifications.applySchedule()
    }

    fun setEvening(enabled: Boolean) = launch {
        preferences.setEveningNotes(enabled)
        notifications.applySchedule()
    }

    fun restorePurchases() = launch { billing.refresh() }

    fun retryOffers() = launch { billing.refresh() }

    fun launchPurchase(activity: Activity, productId: String): Boolean = billing.launch(activity, productId)

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}

typealias HiveBillingEvent = BillingEvent
