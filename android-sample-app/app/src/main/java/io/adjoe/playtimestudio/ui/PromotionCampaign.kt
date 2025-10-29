package io.adjoe.playtimestudio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import io.adjoe.playtimestudio.CampaignHelper
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette
import io.adjoe.sdk.studio.PlaytimeCampaign

@Composable
fun PromotionCampaign(
    event: PlaytimeCampaign,
    openCampaignDetails: (activeCampaign: PlaytimeCampaign) -> Unit
) {
    val maxReward = CampaignHelper.getMaxReward(event)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = LocalStudioColorsPalette.current.PrimaryColor
            )
            .border(
                width = 1.dp,
                color = LocalStudioColorsPalette.current.PrimaryColor,
                shape = RoundedCornerShape(8.dp)
            )
            .background(LocalStudioColorsPalette.current.White)
            .clip(RoundedCornerShape(8.dp))
            .paint(
                rememberAsyncImagePainter(event.image?.landscape),
                contentScale = ContentScale.Crop,
            )
            .clickable(true, onClick = {
                openCampaignDetails(event)
            }),
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            LocalStudioColorsPalette.current.PrimaryColor,
                        ),
                        endY = 200f,
                    )
                )
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Row(
                    Modifier
                        .clip(RoundedCornerShape(100))
                        .background(LocalStudioColorsPalette.current.Yellow40)
                        .padding(top = 4.dp, start = 12.dp, end = 12.dp, bottom = 4.dp)
                ) {
                    Text(text = "Earn up to", fontSize = 14.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = maxReward.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = event.appName,
                color = LocalStudioColorsPalette.current.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }

    }

    Spacer(modifier = Modifier.height(22.dp))
}
