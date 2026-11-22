package com.devforgely.beeproductive.domain.model

object HiveProgress {
    const val CELLS_PER_LEVEL = 10

    fun level(filledCells: Int): Int = (filledCells.coerceAtLeast(0) / CELLS_PER_LEVEL) + 1

    fun title(level: Int): String = when (level) {
        1 -> "Starter Comb"
        2 -> "Busy Comb"
        3 -> "Golden Comb"
        4 -> "Cozy Comb"
        else -> "Royal Comb"
    }

    fun cellsToNext(filledCells: Int): Int {
        val filled = filledCells.coerceAtLeast(0)
        val remainder = filled % CELLS_PER_LEVEL
        return if (remainder == 0 && filled > 0) CELLS_PER_LEVEL else CELLS_PER_LEVEL - remainder
    }

    fun progressToNext(filledCells: Int): Float {
        val remaining = cellsToNext(filledCells)
        return ((CELLS_PER_LEVEL - remaining).toFloat() / CELLS_PER_LEVEL).coerceIn(0f, 1f)
    }
}
