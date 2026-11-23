package com.devforgely.beeproductive.ui.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.R
import com.devforgely.beeproductive.core.components.SolidButton
import com.devforgely.beeproductive.core.theme.DeepAmber
import com.devforgely.beeproductive.core.theme.HoneyGold
import com.devforgely.beeproductive.core.theme.Ink
import com.devforgely.beeproductive.core.theme.InkSoft
import com.devforgely.beeproductive.core.theme.SettingsBackground
import com.devforgely.beeproductive.core.theme.SupporterPurple
import com.devforgely.beeproductive.core.theme.SupporterPurpleDeep
import com.devforgely.beeproductive.core.theme.Sky
import com.devforgely.beeproductive.core.theme.SkyDeep
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.HiveBackButton
import com.devforgely.beeproductive.ui.common.PaperPage

private val SettingsGroupShape = RoundedCornerShape(22.dp)
private val SettingsIconShape = RoundedCornerShape(10.dp)
private val SettingsShadow = Color.Black.copy(alpha = 0.08f)
private val SettingsDivider = Color(0xFFF0E4C8)
private val SupporterBannerShape = RoundedCornerShape(20.dp)

@Composable
fun SettingView(
    onBack: () -> Unit,
    onSupport: () -> Unit,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val prefs by viewModel.prefs.collectAsState()
    var notificationsOpen by remember { mutableStateOf(false) }
    PaperPage(background = SettingsBackground) {
        HiveBackButton(onBack)
        Column(Modifier.fillMaxWidth()) {
            SettingsSectionLabel("Account")
            SettingsGroup {
                ExpandableSettingsRow(
                    icon = R.drawable.ic_notifications,
                    iconTint = DeepAmber,
                    iconBackground = HoneyGold.copy(alpha = 0.42f),
                    title = "Notifications",
                    expanded = notificationsOpen,
                    onToggle = { notificationsOpen = !notificationsOpen },
                )
                AnimatedVisibility(
                    visible = notificationsOpen,
                    enter = expandVertically(
                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
                    ) + fadeIn(animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)),
                    exit = shrinkVertically(
                        animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing),
                    ) + fadeOut(animationSpec = tween(durationMillis = 140, easing = FastOutLinearInEasing)),
                ) {
                    Column {
                        SettingsRowDivider()
                        SettingsToggleRow(
                            icon = R.drawable.ic_sun,
                            iconTint = DeepAmber,
                            iconBackground = HoneyGold.copy(alpha = 0.42f),
                            title = "Morning whisper",
                            body = "A gentle invitation around 8.",
                            checked = prefs.morningNotes,
                            onChecked = viewModel::setMorning,
                        )
                        SettingsRowDivider()
                        SettingsToggleRow(
                            icon = R.drawable.ic_moon,
                            iconTint = SkyDeep,
                            iconBackground = Sky.copy(alpha = 0.55f),
                            title = "Evening tuck-in",
                            body = "An optional note around 8 in the evening.",
                            checked = prefs.eveningNotes,
                            onChecked = viewModel::setEvening,
                        )
                    }
                }
            }
        }
        SupporterBanner(
            alreadySupporter = prefs.supporter,
            onSupport = onSupport,
        )
    }
}

@Composable
private fun SupporterBanner(
    alreadySupporter: Boolean,
    onSupport: () -> Unit,
) {
    Surface(
        onClick = onSupport,
        modifier = Modifier.fillMaxWidth(),
        shape = SupporterBannerShape,
        color = SupporterPurple,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 10.dp, top = 14.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f).padding(end = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Bee supporter",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.width(8.dp))
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
                Text(
                    text = if (alreadySupporter) {
                        "Thank you for keeping the garden growing."
                    } else {
                        "Every feature is free. A gift is optional."
                    },
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            Box(
                Modifier
                    .padding(top = 4.dp)
                    .width(IntrinsicSize.Max),
            ) {
                SolidButton(
                    text = if (alreadySupporter) "Gift again" else "Support",
                    enabled = true,
                    onClick = onSupport,
                    containerColor = Color.White,
                    shadowColor = SupporterPurpleDeep,
                    contentColor = SupporterPurpleDeep,
                    height = 36.dp,
                    fontSize = 14.sp,
                    shape = RoundedCornerShape(14.dp),
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
        color = InkSoft,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.1.sp,
        style = MaterialTheme.typography.labelLarge,
    )
}

@Composable
private fun SettingsGroup(
    contentPadding: PaddingValues = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
    content: @Composable () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = SettingsGroupShape,
                ambientColor = SettingsShadow,
                spotColor = SettingsShadow,
            )
            .clip(SettingsGroupShape)
            .background(Color.White)
            .padding(contentPadding),
    ) {
        content()
    }
}

@Composable
private fun ExpandableSettingsRow(
    icon: Int,
    iconTint: Color,
    iconBackground: Color,
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    val chevronTurn by animateFloatAsState(
        targetValue = if (expanded) 0f else -90f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "settings-chevron",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onToggle)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsRowIcon(icon, iconTint, iconBackground)
        Spacer(Modifier.width(12.dp))
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = Ink,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium,
        )
        Image(
            painter = painterResource(R.drawable.ic_chevron_down),
            contentDescription = if (expanded) "Collapse" else "Expand",
            colorFilter = ColorFilter.tint(InkSoft.copy(alpha = 0.7f)),
            modifier = Modifier
                .size(28.dp)
                .graphicsLayer { rotationZ = chevronTurn },
        )
    }
}

@Composable
private fun SettingsToggleRow(
    icon: Int,
    iconTint: Color,
    iconBackground: Color,
    title: String,
    body: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsRowIcon(icon, iconTint, iconBackground)
        Spacer(Modifier.width(12.dp))
        Column(
            Modifier
                .weight(1f)
                .padding(end = 16.dp),
        ) {
            Text(
                text = title,
                color = Ink,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = body,
                color = InkSoft,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onChecked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SkyDeep,
                checkedBorderColor = Color.Transparent,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD8D0C6),
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }
}

@Composable
private fun SettingsRowIcon(icon: Int, tint: Color, background: Color) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(SettingsIconShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(tint),
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun SettingsRowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 60.dp, end = 12.dp),
        color = SettingsDivider,
    )
}
