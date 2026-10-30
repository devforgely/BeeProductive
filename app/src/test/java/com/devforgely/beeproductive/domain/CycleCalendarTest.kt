package com.devforgely.beeproductive.domain

import com.devforgely.beeproductive.domain.model.CycleCalendar
import com.devforgely.beeproductive.domain.model.CycleStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CycleCalendarTest {
    private val start = LocalDate.of(2026, 1, 1)

    @Test
    fun dayIndexStartsAtOne() {
        assertEquals(1, CycleCalendar.dayIndex(start, start))
    }

    @Test
    fun stageEdges() {
        assertEquals(CycleStage.POLLINATING, CycleCalendar.stage(1))
        assertEquals(CycleStage.POLLINATING, CycleCalendar.stage(30))
        assertEquals(CycleStage.NECTAR_FLOW, CycleCalendar.stage(31))
        assertEquals(CycleStage.NECTAR_FLOW, CycleCalendar.stage(60))
        assertEquals(CycleStage.CAPPING, CycleCalendar.stage(61))
        assertEquals(CycleStage.CAPPING, CycleCalendar.stage(90))
    }

    @Test
    fun day90StaysClamped() {
        assertEquals(90, CycleCalendar.dayIndex(start, start.plusDays(120)))
        assertTrue(CycleCalendar.isHarvestDay(90))
        assertTrue(CycleCalendar.isPhaseMilestone(30))
        assertTrue(CycleCalendar.isPhaseMilestone(60))
        assertFalse(CycleCalendar.isPhaseMilestone(31))
    }

    @Test
    fun weeksAdvanceEverySevenDays() {
        assertEquals(1, CycleCalendar.weekIndex(1))
        assertEquals(1, CycleCalendar.weekIndex(7))
        assertEquals(2, CycleCalendar.weekIndex(8))
        assertEquals(13, CycleCalendar.weekIndex(90))
    }
}
