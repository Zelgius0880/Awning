package com.zelgius.awning.wear.ui

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.RadioButton
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.zelgius.awning.Status
import com.zelgius.awning.wear.R
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun Settings(
    viewModel: HomeViewModel,
    scalingLazyListState: ScalingLazyListState
) {
    val status by viewModel.status.collectAsState(initial = Status.Stopped)
    SettingsScreen(scalingLazyListState = scalingLazyListState, status = status,
        onStatusChanged = {
            viewModel.forceStatus(it)
        },
        onClosePressed = {
            viewModel.close()
        },
        onOpenPressed = {
            viewModel.open()
        },
        onStopped = {
            viewModel.stop()
        }
    )
}

@Composable
private fun SettingsScreen(
    status: Status = Status.Stopped,
    scalingLazyListState: ScalingLazyListState,
    onStatusChanged: (Status) -> Unit = {},
    onOpenPressed: () -> Unit = {},
    onClosePressed: () -> Unit = {},
    onStopped: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize()
            .onRotaryScrollEvent {
                coroutineScope.launch {
                    scalingLazyListState.scrollBy(it.verticalScrollPixels)
                }
                true
            }
            .focusRequester(focusRequester)
            .focusable(),
        autoCentering = AutoCenteringParams(itemIndex = 0),
        state = scalingLazyListState
    ) {
        item {
            Text(
                text = stringResource(id = R.string.manual_opening),
                style = MaterialTheme.typography.titleSmall
            )
        }

        item {
            ManualControl(onOpenPressed, onClosePressed, onStopped)
        }

        item {
            Text(
                text = stringResource(id = R.string.it_is_now),
                style = MaterialTheme.typography.titleSmall
            )
        }

        item {
            ToggleRadio(
                label = stringResource(id = R.string.opened),
                isChecked = status == Status.Opened
            ) {
                if (it) onStatusChanged(Status.Opened)
            }
        }

        item {
            ToggleRadio(
                label = stringResource(id = R.string.closed),
                isChecked = status == Status.Closed
            ) {
                if (it) onStatusChanged(Status.Closed)
            }
        }

    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}

@Composable
fun ToggleRadio(label: String, isChecked: Boolean, onClick: (Boolean) -> Unit = {}) {
    ToggleChip(modifier = Modifier.fillMaxWidth(), checked = isChecked, onCheckedChange = onClick,
        label = {
            Text(label)
        },
        toggleControl = {
            RadioButton(selected = isChecked)
        })
}


@Composable
fun ManualControl(
    onOpenPressed: () -> Unit = {},
    onClosePressed: () -> Unit = {},
    onStopped: () -> Unit = {}
) {
    Card(onClick = {}) {

        Row(modifier = Modifier.padding(top = 8.dp)) {
            PressButton(
                onPressed = onOpenPressed,
                onReleased = onStopped,
                colors = ButtonDefaults.primaryButtonColors(backgroundColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    painterResource(id = R.drawable.keyboard_arrow_down),
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PressButton(
                onPressed = onClosePressed,
                onReleased = onStopped,
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Icon(
                    painterResource(id = R.drawable.keyboard_arrow_up),
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun PressButton(
    onPressed: () -> Unit = {},
    onReleased: () -> Unit = {},
    colors: ButtonColors = ButtonDefaults.primaryButtonColors(),
    content: @Composable BoxScope.() -> Unit
) {
    Button(
        onClick = { },
        colors = colors,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    onPressed()
                    try {
                        tryAwaitRelease()
                    } catch (_: CancellationException) {
                    }

                    onReleased()
                }
            )
        }) {

        this.content()
    }
}