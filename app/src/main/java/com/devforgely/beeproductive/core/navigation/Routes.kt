package com.devforgely.beeproductive.core.navigation

object Routes {
    const val ONBOARDING = "onboarding"
    const val SUPPORTER = "supporter"
    const val HIVE = "hive"
    const val TODAY = "today"
    const val MEADOW = "meadow"
    const val APIARY = "apiary"
    const val REFLECT = "reflect"
    const val FOCUS = "focus"
    const val REFLECTION = "reflection"
    const val GRATITUDE = "gratitude"
    const val EVENING = "evening"
    const val FOCUS_FORAGE = "focus-forage"
    const val WEEKLY = "weekly"
    const val PHASE = "phase/{dayMark}"
    const val HARVEST = "harvest"
    const val SHOP = "shop"
    const val INSIGHTS = "insights"
    const val SETTINGS = "settings"
    const val NEW_CYCLE = "new-cycle"

    val tabs = setOf(HIVE, TODAY, MEADOW, APIARY)

    fun phase(dayMark: Int) = "phase/$dayMark"
}
