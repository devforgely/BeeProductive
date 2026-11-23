package com.devforgely.beeproductive.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devforgely.beeproductive.data.repository.BillingRepository
import com.devforgely.beeproductive.data.repository.HiveRepository
import com.devforgely.beeproductive.data.repository.NotificationRepository
import com.devforgely.beeproductive.data.repository.UserPreferencesRepository
import com.devforgely.beeproductive.domain.model.GoalCompletion
import com.devforgely.beeproductive.domain.model.HabitAddResult
import com.devforgely.beeproductive.domain.model.HiveSnapshot
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

    fun completeHabit(id: Long, onDone: (GoalCompletion) -> Unit = {}) = launch {
        onDone(repository.completeHabit(id))
    }

    fun renameHabit(id: Long, title: String) = launch { repository.renameHabit(id, title) }

    fun addHabit(title: String, onDone: (HabitAddResult) -> Unit = {}) = launch {
        onDone(repository.addHabit(title))
    }

    fun reorderHabits(orderedIds: List<Long>) = launch { repository.reorderHabits(orderedIds) }

    fun grantFocusBlock(onGranted: (Boolean) -> Unit = {}) = launch {
        onGranted(repository.grantFocusBlock())
    }

    fun repair(onResult: (RepairOutcome) -> Unit) = launch { onResult(repository.repairStreak()) }

    fun buy(id: String, onResult: (ShopOutcome) -> Unit) = launch { onResult(repository.buy(id)) }

    fun setMorning(enabled: Boolean) = launch {
        preferences.setMorningNotes(enabled)
        notifications.applySchedule()
    }

    fun setEvening(enabled: Boolean) = launch {
        preferences.setEveningNotes(enabled)
        notifications.applySchedule()
    }

    fun retryOffers() = launch { billing.refresh() }

    fun launchPurchase(activity: Activity, productId: String): Boolean = billing.launch(activity, productId)

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}