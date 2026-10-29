package io.adjoe.playtimestudio.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.adjoe.playtimestudio.CampaignHelper
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette
import io.adjoe.playtimestudio.viewModels.CampaignDetailsViewModel
import io.adjoe.sdk.studio.PlaytimeRewardAction

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RewardsSection(
    rewards: List<PlaytimeRewardAction>,
    viewModel: CampaignDetailsViewModel,
    sectionTitle: String? = "REWARDS"
) {
    RewardsContainer(
        sectionTitle = sectionTitle,
        total = rewards.count()
    ) {
        rewards.forEachIndexed { i, it ->
            val prevReward = if (i == 0) null else rewards.get(i - 1)
            val rewardText = viewModel.getRewardText(it, prevReward)
            val isRepeatable = CampaignHelper.rewardIsRepeatable(it)

            Column(
                modifier = Modifier
                    .height(108.dp)
                    .width(118.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp))
                    .background(viewModel.getRewardColor(it, prevReward))
                    .clip(RoundedCornerShape(8.dp))
                    .padding(1.dp)
                    .clickable(true, onClick = {
                        viewModel.onRewardsClick(listOf(prevReward, it))
                    })
            ) {
                // Repeatable Header
                if (isRepeatable) {
                    Box {
                        Text(
                            text = "Repeatable",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = LocalStudioColorsPalette.current.White,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .weight(1f)
                        .background(LocalStudioColorsPalette.current.White)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = viewModel.getRewardLabel(it),
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = it.amount.toString(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                if (!rewardText.isNullOrEmpty()) {
                    Box {
                        Text(
                            text = rewardText,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = LocalStudioColorsPalette.current.White,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Repeatable Footer
                if (isRepeatable) {
                    Box {
                        Text(
                            text = "${it.rewardsCount} times",
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
