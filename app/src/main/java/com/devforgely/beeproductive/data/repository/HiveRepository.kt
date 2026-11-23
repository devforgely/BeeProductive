package com.devforgely.beeproductive.data.repository

import com.devforgely.beeproductive.data.local.dao.HiveDao
import com.devforgely.beeproductive.data.local.entity.BeeProfileEntity
import com.devforgely.beeproductive.data.local.entity.CheckInEntity
import com.devforgely.beeproductive.data.local.entity.CycleEntity
import com.devforgely.beeproductive.data.local.entity.FocusGrantEntity
import com.devforgely.beeproductive.data.local.entity.ForageEntity
import com.devforgely.beeproductive.data.local.entity.HabitEntity
import com.devforgely.beeproductive.data.local.entity.HabitLogEntity
import com.devforgely.beeproductive.data.local.entity.HoneyJarEntity
import com.devforgely.beeproductive.data.local.entity.HoneyLedgerEntryEntity
import com.devforgely.beeproductive.data.local.entity.JournalEntity
import com.devforgely.beeproductive.data.local.entity.MilestoneEntity
import com.devforgely.beeproductive.data.local.entity.MiniGoalEntity
import com.devforgely.beeproductive.data.local.entity.OwnedCosmeticEntity
import com.devforgely.beeproductive.data.local.entity.StreakRepairEntity
import com.devforgely.beeproductive.data.local.entity.WeeklyReviewEntity
import com.devforgely.beeproductive.data.widget.WidgetFace
import com.devforgely.beeproductive.data.widget.WidgetUpdater
import com.devforgely.beeproductive.domain.model.BeePersonality
import com.devforgely.beeproductive.domain.model.BeeProfile
import com.devforgely.beeproductive.domain.model.CheckItem
import com.devforgely.beeproductive.domain.model.CosmeticCatalog
import com.devforgely.beeproductive.domain.model.CosmeticKind
import com.devforgely.beeproductive.domain.model.CycleCalendar
import com.devforgely.beeproductive.domain.model.CycleStage
import com.devforgely.beeproductive.domain.model.CycleStatus
import com.devforgely.beeproductive.domain.model.CycleView
import com.devforgely.beeproductive.domain.model.ForageItem
import com.devforgely.beeproductive.domain.model.ForageRewardKind
import com.devforgely.beeproductive.domain.model.GoalCompletion
import com.devforgely.beeproductive.domain.model.GoalItem
import com.devforgely.beeproductive.domain.model.HabitAddResult
import com.devforgely.beeproductive.domain.model.HabitItem
import com.devforgely.beeproductive.domain.model.HiveSnapshot
import com.devforgely.beeproductive.domain.model.HoneyLedgerItem
import com.devforgely.beeproductive.domain.model.HoneyRules
import com.devforgely.beeproductive.domain.model.JarItem
import com.devforgely.beeproductive.domain.model.JournalItem
import com.devforgely.beeproductive.domain.model.JournalKind
import com.devforgely.beeproductive.domain.model.LifeArea
import com.devforgely.beeproductive.domain.model.LifeAreaTemplate
import com.devforgely.beeproductive.domain.model.LifeAreaTemplates
import com.devforgely.beeproductive.domain.model.LogItem
import com.devforgely.beeproductive.domain.model.Mood
import com.devforgely.beeproductive.domain.model.OnboardingDraft
import com.devforgely.beeproductive.domain.model.PlannedHabit
import com.devforgely.beeproductive.domain.model.RepairOutcome
import com.devforgely.beeproductive.domain.model.ShopOutcome
import com.devforgely.beeproductive.domain.model.StreakRepair
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HiveRepository @Inject constructor(
    private val dao: HiveDao,
    private val preferences: UserPreferencesRepository,
    private val notifications: NotificationRepository,
    private val widgetUpdater: WidgetUpdater,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        scope.launch {
            observe().collect { snapshot ->
                widgetUpdater.push(
                    WidgetFace(
                        beeName = snapshot.bee?.name ?: "Your bee",
                        moodLine = snapshot.greeting,
                        fill = snapshot.todayFill,
                        honey = snapshot.balance,
                    ),
                )
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun observe(): Flow<HiveSnapshot> {
        val core = combine(
            dao.observeBee(),
            dao.observeActiveCycle(),
            dao.observeJars(),
            dao.observeHoneyLedger(),
            dao.observeOwned(),
        ) { bee, cycle, jars, honeyLedger, owned ->
            CoreHeader(bee, cycle, jars, honeyLedger, owned.map { it.cosmeticId }.toSet())
        }
        return core.flatMapLatest { coreHeader ->
            val header = Header(
                coreHeader.bee,
                coreHeader.cycle,
                coreHeader.jars,
                coreHeader.honeyLedger,
                coreHeader.owned,
            )
            val today = LocalDate.now()
            val cycle = header.cycle
            if (cycle == null) {
                flowOf(header.toSnapshot(today))
            } else {
                val day = CycleCalendar.dayIndex(LocalDate.parse(cycle.startDate), today)
                val week = CycleCalendar.weekIndex(day)
                combine(
                    dao.observeHabits(cycle.id),
                    dao.observeLogs(cycle.id),
                    dao.observeGoals(cycle.id),
                    dao.observeCheckIns(cycle.id),
                    dao.observeForages(cycle.id),
                ) { habits, logs, goals, checks, forages ->
                    CycleChunk(habits, logs, goals, checks, forages)
                }.combine(
                    combine(
                        dao.observeJournals(cycle.id),
                        dao.observeWeekly(cycle.id, week),
                        dao.observeRepairs(cycle.id),
                        dao.observeMilestones(cycle.id),
                        dao.observeFocusCount(cycle.id, today.toString()),
                    ) { journals, weekly, repairs, milestones, focusCount ->
                        ExtraChunk(journals, weekly, repairs, milestones, focusCount > 0)
                    },
                ) { chunk, extra ->
                    header.toSnapshot(today, chunk, extra)
                }
            }
        }
    }

    suspend fun buildHive(draft: OnboardingDraft) {
        val today = LocalDate.now()
        preferences.setIdentity(draft.userName.trim(), draft.pronoun.trim())
        dao.upsertBee(
            BeeProfileEntity(
                name = draft.name.trim().ifBlank { "Clover" },
                personality = draft.personality.name,
                outfitId = null,
                decorationId = null,
                themeId = null,
            ),
        )
        val cycleId = insertActiveCycle(
            northStar = draft.northStar,
            area = draft.area,
            tone = draft.tone,
            start = today,
        )
        val planned = draft.habits.ifEmpty {
            LifeAreaTemplates.forArea(draft.area).starterHabits.map { PlannedHabit(it, draft.area) }
        }
        insertHabits(cycleId, planned)
        insertGoals(cycleId, LifeAreaTemplates.forArea(draft.area), elevate = false)
        preferences.setOnboardingComplete()
        notifications.applySchedule()
    }

    suspend fun completeHabit(habitId: Long): GoalCompletion {
        val cycle = dao.activeCycle() ?: return GoalCompletion(saved = false)
        val today = LocalDate.now().toString()
        if (dao.logFor(habitId, today)?.completed == true) return GoalCompletion(saved = false)
        dao.insertLog(
            HabitLogEntity(
                habitId = habitId,
                cycleId = cycle.id,
                day = today,
                completed = true,
            ),
        )
        grant(cycle.id, today, HoneyRules.HABIT_HONEY, "habit")
        val snap = observe().first()
        val habit = snap.habits.firstOrNull { it.id == habitId } ?: return GoalCompletion(
            saved = true,
            honey = HoneyRules.HABIT_HONEY,
            cells = HoneyRules.cellsForHoney(HoneyRules.HABIT_HONEY),
        )
        val areaGoals = snap.activeHabits.filter { it.area == habit.area }
        val areaDone = areaGoals.isNotEmpty() && areaGoals.all { it.doneToday }
        val dayDone = snap.activeHabits.isNotEmpty() && snap.activeHabits.all { it.doneToday }
        return GoalCompletion(
            saved = true,
            honey = HoneyRules.HABIT_HONEY,
            cells = HoneyRules.cellsForHoney(HoneyRules.HABIT_HONEY),
            celebrate = areaDone || dayDone,
        )
    }

    suspend fun addHabit(title: String): HabitAddResult {
        val cleaned = title.trim()
        if (cleaned.isEmpty()) return HabitAddResult.NoCycle
        val snap = observe().first()
        val cycle = snap.cycle ?: return HabitAddResult.NoCycle
        dao.insertHabit(
            HabitEntity(
                cycleId = cycle.id,
                title = cleaned,
                description = "",
                lifeArea = cycle.lifeArea.name,
                active = true,
                sortOrder = snap.habits.size,
            ),
        )
        return HabitAddResult.Added
    }

    suspend fun renameHabit(id: Long, title: String) {
        val cleaned = title.trim()
        if (cleaned.isNotEmpty()) dao.renameHabit(id, cleaned)
    }

    suspend fun reorderHabits(orderedIds: List<Long>) {
        orderedIds.forEachIndexed { index, id ->
            dao.updateHabitSort(id, index)
        }
    }

    suspend fun grantFocusBlock(): Boolean {
        val cycle = dao.activeCycle() ?: return false
        val today = LocalDate.now().toString()
        dao.insertFocusGrant(FocusGrantEntity(cycleId = cycle.id, day = today))
        grant(cycle.id, today, HoneyRules.FOCUS_HONEY, "focus")
        return true
    }

    suspend fun repairStreak(): RepairOutcome {
        val snap = observe().first()
        val cycle = snap.cycle ?: return RepairOutcome.NoCycle
        if (cycle.stage in snap.repairedStages) return RepairOutcome.AlreadyUsed
        val goal = snap.dailyGoal
        val goalDays = snap.logs
            .filter { it.completed }
            .groupBy { it.day }
            .filterValues { it.size >= goal }
            .keys
        val plan = StreakRepair.plan(
            today = snap.today,
            goalDays = goalDays,
            repairedDays = snap.repairedDays,
            alreadyRepairedThisStage = false,
        ) ?: return RepairOutcome.NothingToMend
        dao.insertRepair(
            StreakRepairEntity(
                cycleId = cycle.id,
                stage = cycle.stage.name,
                day = plan.day.toString(),
            ),
        )
        grant(cycle.id, plan.day.toString(), plan.honey, "repair")
        return RepairOutcome.Repaired
    }

    suspend fun buy(cosmeticId: String): ShopOutcome {
        val item = CosmeticCatalog.find(cosmeticId) ?: return ShopOutcome.Missing
        val snap = observe().first()
        val cycle = snap.cycle ?: return ShopOutcome.Missing
        if (cosmeticId in snap.ownedIds) {
            equip(cosmeticId)
            return ShopOutcome.Purchased
        }
        if (snap.balance < item.honeyCost) return ShopOutcome.TooExpensive
        val today = snap.today.toString()
        dao.insertHoneyLedgerEntry(
            HoneyLedgerEntryEntity(
                cycleId = cycle.id,
                day = today,
                amount = -item.honeyCost,
                reason = "spend",
            ),
        )
        dao.insertOwned(OwnedCosmeticEntity(item.id, today))
        equip(item.id)
        return ShopOutcome.Purchased
    }

    suspend fun equip(cosmeticId: String) {
        val bee = dao.bee() ?: return
        val item = CosmeticCatalog.find(cosmeticId) ?: return
        val updated = when (item.kind) {
            CosmeticKind.OUTFIT -> bee.copy(outfitId = item.id)
            CosmeticKind.DECORATION -> bee.copy(decorationId = item.id)
            CosmeticKind.THEME -> bee.copy(themeId = item.id)
        }
        dao.upsertBee(updated)
    }

    private suspend fun insertActiveCycle(
        northStar: String,
        area: LifeArea,
        tone: String,
        start: LocalDate,
    ): Long {
        return dao.insertCycle(
            CycleEntity(
                northStar = northStar.trim().ifBlank { LifeAreaTemplates.forArea(area).northStar },
                lifeArea = area.name,
                startDate = start.toString(),
                status = CycleStatus.ACTIVE.name,
                themeNote = tone,
                harvested = false,
            ),
        )
    }

    private suspend fun insertHabits(cycleId: Long, habits: List<PlannedHabit>): List<Long> {
        return habits.mapIndexedNotNull { index, habit ->
            val cleaned = habit.title.trim()
            if (cleaned.isEmpty()) null
            else dao.insertHabit(
                HabitEntity(
                    cycleId = cycleId,
                    title = cleaned,
                    description = habit.description.ifBlank {
                        LifeAreaTemplates.forArea(habit.area).northStar
                    },
                    lifeArea = habit.area.name,
                    active = true,
                    sortOrder = index,
                ),
            )
        }
    }

    private suspend fun insertGoals(cycleId: Long, template: LifeAreaTemplate, elevate: Boolean) {
        template.roadmap.forEach { (stage, titles) ->
            titles.forEach { title ->
                val label = if (elevate) "$title — higher flight" else title
                dao.insertGoal(
                    MiniGoalEntity(
                        cycleId = cycleId,
                        title = label,
                        stage = stage.name,
                        bloomed = false,
                        weekly = false,
                    ),
                )
            }
        }
    }

    private suspend fun grant(cycleId: Long, day: String, amount: Int, reason: String) {
        dao.insertHoneyLedgerEntry(
            HoneyLedgerEntryEntity(
                cycleId = cycleId,
                day = day,
                amount = amount,
                reason = reason,
            ),
        )
    }
}

private data class CoreHeader(
    val bee: BeeProfileEntity?,
    val cycle: CycleEntity?,
    val jars: List<HoneyJarEntity>,
    val honeyLedger: List<HoneyLedgerEntryEntity>,
    val owned: Set<String>,
)

private data class Header(
    val bee: BeeProfileEntity?,
    val cycle: CycleEntity?,
    val jars: List<HoneyJarEntity>,
    val honeyLedger: List<HoneyLedgerEntryEntity>,
    val owned: Set<String>,
)

private data class CycleChunk(
    val habits: List<HabitEntity>,
    val logs: List<HabitLogEntity>,
    val goals: List<MiniGoalEntity>,
    val checks: List<CheckInEntity>,
    val forages: List<ForageEntity>,
)

private data class ExtraChunk(
    val journals: List<JournalEntity>,
    val weekly: WeeklyReviewEntity?,
    val repairs: List<StreakRepairEntity>,
    val milestones: List<MilestoneEntity>,
    val focusGranted: Boolean,
)

private fun Header.toSnapshot(
    today: LocalDate,
    chunk: CycleChunk? = null,
    extra: ExtraChunk? = null,
): HiveSnapshot {
    val cycleView = cycle?.let { entity ->
        val start = LocalDate.parse(entity.startDate)
        val day = CycleCalendar.dayIndex(start, today)
        CycleView(
            id = entity.id,
            northStar = entity.northStar,
            lifeArea = LifeArea.valueOf(entity.lifeArea),
            themeNote = entity.themeNote,
            startDate = start,
            dayIndex = day,
            stage = CycleCalendar.stage(day),
            harvested = entity.harvested,
        )
    }
    val logs = chunk?.logs.orEmpty().map {
        LogItem(it.habitId, LocalDate.parse(it.day), it.completed)
    }
    val doneToday = logs.filter { it.day == today && it.completed }.map { it.habitId }.toSet()
    return HiveSnapshot(
        bee = bee?.let {
            BeeProfile(
                name = it.name,
                personality = BeePersonality.valueOf(it.personality),
                outfitId = it.outfitId,
                decorationId = it.decorationId,
                themeId = it.themeId,
            )
        },
        cycle = cycleView,
        habits = chunk?.habits.orEmpty().map {
            val fallback = cycleView?.lifeArea ?: LifeArea.MIND_FOCUS
            val area = runCatching { LifeArea.valueOf(it.lifeArea) }.getOrDefault(fallback)
            HabitItem(
                id = it.id,
                title = it.title,
                description = it.description,
                area = area,
                active = it.active,
                doneToday = it.id in doneToday,
            )
        },
        goals = chunk?.goals.orEmpty().map { goal ->
            val stage = CycleStage.valueOf(goal.stage)
            GoalItem(
                id = goal.id,
                title = goal.title,
                stage = stage,
                bloomed = goal.bloomed,
                weekly = goal.weekly,
            )
        },
        journals = extra?.journals.orEmpty().map {
            JournalItem(
                id = it.id,
                day = LocalDate.parse(it.day),
                kind = JournalKind.valueOf(it.kind),
                prompt = it.prompt,
                body = it.body,
            )
        },
        jars = jars.map {
            JarItem(it.id, it.title, it.subtitle, it.kind, it.dayMark)
        },
        forages = chunk?.forages.orEmpty().map {
            ForageItem(
                day = LocalDate.parse(it.day),
                kind = ForageRewardKind.valueOf(it.rewardKind),
                title = it.title,
                body = it.body,
            )
        },
        checks = chunk?.checks.orEmpty().map {
            CheckItem(LocalDate.parse(it.day), Mood.valueOf(it.mood))
        },
        honeyLedger = honeyLedger.map {
            HoneyLedgerItem(it.cycleId, LocalDate.parse(it.day), it.amount)
        },
        logs = logs,
        ownedIds = owned,
        repairedDays = extra?.repairs.orEmpty().map { LocalDate.parse(it.day) }.toSet(),
        repairedStages = extra?.repairs.orEmpty().map { CycleStage.valueOf(it.stage) }.toSet(),
        milestones = extra?.milestones.orEmpty().map { it.dayMark }.toSet(),
        focusGrantedToday = extra?.focusGranted == true,
        weeklyNote = extra?.weekly?.note,
        today = today,
    )
}