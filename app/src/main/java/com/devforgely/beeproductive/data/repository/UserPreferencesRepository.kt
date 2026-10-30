package com.devforgely.beeproductive.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.devforgely.beeproductive.data.local.UserPreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class HivePrefs(
    val dusk: Boolean = false,
    val onboardingComplete: Boolean = false,
    val morningNotes: Boolean = true,
    val eveningNotes: Boolean = false,
    val supporter: Boolean = false,
    val lastSupportProduct: String? = null,
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val prefs: Flow<HivePrefs> = dataStore.data.map { store ->
        HivePrefs(
            dusk = store[UserPreferencesKeys.DUSK_MODE] ?: false,
            onboardingComplete = store[UserPreferencesKeys.ONBOARDING_COMPLETE] ?: false,
            morningNotes = store[UserPreferencesKeys.MORNING_NOTES] ?: true,
            eveningNotes = store[UserPreferencesKeys.EVENING_NOTES] ?: false,
            supporter = store[UserPreferencesKeys.SUPPORTER] ?: false,
            lastSupportProduct = store[UserPreferencesKeys.LAST_SUPPORT],
        )
    }

    suspend fun setDusk(enabled: Boolean) = edit { it[UserPreferencesKeys.DUSK_MODE] = enabled }

    suspend fun setOnboardingComplete() = edit {
        it[UserPreferencesKeys.ONBOARDING_COMPLETE] = true
    }

    suspend fun setMorningNotes(enabled: Boolean) = edit {
        it[UserPreferencesKeys.MORNING_NOTES] = enabled
    }

    suspend fun setEveningNotes(enabled: Boolean) = edit {
        it[UserPreferencesKeys.EVENING_NOTES] = enabled
    }

    suspend fun markSupporter(productId: String) = edit {
        it[UserPreferencesKeys.SUPPORTER] = true
        it[UserPreferencesKeys.LAST_SUPPORT] = productId
    }

    private suspend fun edit(block: suspend (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        dataStore.edit { block(it) }
    }
}
