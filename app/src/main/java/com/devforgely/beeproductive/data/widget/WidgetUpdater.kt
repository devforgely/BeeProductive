package com.devforgely.beeproductive.data.widget

import android.content.Context
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.devforgely.beeproductive.ui.widget.HiveWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class WidgetFace(
    val beeName: String,
    val moodLine: String,
    val fill: Float,
    val nectar: Int,
)

object WidgetKeys {
    val NAME = stringPreferencesKey("widget_bee_name")
    val MOOD = stringPreferencesKey("widget_mood")
    val FILL = floatPreferencesKey("widget_fill")
    val NECTAR = intPreferencesKey("widget_nectar")
}

@Singleton
class WidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun push(face: WidgetFace) {
        val manager = GlanceAppWidgetManager(context)
        val widget = HiveWidget()
        manager.getGlanceIds(HiveWidget::class.java).forEach { glanceId ->
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[WidgetKeys.NAME] = face.beeName
                    this[WidgetKeys.MOOD] = face.moodLine
                    this[WidgetKeys.FILL] = face.fill.coerceIn(0f, 1f)
                    this[WidgetKeys.NECTAR] = face.nectar
                }
            }
            widget.update(context, glanceId)
        }
    }
}
