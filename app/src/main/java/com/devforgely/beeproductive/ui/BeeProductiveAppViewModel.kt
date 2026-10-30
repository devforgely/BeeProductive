package com.devforgely.beeproductive.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devforgely.beeproductive.core.navigation.Routes
import com.devforgely.beeproductive.data.repository.HiveRepository
import com.devforgely.beeproductive.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BeeProductiveAppViewModel @Inject constructor(
    hiveRepository: HiveRepository,
    preferences: UserPreferencesRepository,
) : ViewModel() {
    val dusk = preferences.prefs
        .map { it.dusk }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val startRoute = combine(hiveRepository.observe(), preferences.prefs) { snapshot, prefs ->
        when {
            snapshot.bee == null || !prefs.onboardingComplete -> Routes.ONBOARDING
            else -> Routes.HIVE
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
