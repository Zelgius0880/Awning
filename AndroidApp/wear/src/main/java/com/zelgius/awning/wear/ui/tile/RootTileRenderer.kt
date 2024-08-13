package com.zelgius.awning.wear.ui.tile

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.StateBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.tiles.render.TileLayoutRenderer
import com.zelgius.awning.BuildConfig
import com.zelgius.awning.Status
import com.zelgius.awning.wear.CommandTileService
import java.util.UUID

@OptIn(ExperimentalHorologistApi::class)
class RootTileRenderer(context: Context) : TileLayoutRenderer<AwningState, Unit> {

    private val progressTileRenderer = ProgressTileRenderer(context)
    private val commandTileRenderer = CommandTileRenderer(context)

    override fun renderTimeline(
        state: AwningState,
        requestParams: RequestBuilders.TileRequest
    ): TileBuilders.Tile {
        val progressLayout =
            progressTileRenderer.renderTile(state, requestParams.deviceConfiguration)
        val commandLayout = commandTileRenderer.renderTile(state, requestParams.deviceConfiguration)
        val timeline = TimelineBuilders.Timeline.Builder()
            .addTimelineEntry(
                TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(
                        LayoutElementBuilders.Layout.Builder()
                            .setRoot(progressLayout)
                            .build()
                    )
                    .build()
            )
            .addTimelineEntry(
                TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(
                        LayoutElementBuilders.Layout.Builder()
                            .setRoot(commandLayout)
                            .build()
                    )
                    .build()
            )
            .build()

        return TileBuilders.Tile.Builder()
            .setResourcesVersion(
                if (BuildConfig.DEBUG) {
                    UUID.randomUUID().toString()
                } else {
                    CommandTileService.RESOURCES_VERSION
                }
            )
            .setState(StateBuilders.State.Builder().build())
            .setTileTimeline(timeline)
            .build()
    }

    override fun produceRequestedResources(
        resourceState: Unit,
        requestParams: RequestBuilders.ResourcesRequest
    ): ResourceBuilders.Resources {
        return commandTileRenderer.produceRequestedResources(resourceState, requestParams)
    }
}


@OptIn(ExperimentalHorologistApi::class)
@WearDevicePreview
@Composable
fun RootPreview() {
    val state = AwningState(status = Status.Opening, progress = 75)
    val context = LocalContext.current
    TileLayoutPreview(
        state = state,
        resourceState = Unit,
        renderer = CommandTileRenderer(context)
    )
}