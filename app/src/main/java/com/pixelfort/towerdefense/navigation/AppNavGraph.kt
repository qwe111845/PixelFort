package com.pixelfort.towerdefense.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pixelfort.towerdefense.feature.game.ui.GameScreen
import com.pixelfort.towerdefense.feature.levelselect.LevelSelectScreen
import com.pixelfort.towerdefense.feature.menu.ui.MainMenuScreen
import com.pixelfort.towerdefense.feature.metaupgrade.MetaUpgradeScreen

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
                onStartLevel = { levelId ->
                    navController.navigate(Routes.Game(levelId = levelId))
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
                onBack = {
                    navController.popBackStack()
                },
                onGoToUpgrades = {
                    // Pop to LevelSelect then navigate to MetaUpgrade
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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Settings - Coming Soon")
            }
        }
    }
}
