package com.devforgely.beeproductive.domain.model

data class CycleSnapshot(
    val northStar: String,
    val lifeArea: LifeArea,
    val themeNote: String,
)

data class NextCyclePlan(
    val northStar: String,
    val lifeArea: LifeArea,
    val themeNote: String,
    val elevate: Boolean,
)

data class PlannedHarvest(
    val closedStatus: CycleStatus,
    val jarTitle: String,
    val jarSubtitle: String,
    val next: NextCyclePlan?,
)

object HarvestPlanner {
    fun plan(choice: HarvestChoice, cycle: CycleSnapshot): PlannedHarvest {
        val jarTitle = cycle.northStar.ifBlank { "A sweet season" }
        return when (choice) {
            HarvestChoice.ARCHIVE -> PlannedHarvest(
                closedStatus = CycleStatus.ARCHIVED,
                jarTitle = jarTitle,
                jarSubtitle = "Harvested and set on the shelf.",
                next = null,
            )
            HarvestChoice.LOOP -> PlannedHarvest(
                closedStatus = CycleStatus.LOOPED,
                jarTitle = jarTitle,
                jarSubtitle = "Bottled, then lifted into a higher flight.",
                next = NextCyclePlan(
                    northStar = cycle.northStar,
                    lifeArea = cycle.lifeArea,
                    themeNote = "Elevated from the last season.",
                    elevate = true,
                ),
            )
            HarvestChoice.PIVOT -> PlannedHarvest(
                closedStatus = CycleStatus.PIVOTED,
                jarTitle = jarTitle,
                jarSubtitle = "The theme stays. The flight path changes.",
                next = NextCyclePlan(
                    northStar = cycle.northStar,
                    lifeArea = cycle.lifeArea,
                    themeNote = cycle.themeNote.ifBlank { "Adjusted after the harvest." },
                    elevate = false,
                ),
            )
        }
    }
}
