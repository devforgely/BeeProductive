package com.devforgely.beeproductive.domain.model

import java.time.LocalDate

data class BeeProfile(
    val name: String,
    val personality: BeePersonality,
    val outfitId: String?,
    val decorationId: String?,
    val themeId: String?,
    val wallpaperId: String? = null,
    val flooringId: String? = null,
)

data class CycleView(
    val id: Long,
    val lifeArea: LifeArea,
    val dayIndex: Int,
    val stage: CycleStage,
)

data class HabitItem(
    val id: Long,
    val title: String,
    val description: String,
    val area: LifeArea,
    val active: Boolean,
    val doneToday: Boolean,
)

data class ForageItem(
    val day: LocalDate,
)

data class HoneyLedgerItem(
    val cycleId: Long,
    val day: LocalDate,
    val amount: Int,
)

data class LogItem(
    val habitId: Long,
    val day: LocalDate,
    val completed: Boolean,
)

data class HiveSnapshot(
    val bee: BeeProfile? = null,
    val cycle: CycleView? = null,
    val habits: List<HabitItem> = emptyList(),
    val forages: List<ForageItem> = emptyList(),
    val honeyLedger: List<HoneyLedgerItem> = emptyList(),
    val logs: List<LogItem> = emptyList(),
    val ownedIds: Set<String> = emptySet(),
    val repairedDays: Set<LocalDate> = emptySet(),
    val repairedStages: Set<CycleStage> = emptySet(),
    val milestones: Set<Int> = emptySet(),
    val today: LocalDate = LocalDate.now(),
) {
    val activeHabits: List<HabitItem> get() = habits.filter { it.active }
    val dailyGoal: Int get() = HoneyRules.dailyGoal(activeHabits.size)
    val balance: Int get() = honeyLedger.sumOf { it.amount }.coerceAtLeast(0)
    val earnedThisCycle: Int
        get() {
            val id = cycle?.id ?: return 0
            return honeyLedger.filter { it.cycleId == id && it.amount > 0 }.sumOf { it.amount }
        }
    val todayHoney: Int
        get() {
            val id = cycle?.id ?: return 0
            return honeyLedger.filter { it.cycleId == id && it.day == today && it.amount > 0 }.sumOf { it.amount }
        }
    val cells: List<CellVisual>
        get() = cycle?.let { CombModel.cells(earnedThisCycle, it.stage) }.orEmpty()

    val streak: Int
        get() = StreakCalculator.current(today, goalDays(), repairedDays)

    val energyShown: Int get() = todayHoney.coerceAtMost(HoneyRules.ENERGY_CAP)

    val questAreas: List<LifeArea>
        get() = LifeArea.entries.filter { area -> activeHabits.any { it.area == area } }

    fun habitsIn(area: LifeArea): List<HabitItem> = activeHabits.filter { it.area == area }

    fun completionsOn(area: LifeArea, day: LocalDate): Int {
        val ids = habits.filter { it.area == area }.map { it.id }.toSet()
        return logs.count { it.completed && it.day == day && it.habitId in ids }
    }

    fun areaRun(area: LifeArea): Int {
        val ids = habits.filter { it.area == area }.map { it.id }.toSet()
        val met = logs.filter { it.completed && it.habitId in ids }.map { it.day }.toSet()
        return StreakCalculator.current(today, met, emptySet())
    }

    fun honeyOn(day: LocalDate): Int {
        val id = cycle?.id ?: return 0
        return honeyLedger.filter { it.cycleId == id && it.day == day && it.amount > 0 }.sumOf { it.amount }
    }

    val repairPlan: RepairPlan?
        get() {
            val current = cycle ?: return null
            return StreakRepair.plan(
                today = today,
                goalDays = goalDays(),
                repairedDays = repairedDays,
                alreadyRepairedThisStage = current.stage in repairedStages,
            )
        }

    private fun goalDays(): Set<LocalDate> {
        val goal = dailyGoal
        if (goal == 0) return emptySet()
        return logs
            .filter { it.completed }
            .groupBy { it.day }
            .filterValues { dayLogs -> dayLogs.size >= goal }
            .keys
    }
}

data class PlannedHabit(
    val title: String,
    val area: LifeArea,
    val description: String = "",
)

data class OnboardingDraft(
    val name: String,
    val personality: BeePersonality,
    val area: LifeArea,
    val tone: String,
    val northStar: String,
    val habits: List<PlannedHabit>,
    val userName: String,
    val pronoun: String,
)

data class GoalCompletion(
    val saved: Boolean,
    val honey: Int = 0,
    val cells: Int = 0,
    val celebrate: Boolean = false,
)

data class SupportOffer(
    val productId: String,
    val title: String,
    val blurb: String,
    val formattedPrice: String,
)

sealed interface OfferLoad {
    data object Loading : OfferLoad
    data class Ready(val tiers: List<SupportOffer>) : OfferLoad
    data class Unavailable(val message: String) : OfferLoad
}

sealed interface HabitAddResult {
    data object Added : HabitAddResult
    data object NoCycle : HabitAddResult
}

sealed interface RepairOutcome {
    data object Repaired : RepairOutcome
    data object AlreadyUsed : RepairOutcome
    data object NothingToMend : RepairOutcome
    data object NoCycle : RepairOutcome
}

sealed interface ShopOutcome {
    data object Purchased : ShopOutcome
    data object TooExpensive : ShopOutcome
    data object Missing : ShopOutcome
}
