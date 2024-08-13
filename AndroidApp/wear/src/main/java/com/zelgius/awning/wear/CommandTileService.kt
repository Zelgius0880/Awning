package com.zelgius.awning.wear

import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.zelgius.awning.AwningRepository
import com.zelgius.awning.Status
import com.zelgius.awning.wear.ui.tile.AwningState
import com.zelgius.awning.wear.ui.tile.CommandTileRenderer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class CommandTileService : TileService() {
    @Inject
    lateinit var awningRepository: AwningRepository
    private lateinit var renderer: CommandTileRenderer
    private lateinit var tileStateFlow: StateFlow<AwningState>

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate() {
        super.onCreate()
        renderer = CommandTileRenderer(this)

        scope.launch {
            awningRepository.startListening()
        }

    }

    override fun onDestroy() {
        awningRepository.stopListening()
        super.onDestroy()
    }

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        scope.launch {
            when(requestParams.currentState.lastClickableId) {
                "close" -> awningRepository.setStatus(Status.Closed)
                "open" -> awningRepository.setStatus(Status.Opened)
                "stop" -> awningRepository.setStatus(Status.Stopped)
            }
        }

        val lastState = runBlocking {
            awningRepository.awningFlow
                .map { AwningState(status = it.status, progress = it.progress) }
                .first()
        }.let {
            it.copy(status = when(requestParams.currentState.lastClickableId) {
                "close" -> Status.Closing
                "open"  -> Status.Opening
                "stop"  -> Status.Stopped
                else -> it.status
            })
        }

        return Futures.immediateFuture(
            TileBuilders.Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setFreshnessIntervalMillis(1000)
                .setTileTimeline(
                    TimelineBuilders.Timeline.Builder().addTimelineEntry(
                        TimelineBuilders.TimelineEntry.Builder().setLayout(
                            LayoutElementBuilders.Layout.Builder().setRoot(
                                renderer.renderTile(
                                    lastState,
                                    requestParams.deviceConfiguration
                                )
                            ).build()
                        ).build()
                    ).build()
                ).build()
        )
    }

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> =
        Futures.immediateFuture(
            ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .addIdToImageMapping(
                    CommandTileRenderer.ID_IC_UP,
                    drawableResToImageResource(R.drawable.keyboard_arrow_up)
                )
                .addIdToImageMapping(
                    CommandTileRenderer.ID_IC_DOWN,
                    drawableResToImageResource(R.drawable.keyboard_arrow_down)
                )
                .build()
        )

    companion object {
        val RESOURCES_VERSION = UUID.randomUUID().toString()
    }
}
/*

@OptIn(ExperimentalHorologistApi::class)
@AndroidEntryPoint
class CommandTileService : SuspendingTileService() {

    @Inject
    lateinit var awningRepository: AwningRepository
    private lateinit var renderer: CommandTileRenderer
    private lateinit var tileStateFlow: StateFlow<AwningState>

    override fun onCreate() {
        super.onCreate()
        renderer = CommandTileRenderer(this)

        lifecycleScope.launch {
            awningRepository.startListening()
        }

        tileStateFlow = awningRepository.awningFlow
            .map { AwningState(status = it.status, progress = it.progress) }
            //.distinctUntilChanged()
            .onEach {
                getUpdater(this).requestUpdate(this::class.java)
            }
            .stateIn(
                lifecycleScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AwningState(Status.Stopped, null)
            )
    }

    private suspend fun latestTileState(): AwningState {
        return tileStateFlow.filterNotNull().first()
    }


    companion object {
        val RESOURCES_VERSION = UUID.randomUUID().toString()
    }
    override suspend fun tileRequest(requestParams: RequestBuilders.TileRequest): TileBuilders.Tile {
        val tileState = latestTileState()
        return renderer.renderTimeline(tileState, requestParams)
    }


    override suspend fun resourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ResourceBuilders.Resources =
        ResourceBuilders.Resources.Builder()
            .setVersion(RESOURCES_VERSION)
            .build()

}*/
