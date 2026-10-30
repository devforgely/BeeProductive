package com.devforgely.beeproductive.domain.model

import java.time.LocalDate

data class BeeProfile(
    val name: String,
    val accent: BeeAccent,
    val personality: BeePersonality,
    val outfitId: String?,
    val decorationId: String?,
    val themeId: String?,
)

data class CycleView(
    val id: Long,
    val northStar: String,
    val lifeArea: LifeArea,
    val themeNote: String,
    val startDate: LocalDate,
    val dayIndex: Int,
    val stage: CycleStage,
    val harvested: Boolean,
)

data class HabitItem(
    val id: Long,
    val title: String,
    val active: Boolean,
    val doneToday: Boolean,
)

data class GoalItem(
    val id: Long,
    val title: String,
    val stage: CycleStage,
    val bloomed: Boolean,
    val weekly: Boolean,
)

data class JournalItem(
    val id: Long,
    val day: LocalDate,
    val kind: JournalKind,
    val prompt: String,
    val body: String,
)

data class JarItem(
    val id: Long,
    val title: String,
    val subtitle: String,
    val kind: String,
    val dayMark: Int,
)

data class ForageItem(
    val day: LocalDate,
    val kind: ForageRewardKind,
    val title: String,
    val body: String,
)

data class CheckItem(
    val day: LocalDate,
    val mood: Mood,
)

data class NectarItem(
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
    val goals: List<GoalItem> = emptyList(),
    val journals: List<JournalItem> = emptyList(),
    val jars: List<JarItem> = emptyList(),
    val forages: List<ForageItem> = emptyList(),
    val checks: List<CheckItem> = emptyList(),
    val nectar: List<NectarItem> = emptyList(),
    val logs: List<LogItem> = emptyList(),
    val ownedIds: Set<String> = emptySet(),
    val repairedDays: Set<LocalDate> = emptySet(),
    val repairedStages: Set<CycleStage> = emptySet(),
    val milestones: Set<Int> = emptySet(),
    val focusGrantedToday: Boolean = false,
    val weeklyNote: String? = null,
    val today: LocalDate = LocalDate.now(),
) {
    val activeHabits: List<HabitItem> get() = habits.filter { it.active }
    val completedToday: Int get() = activeHabits.count { it.doneToday }
    val dailyGoal: Int get() = NectarRules.dailyGoal(activeHabits.size)
    val goalMet: Boolean get() = NectarRules.goalMet(completedToday, activeHabits.size)
    val balance: Int get() = nectar.sumOf { it.amount }.coerceAtLeast(0)
    val earnedThisCycle: Int
        get() {
            val id = cycle?.id ?: return 0
            return nectar.filter { it.cycleId == id && it.amount > 0 }.sumOf { it.amount }
        }
    val todayNectar: Int
        get() {
            val id = cycle?.id ?: return 0
            return nectar.filter { it.cycleId == id && it.day == today && it.amount > 0 }.sumOf { it.amount }
        }
    val mood: Mood? get() = checks.firstOrNull { it.day == today }?.mood
    val foragedToday: Boolean get() = forages.any { it.day == today }
    val cells: List<CellVisual>
        get() = cycle?.let { CombModel.cells(earnedThisCycle, it.stage) }.orEmpty()
    val queenGlow: Float get() = cycle?.let { CombModel.queenGlow(it.dayIndex) } ?: 0.05f
    val todayFill: Float get() = CombModel.todayFill(todayNectar)

    val streak: Int
        get() {
            val goal = dailyGoal
            if (goal == 0) return 0
            val goalDays = logs
                .filter { it.completed }
                .groupBy { it.day }
                .filterValues { dayLogs -> dayLogs.size >= goal }
                .keys
            return StreakCalculator.current(today, goalDays, repairedDays)
        }

    val ritual: Ritual?
        get() {
            val current = cycle ?: return null
            if (current.harvested) return null
            if (current.dayIndex >= CycleCalendar.CYCLE_LENGTH) return Ritual.HARVEST
            if (current.dayIndex >= 30 && 30 !in milestones) return Ritual.PHASE_30
            if (current.dayIndex >= 60 && 60 !in milestones) return Ritual.PHASE_60
            return null
        }

    val greeting: String
        get() = HiveCopy.greeting(
            beeName = bee?.name ?: "little worker",
            personality = bee?.personality ?: BeePersonality.CALM,
            mood = mood,
            streak = streak,
        )
}

data class OnboardingDraft(
    val name: String,
    val accent: BeeAccent,
    val personality: BeePersonality,
    val area: LifeArea,
    val tone: String,
    val northStar: String,
    val habits: List<String>,
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

sealed interface ForageOutcome {
    data class Found(val title: String, val body: String, val kind: ForageRewardKind) : ForageOutcome
    data object NotReady : ForageOutcome
    data object AlreadyWent : ForageOutcome
    data object NoCycle : ForageOutcome
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
