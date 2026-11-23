package com.devforgely.beeproductive.ui.bee

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devforgely.beeproductive.R
import com.devforgely.beeproductive.core.components.SoftCard
import com.devforgely.beeproductive.core.theme.Amber
import com.devforgely.beeproductive.core.theme.CombLine
import com.devforgely.beeproductive.core.theme.Cream
import com.devforgely.beeproductive.core.theme.HiveInk
import com.devforgely.beeproductive.core.theme.Ink
import com.devforgely.beeproductive.core.theme.InkSoft
import com.devforgely.beeproductive.core.theme.WaxShadow
import com.devforgely.beeproductive.core.theme.isDuskTheme
import com.devforgely.beeproductive.domain.model.BeePersonality
import com.devforgely.beeproductive.domain.model.Cosmetic
import com.devforgely.beeproductive.domain.model.CosmeticCatalog
import com.devforgely.beeproductive.domain.model.CosmeticKind
import com.devforgely.beeproductive.domain.model.CycleStage
import com.devforgely.beeproductive.domain.model.RepairOutcome
import com.devforgely.beeproductive.ui.HiveViewModel
import com.devforgely.beeproductive.ui.common.HoneyPillButton

private val PassportPaper = Cream
private val PassportLabel = Color(0xFFC6A27A)
private val PassportTab = Color(0xFFE0B56E)
private val PassportTabDeep = Color(0xFFC99655)
private val PassportHeart = Color(0xFFE2556A)
private val PassportWindow = Color(0xFFF6E4C0)
private val CardShape = RoundedCornerShape(32.dp)
private val ShelfShape = RoundedCornerShape(26.dp)

private enum class PassportPage { About, Personality }

@Composable
fun BeeView(viewModel: HiveViewModel = hiltViewModel()) {
    val snapshot by viewModel.snapshot.collectAsState()
    val prefs by viewModel.prefs.collectAsState()
    val bee = snapshot.bee
    val beeName = bee?.name ?: "Your bee"
    val days = snapshot.cycle?.dayIndex ?: 0
    val humanName = prefs.userName.trim()
    val human = humanName.ifBlank { "You" }
    val owned = CosmeticCatalog.all.filter { it.id in snapshot.ownedIds }
    val worn = setOfNotNull(
        bee?.outfitId,
        bee?.themeId,
        bee?.decorationId,
        bee?.wallpaperId,
        bee?.flooringId,
    )
    var mendNote by remember { mutableStateOf<String?>(null) }
    var page by remember { mutableStateOf(PassportPage.About) }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        PassportCard(
            beeName = beeName,
            subtitle = passportSubtitle(prefs.pronoun, days),
            bond = bondLabel(snapshot.streak),
            showHeart = snapshot.streak >= 7,
            human = human,
            mark = hiveMark(beeName),
            honey = snapshot.balance,
            energy = snapshot.energyShown,
            milestones = snapshot.milestones.size,
            page = page,
            about = aboutCopy(beeName, humanName.ifBlank { "you" }, days, snapshot.cycle?.stage),
            personality = personalityCopy(bee?.personality),
            onPage = { page = it },
        )
        StreakCard(
            days = snapshot.streak,
            canMend = snapshot.repairPlan != null,
            honeyCost = snapshot.repairPlan?.honey ?: 0,
            note = mendNote,
            onMend = {
                viewModel.repair { outcome ->
                    mendNote = when (outcome) {
                        RepairOutcome.Repaired -> "Yesterday is mended."
                        RepairOutcome.AlreadyUsed -> "This stage already used its mend."
                        RepairOutcome.NothingToMend -> "Nothing is torn."
                        RepairOutcome.NoCycle -> "There is no active hive to mend."
                    }
                }
            },
        )
        CollectionBlock(beeName = beeName, owned = owned, worn = worn)
    }
}

@Composable
private fun PassportCard(
    beeName: String,
    subtitle: String,
    bond: String,
    showHeart: Boolean,
    human: String,
    mark: String,
    honey: Int,
    energy: Int,
    milestones: Int,
    page: PassportPage,
    about: String,
    personality: String,
    onPage: (PassportPage) -> Unit,
) {
    val entrance = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        entrance.animateTo(1f, tween(durationMillis = 420, easing = FastOutSlowInEasing))
    }
    val progress = entrance.value
    val stamp = ((progress - 0.2f) / 0.8f).coerceIn(0f, 1f)
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .graphicsLayer {
                alpha = progress
                translationY = (1f - progress) * 18.dp.toPx()
            },
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = CardShape,
                    ambientColor = WaxShadow.copy(alpha = 0.12f),
                    spotColor = WaxShadow.copy(alpha = 0.18f),
                )
                .clip(CardShape)
                .background(PassportPaper),
        ) {
            PassportStamp(
                progress = stamp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 28.dp, end = 16.dp)
                    .size(96.dp),
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, top = 22.dp, bottom = 16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(PassportWindow),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.bee_avatar),
                            contentDescription = beeName,
                            modifier = Modifier.size(78.dp),
                            contentScale = ContentScale.Fit,
                        )
                    }
                    Column(Modifier.padding(start = 14.dp).weight(1f)) {
                        Text(
                            text = beeName,
                            color = HiveInk,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = subtitle,
                            color = PassportLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                PassportRow("Friendship", bond, heart = showHeart)
                PassportRow("Human", human)
                PassportRow("Hive mark", mark)
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PassportStat(R.drawable.honey, honey.toString(), "Honey $honey")
                    PassportStat(R.drawable.energy, energy.toString(), "Energy $energy")
                    PassportStat(R.drawable.star_badge, milestones.toString(), "Milestones $milestones")
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PassportPageButton(
                        label = "About",
                        selected = page == PassportPage.About,
                        modifier = Modifier.weight(1f),
                        onClick = { onPage(PassportPage.About) },
                    )
                    PassportPageButton(
                        label = "Personality",
                        selected = page == PassportPage.Personality,
                        modifier = Modifier.weight(1f),
                        onClick = { onPage(PassportPage.Personality) },
                    )
                }
                Spacer(Modifier.height(12.dp))
                AnimatedContent(
                    targetState = page,
                    transitionSpec = {
                        (fadeIn(tween(200, easing = FastOutSlowInEasing)) +
                            slideInVertically(tween(220, easing = FastOutSlowInEasing)) { it / 4 })
                            .togetherWith(
                                fadeOut(tween(140, easing = FastOutLinearInEasing)) +
                                    slideOutVertically(tween(140, easing = FastOutLinearInEasing)) { -it / 5 },
                            )
                            .using(
                                SizeTransform(clip = false) { _, _ ->
                                    tween(220, easing = FastOutSlowInEasing)
                                },
                            )
                    },
                    label = "passport-page",
                ) { selected ->
                    Text(
                        text = if (selected == PassportPage.About) about else personality,
                        color = Ink,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        PassportRibbon(Modifier.align(Alignment.TopCenter).offset(y = (-11).dp))
    }
}

@Composable
private fun PassportRibbon(modifier: Modifier = Modifier) {
    Column(
        modifier
            .width(68.dp)
            .height(20.dp)
            .clip(RoundedCornerShape(topStart = 9.dp, topEnd = 9.dp)),
    ) {
        Box(Modifier.weight(1f).fillMaxWidth().background(PassportTab))
        Box(Modifier.height(5.dp).fillMaxWidth().background(PassportTabDeep))
    }
}

@Composable
private fun PassportStamp(progress: Float, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_passport_stamp),
        contentDescription = null,
        modifier = modifier.graphicsLayer {
            alpha = 0.22f * progress
            rotationZ = (1f - progress) * -8f
            val settled = 0.86f + 0.14f * progress
            scaleX = settled
            scaleY = settled
        },
    )
}

@Composable
private fun PassportRow(label: String, value: String, heart: Boolean = false) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label.uppercase(),
            modifier = Modifier.weight(0.42f),
            color = PassportLabel,
            style = MaterialTheme.typography.labelMedium.copy(
                letterSpacing = 0.8.sp,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            ),
        )
        Row(
            Modifier.weight(0.58f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value,
                modifier = Modifier.weight(1f, fill = false),
                color = HiveInk,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (heart) {
                Spacer(Modifier.width(4.dp))
                Image(
                    painter = painterResource(R.drawable.ic_heart),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(PassportHeart),
                )
            }
        }
    }
}

@Composable
private fun PassportStat(icon: Int, value: String, description: String) {
    Row(
        Modifier.semantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = value,
            color = HiveInk,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun PassportPageButton(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    val fill by animateColorAsState(
        targetValue = if (selected) Color.White else Color.Transparent,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "passport-fill",
    )
    val line by animateColorAsState(
        targetValue = if (selected) Color.Transparent else PassportLabel.copy(alpha = 0.85f),
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "passport-line",
    )
    Box(
        modifier
            .height(46.dp)
            .clip(shape)
            .background(fill)
            .border(1.5.dp, line, shape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label.uppercase(),
            color = if (selected) HiveInk else PassportLabel,
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 0.8.sp),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun StreakCard(
    days: Int,
    canMend: Boolean,
    honeyCost: Int,
    note: String?,
    onMend: () -> Unit,
) {
    val title = when (days) {
        0 -> "No streak yet"
        1 -> "1 day streak"
        else -> "$days day streak"
    }
    val subtitle = when {
        canMend -> "Yesterday's streak stopped."
        days == 0 -> "One finished day starts it."
        else -> "The comb is still warm."
    }
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(
                    if (days > 0 && !canMend) R.drawable.ic_streak_sun_checked else R.drawable.ic_streak_sun,
                ),
                contentDescription = null,
                modifier = Modifier.size(52.dp),
            )
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(
                    text = title,
                    color = HiveInk,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = subtitle,
                    color = InkSoft,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        if (canMend) {
            Spacer(Modifier.height(12.dp))
            HoneyPillButton(text = "Mend streak · $honeyCost honey", onClick = onMend)
        }
        if (note != null) {
            Text(
                text = note,
                color = Amber,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun CollectionBlock(beeName: String, owned: List<Cosmetic>, worn: Set<String>) {
    val night = isDuskTheme()
    val titleInk = if (night) MaterialTheme.colorScheme.onBackground else HiveInk
    val hintInk = if (night) MaterialTheme.colorScheme.onSurfaceVariant else InkSoft
    var openKind by remember { mutableStateOf<CosmeticKind?>(null) }
    val shelves = remember(owned) { collectionShelves(owned) }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "$beeName's collection",
            color = titleInk,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        if (shelves.isEmpty()) {
            SoftCard {
                Text("Nothing tucked away yet.", color = Ink, fontWeight = FontWeight.SemiBold)
                Text(
                    "Mr.Bramble's shop keeps a few sweet things for honey.",
                    color = InkSoft,
                )
            }
        } else {
            shelves.forEach { shelf ->
                CollectionShelf(
                    shelf = shelf,
                    worn = worn,
                    titleInk = titleInk,
                    hintInk = hintInk,
                    open = openKind == shelf.kind,
                    onInfo = {
                        openKind = if (openKind == shelf.kind) null else shelf.kind
                    },
                )
            }
        }
    }
}

@Composable
private fun CollectionShelf(
    shelf: Shelf,
    worn: Set<String>,
    titleInk: Color,
    hintInk: Color,
    open: Boolean,
    onInfo: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = shelf.title,
                color = titleInk,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Box(
                Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, titleInk.copy(alpha = 0.35f), CircleShape)
                    .clickable(role = Role.Button, onClick = onInfo)
                    .semantics { contentDescription = "About ${shelf.title}" },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "i",
                    color = titleInk,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clearAndSetSemantics {},
                )
            }
        }
        AnimatedVisibility(
            visible = open,
            enter = fadeIn(tween(180, easing = FastOutSlowInEasing)) +
                expandVertically(tween(220, easing = FastOutSlowInEasing)),
            exit = fadeOut(tween(120, easing = FastOutLinearInEasing)) +
                shrinkVertically(tween(160, easing = FastOutLinearInEasing)),
        ) {
            Text(text = shelf.hint, color = hintInk, style = MaterialTheme.typography.bodyMedium)
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(shelf.items, key = { it.id }) { item ->
                CollectionCard(item = item, wearing = item.id in worn)
            }
        }
    }
}

@Composable
private fun CollectionCard(item: Cosmetic, wearing: Boolean) {
    Column(
        Modifier
            .width(124.dp)
            .height(148.dp)
            .shadow(
                elevation = 4.dp,
                shape = ShelfShape,
                ambientColor = WaxShadow.copy(alpha = 0.1f),
                spotColor = WaxShadow.copy(alpha = 0.14f),
            )
            .clip(ShelfShape)
            .background(Color.White)
            .border(
                width = if (wearing) 2.dp else 1.dp,
                color = if (wearing) Amber else CombLine.copy(alpha = 0.35f),
                shape = ShelfShape,
            )
            .padding(horizontal = 10.dp, vertical = 12.dp)
            .semantics {
                contentDescription = if (wearing) "${item.name}, wearing" else item.name
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(cosmeticIcon(item.id)),
            contentDescription = null,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentScale = ContentScale.Fit,
        )
        Text(
            text = item.name,
            color = HiveInk,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

private data class Shelf(
    val kind: CosmeticKind,
    val title: String,
    val hint: String,
    val items: List<Cosmetic>,
)

private fun collectionShelves(owned: List<Cosmetic>): List<Shelf> {
    val order = listOf(
        Triple(CosmeticKind.OUTFIT, "Wardrobe", "Clothes and crowns this bee can wear."),
        Triple(CosmeticKind.THEME, "Themes", "Skies and meadows for the hive."),
        Triple(CosmeticKind.DECORATION, "Keepsakes", "Little things kept in the nest."),
        Triple(CosmeticKind.WALLPAPER, "Wallpaper", "Paper for the hive walls."),
        Triple(CosmeticKind.FLOORING, "Flooring", "Boards and tiles underfoot."),
    )
    return order.mapNotNull { (kind, title, hint) ->
        val items = owned.filter { it.kind == kind }
        if (items.isEmpty()) null else Shelf(kind, title, hint, items)
    }
}

private fun cosmeticIcon(id: String): Int = when (id) {
    "outfit_flower" -> R.drawable.ic_outfit_flower
    "outfit_scarf" -> R.drawable.ic_outfit_scarf
    "outfit_bow" -> R.drawable.ic_outfit_bow
    "theme_blossom" -> R.drawable.ic_theme_blossom
    "theme_dusk" -> R.drawable.ic_theme_dusk
    "theme_rain" -> R.drawable.ic_theme_rain
    "decor_lantern" -> R.drawable.ic_decor_lantern
    "decor_mushroom" -> R.drawable.ic_decor_mushroom
    else -> R.drawable.honey
}

private fun passportSubtitle(pronoun: String, days: Int): String {
    val pretty = pronoun.split("/")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString("/") { it.replaceFirstChar { char -> char.uppercase() } }
    val adventures = when (days) {
        0 -> "New to the hive"
        1 -> "1 Adventure"
        else -> "$days Adventures"
    }
    return if (pretty.isBlank()) adventures else "$pretty · $adventures"
}

private fun bondLabel(streak: Int): String = when {
    streak >= 21 -> "Besties"
    streak >= 7 -> "Close friends"
    streak >= 1 -> "Friends"
    else -> "New friend"
}

private fun hiveMark(name: String): String {
    val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    val source = name.lowercase().trim().ifBlank { "bee" }
    var hash = 2166136261L
    source.forEach { char ->
        hash = hash xor char.code.toLong()
        hash = (hash * 16777619L) and 0xFFFFFFFFL
    }
    return buildString(9) {
        var value = hash
        repeat(9) {
            append(alphabet[(value % alphabet.length).toInt()])
            value = ((value / 3) + 17 + hash) and 0xFFFFFFFFL
        }
    }
}

private fun aboutCopy(name: String, human: String, days: Int, stage: CycleStage?): String {
    val season = stage?.let { " This season is ${it.label}." }.orEmpty()
    val line = when (days) {
        0 -> "$name just hatched and is keeping $human company."
        1 -> "$name has been in the hive for a day, keeping $human company."
        else -> "$name has been in the hive for $days days, keeping $human company."
    }
    return line + season
}

private fun personalityCopy(personality: BeePersonality?): String =
    personality?.let { "${it.label}. ${it.blurb}" } ?: "Still settling into the hive."
