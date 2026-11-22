package com.devforgely.beeproductive.domain

import com.devforgely.beeproductive.domain.model.CombModel
import com.devforgely.beeproductive.domain.model.CycleStage
import com.devforgely.beeproductive.domain.model.HoneyRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HoneyRulesTest {
    @Test
    fun habitAwardsTenHoney() {
        assertEquals(10, HoneyRules.HABIT_HONEY)
    }

    @Test
    fun dailyGoalCapsAtThree() {
        assertEquals(0, HoneyRules.dailyGoal(0))
        assertEquals(2, HoneyRules.dailyGoal(2))
        assertEquals(3, HoneyRules.dailyGoal(5))
    }

    @Test
    fun goalMetUsesDailyGoal() {
        assertFalse(HoneyRules.goalMet(2, 5))
        assertTrue(HoneyRules.goalMet(3, 5))
        assertTrue(HoneyRules.goalMet(2, 2))
    }

    @Test
    fun forageBonusWhenGoalMet() {
        assertEquals(5, HoneyRules.forageBonus())
        assertEquals(10, HoneyRules.FORAGE_HONEY + HoneyRules.forageBonus())
    }

    @Test
    fun focusHoneyIsOnePerFiveMinutes() {
        assertEquals(1, HoneyRules.FOCUS_HONEY)
        assertEquals(5, HoneyRules.FOCUS_BLOCK_MINUTES)
    }

    @Test
    fun energyMeterCapsAtFifty() {
        assertEquals(50, HoneyRules.ENERGY_CAP)
    }

    @Test
    fun aFinishedGoalFillsOneCell() {
        assertEquals(1, HoneyRules.cellsForHoney(HoneyRules.HABIT_HONEY))
    }

    @Test
    fun combFillsInOrderAndCapsOnlyInStageThree() {
        assertEquals(6, CombModel.openCellCount(CycleStage.POLLINATING))
        assertEquals(18, CombModel.openCellCount(CycleStage.NECTAR_FLOW))
        assertEquals(36, CombModel.openCellCount(CycleStage.CAPPING))
        val partial = CombModel.cells(15, CycleStage.POLLINATING)
        assertEquals(6, partial.size)
        assertEquals(1f, partial[0].fill, 0.001f)
        assertEquals(0.5f, partial[1].fill, 0.001f)
        val capped = CombModel.cells(10, CycleStage.CAPPING)
        assertEquals(36, capped.size)
    }
}