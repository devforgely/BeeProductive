package com.devforgely.beeproductive.core.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

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
    background = HiveBackground,
    onBackground = Ink,
    surface = Color.White,
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !dusk
                isAppearanceLightNavigationBars = !dusk
            }
        }
    }
    MaterialTheme(
        colorScheme = if (dusk) DuskScheme else GardenScheme,
        typography = BeeTypography,
        shapes = BeeShapes,
        content = content,
    )
}

@Composable
fun HiveBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val night = isDuskTheme()
    Box(
        modifier
            .fillMaxSize()
            .background(if (night) DuskBackground else HiveFloor),
    ) {
        content()
    }
}

@Composable
fun isDuskTheme(): Boolean = MaterialTheme.colorScheme.background == DuskBackground

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
