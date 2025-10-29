package io.adjoe.playtimestudio.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette
import io.adjoe.playtimestudio.viewModels.CampaignDetailsViewModel

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardDrawer(viewModel: CampaignDetailsViewModel) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = {
            viewModel.onRewardsClick(null)
        },
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = viewModel.getRewardLabel(viewModel.openedReward),
                fontSize = 16.sp
            )

            Text(
                text = viewModel.getRewardText(
                    viewModel.openedReward,
                    viewModel.openedRewards?.get(0)
                ) ?: "",
                color = viewModel.getRewardColor(
                    viewModel.openedReward,
                    viewModel.openedRewards?.get(0)
                ),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LocalStudioColorsPalette.current.BlueGrey50)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Total Reward",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = viewModel.openedReward?.amount.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
