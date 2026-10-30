package com.devforgely.beeproductive.domain.model

data class Cosmetic(
    val id: String,
    val name: String,
    val kind: CosmeticKind,
    val nectarCost: Int,
    val accent: BeeAccent? = null,
)

object CosmeticCatalog {
    val all: List<Cosmetic> = listOf(
        Cosmetic("outfit_flower", "Flower crown", CosmeticKind.OUTFIT, 40),
        Cosmetic("outfit_scarf", "Honey scarf", CosmeticKind.OUTFIT, 50),
        Cosmetic("outfit_bow", "Sage bow", CosmeticKind.OUTFIT, 60),
        Cosmetic("theme_blossom", "Blossom meadow", CosmeticKind.THEME, 70, BeeAccent.BLUSH),
        Cosmetic("theme_dusk", "Dusk garden", CosmeticKind.THEME, 80, BeeAccent.AMBER),
        Cosmetic("theme_rain", "Soft rain", CosmeticKind.THEME, 80, BeeAccent.SKY),
        Cosmetic("decor_lantern", "Hive lantern", CosmeticKind.DECORATION, 45),
        Cosmetic("decor_mushroom", "Mossy mushroom", CosmeticKind.DECORATION, 55),
    )

    fun find(id: String?): Cosmetic? = all.firstOrNull { it.id == id }

    val freeForageGift: Cosmetic = all.first { it.id == "decor_lantern" }
}
