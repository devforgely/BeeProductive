package com.devforgely.beeproductive.ui.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.devforgely.beeproductive.MainActivity
import com.devforgely.beeproductive.data.widget.WidgetKeys

class HiveWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val openApp = Intent(context, MainActivity::class.java)
        provideContent { HiveWidgetBody(openApp) }
    }
}

@androidx.compose.runtime.Composable
private fun HiveWidgetBody(openApp: Intent) {
    val prefs = currentState<Preferences>()
    val name = prefs[WidgetKeys.NAME] ?: "Your bee"
    val mood = prefs[WidgetKeys.MOOD] ?: "A small action is enough today."
    val fill = ((prefs[WidgetKeys.FILL] ?: 0f) * 100).toInt()
    val nectar = prefs[WidgetKeys.NECTAR] ?: 0
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFFFFF6E8)))
            .padding(12.dp)
            .clickable(actionStartActivity(openApp)),
    ) {
        Text(
            text = name,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = ColorProvider(Color(0xFF3E3428)),
            ),
        )
        Spacer(GlanceModifier.height(4.dp))
        Text(
            text = mood,
            style = TextStyle(fontSize = 12.sp, color = ColorProvider(Color(0xFF6E6256))),
            maxLines = 2,
        )
        Spacer(GlanceModifier.height(8.dp))
        Text(
            text = "Honeycomb $fill%  ·  $nectar nectar",
            style = TextStyle(fontSize = 12.sp, color = ColorProvider(Color(0xFFC9842A))),
        )
    }
}

class HiveWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HiveWidget()
}
