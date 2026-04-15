package io.adjoe.playtimestudio.ui

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import io.adjoe.playtimestudio.CampaignHelper
import io.adjoe.playtimestudio.Util
import io.adjoe.playtimestudio.ui.theme.BorderColor
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette
import io.adjoe.playtimestudio.viewModels.CatalogueViewModel
import io.adjoe.sdk.studio.PlaytimeCampaign
import io.adjoe.sdk.studio.PlaytimeEngagementType
import io.adjoe.sdk.studio.PlaytimeExecuteEngagementListener
import io.adjoe.sdk.studio.PlaytimeOpenStoreListener
import io.adjoe.sdk.studio.PlaytimeResponseError
import io.adjoe.sdk.studio.PlaytimeStudio

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CatalogueCampaign(
    event: PlaytimeCampaign,
    context: Context,
    openCampaignDetails: (activeCampaign: PlaytimeCampaign) -> Unit,
    viewModel: CatalogueViewModel
) {
    val isTimeBased = CampaignHelper.getIsTimeBased(event)
    val maxReward = CampaignHelper.getMaxReward(event)

    LaunchedEffect(true) {
        PlaytimeStudio.executeEngagement(
            context,
            event,
            PlaytimeEngagementType.DEFAULT,
            object : PlaytimeExecuteEngagementListener {
                override fun onAlreadyEngaging() {
                    Util.logVisible(context as Activity, "On already engaging ${event.appName}")
                }

                override fun onError(error: PlaytimeResponseError?) {
                    Util.logVisible(context as Activity, "Error executing engagement for ${event.appName}: ${error?.error?.message}")
                }

                override fun onFinished() { }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
            .background(Color.White)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Image(
            painter = rememberAsyncImagePainter(event.image?.landscape),
            contentDescription = event.appName,
            modifier = Modifier
                .height(164.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = event.appName,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "More",
                    fontSize = 13.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    color = LocalStudioColorsPalette.current.PrimaryColor,
                    modifier = Modifier.clickable(onClick = {
                        openCampaignDetails(event)
                    })
                )
            }

            event.rewardingExpiresAfter?.let { rewardingExpiresAfter ->
                Text(
                    text = "$rewardingExpiresAfter days",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalStudioColorsPalette.current.LightGrey70
                )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, BorderColor, shape = RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "$maxReward",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocalStudioColorsPalette.current.PrimaryColor,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = if (isTimeBased) "max per minute" else "max reward",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, BorderColor, shape = RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "${event.eventConfig?.totalCoinsPossible}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocalStudioColorsPalette.current.PrimaryColor,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "total",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LocalStudioColorsPalette.current.Grey70,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SimpleButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Play Now",
                onClick = {
                    PlaytimeStudio.openInStore(
                        context,
                        event,
                        object : PlaytimeOpenStoreListener {
                            override fun onFinished() {
                                Util.logVisible(
                                    context as Activity,
                                    "${event.appName} Opened Successfully"
                                )
                                viewModel.startCheckInstallWorker(context, event)
                            }

                            override fun onError(p0: PlaytimeResponseError?) {
                                Util.logError(
                                    context as Activity,
                                    "Error opening ${event.appName}: ${p0?.error?.message}"
                                )
                            }

                            override fun onAlreadyClicking() {
                                Util.logError(
                                    context as Activity,
                                    "Already Clicking ${event.appName}"
                                )
                            }
                        })
                }
            )

            LinkButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Deeplink to WebCatalog",
                onClick = {
                    viewModel.deeplinkCampaign(context as Activity, campaign = event)
                }
            )
        }
    }

    Spacer(modifier = Modifier.height(22.dp))
}
