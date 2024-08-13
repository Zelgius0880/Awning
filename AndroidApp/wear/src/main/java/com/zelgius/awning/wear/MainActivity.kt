package com.zelgius.awning.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.zelgius.awning.wear.ui.HomeViewModel
import com.zelgius.awning.wear.ui.Settings
import com.zelgius.awning.wear.ui.StatusScreen
import com.zelgius.awning.wear.ui.theme.WearAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearAppTheme {
                val navController = rememberSwipeDismissableNavController()
                val listState = rememberScalingLazyListState()

                val viewModel: HomeViewModel = hiltViewModel()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    positionIndicator = {
                        PositionIndicator(scalingLazyListState = listState)
                    },
                    timeText = {
                        TimeText()
                    },
                    content = {
                        SwipeDismissableNavHost(
                            navController = navController,
                            startDestination = "home"
                        ) {
                            composable("home") {
                                StatusScreen(viewModel = viewModel, navController)
                            }

                            composable("settings") {
                                Settings(viewModel = viewModel, listState)
                            }
                        }
                    })
            }
        }
    }
}