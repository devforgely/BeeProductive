package com.devforgely.beeproductive.domain.model

enum class LifeArea(val label: String) {
    HEALTH_ENERGY("Health & Energy"),
    MIND_FOCUS("Mind & Focus"),
    WORK_GOALS("Work & Goals"),
    RELATIONSHIPS("Relationships"),
    CREATIVITY("Creativity"),
    REST("Rest"),
}

enum class BeePersonality(val label: String, val blurb: String) {
    DILIGENT("Diligent", "Steady wings, careful work."),
    PLAYFUL("Playful", "A bounce in every flight."),
    CALM("Calm", "Soft hover, unhurried heart."),
    CURIOUS("Curious", "Always sniffing the next blossom."),
}

enum class BeeAccent(val label: String, val argb: Long) {
    HONEY("Honey", 0xFFE0A106),
    SAGE("Sage", 0xFF6E9A62),
    SKY("Sky", 0xFF6AA8D6),
    BLUSH("Blush", 0xFFE89284),
    AMBER("Amber", 0xFFD4842A),
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
        "Nectar Flow",
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
    WELCOME,
}

enum class Ritual {
    PHASE_30,
    PHASE_60,
    HARVEST,
}

data class SupportTier(
    val id: String,
    val title: String,
    val blurb: String,
)

object SupportProducts {
    const val NECTAR = "bee_support_nectar"
    const val HIVE = "bee_support_hive"
    const val GARDEN = "bee_support_garden"

    val catalog = listOf(
        SupportTier(NECTAR, "A drop of nectar", "A small thank-you for the person who tends this app."),
        SupportTier(HIVE, "A jar for the keeper", "A warmer gift. The hive stays exactly as free as before."),
        SupportTier(GARDEN, "A whole meadow", "A generous gift that helps the garden keep growing."),
    )

    val ALL = catalog.map { it.id }.toSet()

    fun find(id: String): SupportTier? = catalog.firstOrNull { it.id == id }

    fun matching(productIds: List<String>): String? = productIds.firstOrNull { it in ALL }
}
