package com.devforgely.beeproductive.domain.model

object NectarRules {
    const val HABIT_NECTAR = 10
    const val MINI_GOAL_NECTAR = 15
    const val FORAGE_NECTAR = 5
    const val PLUS_FORAGE_BONUS = 5
    const val FOCUS_NECTAR = 5
    const val REPAIR_NECTAR = 8
    const val FREE_HABIT_CAP = 3
    const val CELL_CAPACITY = 10

    fun dailyGoal(activeHabitCount: Int): Int = when {
        activeHabitCount <= 0 -> 0
        activeHabitCount < FREE_HABIT_CAP -> activeHabitCount
        else -> FREE_HABIT_CAP
    }

    fun goalMet(completedToday: Int, activeHabitCount: Int): Boolean {
        val goal = dailyGoal(activeHabitCount)
        return goal > 0 && completedToday >= goal
    }

    fun canGrantFocus(alreadyGrantedToday: Boolean): Boolean = !alreadyGrantedToday

    fun forageBonus(): Int = PLUS_FORAGE_BONUS
}
