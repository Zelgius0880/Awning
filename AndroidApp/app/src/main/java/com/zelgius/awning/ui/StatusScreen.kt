package com.zelgius.awning.ui

import android.view.MotionEvent
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zelgius.awning.HomeViewModel
import com.zelgius.awning.app.R
import com.zelgius.awning.Status

@Composable
fun StatusScreen(
    viewModel: HomeViewModel
) {
    val progress by viewModel.progress.collectAsState(initial = null)
    val closingTime by viewModel.closingTime.collectAsState(initial = 0L)
    val openingTime by viewModel.openingTime.collectAsState(initial = 0L)
    val status by viewModel.status.collectAsState(initial = Status.Stopped)
    val network by viewModel.network.collectAsState(initial = 0)
    Status(
        status = status,
        openingTime = openingTime,
        closingTime = closingTime,
        progress = progress,
        network = network,
        onOpen = { viewModel.open() },
        onClose = { viewModel.close() },
        onStop = { viewModel.stop() },
        onOpeningTimeUpdate = { viewModel.setOpeningTime(it) },
        onClosingTimeUpdate = { viewModel.setClosingTime(it) },
        onStatusChanged = { viewModel.forceStatus(it) }
    )
}


@Composable
fun Status(
    status: Status,
    openingTime: Long,
    closingTime: Long,
    network: Int,
    modifier: Modifier = Modifier,
    progress: Int? = null,
    onOpen: () -> Unit = {},
    onClose: () -> Unit = {},
    onStop: () -> Unit = {},
    onOpeningTimeUpdate: (time: Long) -> Unit = {},
    onClosingTimeUpdate: (time: Long) -> Unit = {},
    onStatusChanged: (status: Status) -> Unit = {}
) {
    Column {
        Card(modifier = modifier.padding(8.dp)) {
            Row(modifier = Modifier.padding(8.dp) then Modifier.fillMaxWidth()) {

                Box(
                    modifier = Modifier.padding(end = 4.dp) then Modifier
                        .weight(1f)
                        .aspectRatio(0.5f)
                ) {

                    OpenButton(
                        status = status,
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(topStartPercent = 60, bottomStartPercent = 60),
                        onClick = onOpen,
                        onStop = onStop
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "",
                            modifier = Modifier.size(100.dp)
                        )
                    }

                    CircularProgress(
                        progress = if (status == Status.Opening) progress else null,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }

                Box(
                    modifier = Modifier.padding(start = 4.dp) then Modifier
                        .weight(1f)
                        .aspectRatio(0.5f),
                ) {

                    CloseButton(
                        status = status,
                        shape = RoundedCornerShape(bottomEndPercent = 60, topEndPercent = 60),
                        modifier = Modifier.fillMaxSize(),
                        onClick = onClose,
                        onStop = onStop
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowUp,
                            contentDescription = "",
                            modifier = Modifier.size(100.dp)
                        )
                    }

                    CircularProgress(
                        progress = if (status == Status.Closing) progress else null,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }

            }
        }

        Card(modifier = modifier.padding(8.dp)) {
            Column {
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(8.dp)
                ) {
                    Text(text = "$network%")
                    Icon(
                        painterResource(id = R.drawable.baseline_signal_wifi_statusbar_4_bar_24),
                        contentDescription = ""
                    )
                }

                Settings(
                    openingTime = openingTime,
                    closingTime = closingTime,
                    status = status,
                    onOpeningUpdate = onOpeningTimeUpdate,
                    onClosingUpdate = onClosingTimeUpdate,
                    openPressChanged = {
                        if (it) onOpen() else onStop()
                    },
                    closePressChanged = {
                        if (it) onClose() else onStop()
                    },
                    onStatusChanged = onStatusChanged
                )

            }
        }
    }
}

@Composable
fun CircularProgress(progress: Int?, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = progress != null && progress >= 0, modifier = modifier
            .padding(8.dp)
    ) {
        CircularProgressIndicator((progress ?: 0) / 100f)
    }
}

@Composable
@Preview
fun StatusPreview() {
    MaterialTheme() {
        Status(Status.Closing, 2000, 2000, network = 50, progress = 64)
    }
}

@Composable
fun Settings(
    openingTime: Long,
    closingTime: Long,
    status: Status,
    onOpeningUpdate: (time: Long) -> Unit = {},
    onClosingUpdate: (time: Long) -> Unit = {},
    openPressChanged: (isPressed: Boolean) -> Unit = {},
    closePressChanged: (isPressed: Boolean) -> Unit = {},
    onStatusChanged: (status: Status) -> Unit = {}
) {
    var isPanelOpened by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (isPanelOpened) 60f else 0f, label = "")
    Column(modifier = Modifier.animateContentSize()) {

        Row {
            Crossfade(
                targetState = isPanelOpened,
                modifier = Modifier.weight(1f),
                animationSpec = tween(150), label = ""
            ) {
                if (it)
                    Text(
                        text = stringResource(id = R.string.settings),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(8.dp)
                    )
            }

            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp),
                onClick = {
                    isPanelOpened = !isPanelOpened
                }) {
                Icon(
                    Icons.TwoTone.Settings,
                    contentDescription = "",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }


        if (isPanelOpened) {
            Time(
                modifier = Modifier.height(IntrinsicSize.Min),
                title = stringResource(
                    id = R.string.opening_time,
                    openingTime
                ), time = openingTime, onTimeUpdated = onOpeningUpdate
            )
            Time(
                modifier = Modifier.height(IntrinsicSize.Min),
                title = stringResource(
                    id = R.string.closing_time,
                    closingTime
                ), time = closingTime, onTimeUpdated = onClosingUpdate
            )

            Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {


                Text(stringResource(id = R.string.it_is_now))
                ForceStatusButton(
                    currentStatus = status,
                    onStateClicked = onStatusChanged,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            ManualOpening(
                openPressChanged = openPressChanged,
                closePressChanged = closePressChanged
            )
        }
    }
}

@Composable
fun ManualOpening(
    openPressChanged: (isPressed: Boolean) -> Unit,
    closePressChanged: (isPressed: Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = stringResource(id = R.string.manual_opening),
            style = MaterialTheme.typography.headlineMedium
        )

        Row(modifier = Modifier.padding(top = 8.dp)) {
            PressButton(
                text = stringResource(id = R.string.open),
                colors = MaterialTheme.colorScheme,
                onPressChanged = openPressChanged,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            PressButton(
                text = stringResource(id = R.string.close),
                colors = MaterialTheme.colorScheme,
                onPressChanged = closePressChanged,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PressButton(
    text: String,
    colors: ColorScheme,
    modifier: Modifier = Modifier,
    onPressChanged: (isPressed: Boolean) -> Unit,
) {
    var pressed by remember {
        mutableStateOf(false)
    }
    val colorPressed = colors.primary.copy(alpha = 0.6f)

    val background = remember { Animatable(colors.primary) }
    LaunchedEffect(pressed) {
        background.animateTo(if (!pressed) colors.primary else colorPressed)
    }

    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(color = colors.onPrimary),
        textAlign = TextAlign.Center,
        modifier = modifier

            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        onPressChanged(true)
                        pressed = true
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        pressed = false
                        onPressChanged(false)
                        true
                    }

                    else -> false
                }
            }
            .background(
                background.value, shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 8.dp, vertical = 16.dp)
    )
}

@Composable
@Preview
fun SettingsPreview() {
    Settings(
        500,
        500,
        Status.Closing,
        openPressChanged = {},
        closePressChanged = {})
}