package io.adjoe.playtimestudio.ui

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette
import io.adjoe.playtimestudio.viewModels.CampaignDetailsViewModel
import io.adjoe.sdk.PlaytimeException
import io.adjoe.sdk.studio.PlaytimeOpenStoreListener
import io.adjoe.sdk.studio.PlaytimeStudio

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CampaignDetails(
    modifier: Modifier,
    viewModel: CampaignDetailsViewModel,
    player: Player,
    playerView: PlayerView,
    onFinish: () -> Unit,
    activity: Activity
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(226.dp)
                .background(Color.Black)
        ) {
            if (viewModel.showVideo) {
                val mediaItem = MediaItem.fromUri(viewModel.video.toString())
                player.setMediaItem(mediaItem)

                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(true, onClick = {
                            viewModel.onBannerClick()
                        }),
                    factory = { playerView }
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .paint(
                                rememberAsyncImagePainter(
                                    viewModel.activeCampaign?.image?.landscape
                                ),
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f
                            )
                            .clickable(
                                true,
                                onClick = { viewModel.onBannerClick() }),
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                tint = LocalStudioColorsPalette.current.White,
                                contentDescription = "Play",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable(true, onClick = {
                                        onFinish()
                                    })
                            )
                        }

                        if (viewModel.hasVideo) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    tint = LocalStudioColorsPalette.current.White,
                                    contentDescription = "Play",
                                    modifier = Modifier.size(72.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .offset(y = (-80).dp)
        ) {
            Text(
                text = viewModel.activeCampaign?.appName.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = LocalStudioColorsPalette.current.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                if (viewModel.isInstalled) {
                    Column(Modifier.weight(1f)) {
                        ProgressBar(
                            viewModel.rewardedCoins,
                            viewModel.activeCampaign?.eventConfig?.totalCoinsPossible ?: 1,
                            viewModel.campaignExpired
                        )
                    }
                } else {
                    Text(
                        text = "${viewModel.activeCampaign?.eventConfig?.totalCoinsPossible}",
                        fontWeight = FontWeight.ExtraBold,
                        color = LocalStudioColorsPalette.current.PrimaryColor,
                        fontSize = 18.sp
                    )
                    Text(
                        text = " coins total",
                        fontSize = 12.sp,
                        color = LocalStudioColorsPalette.current.Grey70
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            viewModel.activeCampaign?.rewardingExpiresAfter?.let { rewardingExpiresAfter ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(LocalStudioColorsPalette.current.BlueGrey50)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "You have $rewardingExpiresAfter " +
                                "days to complete tasks after installing.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (viewModel.isInstalled) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(viewModel.getInstalledDaysBackground())
                        .padding(12.dp)
                ) {
                    Text(
                        text = viewModel.getInstalledDaysLeftText(),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = "DESCRIPTION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LocalStudioColorsPalette.current.BlueGrey500
            )
            Text(
                text = viewModel.activeCampaign?.appDescription.toString(),
                fontSize = 14.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(true, onClick = {
                        viewModel.toggleRewardSection()
                    })
            ) {
                Text(
                    text = "REWARD CONDITIONS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalStudioColorsPalette.current.BlueGrey500,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (!viewModel.rewardSectionsExpanded)
                        Icons.Default.KeyboardArrowDown
                    else
                        Icons.Default.KeyboardArrowUp,
                    tint = LocalStudioColorsPalette.current.BlueGrey500,
                    contentDescription = "Play",
                )
            }

            ExpandableView(viewModel.rewardSectionsExpanded) {
                Text(
                    text = "- Your reward may be delayed up to 48 hours\n" +
                            "- Only new users are eligible for the reward\n" +
                            "- A user would only be rewarded if the app was downloaded using the" +
                            " link in the offer",
                    fontSize = 14.sp,
                    lineHeight = 16.sp
                )
            }

            viewModel.rewards?.let { rewards ->
                RewardsSection(rewards, viewModel)
            }

            viewModel.multipliers?.let { multipliers ->
                if (multipliers.isNotEmpty()) {
                    MultipliersSection(multipliers)
                }
            }

            viewModel.bonusRewards?.let { bonusRewards ->
                if (bonusRewards.isNotEmpty()) {
                    BonusRewardsSection(
                        viewModel.bonusRewards ?: listOf(),
                        viewModel,
                        "BONUS REWARDS"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "DESCRIPTION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LocalStudioColorsPalette.current.BlueGrey500
            )
            Text(
                text = "Once you’ve completed a task, you’ll receive your reward in less than an" +
                        " hour – unless specified.\n" +
                        "You'll receive your rewards upon completion, but in some rare cases it can" +
                        " take up to 48 hours.\n" +
                        "Still missing currency? Submit a ticket to our team," +
                        " who will be happy to help you.",
                fontSize = 14.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            SimpleButton(
                text = if (viewModel.campaignExpired) "Play without Rewards" else "Play Now",
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    PlaytimeStudio.openInStore(
                        campaign = viewModel.activeCampaign!!,
                        useInlineInstall = true,
                        object : PlaytimeOpenStoreListener {
                            override fun onFinished() { }

                            override fun onError(error: PlaytimeException) {}
                            override fun onAlreadyClicking() {}
                        })
                }
            )

            // Catalog Support
            DangerButton(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                text = "Need help with something? Ask our chatbot!",
                onClick = {
                    viewModel.openChatbot(activity)
                }
            )

            viewModel.openedRewards?.let {
                RewardDrawer(viewModel)
            }
        }
    }
}
