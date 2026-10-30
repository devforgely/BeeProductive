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

    fun ringCount(stage: CycleStage): Int = when (stage) {
        CycleStage.POLLINATING -> 1
        CycleStage.NECTAR_FLOW -> 2
        CycleStage.CAPPING -> 3
    }

    fun cells(cycleNectar: Int, stage: CycleStage): List<CellVisual> {
        val open = openCellCount(stage)
        var remaining = cycleNectar.coerceAtLeast(0)
        return (0 until open).map { index ->
            val taken = remaining.coerceAtMost(NectarRules.CELL_CAPACITY)
            remaining -= taken
            val fill = taken / NectarRules.CELL_CAPACITY.toFloat()
            val ring = ringFor(index)
            CellVisual(
                index = index,
                ring = ring,
                fill = fill,
                capped = stage == CycleStage.CAPPING && fill >= 1f,
            )
        }
    }

    fun todayFill(todayNectar: Int): Float {
        val cap = NectarRules.CELL_CAPACITY * NectarRules.FREE_HABIT_CAP
        return (todayNectar.coerceAtLeast(0) / cap.toFloat()).coerceIn(0f, 1f)
    }

    fun queenGlow(dayIndex: Int): Float =
        (dayIndex.coerceIn(1, CycleCalendar.CYCLE_LENGTH) / CycleCalendar.CYCLE_LENGTH.toFloat())

    private fun ringFor(index: Int): Int = when {
        index < 6 -> 1
        index < 18 -> 2
        else -> 3
    }
}
