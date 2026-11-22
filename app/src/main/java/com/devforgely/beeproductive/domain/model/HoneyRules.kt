package com.devforgely.beeproductive.domain.model

object HoneyRules {
    const val HABIT_HONEY = 10
    const val MINI_GOAL_HONEY = 15
    const val FORAGE_HONEY = 5
    const val PLUS_FORAGE_BONUS = 5
    const val FOCUS_HONEY = 1
    const val FOCUS_BLOCK_MINUTES = 5
    const val ENERGY_CAP = 50
    const val REPAIR_HONEY = 8
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

    fun forageBonus(): Int = PLUS_FORAGE_BONUS

    fun cellsForHoney(honey: Int): Int = (honey.coerceAtLeast(0) / CELL_CAPACITY)
}