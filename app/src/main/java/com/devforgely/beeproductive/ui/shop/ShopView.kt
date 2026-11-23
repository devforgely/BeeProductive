package com.devforgely.beeproductive.ui.shop

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.devforgely.beeproductive.R
import com.devforgely.beeproductive.core.theme.Amber
import com.devforgely.beeproductive.core.theme.Blush
import com.devforgely.beeproductive.core.theme.BlushDeep
import com.devforgely.beeproductive.core.theme.CreamDeep
import com.devforgely.beeproductive.core.theme.DeepAmber
import com.devforgely.beeproductive.core.theme.HiveFloor
import com.devforgely.beeproductive.core.theme.HiveFloorDeep
import com.devforgely.beeproductive.core.theme.HiveWall
import com.devforgely.beeproductive.core.theme.HiveWood
import com.devforgely.beeproductive.core.theme.HiveWoodDeep
import com.devforgely.beeproductive.core.theme.HoneyGold
import com.devforgely.beeproductive.core.theme.RubikFontFamily
import com.devforgely.beeproductive.core.theme.Sage
import com.devforgely.beeproductive.core.theme.SageDeep
import com.devforgely.beeproductive.core.theme.Sky
import com.devforgely.beeproductive.core.theme.SkyDeep
import com.devforgely.beeproductive.core.theme.isDuskTheme
import com.devforgely.beeproductive.domain.model.Cosmetic
import com.devforgely.beeproductive.domain.model.CosmeticCatalog
import com.devforgely.beeproductive.domain.model.CosmeticKind
import com.devforgely.beeproductive.domain.model.ShopOutcome
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.HoneyBalanceChip
import com.devforgely.beeproductive.ui.onboarding.rememberBeeTraitsComposition
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.seconds

private val ShopSky = Color(0xFF4EC6F5)
private val ShopSkyNight = Color(0xFF1F5878)
private val PlusCard = Color(0xFF8090F6)
private val PlusInk = Color(0xFF3D4458)
private val Wood = Color(0xFFC4843A)
private val WoodNight = Color(0xFF8A5528)
private val WoodSelected = Color(0xFFDCA15A)
private val WoodSelectedNight = Color(0xFFB47A3E)
private val FreePill = Color(0xFFFFE7A0)
private val FreeInk = Color(0xFFC47A1A)
private val RefreshBlue = Color(0xFF3DB7EA)
private val ShelfShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
private val CardShape = RoundedCornerShape(22.dp)

private data class Swatch(val face: Color, val lip: Color)

private val swatches = listOf(
    Swatch(Color(0xFF7EDAF5), Color(0xFF3EAFD4)),
    Swatch(Color(0xFFC9A4F2), Color(0xFF9A6AD4)),
    Swatch(Color(0xFFF6B0CC), Color(0xFFE07AA4)),
)

private enum class ShopMode(val label: String) {
    Catalog("Catalog"),
    Owned("Owned"),
}

private enum class ShopShelf(val label: String, val kind: CosmeticKind?) {
    Furniture("Furniture", CosmeticKind.DECORATION),
    Wallpaper("Wallpaper", CosmeticKind.WALLPAPER),
    Flooring("Flooring", CosmeticKind.FLOORING),
    Travel("Travel", null),
}

private val brambleLines = listOf(
    "I love honey. I would wear it, if it would stay put.",
    "Sticky paws, happy heart. That is shop policy.",
    "I tried the ribbon. For quality. It passed.",
    "One spoon of honey and the day behaves.",
    "Those crumbs are sentimental. Leave them.",
    "The bee brings the honey. I bring the appetite.",
)

@Composable
fun ShopView(
    onClose: () -> Unit = {},
    onSupport: () -> Unit = {},
    bottomInset: Dp = 0.dp,
    viewModel: HiveViewModel = hiltViewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    val prefs by viewModel.prefs.collectAsState()
    val night = isDuskTheme()
    val bee = snapshot.bee
    val wearing = setOfNotNull(
        bee?.outfitId,
        bee?.themeId,
        bee?.decorationId,
        bee?.wallpaperId,
        bee?.flooringId,
    )
    var mode by remember { mutableStateOf(ShopMode.Catalog) }
    var shelf by remember { mutableStateOf(ShopShelf.Furniture) }
    var shift by remember { mutableIntStateOf(0) }
    var note by remember { mutableStateOf<String?>(null) }
    var lineIndex by remember { mutableIntStateOf(0) }
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    val entrance = remember { Animatable(0f) }
    val pop = remember { Animatable(1f) }
    val spin = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        entrance.animateTo(1f, tween(durationMillis = 420, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1.seconds)
            now = LocalDateTime.now()
        }
    }
    LaunchedEffect(note) {
        if (note != null) {
            pop.snapTo(0.9f)
            pop.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 420f))
            delay(3.2.seconds)
            note = null
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(5.4.seconds)
            if (note == null) lineIndex = (lineIndex + 1) % brambleLines.size
        }
    }
    LaunchedEffect(shift) {
        if (shift == 0) return@LaunchedEffect
        spin.animateTo(shift * 180f, tween(durationMillis = 280, easing = FastOutSlowInEasing))
    }

    val sky by animateColorAsState(
        targetValue = shopSky(bee?.themeId, night),
        animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
        label = "shop-sky",
    )
    val pool = remember(shelf, mode, snapshot.ownedIds, now.toLocalDate(), shift) {
        val kind = shelf.kind
        val base = if (kind == null) {
            emptyList()
        } else {
            CosmeticCatalog.all.filter { item ->
                item.kind == kind && (mode == ShopMode.Catalog || item.id in snapshot.ownedIds)
            }
        }
        featured(base, shift, now.dayOfYear)
    }
    val emptyText = when {
        shelf == ShopShelf.Travel -> "New places are still on the wing."
        mode == ShopMode.Owned -> "Nothing kept here yet."
        else -> "Nothing on this shelf yet."
    }
    val appear = entrance.value

    Box(
        Modifier
            .fillMaxSize()
            .background(sky),
    ) {
        Column(Modifier.fillMaxSize()) {
            ShopTopBar(
                balance = snapshot.balance,
                mode = mode,
                night = night,
                onClose = onClose,
                onMode = { mode = it },
            )
            PlusBanner(
                supporter = prefs.supporter,
                appear = appear,
                onSupport = onSupport,
                modifier = Modifier
                    .zIndex(1f)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            )
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                ShopMascot(
                    line = note ?: brambleLines[lineIndex],
                    decorationId = bee?.decorationId,
                    pop = pop.value,
                    appear = appear,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            ItemShelf(
                items = pool,
                emptyText = emptyText,
                owned = snapshot.ownedIds,
                wearing = wearing,
                shelf = shelf,
                night = night,
                refreshLabel = refreshLabel(now),
                spin = spin.value,
                appear = appear,
                onRefresh = { shift += 1 },
                onShelf = {
                    shelf = it
                    note = null
                },
                bottomInset = bottomInset,
                onBuy = { item ->
                    val already = item.id in snapshot.ownedIds
                    viewModel.buy(item.id) { outcome ->
                        note = when (outcome) {
                            ShopOutcome.Purchased ->
                                if (already) "Wearing ${item.name}. Honey of a choice."
                                else "${item.name} is yours. I licked the ribbon. Sorry."
                            ShopOutcome.TooExpensive ->
                                "A little more honey and it is yours. I can wait. I have snacks."
                            ShopOutcome.Missing -> "That one flew off. I only turned around for a snack."
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun BrambleSpeech(line: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier
            .widthIn(max = 248.dp)
            .shadow(6.dp, shape, ambientColor = Color(0x14000000), spotColor = Color(0x1A000000))
            .clip(shape)
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(
            text = "Mr.Bramble",
            color = Color(0xFF2C2C2C),
            fontFamily = RubikFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
        )
        AnimatedContent(
            targetState = line,
            transitionSpec = {
                (fadeIn(tween(200, easing = FastOutSlowInEasing)) +
                    scaleIn(initialScale = 0.96f, animationSpec = tween(200, easing = FastOutSlowInEasing)))
                    .togetherWith(fadeOut(tween(120, easing = FastOutLinearInEasing)))
            },
            label = "bramble-line",
        ) { spoken ->
            Text(
                text = spoken,
                color = Color(0xFF8E8E93),
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun ShopTopBar(
    balance: Int,
    mode: ShopMode,
    night: Boolean,
    onClose: () -> Unit,
    onMode: (ShopMode) -> Unit,
) {
    Row(
        Modifier
            .zIndex(1f)
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val closeInteraction = remember { MutableInteractionSource() }
        val closeScale = pressScale(closeInteraction)
        Box(
            Modifier
                .size(36.dp)
                .graphicsLayer {
                    scaleX = closeScale
                    scaleY = closeScale
                }
                .clip(CircleShape)
                .background(Color.White)
                .clickable(
                    interactionSource = closeInteraction,
                    indication = null,
                    role = Role.Button,
                    onClick = onClose,
                )
                .semantics { contentDescription = "Close shop" },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.size(16.dp)) { drawCloseMark() }
        }
        Spacer(Modifier.width(8.dp))
        HoneyBalanceChip(balance)
        Spacer(Modifier.weight(1f))
        ShopMode.entries.forEach { entry ->
            ShopModeButton(
                mode = entry,
                selected = mode == entry,
                night = night,
                onClick = { onMode(entry) },
            )
            if (entry != ShopMode.Owned) Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
private fun ShopModeButton(
    mode: ShopMode,
    selected: Boolean,
    night: Boolean,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .clickable(role = Role.Tab, onClick = onClick)
            .semantics {
                this.selected = selected
                contentDescription = mode.label
            }
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(Modifier.size(28.dp)) {
            when (mode) {
                ShopMode.Catalog -> drawCatalogMark()
                ShopMode.Owned -> drawOwnedMark()
            }
        }
        Text(
            text = mode.label,
            color = Color.White,
            fontFamily = RubikFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
        )
        Box(
            Modifier
                .padding(top = 3.dp)
                .width(4.dp)
                .height(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    when {
                        !selected -> Color.Transparent
                        night -> HoneyGold
                        else -> Color(0xFFF08A2A)
                    },
                ),
        )
    }
}

@Composable
private fun PlusBanner(
    supporter: Boolean,
    appear: Float,
    onSupport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(22.dp)
    val hover = rememberInfiniteTransition(label = "plus-hover")
    val phase by hover.animateFloat(
        initialValue = 0f,
        targetValue = (PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(tween(durationMillis = 1800, easing = LinearEasing)),
        label = "plus-phase",
    )
    val interaction = remember { MutableInteractionSource() }
    val scale = pressScale(interaction)
    Row(
        modifier
            .graphicsLayer {
                val reveal = reveal(appear, 0.05f)
                alpha = reveal
                translationY = (1f - reveal) * 18.dp.toPx()
            }
            .shadow(10.dp, shape, ambientColor = Color(0x33000000), spotColor = Color(0x40000000))
            .clip(shape)
            .background(PlusCard)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Bee",
                    color = Color.White,
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .height(20.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White)
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "PLUS",
                        color = PlusCard,
                        fontFamily = RubikFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        letterSpacing = 0.4.sp,
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (supporter) {
                    "Thank you. Another gift is welcome, and every feature stays free."
                } else {
                    "A gift for the person who builds this hive. Every feature stays free."
                },
                color = Color.White,
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 18.sp,
            )
            Spacer(Modifier.height(10.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        role = Role.Button,
                        onClick = onSupport,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (supporter) "Send another gift" else "Send a gift",
                    color = PlusInk,
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        PlusBee(
            phase = phase,
            modifier = Modifier.size(104.dp),
        )
    }
}

@Composable
private fun PlusBee(phase: Float, modifier: Modifier = Modifier) {
    val idle by rememberBeeTraitsComposition("bee_idle")
    val progress by animateLottieCompositionAsState(
        composition = idle,
        iterations = LottieConstants.IterateForever,
    )
    Box(modifier.semantics { contentDescription = "Bee" }) {
        Canvas(Modifier.fillMaxSize()) { drawSparkles(phase) }
        LottieAnimation(
            composition = idle,
            progress = { progress },
            modifier = Modifier
                .align(Alignment.Center)
                .size(92.dp)
                .graphicsLayer {
                    translationY = 3.dp.toPx() * sin(phase)
                },
        )
    }
}

@Composable
private fun ShopMascot(
    line: String,
    decorationId: String?,
    pop: Float,
    appear: Float,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier) {
        val gap = maxHeight
        val bearWidth = minOf(maxWidth * 0.58f, 280.dp)
        val imageHeight = bearWidth * (2274f / 1856f)
        // Top of the artwork includes the hat. The box only cuts off the legs.
        val visibleHeight = minOf(imageHeight * 0.72f, gap)
        BrambleSpeech(
            line = line,
            modifier = Modifier
                .align(Alignment.TopStart)
                .zIndex(2f)
                .padding(
                    start = 8.dp + bearWidth - 12.dp,
                    top = (gap - visibleHeight).coerceAtLeast(0.dp) + 8.dp,
                    end = 12.dp,
                )
                .widthIn(max = (maxWidth - bearWidth).coerceIn(136.dp, 210.dp))
                .graphicsLayer { alpha = reveal(appear, 0.08f) },
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp)
                .size(bearWidth, visibleHeight)
                .clipToBounds(),
        ) {
            Image(
                painter = painterResource(R.drawable.bear),
                contentDescription = "Mr.Bramble",
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter,
                modifier = Modifier
                    .requiredSize(bearWidth, imageHeight)
                    .graphicsLayer {
                        val reveal = reveal(appear, 0.12f)
                        alpha = reveal
                        val settle = 0.88f + 0.12f * reveal
                        scaleX = settle * pop
                        scaleY = settle * pop
                        transformOrigin = TransformOrigin(0.5f, 0f)
                    },
            )
        }
        if (decorationId != null) {
            Canvas(
                Modifier
                    .align(Alignment.BottomStart)
                    .zIndex(1f)
                    .padding(start = bearWidth - 4.dp, bottom = 4.dp)
                    .size(44.dp),
            ) {
                drawShelfItem(decorationId)
            }
        }
    }
}

@Composable
private fun ItemShelf(
    items: List<Cosmetic>,
    emptyText: String,
    owned: Set<String>,
    wearing: Set<String>,
    shelf: ShopShelf,
    night: Boolean,
    refreshLabel: String,
    spin: Float,
    appear: Float,
    bottomInset: Dp,
    onRefresh: () -> Unit,
    onShelf: (ShopShelf) -> Unit,
    onBuy: (Cosmetic) -> Unit,
) {
    val wood = if (night) WoodNight else Wood
    val selected = if (night) WoodSelectedNight else WoodSelected
    Column(
        Modifier
            .fillMaxWidth()
            .zIndex(1f)
            .graphicsLayer {
                val reveal = reveal(appear, 0.2f)
                alpha = reveal
                translationY = (1f - reveal) * 28.dp.toPx()
            }
            .shadow(12.dp, ShelfShape, clip = false)
            .clip(ShelfShape)
            .background(wood)
            .padding(bottom = bottomInset)
            .padding(top = 14.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Items refresh: $refreshLabel",
                color = Color.White,
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f),
            )
            FreeRefresh(spin = spin, onClick = onRefresh)
        }
        AnimatedContent(
            targetState = items to emptyText,
            contentKey = { (row, text) -> row.joinToString { it.id } + text },
            transitionSpec = {
                (fadeIn(tween(200, easing = FastOutSlowInEasing)) +
                    slideInHorizontally(tween(240, easing = FastOutSlowInEasing)) { it / 6 })
                    .togetherWith(
                        fadeOut(tween(140, easing = FastOutLinearInEasing)) +
                            slideOutHorizontally(tween(180, easing = FastOutLinearInEasing)) { -it / 6 },
                    )
            },
            label = "shelf-items",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) { (row, message) ->
            if (row.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(136.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = message,
                        color = Color.White,
                        fontFamily = RubikFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(136.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    row.forEachIndexed { index, item ->
                        key(item.id) {
                            ShelfCard(
                                item = item,
                                swatch = swatches[index % swatches.size],
                                owned = item.id in owned,
                                wearing = item.id in wearing,
                                onBuy = { onBuy(item) },
                                modifier = if (row.size == 1) Modifier.width(112.dp) else Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ShopShelf.entries.forEach { entry ->
                ShelfTab(
                    shelf = entry,
                    selected = shelf == entry,
                    chip = selected,
                    onClick = { onShelf(entry) },
                )
            }
        }
    }
}

@Composable
private fun FreeRefresh(spin: Float, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val scale = pressScale(interaction)
    Row(
        Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(20.dp))
            .background(FreePill)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .semantics { contentDescription = "Refresh items for free" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "FREE",
            color = FreeInk,
            fontFamily = RubikFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
        )
        Spacer(Modifier.width(4.dp))
        Canvas(
            Modifier
                .size(16.dp)
                .graphicsLayer { rotationZ = spin },
        ) {
            drawRefreshGlyph()
        }
    }
}

@Composable
private fun ShelfCard(
    item: Cosmetic,
    swatch: Swatch,
    owned: Boolean,
    wearing: Boolean,
    onBuy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val scale = pressScale(interaction)
    val price = when {
        wearing -> "On"
        owned -> "Wear"
        else -> item.honeyCost.toString()
    }
    val description = when {
        wearing -> "${item.name}, wearing"
        owned -> "${item.name}, owned, wear it"
        else -> "${item.name}, ${item.honeyCost} honey"
    }
    Box(
        modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(4.dp, CardShape)
            .clip(CardShape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onBuy,
            )
            .semantics { contentDescription = description },
    ) {
        Column(Modifier.fillMaxSize()) {
            Canvas(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(swatch.face),
            ) {
                drawShelfItem(item.id)
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .background(swatch.lip),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!owned) {
                    Image(
                        painter = painterResource(R.drawable.honey),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                }
                Text(
                    text = price,
                    color = Color.White,
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
        }
        if (wearing) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "✓",
                    color = SageDeep,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun RowScope.ShelfTab(
    shelf: ShopShelf,
    selected: Boolean,
    chip: Color,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) chip else Color.Transparent)
            .clickable(role = Role.Tab, onClick = onClick)
            .semantics {
                this.selected = selected
                contentDescription = shelf.label
            }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (shelf == ShopShelf.Travel) {
            Text(
                text = "?",
                color = Color.White,
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                lineHeight = 22.sp,
                modifier = Modifier.height(26.dp),
            )
        } else {
            Canvas(Modifier.size(26.dp)) { drawShelfGlyph(shelf) }
        }
        Text(
            text = shelf.label,
            color = Color.White,
            fontFamily = RubikFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun pressScale(interaction: MutableInteractionSource): Float {
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 500f),
        label = "press",
    )
    return scale
}

private fun reveal(progress: Float, start: Float): Float =
    ((progress - start) / (1f - start)).coerceIn(0f, 1f)

private fun featured(items: List<Cosmetic>, shift: Int, dayOfYear: Int): List<Cosmetic> {
    if (items.size < 2) return items
    val offset = (dayOfYear + shift).mod(items.size)
    return items.drop(offset) + items.take(offset)
}

private fun refreshLabel(now: LocalDateTime): String {
    val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
    val total = java.time.Duration.between(now, midnight).seconds.coerceAtLeast(0)
    val hours = total / 3600
    val minutes = (total % 3600) / 60
    val seconds = total % 60
    return "%d:%02d:%02d".format(hours, minutes, seconds)
}

private fun shopSky(themeId: String?, night: Boolean): Color = when {
    night && themeId == "theme_blossom" -> Color(0xFF3E6A78)
    night && themeId == "theme_dusk" -> Color(0xFF243044)
    night && themeId == "theme_rain" -> Color(0xFF35586A)
    night -> ShopSkyNight
    themeId == "theme_blossom" -> Color(0xFF6ED0E8)
    themeId == "theme_dusk" -> Color(0xFF6AA0D8)
    themeId == "theme_rain" -> Color(0xFF8EC4D4)
    else -> ShopSky
}

private fun DrawScope.drawCloseMark() {
    val color = Color(0xFF2A2A2A)
    val inset = size.minDimension * 0.18f
    drawLine(
        color,
        Offset(inset, inset),
        Offset(size.width - inset, size.height - inset),
        strokeWidth = size.minDimension * 0.14f,
        cap = StrokeCap.Round,
    )
    drawLine(
        color,
        Offset(size.width - inset, inset),
        Offset(inset, size.height - inset),
        strokeWidth = size.minDimension * 0.14f,
        cap = StrokeCap.Round,
    )
}

private fun DrawScope.drawCatalogMark() {
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(size.width * 0.16f, size.height * 0.18f),
        size = Size(size.width * 0.68f, size.height * 0.64f),
        cornerRadius = CornerRadius(size.width * 0.08f, size.width * 0.08f),
        style = Stroke(width = size.minDimension * 0.08f),
    )
    drawLine(
        Color.White,
        Offset(size.width * 0.5f, size.height * 0.22f),
        Offset(size.width * 0.5f, size.height * 0.78f),
        strokeWidth = size.minDimension * 0.06f,
        cap = StrokeCap.Round,
    )
}

private fun DrawScope.drawOwnedMark() {
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(size.width * 0.28f, size.height * 0.14f),
        size = Size(size.width * 0.44f, size.height * 0.16f),
        cornerRadius = CornerRadius(size.width * 0.08f, size.width * 0.08f),
    )
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(size.width * 0.22f, size.height * 0.32f),
        size = Size(size.width * 0.56f, size.height * 0.5f),
        cornerRadius = CornerRadius(size.width * 0.16f, size.width * 0.16f),
        style = Stroke(width = size.minDimension * 0.08f),
    )
    drawCircle(HoneyGold, size.minDimension * 0.08f, Offset(size.width * 0.5f, size.height * 0.56f))
}

private fun DrawScope.drawRefreshGlyph() {
    val stroke = size.minDimension * 0.14f
    val pad = stroke
    val arcSize = Size(size.width - pad * 2, size.height - pad * 2)
    val topLeft = Offset(pad, pad)
    drawArc(
        color = RefreshBlue,
        startAngle = 40f,
        sweepAngle = 280f,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = stroke, cap = StrokeCap.Round),
    )
    val radius = arcSize.minDimension / 2f
    val center = topLeft + Offset(radius, radius)
    val angle = Math.toRadians(40.0)
    val tip = center + Offset((cos(angle) * radius).toFloat(), (sin(angle) * radius).toFloat())
    val tx = -sin(angle).toFloat()
    val ty = cos(angle).toFloat()
    val nx = cos(angle).toFloat()
    val ny = sin(angle).toFloat()
    val wing = radius * 0.55f
    val path = Path().apply {
        moveTo(tip.x, tip.y)
        lineTo(
            tip.x - tx * wing + nx * wing * 0.7f,
            tip.y - ty * wing + ny * wing * 0.7f,
        )
        lineTo(
            tip.x - tx * wing - nx * wing * 0.15f,
            tip.y - ty * wing - ny * wing * 0.15f,
        )
        close()
    }
    drawPath(path, RefreshBlue)
}

private fun DrawScope.drawSparkles(phase: Float) {
    val spots = listOf(
        Offset(size.width * 0.12f, size.height * 0.2f) to HoneyGold,
        Offset(size.width * 0.82f, size.height * 0.16f) to Color.White,
        Offset(size.width * 0.88f, size.height * 0.72f) to Blush,
        Offset(size.width * 0.18f, size.height * 0.78f) to Sky,
    )
    spots.forEachIndexed { index, (at, color) ->
        val twinkle = 0.35f + 0.65f * ((sin(phase + index * 1.3f) + 1f) / 2f)
        drawCircle(color.copy(alpha = twinkle), size.minDimension * 0.045f, at)
    }
}

private fun DrawScope.drawShelfGlyph(shelf: ShopShelf) {
    val white = Color.White
    when (shelf) {
        ShopShelf.Furniture -> {
            drawRoundRect(
                white,
                Offset(size.width * 0.22f, size.height * 0.1f),
                Size(size.width * 0.56f, size.height * 0.12f),
                CornerRadius(size.width * 0.06f, size.width * 0.06f),
            )
            drawRoundRect(
                white,
                Offset(size.width * 0.3f, size.height * 0.2f),
                Size(size.width * 0.1f, size.height * 0.22f),
                CornerRadius(size.width * 0.04f, size.width * 0.04f),
            )
            drawRoundRect(
                white,
                Offset(size.width * 0.14f, size.height * 0.4f),
                Size(size.width * 0.72f, size.height * 0.16f),
                CornerRadius(size.width * 0.06f, size.width * 0.06f),
            )
            drawRoundRect(
                white,
                Offset(size.width * 0.22f, size.height * 0.56f),
                Size(size.width * 0.1f, size.height * 0.28f),
                CornerRadius(size.width * 0.04f, size.width * 0.04f),
            )
            drawRoundRect(
                white,
                Offset(size.width * 0.68f, size.height * 0.56f),
                Size(size.width * 0.1f, size.height * 0.28f),
                CornerRadius(size.width * 0.04f, size.width * 0.04f),
            )
        }
        ShopShelf.Wallpaper -> {
            drawRoundRect(
                white,
                Offset(size.width * 0.16f, size.height * 0.12f),
                Size(size.width * 0.68f, size.height * 0.76f),
                CornerRadius(size.width * 0.08f, size.width * 0.08f),
                style = Stroke(width = size.minDimension * 0.08f),
            )
            drawLine(
                white,
                Offset(size.width * 0.16f, size.height * 0.38f),
                Offset(size.width * 0.84f, size.height * 0.38f),
                strokeWidth = size.minDimension * 0.06f,
            )
            drawCircle(white, size.minDimension * 0.07f, Offset(size.width * 0.34f, size.height * 0.58f))
            drawCircle(white, size.minDimension * 0.07f, Offset(size.width * 0.5f, size.height * 0.7f))
            drawCircle(white, size.minDimension * 0.07f, Offset(size.width * 0.66f, size.height * 0.58f))
        }
        ShopShelf.Flooring -> {
            val tile = size.width * 0.32f
            val gap = size.width * 0.06f
            val startX = (size.width - tile * 2 - gap) / 2f
            val startY = (size.height - tile * 2 - gap) / 2f
            listOf(
                Offset(startX, startY),
                Offset(startX + tile + gap, startY),
                Offset(startX, startY + tile + gap),
                Offset(startX + tile + gap, startY + tile + gap),
            ).forEach { origin ->
                drawRoundRect(
                    white,
                    origin,
                    Size(tile, tile),
                    CornerRadius(size.width * 0.06f, size.width * 0.06f),
                )
            }
        }
        ShopShelf.Travel -> Unit
    }
}

private fun DrawScope.drawShelfItem(id: String) {
    val c = center
    val unit = size.minDimension
    when (id) {
        "outfit_flower" -> {
            drawCircle(Sage, unit * 0.09f, c + Offset(-unit * 0.1f, unit * 0.16f))
            drawCircle(SageDeep, unit * 0.09f, c + Offset(unit * 0.1f, unit * 0.16f))
            drawCircle(Blush, unit * 0.16f, c + Offset(-unit * 0.22f, unit * 0.02f))
            drawCircle(HoneyGold, unit * 0.18f, c + Offset(0f, -unit * 0.08f))
            drawCircle(Sky, unit * 0.16f, c + Offset(unit * 0.22f, unit * 0.02f))
            drawCircle(Color.White, unit * 0.06f, c + Offset(0f, -unit * 0.08f))
        }
        "outfit_scarf" -> {
            drawRoundRect(
                Amber,
                Offset(c.x - unit * 0.34f, c.y - unit * 0.08f),
                Size(unit * 0.68f, unit * 0.22f),
                CornerRadius(unit * 0.1f, unit * 0.1f),
            )
            drawRoundRect(
                DeepAmber,
                Offset(c.x + unit * 0.08f, c.y + unit * 0.06f),
                Size(unit * 0.16f, unit * 0.32f),
                CornerRadius(unit * 0.08f, unit * 0.08f),
            )
            drawCircle(HoneyGold, unit * 0.05f, Offset(c.x + unit * 0.16f, c.y + unit * 0.34f))
        }
        "outfit_bow" -> {
            drawCircle(Sage, unit * 0.2f, c + Offset(-unit * 0.18f, -unit * 0.02f))
            drawCircle(SageDeep, unit * 0.2f, c + Offset(unit * 0.18f, -unit * 0.02f))
            drawCircle(HoneyGold, unit * 0.09f, c + Offset(0f, -unit * 0.02f))
            drawRoundRect(
                Sage,
                Offset(c.x - unit * 0.1f, c.y + unit * 0.06f),
                Size(unit * 0.09f, unit * 0.24f),
                CornerRadius(unit * 0.04f, unit * 0.04f),
            )
            drawRoundRect(
                SageDeep,
                Offset(c.x + unit * 0.02f, c.y + unit * 0.06f),
                Size(unit * 0.09f, unit * 0.24f),
                CornerRadius(unit * 0.04f, unit * 0.04f),
            )
        }
        "theme_blossom" -> {
            drawCircle(Sage.copy(alpha = 0.9f), unit * 0.32f, c + Offset(-unit * 0.1f, unit * 0.26f))
            drawCircle(SageDeep.copy(alpha = 0.85f), unit * 0.26f, c + Offset(unit * 0.22f, unit * 0.3f))
            drawCircle(Blush, unit * 0.07f, c + Offset(-unit * 0.16f, -unit * 0.02f))
            drawCircle(HoneyGold, unit * 0.06f, c + Offset(unit * 0.02f, -unit * 0.14f))
            drawCircle(SkyDeep, unit * 0.05f, c + Offset(unit * 0.18f, unit * 0.02f))
        }
        "theme_dusk" -> {
            drawCircle(Color(0xFF3A3128), unit * 0.28f, c + Offset(unit * 0.04f, 0f))
            drawCircle(Color(0xFFFFF6E8), unit * 0.22f, c + Offset(unit * 0.18f, -unit * 0.06f))
            drawCircle(HoneyGold, unit * 0.035f, c + Offset(-unit * 0.24f, -unit * 0.16f))
            drawCircle(HoneyGold, unit * 0.02f, c + Offset(-unit * 0.28f, unit * 0.08f))
            drawCircle(HoneyGold, unit * 0.025f, c + Offset(-unit * 0.1f, unit * 0.2f))
        }
        "theme_rain" -> {
            drawCircle(Sky, unit * 0.22f, c + Offset(-unit * 0.1f, -unit * 0.1f))
            drawCircle(SkyDeep.copy(alpha = 0.75f), unit * 0.16f, c + Offset(unit * 0.12f, -unit * 0.02f))
            drawCircle(Color.White.copy(alpha = 0.85f), unit * 0.07f, c + Offset(-unit * 0.16f, -unit * 0.14f))
            listOf(-0.18f to 0.2f, 0f to 0.24f, 0.16f to 0.16f).forEach { (x, y) ->
                drawCircle(SkyDeep, unit * 0.045f, c + Offset(unit * x, unit * y))
            }
        }
        "decor_lantern" -> {
            drawRoundRect(
                HiveWoodDeep,
                Offset(c.x - unit * 0.12f, c.y - unit * 0.32f),
                Size(unit * 0.24f, unit * 0.08f),
                CornerRadius(unit * 0.03f, unit * 0.03f),
            )
            drawRoundRect(
                HoneyGold,
                Offset(c.x - unit * 0.16f, c.y - unit * 0.22f),
                Size(unit * 0.32f, unit * 0.36f),
                CornerRadius(unit * 0.08f, unit * 0.08f),
            )
            drawRoundRect(
                Amber,
                Offset(c.x - unit * 0.07f, c.y - unit * 0.12f),
                Size(unit * 0.14f, unit * 0.18f),
                CornerRadius(unit * 0.03f, unit * 0.03f),
            )
            drawCircle(HiveWoodDeep, unit * 0.04f, c + Offset(0f, unit * 0.2f))
        }
        "decor_mushroom" -> {
            drawCircle(BlushDeep, unit * 0.26f, c + Offset(0f, -unit * 0.08f))
            drawCircle(Color.White, unit * 0.05f, c + Offset(-unit * 0.08f, -unit * 0.12f))
            drawCircle(Color.White, unit * 0.035f, c + Offset(unit * 0.1f, -unit * 0.02f))
            drawRoundRect(
                CreamDeep,
                Offset(c.x - unit * 0.08f, c.y),
                Size(unit * 0.16f, unit * 0.28f),
                CornerRadius(unit * 0.06f, unit * 0.06f),
            )
        }
        "wall_honeycomb" -> {
            drawRoundRect(
                HiveWall,
                Offset(c.x - unit * 0.34f, c.y - unit * 0.28f),
                Size(unit * 0.68f, unit * 0.56f),
                CornerRadius(unit * 0.08f, unit * 0.08f),
            )
            listOf(-0.16f to -0.08f, 0.02f to -0.08f, -0.07f to 0.08f, 0.11f to 0.08f).forEach { (x, y) ->
                drawCircle(HoneyGold, unit * 0.09f, c + Offset(unit * x, unit * y))
            }
        }
        "wall_blossom" -> {
            drawRoundRect(
                Color(0xFFFFE8D6),
                Offset(c.x - unit * 0.34f, c.y - unit * 0.28f),
                Size(unit * 0.68f, unit * 0.56f),
                CornerRadius(unit * 0.08f, unit * 0.08f),
            )
            drawCircle(Blush, unit * 0.1f, c + Offset(-unit * 0.14f, -unit * 0.08f))
            drawCircle(BlushDeep, unit * 0.08f, c + Offset(unit * 0.12f, 0f))
            drawCircle(HoneyGold, unit * 0.07f, c + Offset(-unit * 0.02f, unit * 0.12f))
        }
        "wall_sage" -> {
            drawRoundRect(
                Sage.copy(alpha = 0.55f),
                Offset(c.x - unit * 0.34f, c.y - unit * 0.28f),
                Size(unit * 0.68f, unit * 0.56f),
                CornerRadius(unit * 0.08f, unit * 0.08f),
            )
            listOf(-0.18f, 0f, 0.18f).forEach { x ->
                drawRoundRect(
                    SageDeep,
                    Offset(c.x + unit * x - unit * 0.05f, c.y - unit * 0.24f),
                    Size(unit * 0.1f, unit * 0.48f),
                    CornerRadius(unit * 0.04f, unit * 0.04f),
                )
            }
        }
        "floor_wood" -> {
            listOf(-0.18f, 0f, 0.18f).forEachIndexed { index, y ->
                drawRoundRect(
                    if (index == 1) HiveWoodDeep else HiveWood,
                    Offset(c.x - unit * 0.32f, c.y + unit * y - unit * 0.08f),
                    Size(unit * 0.64f, unit * 0.14f),
                    CornerRadius(unit * 0.04f, unit * 0.04f),
                )
            }
        }
        "floor_honey" -> {
            drawRoundRect(
                HiveFloor,
                Offset(c.x - unit * 0.32f, c.y - unit * 0.24f),
                Size(unit * 0.64f, unit * 0.48f),
                CornerRadius(unit * 0.08f, unit * 0.08f),
            )
            drawRoundRect(
                HiveFloorDeep,
                Offset(c.x - unit * 0.28f, c.y - unit * 0.2f),
                Size(unit * 0.28f, unit * 0.2f),
                CornerRadius(unit * 0.04f, unit * 0.04f),
            )
            drawRoundRect(
                HoneyGold,
                Offset(c.x + unit * 0.02f, c.y),
                Size(unit * 0.26f, unit * 0.2f),
                CornerRadius(unit * 0.04f, unit * 0.04f),
            )
        }
        "floor_moss" -> {
            drawRoundRect(
                Sage,
                Offset(c.x - unit * 0.32f, c.y - unit * 0.24f),
                Size(unit * 0.64f, unit * 0.48f),
                CornerRadius(unit * 0.08f, unit * 0.08f),
            )
            drawRoundRect(
                SageDeep,
                Offset(c.x - unit * 0.28f, c.y - unit * 0.08f),
                Size(unit * 0.56f, unit * 0.12f),
                CornerRadius(unit * 0.04f, unit * 0.04f),
            )
            drawCircle(HoneyGold, unit * 0.05f, c + Offset(unit * 0.16f, -unit * 0.12f))
        }
        else -> drawCircle(HoneyGold, unit * 0.2f, c)
    }
}
