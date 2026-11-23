package com.devforgely.beeproductive.domain.model

enum class LifeArea(val label: String) {
    HEALTH_ENERGY("Health & Energy"),
    MIND_FOCUS("Mind & Focus"),
    WORK_GOALS("Work & Goals"),
    CREATIVITY("Creativity"),
    REST("Rest"),
}

enum class BeePersonality(val label: String, val blurb: String) {
    DILIGENT("Diligent", "Steady wings, careful work."),
    PLAYFUL("Playful", "A bounce in every flight."),
    CALM("Calm", "Soft hover, unhurried heart."),
    CURIOUS("Curious", "Always sniffing the next blossom."),
    COMPASSIONATE("Compassionate", "A soft wing for a caring day."),
    CONFIDENT("Confident", "Bright flight, steady heart."),
}

enum class Mood(val label: String) {
    LOW("Low"),
    STEADY("Steady"),
    BRIGHT("Bright"),
}

enum class CycleStage(val label: String, val dayRange: String, val summary: String) {
    POLLINATING(
        "Pollinating",
        "Days 1–30",
        "Seeding and momentum. Showing up matters more than perfection.",
    ),
    NECTAR_FLOW(
        "Honey Flow",
        "Days 31–60",
        "Rhythm and expansion. Longer flights, richer blossoms.",
    ),
    CAPPING(
        "Capping",
        "Days 61–90",
        "Mastery and refinement. Full cells are sealed in gold.",
    ),
}

enum class CycleStatus {
    ACTIVE,
    ARCHIVED,
    LOOPED,
    PIVOTED,
}

enum class HarvestChoice {
    ARCHIVE,
    LOOP,
    PIVOT,
}

enum class JournalKind {
    REFLECTION,
    GRATITUDE,
    EVENING,
    FOCUS_FORAGE,
    PHASE,
}

enum class CosmeticKind {
    OUTFIT,
    THEME,
    DECORATION,
}

enum class ForageRewardKind {
    INSIGHT,
    STORY,
    COSMETIC,
}

enum class BeeClip {
    IDLE,
    FLUTTER,
    CELEBRATE,
    FORAGE,
}

data class SupportTier(
    val id: String,
    val title: String,
    val blurb: String,
)

object SupportProducts {
    const val HONEY = "bee_support_honey"
    const val JAR = "bee_support_jar"
    const val COMB = "bee_support_comb"

    val catalog = listOf(
        SupportTier(HONEY, "Drops of honey", "A small thank-you for the developer."),
        SupportTier(JAR, "A jar of honey", "A bigger thank-you for the developer."),
        SupportTier(COMB, "A full comb", "A generous thank-you that helps the developer keep building."),
    )

    val ALL = catalog.map { it.id }.toSet()

    fun matching(productIds: List<String>): String? = productIds.firstOrNull { it in ALL }
}
