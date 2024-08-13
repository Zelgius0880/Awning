package com.zelgius.awning.wear.ui.tile

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.zelgius.awning.Status

data class AwningState(
    val status: Status,
    val progress: Int?
)

@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Small Round",
)
public annotation class WearDevicePreview