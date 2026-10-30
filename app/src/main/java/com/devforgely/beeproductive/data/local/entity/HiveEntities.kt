package com.devforgely.beeproductive.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "bee_profile")
data class BeeProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val accent: String,
    val personality: String,
    val outfitId: String?,
    val decorationId: String?,
    val themeId: String?,
)

@Entity(tableName = "cycles")
data class CycleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val northStar: String,
    val lifeArea: String,
    val startDate: String,
    val status: String,
    val themeNote: String,
    val harvested: Boolean,
)

@Entity(
    tableName = "mini_goals",
    indices = [Index("cycleId")],
)
data class MiniGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cycleId: Long,
    val title: String,
    val stage: String,
    val bloomed: Boolean,
    val weekly: Boolean,
)

@Entity(
    tableName = "habits",
    indices = [Index("cycleId")],
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cycleId: Long,
    val title: String,
    val active: Boolean,
    val sortOrder: Int,
)

@Entity(
    tableName = "habit_logs",
    indices = [Index(value = ["habitId", "day"], unique = true), Index("cycleId")],
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val cycleId: Long,
    val day: String,
    val completed: Boolean,
)

@Entity(
    tableName = "check_ins",
    indices = [Index(value = ["cycleId", "day"], unique = true)],
)
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cycleId: Long,
    val day: String,
    val mood: String,
)

@Entity(
    tableName = "nectar_ledger",
    indices = [Index("cycleId")],
)
data class NectarEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cycleId: Long,
    val day: String,
    val amount: Int,
    val reason: String,
)

@Entity(
    tableName = "forages",
    indices = [Index(value = ["cycleId", "day"], unique = true)],
)
data class ForageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cycleId: Long,
    val day: String,
    val rewardKind: String,
    val title: String,
    val body: String,
)

@Entity(
    tableName = "journals",
    indices = [Index("cycleId")],
)
data class JournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cycleId: Long,
    val day: String,
    val kind: String,
    val prompt: String,
    val body: String,
)

@Entity(
    tableName = "weekly_reviews",
    indices = [Index(value = ["cycleId", "weekIndex"], unique = true)],
)
data class WeeklyReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cycleId: Long,
    val weekIndex: Int,
    val note: String,
)

@Entity(tableName = "honey_jars")
data class HoneyJarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cycleId: Long,
    val title: String,
    val subtitle: String,
    val kind: String,
    val dayMark: Int,
    val createdOn: String,
)

@Entity(tableName = "owned_cosmetics")
data class OwnedCosmeticEntity(
    @PrimaryKey val cosmeticId: String,
    val ownedOn: String,
)

@Entity(
    tableName = "streak_repairs",
    indices = [Index(value = ["cycleId", "stage"], unique = true)],
)
data class StreakRepairEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cycleId: Long,
    val stage: String,
    val day: String,
)

@Entity(
    tableName = "milestones",
    indices = [Index(value = ["cycleId", "dayMark"], unique = true)],
)
data class MilestoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cycleId: Long,
    val dayMark: Int,
)

@Entity(
    tableName = "focus_grants",
    indices = [Index(value = ["cycleId", "day"], unique = true)],
)
data class FocusGrantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cycleId: Long,
    val day: String,
)
