package com.zelgius.awning.wear.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.zelgius.awning.Status
import com.zelgius.awning.wear.R
import com.zelgius.awning.wear.ui.theme.WearAppTheme

@Composable
fun OpenButton(
    modifier: Modifier = Modifier,
    status: Status,
    onClick: () -> Unit = {},
    onStop: () -> Unit = {},
) {
    val shape = StartHalfCircleShape()

    if (status != Status.Opening)
        Button(
            onClick = onClick,
            shape = shape,
            enabled = status != Status.Opened,
            colors = ButtonDefaults.primaryButtonColors(backgroundColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = modifier.fillMaxSize()
        ) {
            Icon(
                painterResource(id = R.drawable.keyboard_arrow_down),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(100.dp)
            )
        }
    else
        Button(
            onClick = onStop,
            shape = shape,
            colors = ButtonDefaults.primaryButtonColors(backgroundColor = MaterialTheme.colorScheme.surface),
            modifier = modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(id = R.string.stop).uppercase(),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
}

@Composable
fun CloseButton(
    modifier: Modifier = Modifier,
    status: Status,
    onClick: () -> Unit = {},
    onStop: () -> Unit = {},
) {
    val shape = EndHalfCircleShape()
    if (status != Status.Closing)
        Button(
            onClick = onClick,
            shape = EndHalfCircleShape(),
            enabled = status != Status.Closed,
            colors = ButtonDefaults.primaryButtonColors(backgroundColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = modifier.fillMaxSize()
        ) {
            Icon(
                painterResource(id = R.drawable.keyboard_arrow_up),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(100.dp)
            )
        }
    else
        Button(
            onClick = onStop,
            shape = shape,
            colors = ButtonDefaults.primaryButtonColors(backgroundColor = MaterialTheme.colorScheme.surface),
            modifier = modifier.fillMaxSize()
        ) {

            Text(
                text = stringResource(id = R.string.stop).uppercase(),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
}


@Preview(
    widthDp = WEAR_PREVIEW_ELEMENT_WIDTH_DP,
    heightDp = WEAR_PREVIEW_ELEMENT_HEIGHT_DP,
    uiMode = WEAR_PREVIEW_UI_MODE,
    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
)
@Composable
fun OpenButtonPreview() {
    WearAppTheme {
        OpenButton(status = Status.Closed)
    }
}


class StartHalfCircleShape : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            // Draw your custom path here
            path = Path().apply {
                reset()
                arcTo(
                    Rect(
                        left = 0f,
                        top = 0f,
                        right = size.width * 2,
                        bottom = size.width * 2
                    ),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )

                close()
            }
        )
    }
}

class EndHalfCircleShape : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            // Draw your custom path here
            path = Path().apply {
                reset()
                arcTo(
                    Rect(
                        left = -size.width,
                        top = 0f,
                        right = size.width,
                        bottom = size.width * 2
                    ),
                    startAngleDegrees = -90f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = true
                )

                close()
            }
        )
    }
}
