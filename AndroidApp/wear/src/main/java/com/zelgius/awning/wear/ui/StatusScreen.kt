package com.zelgius.awning.wear.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material3.MaterialTheme
import com.zelgius.awning.Status
import com.zelgius.awning.wear.R

@Composable
fun StatusScreen(
    viewModel: HomeViewModel,
    navController: NavController
) {
    val progress by viewModel.progress.collectAsState(initial = null)
    val status by viewModel.status.collectAsState(initial = Status.Stopped)

    val loading by viewModel.loading.collectAsState()

    ScaleAnimatedVisibility(visible = loading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator( Modifier.size(size = 48.dp).align(Alignment.Center))
        }
    }


    ScaleAnimatedVisibility(visible = !loading) {
        Status(status = status, progress = progress,
            onOpen = {
                viewModel.open()
            },
            onClose = {
                viewModel.close()
            },
            onStop = {
                viewModel.stop()
            },
            onSettingsClick = {
                navController.navigate("settings")
            }
        )
    }
}

@Composable
fun ScaleAnimatedVisibility(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(visible = visible, enter = scaleIn(), exit = scaleOut()) {
        content()
    }
}

@Composable
fun Status(
    status: Status,
    progress: Int?,
    onOpen: () -> Unit = {},
    onClose: () -> Unit = {},
    onStop: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {

    Box(modifier = Modifier.fillMaxSize()) {

        AnimatedVisibility(visible = progress != null) {
            CircularProgressIndicator(
                startAngle = 290f,
                endAngle = 250f,
                progress = (progress ?: 0) / 100f,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .padding(12.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .padding(20.dp)
        ) {
            OpenButton(
                status = status,
                modifier = Modifier
                    .weight(1f),
                onClick = onOpen,
                onStop = onStop
            )
            CloseButton(
                status = status,
                modifier = Modifier
                    .weight(1f),
                onClick = onClose,
                onStop = onStop
            )

        }

        Box(
            Modifier
                .fillMaxHeight()
                .align(Alignment.Center)
                .zIndex(20f)
                .padding(vertical = 16.dp)
                .width(4.dp)
                .background(color = Color.Black)
        )

        Button(
            onClick = onSettingsClick,
            colors = ButtonDefaults.primaryButtonColors(backgroundColor = MaterialTheme.colorScheme.tertiaryContainer),
            modifier = Modifier
                .size(32.dp)
                .zIndex(21f)
                .align(Alignment.BottomCenter)
                .padding(4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_settings_24),
                contentDescription = "",
                modifier = Modifier
                    .size(16.dp)
            )
        }
    }

}

@Preview(
    group = "Button",
    widthDp = WEAR_PREVIEW_ELEMENT_WIDTH_DP,
    heightDp = WEAR_PREVIEW_ELEMENT_HEIGHT_DP,
    uiMode = WEAR_PREVIEW_UI_MODE,
    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
)
@Composable
fun StatusPreview() {
    Status(Status.Opened, progress = 50)
}