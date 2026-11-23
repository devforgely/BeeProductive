package com.devforgely.beeproductive.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
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
import com.devforgely.beeproductive.core.theme.SettingsBackground
import com.devforgely.beeproductive.core.theme.SupporterPurple
import com.devforgely.beeproductive.ui.bee.BeeView
import com.devforgely.beeproductive.ui.celebrate.CelebrationView
import com.devforgely.beeproductive.ui.focus.FocusView
import com.devforgely.beeproductive.ui.hive.HiveView
import com.devforgely.beeproductive.ui.home.HomeView
import com.devforgely.beeproductive.ui.onboarding.OnboardingView
import com.devforgely.beeproductive.ui.quest.QuestDetailView
import com.devforgely.beeproductive.ui.quest.QuestListView
import com.devforgely.beeproductive.ui.setting.SettingView
import com.devforgely.beeproductive.ui.shop.ShopView
import com.devforgely.beeproductive.ui.supporter.SupporterView

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
    val openSupporter = remember(nav) {
        {
            nav.navigate(Routes.SUPPORTER) {
                launchSingleTop = true
            }
        }
    }
    Scaffold(
        containerColor = when (current) {
            Routes.SETTINGS -> SettingsBackground
            Routes.SUPPORTER -> SupporterPurple
            else -> MaterialTheme.colorScheme.background
        },
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
        val layoutDirection = LocalLayoutDirection.current
        val hostPadding = if (current == Routes.QUEST) {
            PaddingValues(
                start = padding.calculateStartPadding(layoutDirection),
                top = padding.calculateTopPadding(),
                end = padding.calculateEndPadding(layoutDirection),
            )
        } else {
            padding
        }
        NavHost(
            navController = nav,
            startDestination = startRoute,
            modifier = Modifier.padding(hostPadding),
        ) {
            composable(Routes.ONBOARDING) {
                OnboardingView(onFinished = {
                    nav.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                })
            }
            composable(Routes.HOME) {
                HomeView(
                    onSettings = { nav.navigate(Routes.SETTINGS) },
                    onFocus = { nav.navigate(Routes.FOCUS) },
                    onCelebrate = { honey, cells ->
                        nav.navigate(Routes.celebration(honey, cells))
                    },
                )
            }
            composable(Routes.QUESTS) {
                QuestListView(onArea = { area -> nav.navigate(Routes.quest(area.name)) })
            }
            composable(
                route = Routes.QUEST,
                arguments = listOf(navArgument("area") { type = NavType.StringType }),
            ) { backStack ->
                QuestDetailView(
                    areaName = backStack.arguments?.getString("area").orEmpty(),
                    onBack = { nav.popBackStack() },
                )
            }
            composable(Routes.HIVE) { HiveView() }
            composable(Routes.SHOP) { ShopView() }
            composable(Routes.BEE) { BeeView() }
            composable(Routes.FOCUS) { FocusView(onBack = { nav.popBackStack() }) }
            composable(
                route = Routes.CELEBRATION,
                arguments = listOf(
                    navArgument("honey") { type = NavType.IntType },
                    navArgument("cells") { type = NavType.IntType },
                ),
            ) { backStack ->
                CelebrationView(
                    honey = backStack.arguments?.getInt("honey") ?: 0,
                    cells = backStack.arguments?.getInt("cells") ?: 0,
                    onDone = { nav.popBackStack() },
                )
            }
            composable(Routes.SETTINGS) {
                SettingView(
                    onBack = { nav.popBackStack() },
                    onSupport = openSupporter,
                )
            }
            composable(Routes.SUPPORTER) {
                SupporterView(onClose = { nav.popBackStack() })
            }
        }
    }
}