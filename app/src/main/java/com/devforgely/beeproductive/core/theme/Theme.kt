package com.devforgely.beeproductive.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val GardenScheme = lightColorScheme(
    primary = Amber,
    onPrimary = Ink,
    primaryContainer = HoneyGold,
    onPrimaryContainer = Ink,
    secondary = SageDeep,
    onSecondary = Cream,
    secondaryContainer = Sage.copy(alpha = 0.35f),
    onSecondaryContainer = Ink,
    tertiary = SkyDeep,
    onTertiary = Ink,
    tertiaryContainer = Sky,
    background = Cream,
    onBackground = Ink,
    surface = Color(0xFFFFFBF4),
    onSurface = Ink,
    surfaceVariant = CreamDeep,
    onSurfaceVariant = InkSoft,
    outline = CombLine,
    error = BlushDeep,
    onError = Cream,
)

private val DuskScheme = darkColorScheme(
    primary = HoneyGold,
    onPrimary = DuskBackground,
    primaryContainer = DeepAmber,
    onPrimaryContainer = DuskInk,
    secondary = Sage,
    onSecondary = DuskBackground,
    secondaryContainer = SageDeep,
    onSecondaryContainer = DuskInk,
    tertiary = Sky,
    onTertiary = DuskBackground,
    background = DuskBackground,
    onBackground = DuskInk,
    surface = DuskSurface,
    onSurface = DuskInk,
    surfaceVariant = Color(0xFF3A3128),
    onSurfaceVariant = CreamDeep,
    outline = DeepAmber,
    error = Blush,
    onError = DuskBackground,
)

private val BeeShapes = Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
)

@Composable
fun BeeProductiveTheme(
    dusk: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (dusk) DuskScheme else GardenScheme,
        typography = BeeTypography,
        shapes = BeeShapes,
        content = content,
    )
}
