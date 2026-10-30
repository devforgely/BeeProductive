package com.devforgely.beeproductive.core.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devforgely.beeproductive.core.theme.Amber
import com.devforgely.beeproductive.core.theme.Blush
import com.devforgely.beeproductive.core.theme.Cream
import com.devforgely.beeproductive.core.theme.HoneyGold
import com.devforgely.beeproductive.core.theme.Sage
import com.devforgely.beeproductive.core.theme.Sky
import com.devforgely.beeproductive.domain.model.Mood

@Composable
fun MeadowBackground(themeId: String?, modifier: Modifier = Modifier) {
    val top = when (themeId) {
        "theme_blossom" -> Blush
        "theme_rain" -> Sky
        "theme_dusk" -> Color(0xFF6A4A2A)
        else -> HoneyGold.copy(alpha = 0.55f)
    }
    val bottom = MaterialTheme.colorScheme.background
    Box(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(top.copy(alpha = 0.55f), Sage.copy(alpha = 0.18f), bottom))),
    )
}

@Composable
fun GardenScaffold(
    themeId: String?,
    content: LazyListScope.() -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        MeadowBackground(themeId)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content,
        )
    }
}

@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
            .padding(18.dp),
    ) {
        content()
    }
}

@Composable
fun SpeechBubble(text: String) {
    SoftCard {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun StageChip(label: String, range: String) {
    Row(
        Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.width(8.dp))
        Text(range, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun PrimaryBeeButton(
    text: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Amber,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun QuietButton(
    text: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(28.dp),
    ) {
        Text(text)
    }
}

@Composable
fun MoodRow(selected: Mood?, onSelect: (Mood) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Mood.entries.forEach { mood ->
            val active = mood == selected
            Text(
                text = mood.label,
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(if (active) Amber else MaterialTheme.colorScheme.surface)
                    .clickable { onSelect(mood) }
                    .padding(vertical = 10.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge,
                color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun HabitCard(
    title: String,
    done: Boolean,
    onComplete: () -> Unit,
) {
    var burst by remember(title) { mutableStateOf(done) }
    val travel by animateFloatAsState(if (burst && done) 1f else 0f, tween(800), label = "nectar")
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                if (done) {
                    Text(
                        "Nectar gathered",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (done) Sage else HoneyGold)
                    .clickable(enabled = !done) {
                        burst = true
                        onComplete()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(if (done) "✓" else "+", color = Cream, style = MaterialTheme.typography.titleLarge)
            }
        }
        if (travel > 0f) {
            Box(
                Modifier
                    .padding(top = 8.dp)
                    .size(14.dp)
                    .graphicsLayer {
                        translationY = -70f * travel
                        alpha = 1f - travel
                    }
                    .clip(CircleShape)
                    .background(Amber),
            )
        }
    }
}

@Composable
fun ChoiceCard(
    title: String,
    body: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Amber else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp),
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
