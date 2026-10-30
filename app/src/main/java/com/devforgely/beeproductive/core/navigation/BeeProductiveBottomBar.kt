package com.devforgely.beeproductive.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

private data class HiveTab(val route: String, val label: String, val icon: ImageVector)

private val hiveTabs = listOf(
    HiveTab(Routes.HIVE, "Hive", Icons.Filled.Home),
    HiveTab(Routes.TODAY, "Today", Icons.Filled.Check),
    HiveTab(Routes.MEADOW, "Meadow", Icons.Filled.Star),
    HiveTab(Routes.APIARY, "Apiary", Icons.Filled.Favorite),
)

@Composable
fun BeeProductiveBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
    ) {
        hiveTabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = { onNavigate(tab.route) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    }
}
