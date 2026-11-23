package com.devforgely.beeproductive.core.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devforgely.beeproductive.R
import com.devforgely.beeproductive.core.theme.NavBar
import com.devforgely.beeproductive.core.theme.RubikFontFamily

private data class HiveTab(val route: String, val label: String, val icon: Int)

private val hiveTabs = listOf(
    HiveTab(Routes.HOME, "Home", R.drawable.home),
    HiveTab(Routes.QUESTS, "Quests", R.drawable.quest),
    HiveTab(Routes.HIVE, "Hive", R.drawable.hive),
    HiveTab(Routes.SHOP, "Shop", R.drawable.shop),
    HiveTab(Routes.BEE, "Bee", R.drawable.bee),
)

@Composable
fun BeeProductiveBottomBar(
    currentRoute: String?,
    beeName: String,
    onNavigate: (String) -> Unit,
) {
    Surface(color = NavBar) {
        Row(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            hiveTabs.forEach { tab ->
                val selected = currentRoute == tab.route
                val label = if (tab.route == Routes.BEE) beeName.ifBlank { "Bee" } else tab.label
                Column(
                    Modifier
                        .weight(1f)
                        .clickable { onNavigate(tab.route) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(tab.icon),
                        contentDescription = label,
                        modifier = Modifier.size(36.dp),
                    )
                    Text(
                        text = label,
                        color = if (selected) Color.White else Color.White.copy(alpha = 0.72f),
                        fontFamily = RubikFontFamily,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
