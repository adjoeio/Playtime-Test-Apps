package io.adjoe.playtimestudio.ui

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import io.adjoe.playtimestudio.CampaignHelper
import io.adjoe.playtimestudio.Util
import io.adjoe.playtimestudio.data.InstalledCampaign
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette
import io.adjoe.sdk.studio.PlaytimeCampaign
import io.adjoe.sdk.studio.PlaytimeDeeplinkListener
import io.adjoe.sdk.studio.PlaytimeOpenStoreListener
import io.adjoe.sdk.studio.PlaytimeResponseError
import io.adjoe.sdk.studio.PlaytimeStudio

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InstalledCampaign(
    context: Context,
    installedCampaign: InstalledCampaign,
    openCampaignDetails: (activeCampaign: PlaytimeCampaign) -> Unit,
) {
    val rewardedCoins = CampaignHelper.getRewardedCoins(installedCampaign.campaign)
    val isCompleted = CampaignHelper.getIsCompleted(installedCampaign.campaign)
    val isExpired = CampaignHelper.getIsExpired(installedCampaign.campaign)
    val daysLeft = CampaignHelper
        .getDurationUntilExpiration(installedCampaign.campaign)
        ?.toDays()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
            .background(LocalStudioColorsPalette.current.White)
            .clip(RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.width(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = rememberAsyncImagePainter(installedCampaign.campaign.iconImage),
                contentDescription = installedCampaign.campaign.appName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20)),
                contentScale = ContentScale.FillBounds
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = installedCampaign.campaign.appName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (installedCampaign.isExpired) {
                    Text(
                        text = "Installation expired, postback won't come!",
                        fontSize = 14.sp,
                        color = LocalStudioColorsPalette.current.Red400,
                        fontWeight = FontWeight.SemiBold
                    )
                } else if (!installedCampaign.isInstalled) {
                    Text(
                        text = "Waiting for postback!",
                        fontSize = 14.sp,
                        color = LocalStudioColorsPalette.current.LightGrey70,
                        fontWeight = FontWeight.SemiBold
                    )
                } else if (isCompleted) {
                    Text(
                        text = "Completed",
                        fontSize = 14.sp,
                        color = LocalStudioColorsPalette.current.SecondaryGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                } else if (isExpired) {
                    Text(
                        text = "Expired",
                        fontSize = 14.sp,
                        color = LocalStudioColorsPalette.current.Red400,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = "$daysLeft days left to claim rewards",
                        fontSize = 12.sp,
                        color = LocalStudioColorsPalette.current.LightGrey70
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "More",
                fontSize = 12.sp,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                color = LocalStudioColorsPalette.current.PrimaryColor,
                modifier = Modifier.clickable(onClick = {
                    openCampaignDetails(installedCampaign.campaign)
                })
            )
        }

        if (!isCompleted && !isExpired && installedCampaign.isInstalled) {
            ProgressBar(
                rewardedCoins ?: 0,
                installedCampaign.campaign.eventConfig?.totalCoinsPossible ?: 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            SimpleButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Play Now",
                onClick = {
                    PlaytimeStudio.openInStore(
                        context,
                        installedCampaign.campaign,
                        object : PlaytimeOpenStoreListener {
                            override fun onFinished() {
                                Util.logVisible(
                                    context as Activity,
                                    "${installedCampaign.campaign.appName} Opened Successfully"
                                )
                            }

                            override fun onError(p0: PlaytimeResponseError?) {
                                Util.logError(
                                    context as Activity,
                                    "Error opening ${installedCampaign.campaign.appName}: ${p0?.error?.message}"
                                )
                            }

                            override fun onAlreadyClicking() {
                                Util.logError(
                                    context as Activity,
                                    "Already Clicking ${installedCampaign.campaign.appName}"
                                )
                            }
                        })
                }
            )

            LinkButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Deeplink to WebCatalog",
                onClick = {
                    PlaytimeStudio.showInstalledApps(
                        context as Activity,
                        object : PlaytimeDeeplinkListener {
                            override fun onError(error: PlaytimeResponseError?) {
                                Util.logVisible(
                                    context,
                                    "Error opening campaign: ${error?.error?.message}"
                                )
                            }

                            override fun onOpened() {
                                Util.logVisible(context, "Successfully opened campaign")
                            }
                        }
                    )
                }
            )
        }
    }

    Spacer(modifier = Modifier.height(22.dp))
}
