package com.devforgely.beeproductive.domain.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object CycleCalendar {
    const val CYCLE_LENGTH = 90

    fun dayIndex(start: LocalDate, today: LocalDate): Int {
        val raw = ChronoUnit.DAYS.between(start, today).toInt() + 1
        return raw.coerceIn(1, CYCLE_LENGTH)
    }

    fun stage(day: Int): CycleStage = when {
        day <= 30 -> CycleStage.POLLINATING
        day <= 60 -> CycleStage.NECTAR_FLOW
        else -> CycleStage.CAPPING
    }

    fun weekIndex(day: Int): Int = ((day.coerceIn(1, CYCLE_LENGTH) - 1) / 7) + 1

    fun isPhaseMilestone(day: Int): Boolean = day == 30 || day == 60

    fun isHarvestDay(day: Int): Boolean = day >= CYCLE_LENGTH
}
