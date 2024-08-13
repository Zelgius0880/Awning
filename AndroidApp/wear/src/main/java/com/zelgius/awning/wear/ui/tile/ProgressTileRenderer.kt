package com.zelgius.awning.wear.ui.tile

import android.content.Context
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.material.CircularProgressIndicator
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer
import com.zelgius.awning.Status

@OptIn(ExperimentalHorologistApi::class)
class ProgressTileRenderer(context: Context) : SingleTileLayoutRenderer<AwningState, Unit>(context) {
    override fun renderTile(
        state: AwningState,
        deviceParameters: DeviceParametersBuilders.DeviceParameters
    ): LayoutElementBuilders.LayoutElement {
        return LayoutElementBuilders.Box.Builder().apply {
            if (state.progress != null && (state.status == Status.Opening || state.status == Status.Closing))
                addContent(buildProgress(state.status, state.progress))
        }.build()

    }


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
    
    companion object {

        internal const val ID_IC_UP = "ic_up"
        internal const val ID_IC_DOWN = "ic_DOWN"
    }
}