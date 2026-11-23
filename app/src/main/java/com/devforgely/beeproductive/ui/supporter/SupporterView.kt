package com.devforgely.beeproductive.ui.supporter

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.devforgely.beeproductive.core.components.SolidButton
import com.devforgely.beeproductive.core.theme.Ink
import com.devforgely.beeproductive.core.theme.InkSoft
import com.devforgely.beeproductive.core.theme.RubikFontFamily
import com.devforgely.beeproductive.core.theme.SupporterPurple
import com.devforgely.beeproductive.core.theme.SupporterPurpleDeep
import com.devforgely.beeproductive.data.repository.BillingEvent
import com.devforgely.beeproductive.domain.model.OfferLoad
import com.devforgely.beeproductive.domain.model.SupportOffer
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.HiveBackButton
import com.devforgely.beeproductive.ui.onboarding.BeeCelebrateAnimationId
import com.devforgely.beeproductive.ui.onboarding.BeeWelcomeAnimationId
import com.devforgely.beeproductive.ui.onboarding.rememberBeeTraitsComposition

private val GiftCardShape = RoundedCornerShape(20.dp)
private val GiftCardShadow = Color.Black.copy(alpha = 0.12f)

@Composable
fun SupporterView(
    onClose: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val prefs by viewModel.prefs.collectAsState()
    val offers by viewModel.offers.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var selected by remember { mutableStateOf<String?>(null) }
    var celebrating by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.billingEvents.collect { event ->
            when (event) {
                BillingEvent.Purchased -> celebrating = true
                BillingEvent.Canceled -> snackbar.showSnackbar("Gift canceled. Nothing was sent.")
                is BillingEvent.Failed -> snackbar.showSnackbar(event.message)
            }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF9585E8), SupporterPurple, Color(0xFF5E52C4)),
                ),
            ),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HiveBackButton(onClose)
            }
            Spacer(Modifier.height(8.dp))
            SupporterBee(celebrating = celebrating)
            Spacer(Modifier.height(8.dp))
            AnimatedContent(
                targetState = celebrating,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "supporter-copy",
            ) { thanks ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (thanks) {
                        GiftBadgeRow(title = "Thank you")
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Thank you. Your gift goes to the developer. Every feature stays free.",
                            color = Color.White,
                            fontFamily = RubikFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Center,
                        )
                    } else {
                        GiftBadgeRow(title = "Bee supporter")
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = if (prefs.supporter) {
                                "You've already supported the developer. Another gift is welcome, and still optional."
                            } else {
                                "Every feature is free. A gift here is optional, and it goes to the developer."
                            },
                            color = Color.White,
                            fontFamily = RubikFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            Spacer(Modifier.height(22.dp))
            if (celebrating) {
                Text(
                    text = "Your gift helps the developer keep building.",
                    color = Color.White.copy(alpha = 0.88f),
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(24.dp))
                SolidButton(
                    text = "Done",
                    enabled = true,
                    onClick = onClose,
                    containerColor = Color.White,
                    shadowColor = SupporterPurpleDeep,
                    contentColor = SupporterPurpleDeep,
                )
            } else {
                when (val load = offers) {
                    OfferLoad.Loading -> {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 24.dp),
                        )
                    }
                    is OfferLoad.Unavailable -> {
                        Text(
                            text = load.message,
                            color = Color.White.copy(alpha = 0.9f),
                            fontFamily = RubikFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(14.dp))
                        SolidButton(
                            text = "Try again",
                            enabled = true,
                            onClick = { viewModel.retryOffers() },
                            containerColor = Color.White,
                            shadowColor = SupporterPurpleDeep,
                            contentColor = SupporterPurpleDeep,
                        )
                    }
                    is OfferLoad.Ready -> {
                        Text(
                            text = "Choose a gift",
                            color = Color.White.copy(alpha = 0.9f),
                            fontFamily = RubikFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.1.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                        )
                        load.tiers.forEach { tier ->
                            GiftTierCard(
                                offer = tier,
                                selected = selected == tier.productId,
                                onClick = { selected = tier.productId },
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        SolidButton(
                            text = "Send this gift",
                            enabled = selected != null,
                            onClick = {
                                val productId = selected
                                val activity = context as? Activity
                                if (productId != null && activity != null && !viewModel.launchPurchase(activity, productId)) {
                                    viewModel.retryOffers()
                                }
                            },
                            containerColor = Color.White,
                            shadowColor = SupporterPurpleDeep,
                            contentColor = SupporterPurpleDeep,
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Not today",
                    color = Color.White.copy(alpha = 0.88f),
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onClose)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
            Spacer(Modifier.height(24.dp))
        }
        SnackbarHost(snackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun GiftBadgeRow(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            color = Color.White,
            fontFamily = RubikFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            lineHeight = 34.sp,
        )
        Spacer(Modifier.size(10.dp))
        Box(
            modifier = Modifier
                .height(26.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .padding(horizontal = 7.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "GIFT",
                color = SupporterPurpleDeep,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.6.sp,
                lineHeight = 13.sp,
            )
        }
    }
}

@Composable
private fun SupporterBee(celebrating: Boolean) {
    val animationId = if (celebrating) BeeCelebrateAnimationId else BeeWelcomeAnimationId
    val composition by rememberBeeTraitsComposition(animationId)
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        restartOnPlay = true,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .size(220.dp)
            .semantics { contentDescription = if (celebrating) "Celebrating bee" else "Welcoming bee" },
    )
}

@Composable
private fun GiftTierCard(
    offer: SupportOffer,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (selected) 8.dp else 2.dp,
                shape = GiftCardShape,
                ambientColor = GiftCardShadow,
                spotColor = GiftCardShadow,
            )
            .clip(GiftCardShape)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = offer.title,
                color = Ink,
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = offer.blurb,
                color = InkSoft,
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
            Text(
                text = offer.formattedPrice,
                color = SupporterPurpleDeep,
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
            )
            Text(
                text = "Chosen",
                color = SupporterPurpleDeep,
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.graphicsLayer { alpha = if (selected) 1f else 0f },
            )
        }
    }
}
