package com.devforgely.beeproductive.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devforgely.beeproductive.R
import com.devforgely.beeproductive.core.theme.RubikFontFamily

private val WelcomeSkyBlueShadow = Color(0xFF3A8FC4)
private val WelcomeButtonShadowDrop = 4.dp

@Composable
internal fun WelcomeStep(onNext: () -> Unit) {
    val agreement = buildAnnotatedString {
        append("By continuing, you agree to our ")
        withStyle(SpanStyle(color = WelcomeSkyBlue)) { append("Terms of Service") }
        append("\nand ")
        withStyle(SpanStyle(color = WelcomeSkyBlue)) { append("Privacy Policy") }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Image(
                painter = painterResource(R.drawable.bee_avatar),
                contentDescription = "Bee",
                modifier = Modifier.size(128.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = "Bee Productive",
                color = WelcomeInk,
                fontFamily = RubikFontFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.W800,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Your new buzz-buddy for sweet success.",
                color = WelcomeMuted,
                fontFamily = RubikFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.W800,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(Modifier.weight(0.75f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp + WelcomeButtonShadowDrop),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(0.85f)
                    .height(52.dp)
                    .align(Alignment.BottomCenter)
                    .background(WelcomeSkyBlueShadow, WelcomeButtonShape),
            )
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(52.dp)
                    .align(Alignment.TopCenter),
                shape = WelcomeButtonShape,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WelcomeSkyBlue,
                    contentColor = Color.White,
                )
            ) {
                Text(
                    text = "Meet a new bee",
                    fontFamily = RubikFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = agreement,
            color = WelcomeMuted,
            fontFamily = RubikFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(0.5f))
    }
}
