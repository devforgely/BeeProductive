package com.devforgely.beeproductive.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devforgely.beeproductive.R
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
    Box(modifier = Modifier.fillMaxWidth()) {
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
        if (value.isNotEmpty()) {
            IconButton(
                onClick = onClear,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_x),
                    contentDescription = "Clear",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(NameMuted),
                )
            }
        }
    }
}
