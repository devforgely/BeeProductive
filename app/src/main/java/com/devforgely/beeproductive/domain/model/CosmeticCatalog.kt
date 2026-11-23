package com.devforgely.beeproductive.domain.model

data class Cosmetic(
    val id: String,
    val name: String,
    val kind: CosmeticKind,
    val honeyCost: Int,
)

object CosmeticCatalog {
    val all: List<Cosmetic> = listOf(
        Cosmetic("outfit_flower", "Flower crown", CosmeticKind.OUTFIT, 40),
        Cosmetic("outfit_scarf", "Honey scarf", CosmeticKind.OUTFIT, 50),
        Cosmetic("outfit_bow", "Sage bow", CosmeticKind.OUTFIT, 60),
        Cosmetic("theme_blossom", "Blossom meadow", CosmeticKind.THEME, 70),
        Cosmetic("theme_dusk", "Dusk garden", CosmeticKind.THEME, 80),
        Cosmetic("theme_rain", "Soft rain", CosmeticKind.THEME, 80),
        Cosmetic("decor_lantern", "Hive lantern", CosmeticKind.DECORATION, 45),
        Cosmetic("decor_mushroom", "Mossy mushroom", CosmeticKind.DECORATION, 55),
        Cosmetic("wall_honeycomb", "Honeycomb paper", CosmeticKind.WALLPAPER, 60),
        Cosmetic("wall_blossom", "Blossom plaster", CosmeticKind.WALLPAPER, 70),
        Cosmetic("wall_sage", "Sage stripe", CosmeticKind.WALLPAPER, 80),
        Cosmetic("floor_wood", "Warm boards", CosmeticKind.FLOORING, 55),
        Cosmetic("floor_honey", "Honey tiles", CosmeticKind.FLOORING, 65),
        Cosmetic("floor_moss", "Moss planks", CosmeticKind.FLOORING, 75),
    )

    fun find(id: String?): Cosmetic? = all.firstOrNull { it.id == id }
}
