package com.example.eightpuzzle

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.eightpuzzle.ui.PuzzleScreen
import com.example.eightpuzzle.ui.theme.EightPuzzleTheme
import androidx.compose.runtime.*
import com.example.eightpuzzle.ui.HomeScreen
import com.example.eightpuzzle.ui.StatisticsScreen
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eightpuzzle.viewmodel.GameViewModel


class MainActivity : ComponentActivity() {

    private var controller: WindowInsetsControllerCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        controller = WindowInsetsControllerCompat(window, window.decorView)
        hideSystemBars()

        setContent {

            val gameViewModel: GameViewModel = viewModel()

            var showHome by rememberSaveable { mutableStateOf(true) }
            var showStatistics by rememberSaveable { mutableStateOf(false) }
            var gridSize by rememberSaveable { mutableStateOf(3) }

            EightPuzzleTheme {
                if (showHome) {

                    HomeScreen(
                        onPlay3x3 = {
                            gridSize = 3
                            gameViewModel.startGame(3)
                            showHome = false
                        },
                        onPlay4x4 = {
                            gridSize = 4
                            gameViewModel.startGame(4)
                            showHome = false
                        },
                        onPlay5x5 = {
                            gridSize = 5
                            gameViewModel.startGame(5)
                            showHome = false
                        },
                        onViewStatistics = {
                            showHome = false
                            showStatistics = true
                        }
                    )

                } else if (showStatistics) {

                    StatisticsScreen(
                        viewModel = gameViewModel,
                        onBack = {
                            showStatistics = false
                            showHome = true
                        }
                    )

                } else {

                    PuzzleScreen(
                        gridSize = gridSize,
                        viewModel = gameViewModel,
                        onBack = {
                            gameViewModel.startGame(gridSize)   // reset board
                            showHome = true
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    private fun hideSystemBars() {
        controller?.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}