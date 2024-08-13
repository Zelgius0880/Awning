package com.zelgius.awning.ui

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zelgius.awning.app.R
import com.zelgius.awning.Status
import com.zelgius.awning.ui.theme.AwningTheme

private object PrimaryForceStatusButtonRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor(): Color =
        RippleTheme.defaultRippleColor(MaterialTheme.colorScheme.primary, !isSystemInDarkTheme())

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        MaterialTheme.colorScheme.primary,
        lightTheme = !isSystemInDarkTheme()
    ).let {
        RippleAlpha(
            draggedAlpha = it.draggedAlpha,
            focusedAlpha = it.focusedAlpha,
            hoveredAlpha = it.hoveredAlpha,
            pressedAlpha = if (isSystemInDarkTheme()) 0.2f else 0.7f
        )
    }
}

private object SecondaryForceStatusButtonRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor(): Color =
        RippleTheme.defaultRippleColor(
            MaterialTheme.colorScheme.secondary,
            !isSystemInDarkTheme()
        )

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        MaterialTheme.colorScheme.secondary,
        lightTheme = !isSystemInDarkTheme()
    ).let {
        RippleAlpha(
            draggedAlpha = it.draggedAlpha,
            focusedAlpha = it.focusedAlpha,
            hoveredAlpha = it.hoveredAlpha,
            pressedAlpha = if (isSystemInDarkTheme()) 0.2f else 0.7f
        )
    }
}

@Composable
fun ForceStatusButton(
    currentStatus: Status,
    modifier: Modifier = Modifier,
    onStateClicked: (Status) -> Unit
) {
    val firstItemColor by animateColorAsState(
        targetValue = if (currentStatus == Status.Opened) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        label = ""
    )

    val firstItemTextColor by animateColorAsState(
        targetValue = if (currentStatus == Status.Opened) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = ""
    )

    val secondItemColor by animateColorAsState(
        if (currentStatus == Status.Stopped)
            if (isSystemInDarkTheme()) Color(0xffb1bfca) else Color(0xffe3f2fd)
        else MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp), label = ""
    )

    val thirdItemColor by animateColorAsState(
        targetValue = if (currentStatus == Status.Closed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
        label = ""
    )

    val thirdItemTextColor by animateColorAsState(
        targetValue = if (currentStatus == Status.Closed) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = ""
    )

    Row(modifier) {
        CompositionLocalProvider(LocalRippleTheme provides PrimaryForceStatusButtonRippleTheme) {
            RoundedCornerText(
                text = stringResource(id = R.string.opened),
                backgroundColor = firstItemColor,
                textColor = firstItemTextColor,
                borderColor = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(topStartPercent = 50, bottomStartPercent = 50)
            ) {
                onStateClicked(Status.Opened)
            }
        }

        val border = 1.dp
        val borderColor = MaterialTheme.colorScheme.onSurface
        Text(
            text = " - ",
            Modifier
                .background(secondItemColor)
                .drawBehind {
                    val strokeWidth = border.value * density
                    val y = size.height - strokeWidth / 2

                    drawLine(
                        borderColor,
                        Offset(0f, y),
                        Offset(size.width, y),
                        strokeWidth
                    )

                    drawLine(
                        borderColor,
                        Offset(0f, strokeWidth / 2),
                        Offset(size.width, strokeWidth / 2),
                        strokeWidth
                    )
                }
                .padding(vertical = 8.dp, horizontal = 8.dp)
            ,
            color = MaterialTheme.colorScheme.onSurface
        )

        CompositionLocalProvider(LocalRippleTheme provides SecondaryForceStatusButtonRippleTheme) {
            RoundedCornerText(
                text = stringResource(id = R.string.closed),
                backgroundColor = thirdItemColor,
                textColor = thirdItemTextColor,
                borderColor = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50)
            ) {
                onStateClicked(Status.Closed)
            }
        }
    }
}

@Composable
fun RoundedCornerText(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    onClick: () -> Unit = {}
) {
    Text(
        text = text,
        modifier
            .ifNotNull(shape) {
                clip(it)
            }
            .background(backgroundColor)
            .ifNotNull(shape) {
                border(
                    1.dp,
                    borderColor,
                    it
                )
            }
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        color = textColor,
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ForceStatusButtonPreview() {
    Preview(isDark = isSystemInDarkTheme())
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ForceStatusButtonPreviewDark() {
    Preview(isDark = isSystemInDarkTheme())
}

@Composable
fun Preview(isDark: Boolean) {
    var status by remember {
        mutableStateOf(Status.Stopped)
    }

    Column {
       AwningTheme(isDark) {
            ForceStatusButton(currentStatus = status, onStateClicked = { status = it })
        }
    }
}


inline fun <T : Any> Modifier.ifNotNull(value: T?, builder: Modifier.(T) -> Modifier): Modifier =
    then(if (value != null) builder(value) else Modifier)