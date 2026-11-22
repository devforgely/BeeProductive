package com.devforgely.beeproductive.domain.model

data class CellVisual(
    val index: Int,
    val ring: Int,
    val fill: Float,
    val capped: Boolean,
)

object CombModel {
    fun openCellCount(stage: CycleStage): Int = when (stage) {
        CycleStage.POLLINATING -> 6
        CycleStage.NECTAR_FLOW -> 18
        CycleStage.CAPPING -> 36
    }

    fun cells(cycleHoney: Int, stage: CycleStage): List<CellVisual> {
        val open = openCellCount(stage)
        var remaining = cycleHoney.coerceAtLeast(0)
        return (0 until open).map { index ->
            val taken = remaining.coerceAtMost(HoneyRules.CELL_CAPACITY)
            remaining -= taken
            val fill = taken / HoneyRules.CELL_CAPACITY.toFloat()
            val ring = ringFor(index)
            CellVisual(
                index = index,
                ring = ring,
                fill = fill,
                capped = stage == CycleStage.CAPPING && fill >= 1f,
            )
        }
    }

    fun todayFill(todayHoney: Int): Float {
        val cap = HoneyRules.CELL_CAPACITY * HoneyRules.FREE_HABIT_CAP
        return (todayHoney.coerceAtLeast(0) / cap.toFloat()).coerceIn(0f, 1f)
    }

    fun queenGlow(dayIndex: Int): Float =
        (dayIndex.coerceIn(1, CycleCalendar.CYCLE_LENGTH) / CycleCalendar.CYCLE_LENGTH.toFloat())

    private fun ringFor(index: Int): Int = when {
        index < 6 -> 1
        index < 18 -> 2
        else -> 3
    }
}
