package com.devforgely.beeproductive.core.navigation

object Routes {
    const val ONBOARDING = "onboarding"
    const val SUPPORTER = "supporter"
    const val HOME = "home"
    const val QUESTS = "quests"
    const val QUEST = "quest/{area}"
    const val HIVE = "hive"
    const val SHOP = "shop"
    const val BEE = "bee"
    const val FOCUS = "focus"
    const val CELEBRATION = "celebration/{honey}/{cells}"
    const val SETTINGS = "settings"

    val tabs = setOf(HOME, QUESTS, HIVE, SHOP, BEE)

    fun quest(area: String) = "quest/$area"

    fun celebration(honey: Int, cells: Int) = "celebration/$honey/$cells"
}
