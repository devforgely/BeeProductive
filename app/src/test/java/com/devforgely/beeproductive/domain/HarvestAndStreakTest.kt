package com.devforgely.beeproductive.domain

import com.devforgely.beeproductive.domain.model.CycleSnapshot
import com.devforgely.beeproductive.domain.model.CycleStatus
import com.devforgely.beeproductive.domain.model.HabitLogMark
import com.devforgely.beeproductive.domain.model.HarvestChoice
import com.devforgely.beeproductive.domain.model.HarvestPlanner
import com.devforgely.beeproductive.domain.model.LifeArea
import com.devforgely.beeproductive.domain.model.StreakCalculator
import com.devforgely.beeproductive.domain.model.StreakRepair
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class HarvestAndStreakTest {
    private val cycle = CycleSnapshot(
        northStar = "Write every morning",
        lifeArea = LifeArea.CREATIVITY,
        themeNote = "Start gently.",
    )

    @Test
    fun archiveKeepsTheJarAndFreesTheHive() {
        val planned = HarvestPlanner.plan(HarvestChoice.ARCHIVE, cycle)
        assertEquals(CycleStatus.ARCHIVED, planned.closedStatus)
        assertEquals("Write every morning", planned.jarTitle)
        assertNull(planned.next)
    }

    @Test
    fun loopElevatesTheSameStar() {
        val planned = HarvestPlanner.plan(HarvestChoice.LOOP, cycle)
        assertEquals(CycleStatus.LOOPED, planned.closedStatus)
        assertEquals("Write every morning", planned.next?.northStar)
        assertTrue(planned.next?.elevate == true)
    }

    @Test
    fun pivotCarriesTheThemeWithoutRaisingTheBar() {
        val planned = HarvestPlanner.plan(HarvestChoice.PIVOT, cycle)
        assertEquals(CycleStatus.PIVOTED, planned.closedStatus)
        assertEquals(LifeArea.CREATIVITY, planned.next?.lifeArea)
        assertTrue(planned.next?.elevate == false)
    }

    @Test
    fun repairDoesNotInventHabitLogs() {
        val today = LocalDate.of(2026, 3, 2)
        val yesterday = today.minusDays(1)
        val logs = listOf(HabitLogMark(habitId = 1, day = yesterday, completed = false))
        val plan = StreakRepair.plan(
            today = today,
            goalDays = emptySet(),
            repairedDays = emptySet(),
            alreadyRepairedThisStage = false,
        )
        val applied = StreakRepair.apply(logs, plan!!)
        assertEquals(logs, applied.logs)
        assertEquals(listOf(yesterday), applied.repairedDays)
        assertEquals(1, StreakCalculator.current(today, emptySet(), applied.repairedDays.toSet()))
    }

    @Test
    fun repairIsUnavailableAfterItIsUsedThisStage() {
        val today = LocalDate.of(2026, 3, 2)
        assertNull(
            StreakRepair.plan(today, emptySet(), emptySet(), alreadyRepairedThisStage = true),
        )
    }
}
