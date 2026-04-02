package com.pixelfort.towerdefense.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pixelfort.towerdefense.feature.game.ui.GameScreen
import com.pixelfort.towerdefense.feature.levelselect.LevelSelectScreen
import com.pixelfort.towerdefense.feature.menu.ui.MainMenuScreen
import com.pixelfort.towerdefense.feature.metaupgrade.MetaUpgradeScreen
import com.pixelfort.towerdefense.feature.settings.SettingsScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.MainMenu
    ) {
        composable<Routes.MainMenu> {
            MainMenuScreen(
                onStartGame = {
                    navController.navigate(Routes.LevelSelect)
                },
                onSettings = {
                    navController.navigate(Routes.Settings)
                }
            )
        }

        composable<Routes.LevelSelect> {
            LevelSelectScreen(
                onStartLevel = { levelId, difficulty ->
                    navController.navigate(Routes.Game(levelId = levelId, difficulty = difficulty))
                },
                onStartEndless = {
                    navController.navigate(Routes.Game(levelId = 1, isEndless = true))
                },
                onGoToUpgrades = {
                    navController.navigate(Routes.MetaUpgrade)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Routes.Game> { backStackEntry ->
            val route = backStackEntry.toRoute<Routes.Game>()
            GameScreen(
                levelId = route.levelId,
                difficulty = route.difficulty,
                isEndless = route.isEndless,
                onBack = {
                    navController.popBackStack()
                },
                onGoToUpgrades = {
                    navController.popBackStack(Routes.LevelSelect, inclusive = false)
                    navController.navigate(Routes.MetaUpgrade)
                }
            )
        }

        composable<Routes.MetaUpgrade> {
            MetaUpgradeScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Routes.Settings> {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
