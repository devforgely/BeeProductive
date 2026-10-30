package com.devforgely.beeproductive.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        BeeProfileEntity::class,
        CycleEntity::class,
        MiniGoalEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        CheckInEntity::class,
        NectarEntryEntity::class,
        ForageEntity::class,
        JournalEntity::class,
        WeeklyReviewEntity::class,
        HoneyJarEntity::class,
        OwnedCosmeticEntity::class,
        StreakRepairEntity::class,
        MilestoneEntity::class,
        FocusGrantEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hiveDao(): HiveDao
}
