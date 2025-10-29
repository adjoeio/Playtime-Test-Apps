package io.adjoe.playtimestudio.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.adjoe.playtimestudio.CampaignHelper
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette
import io.adjoe.sdk.studio.PlaytimeCampaign

@Composable
fun MultipliersSection(
    multipliers: List<PlaytimeCampaign.PlaytimeRewardActionMultiplier>,
) {
    RewardsContainer(
        sectionTitle = "MULTIPLIERS",
        total = multipliers.count()
    ) {
        multipliers.forEach { it ->
            Column(
                modifier = Modifier
                    .height(128.dp)
                    .width(165.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp))
                    .background(CampaignHelper.getMultiplierBackground(it))
                    .clip(RoundedCornerShape(8.dp))
                    .padding(1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .weight(1f)
                        .background(LocalStudioColorsPalette.current.White)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CampaignHelper.getMultiplierBackground(it))
                                .padding(8.dp)

                        ) {
                            Text(
                                text = "${it.multiplierFactorPercentage.toString()}X",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = LocalStudioColorsPalette.current.White,
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    if (CampaignHelper.multiplierIsPending(it)) {
                        Text(
                            text = "For ${it.multiplierLevels} level rewards",
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = LocalStudioColorsPalette.current.BlueGrey500
                        )
                        Text(
                            text = "${it.eventDescription}",
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "Active for ${it.usedLevels} time-based levels",
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.width(16.dp))
        }
    }
}
