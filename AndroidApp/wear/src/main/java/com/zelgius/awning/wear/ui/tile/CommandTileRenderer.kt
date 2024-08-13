package com.zelgius.awning.wear.ui.tile

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Spacer
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.ButtonColors
import androidx.wear.protolayout.material.ButtonDefaults
import androidx.wear.protolayout.material.CircularProgressIndicator
import androidx.wear.protolayout.material.layouts.EdgeContentLayout
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer
import com.zelgius.awning.Status
import com.zelgius.awning.wear.R
import java.util.UUID

@OptIn(ExperimentalHorologistApi::class)
class CommandTileRenderer(context: Context) : SingleTileLayoutRenderer<AwningState, Unit>(context) {

    override val freshnessIntervalMillis: Long
        get() = 1000L

    override fun getResourcesVersionForTileState(state: AwningState): String = UUID.randomUUID().toString()

    override fun renderTile(
        state: AwningState,
        deviceParameters: DeviceParametersBuilders.DeviceParameters
    ): LayoutElementBuilders.LayoutElement {
        return EdgeContentLayout.Builder(deviceParameters).apply {
            if (state.progress != null && (state.status == Status.Opening || state.status == Status.Closing))
                setEdgeContent(buildProgress(state.status, state.progress))
        }
            .setContent(
                LayoutElementBuilders.Column.Builder()
                    .addContent(
                        if (state.status == Status.Closing) stopButton()
                        else closeButton(state.status)
                    )
                    .addContent(Spacer.Builder().setHeight(DimensionBuilders.dp(8f)).build())
                    .addContent(
                        if (state.status == Status.Opening) stopButton()
                        else openButton(state.status)
                    )
                    .build()
            ).build()
    }

    private fun closeButton(state: Status) = Button.Builder(
        context, ModifiersBuilders.Clickable.Builder()
            .setId(if (state != Status.Closed) "close" else "ignore")
            .setOnClick(ActionBuilders.LoadAction.Builder().build())
            .build()
    )
        .setButtonColors(
            if (state != Status.Closed) ButtonDefaults.PRIMARY_COLORS
            else ButtonDefaults.SECONDARY_COLORS
        )
        .setIconContent(ID_IC_UP, DimensionBuilders.dp(65f))
        .setSize(DimensionBuilders.dp(65f))
        .build()

    private fun openButton(state: Status) = Button.Builder(
        context, ModifiersBuilders.Clickable.Builder()
            .setId(if (state != Status.Opened) "open" else "ignore")
            .setOnClick(ActionBuilders.LoadAction.Builder().build())
            .build()
    )
        .setIconContent(ID_IC_DOWN, DimensionBuilders.dp(65f))
        .setButtonColors(
            if (state != Status.Opened) ButtonDefaults.PRIMARY_COLORS
            else ButtonDefaults.SECONDARY_COLORS
        )
        .setSize(DimensionBuilders.dp(70f))
        .build()

    private fun stopButton() = Button.Builder(
        context, ModifiersBuilders.Clickable.Builder()
            .setId("stop")
            .setOnClick(ActionBuilders.LoadAction.Builder().build())
            .build()
    )
        .setTextContent(context.getString(R.string.stop))
        .setButtonColors(ButtonColors(0xFF4F378B.toInt(), 0xFFEADDFF.toInt()))
        .setSize(DimensionBuilders.dp(70f))
        .build()


    private fun buildProgress(state: Status, progress: Int) =
        with(CircularProgressIndicator.Builder()) {
            if (state == Status.Opening)
                this.setStartAngle(15f)
                    .setEndAngle(165f)
            else this.setStartAngle(195f)
                .setEndAngle(350f)
        }
            .setProgress(progress / 100f)
            .build()

    override fun ResourceBuilders.Resources.Builder.produceRequestedResources(
        resourceState: Unit,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        resourceIds: MutableList<String>
    ) {
        addIdToImageMapping(ID_IC_UP, drawableResToImageResource(R.drawable.keyboard_arrow_up))
        addIdToImageMapping(ID_IC_DOWN, drawableResToImageResource(R.drawable.keyboard_arrow_down))
    }

    companion object {

        internal const val ID_IC_UP = "ic_up"
        internal const val ID_IC_DOWN = "ic_DOWN"
    }
}


@OptIn(ExperimentalHorologistApi::class)
@WearDevicePreview
@Composable
fun OpeningTileRendererPreview() {
    val state = AwningState(status = Status.Opening, progress = 75)
    val context = LocalContext.current
    TileLayoutPreview(
        state = state,
        resourceState = Unit,
        renderer = CommandTileRenderer(context)
    )
}

@OptIn(ExperimentalHorologistApi::class)
@WearDevicePreview
@Composable
fun ClosingTileRendererPreview() {
    val state = AwningState(status = Status.Closing, progress = 75)
    val context = LocalContext.current
    TileLayoutPreview(
        state = state,
        resourceState = Unit,
        renderer = CommandTileRenderer(context)
    )
}

@OptIn(ExperimentalHorologistApi::class)
@WearDevicePreview
@Composable
fun OpenedTileRendererPreview() {
    val state = AwningState(status = Status.Opened, progress = null)
    val context = LocalContext.current
    TileLayoutPreview(
        state = state,
        resourceState = Unit,
        renderer = CommandTileRenderer(context)
    )
}

@OptIn(ExperimentalHorologistApi::class)
@WearDevicePreview
@Composable
fun ClosedTileRendererPreview() {
    val state = AwningState(status = Status.Closed, progress = null)
    val context = LocalContext.current
    TileLayoutPreview(
        state = state,
        resourceState = Unit,
        renderer = CommandTileRenderer(context)
    )
}


@OptIn(ExperimentalHorologistApi::class)
@WearDevicePreview
@Composable
fun StoppedTileRendererPreview() {
    val state = AwningState(status = Status.Stopped, progress = null)
    val context = LocalContext.current
    TileLayoutPreview(
        state = state,
        resourceState = Unit,
        renderer = CommandTileRenderer(context)
    )
}