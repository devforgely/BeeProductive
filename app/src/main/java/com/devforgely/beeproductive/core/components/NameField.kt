package com.devforgely.beeproductive.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devforgely.beeproductive.core.theme.RubikFontFamily

private val NameInk = Color(0xFF252525)
private val NameMuted = Color(0xFF8E8E93)
private val NameSky = Color(0xFF52B4E8)
private val NameIdle = Color(0xFFE6E6EA)

@Composable
fun NameField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = RubikFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = NameInk,
            textAlign = TextAlign.Center,
        ),
        placeholder = {
            Text(
                text = placeholder,
                modifier = Modifier.fillMaxWidth(),
                color = NameMuted,
                fontFamily = RubikFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                Text(
                    text = "×",
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onClear)
                        .padding(8.dp),
                    color = NameMuted,
                    fontSize = 20.sp,
                )
            }
        },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NameSky,
            unfocusedBorderColor = NameIdle,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = NameSky,
        ),
    )
}
