package com.devforgely.beeproductive.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

object UserPreferencesKeys {
    val DUSK_MODE = booleanPreferencesKey("dusk_mode")
    val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    val MORNING_NOTES = booleanPreferencesKey("morning_notes")
    val EVENING_NOTES = booleanPreferencesKey("evening_notes")
    val SUPPORTER = booleanPreferencesKey("bee_supporter")
    val LAST_SUPPORT = stringPreferencesKey("last_support_product")
}

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "com.devforgely.beeproductive.user_preferences",
)

@Module
@InstallIn(SingletonComponent::class)
object UserPreferencesModule {
    @Provides
    @Singleton
    fun provideUserDataStorePreferences(
        @ApplicationContext applicationContext: Context,
    ): DataStore<Preferences> = applicationContext.userDataStore
}
