package com.devforgely.beeproductive.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.devforgely.beeproductive.R

val RubikFontFamily = FontFamily(
    Font(R.font.rubik_regular, FontWeight.Normal),
    Font(R.font.rubik_medium, FontWeight.Medium),
    Font(R.font.rubik_semibold, FontWeight.SemiBold),
    Font(R.font.rubik_bold, FontWeight.Bold),
    Font(R.font.rubik_extrabold, FontWeight.W800),
)

val BeeTypography = run {
    val base = Typography()
    base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = RubikFontFamily),
        displayMedium = base.displayMedium.copy(fontFamily = RubikFontFamily),
        displaySmall = base.displaySmall.copy(fontFamily = RubikFontFamily),
        headlineLarge = base.headlineLarge.copy(fontFamily = RubikFontFamily, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 40.sp),
        headlineMedium = base.headlineMedium.copy(fontFamily = RubikFontFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
        headlineSmall = base.headlineSmall.copy(fontFamily = RubikFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
        titleLarge = base.titleLarge.copy(fontFamily = RubikFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
        titleMedium = base.titleMedium.copy(fontFamily = RubikFontFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
        titleSmall = base.titleSmall.copy(fontFamily = RubikFontFamily),
        bodyLarge = base.bodyLarge.copy(fontFamily = RubikFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
        bodyMedium = base.bodyMedium.copy(fontFamily = RubikFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
        bodySmall = base.bodySmall.copy(fontFamily = RubikFontFamily),
        labelLarge = base.labelLarge.copy(fontFamily = RubikFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 18.sp),
        labelMedium = base.labelMedium.copy(fontFamily = RubikFontFamily),
        labelSmall = base.labelSmall.copy(fontFamily = RubikFontFamily),
    )
}
