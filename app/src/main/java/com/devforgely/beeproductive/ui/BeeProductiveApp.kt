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
import com.devforgely.beeproductive.domain.model.HarvestChoice
import com.devforgely.beeproductive.ui.apiary.ApiaryScreen
import com.devforgely.beeproductive.ui.apiary.HarvestScreen
import com.devforgely.beeproductive.ui.apiary.InsightsScreen
import com.devforgely.beeproductive.ui.apiary.NewCycleScreen
import com.devforgely.beeproductive.ui.apiary.PhaseScreen
import com.devforgely.beeproductive.ui.apiary.ShopScreen
import com.devforgely.beeproductive.ui.apiary.WeeklyScreen
import com.devforgely.beeproductive.ui.hive.HiveScreen
import com.devforgely.beeproductive.ui.hive.MeadowScreen
import com.devforgely.beeproductive.ui.hive.TodayScreen
import com.devforgely.beeproductive.ui.onboarding.OnboardingScreen
import com.devforgely.beeproductive.ui.reflect.EveningScreen
import com.devforgely.beeproductive.ui.reflect.FocusForageScreen
import com.devforgely.beeproductive.ui.reflect.FocusScreen
import com.devforgely.beeproductive.ui.reflect.GratitudeScreen
import com.devforgely.beeproductive.ui.reflect.ReflectScreen
import com.devforgely.beeproductive.ui.reflect.ReflectionScreen
import com.devforgely.beeproductive.ui.setting.SettingView
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
    val entry by nav.currentBackStackEntryAsState()
    val current = entry?.destination?.route
    val showBar = current in Routes.tabs
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBar) {
                BeeProductiveBottomBar(current) { route ->
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
                    nav.navigate(Routes.HIVE) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                })
            }
            composable(Routes.SUPPORTER) {
                SupporterScreen(onClose = { nav.popBackStack() })
            }
            composable(Routes.HIVE) {
                HiveScreen(
                    onForage = { nav.navigate(Routes.MEADOW) },
                    onFocus = { nav.navigate(Routes.FOCUS) },
                    onReflect = { nav.navigate(Routes.REFLECT) },
                    onEvening = { nav.navigate(Routes.EVENING) },
                    onWeekly = { nav.navigate(Routes.WEEKLY) },
                    onPhase = { day -> nav.navigate(Routes.phase(day)) },
                    onHarvest = { nav.navigate(Routes.HARVEST) },
                    onNewCycle = { nav.navigate(Routes.NEW_CYCLE) },
                    onSettings = { nav.navigate(Routes.SETTINGS) },
                )
            }
            composable(Routes.TODAY) {
                TodayScreen(onForage = { nav.navigate(Routes.MEADOW) })
            }
            composable(Routes.MEADOW) { MeadowScreen() }
            composable(Routes.APIARY) {
                ApiaryScreen(
                    onShop = { nav.navigate(Routes.SHOP) },
                    onInsights = { nav.navigate(Routes.INSIGHTS) },
                    onSettings = { nav.navigate(Routes.SETTINGS) },
                    onSupport = { nav.navigate(Routes.SUPPORTER) },
                )
            }
            composable(Routes.REFLECT) {
                ReflectScreen(
                    onReflection = { nav.navigate(Routes.REFLECTION) },
                    onGratitude = { nav.navigate(Routes.GRATITUDE) },
                    onEvening = { nav.navigate(Routes.EVENING) },
                    onFocus = { nav.navigate(Routes.FOCUS) },
                    onFocusForage = { nav.navigate(Routes.FOCUS_FORAGE) },
                )
            }
            composable(Routes.FOCUS) { FocusScreen() }
            composable(Routes.REFLECTION) { ReflectionScreen() }
            composable(Routes.GRATITUDE) { GratitudeScreen() }
            composable(Routes.EVENING) { EveningScreen() }
            composable(Routes.FOCUS_FORAGE) { FocusForageScreen() }
            composable(Routes.WEEKLY) { WeeklyScreen() }
            composable(
                route = Routes.PHASE,
                arguments = listOf(navArgument("dayMark") { type = NavType.IntType }),
            ) { backStack ->
                PhaseScreen(
                    dayMark = backStack.arguments?.getInt("dayMark") ?: 30,
                    onDone = { nav.popBackStack() },
                )
            }
            composable(Routes.HARVEST) {
                HarvestScreen(onDone = { choice ->
                    if (choice == HarvestChoice.ARCHIVE) {
                        nav.navigate(Routes.NEW_CYCLE) {
                            popUpTo(Routes.HIVE)
                        }
                    } else {
                        nav.popBackStack(Routes.HIVE, inclusive = false)
                    }
                })
            }
            composable(Routes.SHOP) { ShopScreen() }
            composable(Routes.INSIGHTS) { InsightsScreen() }
            composable(Routes.SETTINGS) { SettingView(onSupport = { nav.navigate(Routes.SUPPORTER) }) }
            composable(Routes.NEW_CYCLE) { NewCycleScreen(onDone = { nav.popBackStack(Routes.HIVE, inclusive = false) }) }
        }
    }
}
