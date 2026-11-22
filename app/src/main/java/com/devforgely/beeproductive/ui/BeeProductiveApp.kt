package com.devforgely.beeproductive.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.devforgely.beeproductive.core.navigation.BeeProductiveBottomBar
import com.devforgely.beeproductive.core.navigation.Routes
import com.devforgely.beeproductive.core.theme.BeeProductiveTheme
import com.devforgely.beeproductive.ui.bee.BeeScreen
import com.devforgely.beeproductive.ui.celebrate.CelebrationScreen
import com.devforgely.beeproductive.ui.focus.FocusScreen
import com.devforgely.beeproductive.ui.hive.HiveScreen
import com.devforgely.beeproductive.ui.home.HomeScreen
import com.devforgely.beeproductive.ui.onboarding.OnboardingScreen
import com.devforgely.beeproductive.ui.quest.QuestDetailScreen
import com.devforgely.beeproductive.ui.quest.QuestListScreen
import com.devforgely.beeproductive.ui.setting.SettingView
import com.devforgely.beeproductive.ui.shop.ShopScreen
import com.devforgely.beeproductive.ui.supporter.SupporterScreen

@Composable
fun BeeProductiveApp(viewModel: BeeProductiveAppViewModel = hiltViewModel()) {
    val dusk by viewModel.dusk.collectAsState()
    val start by viewModel.startRoute.collectAsState()
    BeeProductiveTheme(dusk = dusk) {
        var initial by remember { mutableStateOf<String?>(null) }
        if (initial == null && start != null) {
            initial = start
        }
        val route = initial
        if (route == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            HiveNavHost(route)
        }
    }
}

@Composable
private fun HiveNavHost(startRoute: String) {
    val nav = rememberNavController()
    val hiveViewModel: HiveViewModel = hiltViewModel()
    val snapshot by hiveViewModel.snapshot.collectAsState()
    val entry by nav.currentBackStackEntryAsState()
    val current = entry?.destination?.route
    val showBar = current in Routes.tabs
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBar) {
                BeeProductiveBottomBar(
                    currentRoute = current,
                    beeName = snapshot.bee?.name ?: "Bee",
                ) { route ->
                    nav.navigate(route) {
                        popUpTo(nav.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = startRoute,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.ONBOARDING) {
                OnboardingScreen(onFinished = {
                    nav.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                })
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onSettings = { nav.navigate(Routes.SETTINGS) },
                    onFocus = { nav.navigate(Routes.FOCUS) },
                    onCelebrate = { honey, cells ->
                        nav.navigate(Routes.celebration(honey, cells))
                    },
                )
            }
            composable(Routes.QUESTS) {
                QuestListScreen(onArea = { area -> nav.navigate(Routes.quest(area.name)) })
            }
            composable(
                route = Routes.QUEST,
                arguments = listOf(navArgument("area") { type = NavType.StringType }),
            ) { backStack ->
                QuestDetailScreen(
                    areaName = backStack.arguments?.getString("area").orEmpty(),
                    onBack = { nav.popBackStack() },
                )
            }
            composable(Routes.HIVE) { HiveScreen() }
            composable(Routes.SHOP) { ShopScreen() }
            composable(Routes.BEE) { BeeScreen() }
            composable(Routes.FOCUS) { FocusScreen(onBack = { nav.popBackStack() }) }
            composable(
                route = Routes.CELEBRATION,
                arguments = listOf(
                    navArgument("honey") { type = NavType.IntType },
                    navArgument("cells") { type = NavType.IntType },
                ),
            ) { backStack ->
                CelebrationScreen(
                    honey = backStack.arguments?.getInt("honey") ?: 0,
                    cells = backStack.arguments?.getInt("cells") ?: 0,
                    onDone = { nav.popBackStack() },
                )
            }
            composable(Routes.SETTINGS) {
                SettingView(
                    onBack = { nav.popBackStack() },
                    onSupport = { nav.navigate(Routes.SUPPORTER) },
                )
            }
            composable(Routes.SUPPORTER) {
                SupporterScreen(onClose = { nav.popBackStack() })
            }
        }
    }
}
