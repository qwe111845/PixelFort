package com.pixelfort.towerdefense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pixelfort.towerdefense.navigation.AppNavGraph
import com.pixelfort.towerdefense.ui.theme.PixelFortTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PixelFortTheme {
                AppNavGraph()
            }
        }
    }
}
