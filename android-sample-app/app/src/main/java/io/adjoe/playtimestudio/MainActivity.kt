package io.adjoe.playtimestudio

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import io.adjoe.playtimestudio.ui.ApproveTOS
import io.adjoe.playtimestudio.ui.CampaignDetails
import io.adjoe.playtimestudio.ui.Catalogue
import io.adjoe.playtimestudio.ui.Loader
import io.adjoe.playtimestudio.ui.theme.PlaytimeStudioTheme
import io.adjoe.playtimestudio.viewModels.CampaignDetailsViewModel
import io.adjoe.playtimestudio.viewModels.CatalogueViewModel
import io.adjoe.playtimestudio.viewModels.OnboardingViewModel
import io.adjoe.sdk.studio.PlaytimeEngagementType
import io.adjoe.sdk.studio.PlaytimeExecuteEngagementListener
import io.adjoe.sdk.studio.PlaytimeResponseError
import io.adjoe.sdk.studio.PlaytimeStudio

class MainActivity : ComponentActivity() {
    private lateinit var player: Player
    private lateinit var playerView: PlayerView

    private fun initializeSessionAndPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView = PlayerView(this)
        playerView.player = player
        playerView.player?.playWhenReady = true
        playerView.useController = false
        player.prepare()
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (!isPlaying) {
                        player.play()
                    }
                }
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val context = this
        initializeSessionAndPlayer()

        setContent {
            val viewModel by viewModels<OnboardingViewModel>()
            val catalogueViewModel by viewModels<CatalogueViewModel>()
            val campaignDetailsViewModel by viewModels<CampaignDetailsViewModel>()

            LaunchedEffect(true) {
                viewModel.checkPlaytimeInit(context)
            }

            PlaytimeStudioTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (viewModel.playtimeInit) {
                        if (viewModel.onboardingVisible) {
                            ApproveTOS(
                                this,
                                viewModel.onboardingStep,
                                { step ->
                                    viewModel.onboardingStep = step
                                },
                                { permissions ->
                                    viewModel.onApproveTOSFinished(permissions)
                                }
                            )
                        }

                        if (campaignDetailsViewModel.activeCampaign != null) {
                            CampaignDetails(
                                Modifier.padding(innerPadding),
                                campaignDetailsViewModel,
                                player,
                                playerView,
                                onFinish = { campaignDetailsViewModel.activeCampaign = null },
                                startCheckInstallWorker = {
                                    catalogueViewModel.startCheckInstallWorker(
                                        this,
                                        campaignDetailsViewModel.activeCampaign!!
                                    )
                                },
                                baseContext,
                                activity = this@MainActivity
                            )
                        } else {
                            Catalogue(
                                Modifier.padding(innerPadding),
                                context = this,
                                catalogueViewModel,
                                openCampaignDetails = {
                                    campaignDetailsViewModel.activeCampaign = it

                                    PlaytimeStudio.executeEngagement(
                                        context,
                                        it,
                                        PlaytimeEngagementType.ENGAGED,
                                        object : PlaytimeExecuteEngagementListener {
                                            override fun onAlreadyEngaging() {
                                                Util.logVisible(context as Activity, "On already engaging ${it.appName}")
                                            }

                                            override fun onError(error: PlaytimeResponseError?) {
                                                Util.logVisible(context as Activity, "Error executing engagement for ${it.appName}: ${error?.error?.message}")
                                            }

                                            override fun onFinished() { }
                                        })
                                }
                            )
                        }
                    } else {
                        Loader()
                    }
                }
            }
        }
    }
}

