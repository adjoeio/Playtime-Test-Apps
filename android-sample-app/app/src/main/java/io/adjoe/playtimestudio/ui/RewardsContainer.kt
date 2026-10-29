package io.adjoe.playtimestudio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette

@Composable
fun RewardsContainer(
    total: Int,
    sectionTitle: String? = "REWARDS",
    content: @Composable() () -> Unit
) {
    Spacer(modifier = Modifier.height(22.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = sectionTitle.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = LocalStudioColorsPalette.current.BlueGrey500
        )
        Column(
            modifier = Modifier
                .height(26.dp)
                .weight(1f)
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        LocalStudioColorsPalette.current.BlueGrey400
                    )
            )
        }
        Text(
            text = "$total Total",
            fontSize = 12.sp,
            color = LocalStudioColorsPalette.current.BlueGrey500
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        content()
    }

    Spacer(modifier = Modifier.height(12.dp))
}
