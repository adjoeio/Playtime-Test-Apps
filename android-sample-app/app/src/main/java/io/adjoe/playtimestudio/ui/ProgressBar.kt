package io.adjoe.playtimestudio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette
import io.adjoe.playtimestudio.ui.theme.PrimaryColor

@Composable
fun ProgressBar(rewardedCoins: Int, totalCoinsPossible: Int, expired: Boolean? = false) {
    val progressBarText = if (rewardedCoins == totalCoinsPossible) "Completed"
    else if (expired == true) "Expired"
    else "$totalCoinsPossible"

    Column(Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "$rewardedCoins",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = progressBarText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = LocalStudioColorsPalette.current.LightGrey60
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .height(8.dp)
                .background(LocalStudioColorsPalette.current.LightGrey20)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth(rewardedCoins.toFloat() / totalCoinsPossible.toFloat())
                    .height(8.dp)
                    .background(LocalStudioColorsPalette.current.SecondaryGreen)
            )
        }
    }
}
