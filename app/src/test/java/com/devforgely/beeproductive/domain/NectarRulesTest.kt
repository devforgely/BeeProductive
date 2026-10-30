package com.devforgely.beeproductive.domain

import com.devforgely.beeproductive.domain.model.CombModel
import com.devforgely.beeproductive.domain.model.CycleStage
import com.devforgely.beeproductive.domain.model.NectarRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NectarRulesTest {
    @Test
    fun habitAwardsTenNectar() {
        assertEquals(10, NectarRules.HABIT_NECTAR)
    }

    @Test
    fun dailyGoalShrinksForTinyHives() {
        assertEquals(0, NectarRules.dailyGoal(0))
        assertEquals(2, NectarRules.dailyGoal(2))
        assertEquals(3, NectarRules.dailyGoal(5))
    }

    @Test
    fun goalMetRequiresTheDailyCount() {
        assertFalse(NectarRules.goalMet(2, 5))
        assertTrue(NectarRules.goalMet(3, 5))
        assertTrue(NectarRules.goalMet(2, 2))
    }

    @Test
    fun forageAlwaysIncludesTheBonus() {
        assertEquals(5, NectarRules.forageBonus())
        assertEquals(10, NectarRules.FORAGE_NECTAR + NectarRules.forageBonus())
    }

    @Test
    fun focusNectarIsOnce() {
        assertTrue(NectarRules.canGrantFocus(alreadyGrantedToday = false))
        assertFalse(NectarRules.canGrantFocus(alreadyGrantedToday = true))
    }

    @Test
    fun combFillsInOrderAndCapsOnlyInStageThree() {
        assertEquals(6, CombModel.openCellCount(CycleStage.POLLINATING))
        assertEquals(18, CombModel.openCellCount(CycleStage.NECTAR_FLOW))
        assertEquals(36, CombModel.openCellCount(CycleStage.CAPPING))
        val partial = CombModel.cells(15, CycleStage.POLLINATING)
        assertEquals(1f, partial[0].fill)
        assertEquals(0.5f, partial[1].fill)
        assertFalse(partial[0].capped)
        val capped = CombModel.cells(10, CycleStage.CAPPING)
        assertTrue(capped[0].capped)
        assertFalse(capped[1].capped)
    }
}
