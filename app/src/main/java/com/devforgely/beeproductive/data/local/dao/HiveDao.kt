package com.devforgely.beeproductive.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface HiveDao {
    @Query("SELECT * FROM bee_profile WHERE id = 1")
    fun observeBee(): Flow<BeeProfileEntity?>

    @Query("SELECT * FROM bee_profile WHERE id = 1")
    suspend fun bee(): BeeProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBee(bee: BeeProfileEntity)

    @Query("SELECT * FROM cycles WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 1")
    fun observeActiveCycle(): Flow<CycleEntity?>

    @Query("SELECT * FROM cycles WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 1")
    suspend fun activeCycle(): CycleEntity?

    @Insert
    suspend fun insertCycle(cycle: CycleEntity): Long

    @Update
    suspend fun updateCycle(cycle: CycleEntity)

    @Query("SELECT * FROM mini_goals WHERE cycleId = :cycleId ORDER BY id")
    fun observeGoals(cycleId: Long): Flow<List<MiniGoalEntity>>

    @Insert
    suspend fun insertGoal(goal: MiniGoalEntity): Long

    @Query("SELECT * FROM mini_goals WHERE id = :id")
    suspend fun goal(id: Long): MiniGoalEntity?

    @Query("UPDATE mini_goals SET bloomed = 1 WHERE id = :id")
    suspend fun bloomGoal(id: Long)

    @Query("SELECT * FROM habits WHERE cycleId = :cycleId ORDER BY sortOrder, id")
    fun observeHabits(cycleId: Long): Flow<List<HabitEntity>>

    @Insert
    suspend fun insertHabit(habit: HabitEntity): Long

    @Query("UPDATE habits SET title = :title WHERE id = :id")
    suspend fun renameHabit(id: Long, title: String)

    @Query("UPDATE habits SET active = 0 WHERE id = :id")
    suspend fun retireHabit(id: Long)

    @Query("SELECT * FROM habit_logs WHERE cycleId = :cycleId")
    fun observeLogs(cycleId: Long): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND day = :day LIMIT 1")
    suspend fun logFor(habitId: Long, day: String): HabitLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLogEntity)

    @Query("SELECT * FROM check_ins WHERE cycleId = :cycleId")
    fun observeCheckIns(cycleId: Long): Flow<List<CheckInEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCheckIn(checkIn: CheckInEntity)

    @Query("SELECT * FROM nectar_ledger")
    fun observeNectar(): Flow<List<NectarEntryEntity>>

    @Insert
    suspend fun insertNectar(entry: NectarEntryEntity)

    @Query("SELECT * FROM forages WHERE cycleId = :cycleId")
    fun observeForages(cycleId: Long): Flow<List<ForageEntity>>

    @Insert
    suspend fun insertForage(forage: ForageEntity)

    @Query("SELECT * FROM journals WHERE cycleId = :cycleId ORDER BY id DESC")
    fun observeJournals(cycleId: Long): Flow<List<JournalEntity>>

    @Insert
    suspend fun insertJournal(journal: JournalEntity)

    @Query("SELECT * FROM weekly_reviews WHERE cycleId = :cycleId AND weekIndex = :week LIMIT 1")
    fun observeWeekly(cycleId: Long, week: Int): Flow<WeeklyReviewEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWeekly(review: WeeklyReviewEntity)

    @Query("SELECT * FROM honey_jars ORDER BY id DESC")
    fun observeJars(): Flow<List<HoneyJarEntity>>

    @Insert
    suspend fun insertJar(jar: HoneyJarEntity)

    @Query("SELECT * FROM owned_cosmetics")
    fun observeOwned(): Flow<List<OwnedCosmeticEntity>>

    @Query("SELECT cosmeticId FROM owned_cosmetics")
    suspend fun ownedIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOwned(owned: OwnedCosmeticEntity)

    @Query("SELECT * FROM streak_repairs WHERE cycleId = :cycleId")
    fun observeRepairs(cycleId: Long): Flow<List<StreakRepairEntity>>

    @Insert
    suspend fun insertRepair(repair: StreakRepairEntity)

    @Query("SELECT * FROM milestones WHERE cycleId = :cycleId")
    fun observeMilestones(cycleId: Long): Flow<List<MilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMilestone(milestone: MilestoneEntity)

    @Query("SELECT * FROM focus_grants WHERE cycleId = :cycleId AND day = :day LIMIT 1")
    suspend fun focusGrant(cycleId: Long, day: String): FocusGrantEntity?

    @Query("SELECT COUNT(*) FROM focus_grants WHERE cycleId = :cycleId AND day = :day")
    fun observeFocusCount(cycleId: Long, day: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFocusGrant(grant: FocusGrantEntity)
}
