package com.devforgely.beeproductive.domain.model

import java.time.LocalDate

data class HabitLogMark(
    val habitId: Long,
    val day: LocalDate,
    val completed: Boolean,
)

data class RepairPlan(
    val day: LocalDate,
    val nectar: Int,
)

data class RepairApplication(
    val logs: List<HabitLogMark>,
    val repairedDays: List<LocalDate>,
)

object StreakCalculator {
    fun current(
        today: LocalDate,
        goalDays: Set<LocalDate>,
        repairedDays: Set<LocalDate>,
    ): Int {
        val met = goalDays + repairedDays
        var cursor = if (today in met) today else today.minusDays(1)
        if (cursor !in met) return 0
        var streak = 0
        while (cursor in met) {
            streak += 1
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}

object StreakRepair {
    fun plan(
        today: LocalDate,
        goalDays: Set<LocalDate>,
        repairedDays: Set<LocalDate>,
        alreadyRepairedThisStage: Boolean,
    ): RepairPlan? {
        if (alreadyRepairedThisStage) return null
        val yesterday = today.minusDays(1)
        if (yesterday in goalDays || yesterday in repairedDays) return null
        return RepairPlan(day = yesterday, nectar = NectarRules.REPAIR_NECTAR)
    }

    fun apply(logs: List<HabitLogMark>, plan: RepairPlan): RepairApplication {
        return RepairApplication(
            logs = logs,
            repairedDays = listOf(plan.day),
        )
    }
}
