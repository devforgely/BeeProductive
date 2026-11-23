package com.devforgely.beeproductive.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devforgely.beeproductive.core.theme.RubikFontFamily

private val SkyBlue = Color(0xFF52B4E8)
private val SkyBlueShadow = Color(0xFF3A8FC4)
private val SolidButtonShape = RoundedCornerShape(12.dp)
private val SolidButtonShadowDrop = 4.dp

@Composable
fun SolidButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = SkyBlue,
    shadowColor: Color = SkyBlueShadow,
    contentColor: Color = Color.White,
    height: Dp = 52.dp,
    fontSize: TextUnit = 16.sp,
    shape: Shape = SolidButtonShape,
) {
    val disabledFace = lerp(Color.White, containerColor, 0.45f)
    val lip = if (enabled) shadowColor else lerp(disabledFace, shadowColor, 0.3f)
    Box(modifier.height(height + SolidButtonShadowDrop)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(height)
                .align(Alignment.BottomCenter)
                .background(lip, shape),
        )
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .align(Alignment.TopCenter),
            shape = shape,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                disabledElevation = 0.dp,
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = disabledFace,
                disabledContentColor = contentColor.copy(alpha = 0.7f),
            ),
        ) {
            Text(
                text = text,
                fontFamily = RubikFontFamily,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
