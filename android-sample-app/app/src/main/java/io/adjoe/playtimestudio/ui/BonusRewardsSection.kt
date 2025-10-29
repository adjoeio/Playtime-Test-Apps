package io.adjoe.playtimestudio.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.adjoe.playtimestudio.CampaignHelper
import io.adjoe.playtimestudio.DateHelper
import io.adjoe.playtimestudio.data.BonusAction
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette
import io.adjoe.playtimestudio.viewModels.CampaignDetailsViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BonusRewardsSection(
    rewards: List<BonusAction>,
    viewModel: CampaignDetailsViewModel,
    sectionTitle: String? = "BONUS REWARDS"
) {
    RewardsContainer(
        sectionTitle = sectionTitle,
        total = rewards.count()
    ) {
        rewards.forEachIndexed { i, it ->
            Column(
                modifier = Modifier
                    .width(118.dp)
                    .height(138.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp))
                    .background(CampaignHelper.getBonusActionBackground(it))
                    .clip(RoundedCornerShape(8.dp))
                    .padding(1.dp)
            ) {
                // Time Boosted / Bonus
                it.rewardConfig?.let { it ->
                    val isTimeBoosted = CampaignHelper.rewardIsTimeBoosted(it)

                    if (isTimeBoosted) {
                        it.timedCoinsDuration?.let {
                            Box {
                                Text(
                                    text = DateHelper.humanizeMinutes(it),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    color = LocalStudioColorsPalette.current.White,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .weight(1f)
                            .background(LocalStudioColorsPalette.current.White)
                            .padding(horizontal = 8.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = viewModel.getRewardLabel(it),
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        if (isTimeBoosted) {
                            Text(
                                text = it.amount.toString(),
                                style = TextStyle(textDecoration = TextDecoration.LineThrough),
                                fontSize = 12.sp,
                                color = LocalStudioColorsPalette.current.Grey70,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                        Text(
                            text = if (isTimeBoosted) it.timedCoins.toString() else it.amount.toString(),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Cashback (IAP)
                it.cashback?.let {
                    Box {
                        Text(
                            text = "Repeatable",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = LocalStudioColorsPalette.current.White,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .weight(1f)
                            .background(LocalStudioColorsPalette.current.White)
                            .padding(horizontal = 8.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Make in-app purchase",
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = it.maxLimitPerCampaignCoins.toString(),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    Box {
                        Text(
                            text = "${it.exchangeRate} for $1",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = LocalStudioColorsPalette.current.White,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.width(16.dp))
        }
    }
}
