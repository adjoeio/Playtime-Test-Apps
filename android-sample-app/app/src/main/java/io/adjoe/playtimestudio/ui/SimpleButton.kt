package io.adjoe.playtimestudio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette

@Composable
fun SimpleButton(modifier: Modifier = Modifier, text: String = "", onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        LocalStudioColorsPalette.current.Primary80,
                        LocalStudioColorsPalette.current.PrimaryColor
                    )
                )
            )
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center,
        ) {
        Text(
            text = text,
            color = LocalStudioColorsPalette.current.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LinkButton(modifier: Modifier = Modifier, text: String = "", onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = LocalStudioColorsPalette.current.PrimaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DangerButton(modifier: Modifier = Modifier, text: String = "", onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12))
            .background(LocalStudioColorsPalette.current.Red400)
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center,
        ) {
        Text(
            text = text,
            color = LocalStudioColorsPalette.current.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
