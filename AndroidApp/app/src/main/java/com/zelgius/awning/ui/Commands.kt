package com.zelgius.awning.ui

import androidx.compose.runtime.Composable
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zelgius.awning.app.R
import com.zelgius.awning.Status
import java.util.Locale

@Composable
fun OpenButton(
    status: Status,
    modifier: Modifier = Modifier,
    shape: Shape = ButtonDefaults.shape,
    onClick: () -> Unit,
    onStop: () -> Unit,
    content: @Composable () -> Unit = {
        Text(
            text = stringResource(id = R.string.open).uppercase(),
        )
    }
) {

    if (status != Status.Opening)
        Button(
            onClick = onClick,
            enabled = status != Status.Opened,
            modifier = modifier,
            shape = shape,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary)
        ) {

            content()

        }
    else
        OutlinedButton(
            onClick = onStop,
            shape = shape,
            modifier = modifier,
        ) {

            Text(
                text = stringResource(id = R.string.stop).uppercase(),
                color = MaterialTheme.colorScheme.primary
            )

        }

}

@Composable
fun CloseButton(
    status: Status,
    modifier: Modifier = Modifier,
    shape: Shape = ButtonDefaults.shape,
    onClick: () -> Unit,
    onStop: () -> Unit,
    content: @Composable () -> Unit = {
        Text(
            text = stringResource(id = R.string.close).uppercase(),
        )
    }
) {
    if (status != Status.Closing)
        Button(
            onClick = onClick,
            enabled = status != Status.Closed,
            shape = shape,
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary)
        ) {
            content()
        }
    else
        OutlinedButton(
            onClick = onStop,
            shape = shape,
            modifier = modifier
        ) {

            Text(
                text = stringResource(id = R.string.stop).uppercase(),
                color = MaterialTheme.colorScheme.secondary
            )
        }
}

@Composable
fun Time(title: String, time: Long, modifier: Modifier = Modifier, onTimeUpdated: (time: Long) -> Unit) {
    var edit by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("$time") }

    Crossfade(targetState = edit, modifier = modifier, label = "") {
        if (!it) {
            Row(modifier = Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = title
                )

                IconButton(
                    onClick = { edit = true },
                ) {
                    Icon(
                        Icons.TwoTone.Edit,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.padding(
                    8.dp
                ), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text =  title,
                    modifier = Modifier.padding(end = 4.dp)
                )

                BasicTextField(
                    value = text,
                    onValueChange = { s -> text = s },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            BorderStroke(2.dp, color = MaterialTheme.colorScheme.background),
                            RoundedCornerShape(10)
                        )
                        .padding(4.dp),
                )

                Text(text = "ms", modifier = Modifier.padding(start = 4.dp))

                IconButton(
                    onClick = {
                        edit = false
                        text = "$time"
                    },
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.Close,
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null
                    )
                }

                IconButton(modifier = Modifier,
                    onClick = {
                        edit = false
                        text.toLongOrNull().let {
                            if (it == null)
                                text = "$time"
                            else
                                onTimeUpdated(it)
                        }
                    }) {
                    Icon(
                        imageVector = Icons.TwoTone.Check,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
            }

        }
    }
}

@Composable
@Preview
fun TimeCardPreview() {
    Time(title=  stringResource(
        id = R.string.opening_time,
        500
    ), 500, Modifier.padding(8.dp)) {}
}


@Composable
@Preview
fun OpenButtonPreview() {
    OpenButton(Status.Stopped, Modifier.padding(8.dp), onClick = {}, onStop = {})
}

@Composable
@Preview
fun CloseButtonPreview() {
    CloseButton(Status.Stopped, Modifier.padding(8.dp), onClick = {}, onStop = {})
}
