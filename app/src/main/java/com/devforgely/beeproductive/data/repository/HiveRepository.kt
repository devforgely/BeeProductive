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
import com.devforgely.beeproductive.data.local.entity.JournalEntity
import com.devforgely.beeproductive.data.local.entity.MilestoneEntity
import com.devforgely.beeproductive.data.local.entity.MiniGoalEntity
import com.devforgely.beeproductive.data.local.entity.NectarEntryEntity
import com.devforgely.beeproductive.data.local.entity.OwnedCosmeticEntity
import com.devforgely.beeproductive.data.local.entity.StreakRepairEntity
import com.devforgely.beeproductive.data.local.entity.WeeklyReviewEntity
import com.devforgely.beeproductive.data.widget.WidgetFace
import com.devforgely.beeproductive.data.widget.WidgetUpdater
import com.devforgely.beeproductive.domain.model.BeeAccent
import com.devforgely.beeproductive.domain.model.BeeProfile
import com.devforgely.beeproductive.domain.model.BeePersonality
import com.devforgely.beeproductive.domain.model.CheckItem
import com.devforgely.beeproductive.domain.model.CosmeticCatalog
import com.devforgely.beeproductive.domain.model.CosmeticKind
import com.devforgely.beeproductive.domain.model.CycleCalendar
import com.devforgely.beeproductive.domain.model.CycleSnapshot
import com.devforgely.beeproductive.domain.model.CycleStage
import com.devforgely.beeproductive.domain.model.CycleStatus
import com.devforgely.beeproductive.domain.model.CycleView
import com.devforgely.beeproductive.domain.model.ForageItem
import com.devforgely.beeproductive.domain.model.ForageOutcome
import com.devforgely.beeproductive.domain.model.ForageRewardKind
import com.devforgely.beeproductive.domain.model.GoalItem
import com.devforgely.beeproductive.domain.model.HabitAddResult
import com.devforgely.beeproductive.domain.model.HabitItem
import com.devforgely.beeproductive.domain.model.HarvestChoice
import com.devforgely.beeproductive.domain.model.HarvestPlanner
import com.devforgely.beeproductive.domain.model.HiveCopy
import com.devforgely.beeproductive.domain.model.HiveSnapshot
import com.devforgely.beeproductive.domain.model.JarItem
import com.devforgely.beeproductive.domain.model.JournalItem
import com.devforgely.beeproductive.domain.model.JournalKind
import com.devforgely.beeproductive.domain.model.LifeArea
import com.devforgely.beeproductive.domain.model.LifeAreaTemplate
import com.devforgely.beeproductive.domain.model.LifeAreaTemplates
import com.devforgely.beeproductive.domain.model.LogItem
import com.devforgely.beeproductive.domain.model.Mood
import com.devforgely.beeproductive.domain.model.NectarItem
import com.devforgely.beeproductive.domain.model.NectarRules
import com.devforgely.beeproductive.domain.model.OnboardingDraft
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
                        nectar = snapshot.balance,
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
            dao.observeNectar(),
            dao.observeOwned(),
        ) { bee, cycle, jars, nectar, owned ->
            CoreHeader(bee, cycle, jars, nectar, owned.map { it.cosmeticId }.toSet())
        }
        return core.flatMapLatest { coreHeader ->
            val header = Header(
                coreHeader.bee,
                coreHeader.cycle,
                coreHeader.jars,
                coreHeader.nectar,
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

    suspend fun plantHive(draft: OnboardingDraft) {
        val today = LocalDate.now()
        dao.upsertBee(
            BeeProfileEntity(
                name = draft.name.trim().ifBlank { "Clover" },
                accent = draft.accent.name,
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
        val habitIds = insertHabits(cycleId, draft.habits.ifEmpty {
            LifeAreaTemplates.forArea(draft.area).starterHabits
        })
        insertGoals(cycleId, LifeAreaTemplates.forArea(draft.area), elevate = false)
        completeFirst(cycleId, habitIds.first(), today)
        preferences.setOnboardingComplete()
        notifications.applySchedule()
    }

    suspend fun plantFreshCycle(northStar: String, area: LifeArea, habits: List<String>, tone: String) {
        if (dao.activeCycle() != null) return
        val today = LocalDate.now()
        val cycleId = insertActiveCycle(northStar, area, tone, today)
        insertHabits(cycleId, habits.ifEmpty { LifeAreaTemplates.forArea(area).starterHabits })
        insertGoals(cycleId, LifeAreaTemplates.forArea(area), elevate = false)
    }

    suspend fun checkIn(mood: Mood) {
        val cycle = dao.activeCycle() ?: return
        dao.upsertCheckIn(
            CheckInEntity(
                cycleId = cycle.id,
                day = LocalDate.now().toString(),
                mood = mood.name,
            ),
        )
    }

    suspend fun completeHabit(habitId: Long): Boolean {
        val cycle = dao.activeCycle() ?: return false
        val today = LocalDate.now().toString()
        if (dao.logFor(habitId, today)?.completed == true) return false
        dao.insertLog(
            HabitLogEntity(
                habitId = habitId,
                cycleId = cycle.id,
                day = today,
                completed = true,
            ),
        )
        grant(cycle.id, today, NectarRules.HABIT_NECTAR, "habit")
        return true
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

    suspend fun retireHabit(id: Long) {
        dao.retireHabit(id)
    }

    suspend fun bloomGoal(goalId: Long) {
        val goal = dao.goal(goalId) ?: return
        if (goal.bloomed) return
        dao.bloomGoal(goalId)
        grant(goal.cycleId, LocalDate.now().toString(), NectarRules.MINI_GOAL_NECTAR, "goal")
    }

    suspend fun addWeeklyGoal(title: String) {
        val cleaned = title.trim()
        if (cleaned.isEmpty()) return
        val cycle = dao.activeCycle() ?: return
        val stage = CycleCalendar.stage(
            CycleCalendar.dayIndex(LocalDate.parse(cycle.startDate), LocalDate.now()),
        )
        dao.insertGoal(
            MiniGoalEntity(
                cycleId = cycle.id,
                title = cleaned,
                stage = stage.name,
                bloomed = false,
                weekly = true,
            ),
        )
    }

    suspend fun saveWeeklyNote(note: String) {
        val cycle = dao.activeCycle() ?: return
        val day = CycleCalendar.dayIndex(LocalDate.parse(cycle.startDate), LocalDate.now())
        dao.upsertWeekly(
            WeeklyReviewEntity(
                cycleId = cycle.id,
                weekIndex = CycleCalendar.weekIndex(day),
                note = note.trim(),
            ),
        )
    }

    suspend fun forage(): ForageOutcome {
        val snap = observe().first()
        val cycle = snap.cycle ?: return ForageOutcome.NoCycle
        if (snap.foragedToday) return ForageOutcome.AlreadyWent
        if (!snap.goalMet) return ForageOutcome.NotReady
        val script = HiveCopy.forageScript(snap.today.dayOfYear)
        val gift = if (script.grantsCosmetic) CosmeticCatalog.freeForageGift else null
        val ownedGift = gift != null && gift.id in snap.ownedIds
        val resolved = if (ownedGift) {
            HiveCopy.basicForages.first { it.kind == ForageRewardKind.INSIGHT }
        } else {
            script
        }
        val today = snap.today.toString()
        dao.insertForage(
            ForageEntity(
                cycleId = cycle.id,
                day = today,
                rewardKind = resolved.kind.name,
                title = resolved.title,
                body = resolved.body,
            ),
        )
        if (resolved.grantsCosmetic && gift != null) {
            dao.insertOwned(OwnedCosmeticEntity(gift.id, today))
        }
        val amount = NectarRules.FORAGE_NECTAR + NectarRules.forageBonus()
        grant(cycle.id, today, amount, "forage")
        return ForageOutcome.Found(resolved.title, resolved.body, resolved.kind)
    }

    suspend fun saveJournal(kind: JournalKind, prompt: String, body: String) {
        val cleaned = body.trim()
        if (cleaned.isEmpty()) return
        val cycle = dao.activeCycle() ?: return
        dao.insertJournal(
            JournalEntity(
                cycleId = cycle.id,
                day = LocalDate.now().toString(),
                kind = kind.name,
                prompt = prompt,
                body = cleaned,
            ),
        )
    }

    suspend fun grantFocus(): Boolean {
        val cycle = dao.activeCycle() ?: return false
        val today = LocalDate.now().toString()
        if (!NectarRules.canGrantFocus(dao.focusGrant(cycle.id, today) != null)) return false
        dao.insertFocusGrant(FocusGrantEntity(cycleId = cycle.id, day = today))
        grant(cycle.id, today, NectarRules.FOCUS_NECTAR, "focus")
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
        grant(cycle.id, plan.day.toString(), plan.nectar, "repair")
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
        if (snap.balance < item.nectarCost) return ShopOutcome.TooExpensive
        val today = snap.today.toString()
        dao.insertNectar(
            NectarEntryEntity(
                cycleId = cycle.id,
                day = today,
                amount = -item.nectarCost,
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

    suspend fun acknowledgePhase(dayMark: Int, reflection: String) {
        val cycle = dao.activeCycle() ?: return
        dao.insertMilestone(MilestoneEntity(cycleId = cycle.id, dayMark = dayMark))
        val title = if (dayMark == 30) "First comb" else "Open nectar"
        dao.insertJar(
            HoneyJarEntity(
                cycleId = cycle.id,
                title = title,
                subtitle = if (dayMark == 30) {
                    "The foundational comb is in place."
                } else {
                    "The meadow is in full nectar flow."
                },
                kind = "PHASE",
                dayMark = dayMark,
                createdOn = LocalDate.now().toString(),
            ),
        )
        if (reflection.isNotBlank()) {
            dao.insertJournal(
                JournalEntity(
                    cycleId = cycle.id,
                    day = LocalDate.now().toString(),
                    kind = JournalKind.PHASE.name,
                    prompt = HiveCopy.phaseReflection(dayMark),
                    body = reflection.trim(),
                ),
            )
        }
    }

    suspend fun harvest(choice: HarvestChoice, editedNorthStar: String) {
        val cycle = dao.activeCycle() ?: return
        val area = LifeArea.valueOf(cycle.lifeArea)
        val northStar = when (choice) {
            HarvestChoice.PIVOT -> editedNorthStar.trim().ifBlank { cycle.northStar }
            else -> cycle.northStar
        }
        val planned = HarvestPlanner.plan(
            choice,
            CycleSnapshot(northStar, area, cycle.themeNote),
        )
        dao.updateCycle(cycle.copy(status = planned.closedStatus.name, harvested = true))
        dao.insertJar(
            HoneyJarEntity(
                cycleId = cycle.id,
                title = planned.jarTitle,
                subtitle = planned.jarSubtitle,
                kind = "HARVEST",
                dayMark = CycleCalendar.CYCLE_LENGTH,
                createdOn = LocalDate.now().toString(),
            ),
        )
        val next = planned.next ?: return
        val template = LifeAreaTemplates.forArea(next.lifeArea)
        val newId = insertActiveCycle(next.northStar, next.lifeArea, next.themeNote, LocalDate.now())
        val habits = if (next.elevate) template.elevatedHabits else template.starterHabits
        insertHabits(newId, habits)
        insertGoals(newId, template, next.elevate)
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

    private suspend fun insertHabits(cycleId: Long, titles: List<String>): List<Long> {
        return titles.mapIndexedNotNull { index, title ->
            val cleaned = title.trim()
            if (cleaned.isEmpty()) null
            else dao.insertHabit(
                HabitEntity(
                    cycleId = cycleId,
                    title = cleaned,
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

    private suspend fun completeFirst(cycleId: Long, habitId: Long, today: LocalDate) {
        val day = today.toString()
        dao.insertLog(
            HabitLogEntity(
                habitId = habitId,
                cycleId = cycleId,
                day = day,
                completed = true,
            ),
        )
        grant(cycleId, day, NectarRules.HABIT_NECTAR, "habit")
    }

    private suspend fun grant(cycleId: Long, day: String, amount: Int, reason: String) {
        dao.insertNectar(
            NectarEntryEntity(
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
    val nectar: List<NectarEntryEntity>,
    val owned: Set<String>,
)

private data class Header(
    val bee: BeeProfileEntity?,
    val cycle: CycleEntity?,
    val jars: List<HoneyJarEntity>,
    val nectar: List<NectarEntryEntity>,
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
    val milestones: List<com.devforgely.beeproductive.data.local.entity.MilestoneEntity>,
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
                accent = BeeAccent.valueOf(it.accent),
                personality = BeePersonality.valueOf(it.personality),
                outfitId = it.outfitId,
                decorationId = it.decorationId,
                themeId = it.themeId,
            )
        },
        cycle = cycleView,
        habits = chunk?.habits.orEmpty().map {
            HabitItem(it.id, it.title, it.active, it.id in doneToday)
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
        nectar = nectar.map {
            NectarItem(it.cycleId, LocalDate.parse(it.day), it.amount)
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

private fun <T1, T2, R> Flow<T1>.combine(
    other: Flow<T2>,
    transform: suspend (T1, T2) -> R,
): Flow<R> = kotlinx.coroutines.flow.combine(this, other, transform)
