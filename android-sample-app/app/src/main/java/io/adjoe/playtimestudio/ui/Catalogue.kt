package io.adjoe.playtimestudio.ui

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette
import io.adjoe.playtimestudio.ui.theme.PrimaryColor
import io.adjoe.playtimestudio.viewModels.CatalogueViewModel
import io.adjoe.sdk.studio.PlaytimeCampaign

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Catalogue(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: CatalogueViewModel,
    openCampaignDetails: (activeCampaign: PlaytimeCampaign) -> Unit
) {
    val campaigns by viewModel.campaignsListFlow.collectAsState()
    val featuredCampaigns by viewModel.featuredCampaignsListFlow.collectAsState()
    val installedCampaigns by viewModel.installedCampaignsListFlow.collectAsState()

    LaunchedEffect(true) {
        viewModel.getCampaigns(context as Activity)
    }

    Column(modifier = modifier) {
        // Catalog Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalStudioColorsPalette.current.PrimaryColor)
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "adjoe",
                color = LocalStudioColorsPalette.current.White,
                fontWeight = FontWeight.Bold
            )
        }

        // Catalog Tabs
        Row(
            Modifier
                .fillMaxWidth()
                .shadow(elevation = 10.dp)
                .background(LocalStudioColorsPalette.current.White)
        ) {
            viewModel.tabs.forEach { it ->
                val selected = viewModel.selectedTab == it

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = {
                            viewModel.selectedTab = it
                        })
                        .drawBehind {
                            if (selected) {
                                val strokeWidth = 4 * density
                                val y = size.height - strokeWidth / 2

                                drawLine(
                                    PrimaryColor,
                                    Offset(0f, y),
                                    Offset(size.width, y),
                                    strokeWidth
                                )
                            }
                        }
                        .padding(12.dp)
                ) {
                    Text(
                        text = it,
                        color = if (selected) LocalStudioColorsPalette.current.PrimaryColor
                        else Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Catalog Campaigns
        LazyColumn(
            Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(16.dp)
        ) {
            if (viewModel.selectedTab == viewModel.tabs[0]) {
                if (viewModel.loadingMyCampaigns) {
                    item { Loader() }
                } else if (campaigns.isEmpty()) {
                    item {
                        EmptyCampaigns(
                            text = "There are no games available to you at the moment." +
                                    " Discover new games and continue earning when you come back tomorrow!",
                        )
                    }
                } else {
                    if (featuredCampaigns.isNotEmpty()) {
                        item {
                            Text(
                                text = "Happening Now",
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        items(featuredCampaigns) {
                            PromotionCampaign(it, openCampaignDetails)
                        }

                        item {
                            Text(
                                text = "Your Selection",
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    items(campaigns) {
                        CatalogueCampaign(it, context, openCampaignDetails, viewModel)
                    }
                }
            }

            if (viewModel.selectedTab == viewModel.tabs[1]) {
                if (viewModel.loadingInstalledCampaigns) {
                    item { Loader() }
                } else if (installedCampaigns.isEmpty()) {
                    item {
                        EmptyCampaigns(
                            text = "You have not installed any apps yet. Explore the catalog of games" +
                                    ", choose the ones you like the most and start earning!",
                        )
                    }
                } else {
                    items(installedCampaigns) {
                        InstalledCampaign(context, it, openCampaignDetails)
                    }
                }
            }
        }

        // Catalog Support
        DangerButton(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            text = "Need help with something? Ask our chatbot!",
            onClick = {
                viewModel.openChatbot(context as Activity)
            }
        )

        // Catalog Deeplink
        LinkButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Deeplink to Installed Campaigns",
            onClick = {
                viewModel.showInstalledApps(context as Activity)
            }
        )
    }
}
