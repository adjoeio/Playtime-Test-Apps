package io.adjoe.playtimestudio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class StudioColorsPalette(
    val PrimaryColor: Color = Color.Unspecified,
    val Primary80: Color = Color.Unspecified,
    val PurpleGrey80: Color = Color.Unspecified,
    val Pink80: Color = Color.Unspecified,
    val PurpleGrey40: Color = Color.Unspecified,
    val Pink40: Color = Color.Unspecified,
    val Grey20: Color = Color.Unspecified,
    val Grey70: Color = Color.Unspecified,
    val LightGrey20: Color = Color.Unspecified,
    val LightGrey60: Color = Color.Unspecified,
    val LightGrey70: Color = Color.Unspecified,
    val BlueGrey50: Color = Color.Unspecified,
    val BlueGrey400: Color = Color.Unspecified,
    val BlueGrey500: Color = Color.Unspecified,
    val SecondaryGreen: Color = Color.Unspecified,
    val Green50: Color = Color.Unspecified,
    val DarkGray600: Color = Color.Unspecified,
    val BorderColor: Color = Color.Unspecified,
    val Red50: Color = Color.Unspecified,
    val Red400: Color = Color.Unspecified,
    val Yellow40: Color = Color.Unspecified,
    val White: Color = Color.Unspecified,
)

val LightStudioColorsPalette = StudioColorsPalette(
    PrimaryColor = PrimaryColor,
    Primary80 = Primary80,
    PurpleGrey80 = PurpleGrey80,
    Pink80 = Pink80,
    PurpleGrey40 = PurpleGrey40,
    Pink40 = Pink40,
    Grey20 = Grey20,
    Grey70 = Grey70,
    LightGrey20 = LightGrey20,
    LightGrey60 = LightGrey60,
    LightGrey70 = LightGrey70,
    BlueGrey50 = BlueGrey50,
    BlueGrey400 = BlueGrey400,
    BlueGrey500 = BlueGrey500,
    SecondaryGreen = SecondaryGreen,
    Green50 = Green50,
    DarkGray600 = DarkGray600,
    BorderColor = BorderColor,
    Red50 = Red50,
    Red400 = Red400,
    Yellow40 = Yellow40,
    White = Color.White,
)

val DarkStudioColorsPalette = StudioColorsPalette(
    White = Color.White,
    PrimaryColor = PrimaryColor,
    Primary80 = Primary80,
    PurpleGrey80 = PurpleGrey80,
    Pink80 = Pink80,
    PurpleGrey40 = PurpleGrey40,
    Pink40 = Pink40,
    Grey20 = Grey20,
    Grey70 = Grey70,
    LightGrey20 = LightGrey20,
    LightGrey60 = LightGrey60,
    LightGrey70 = LightGrey70,
    BlueGrey50 = BlueGrey50,
    BlueGrey400 = BlueGrey400,
    BlueGrey500 = BlueGrey500,
    SecondaryGreen = SecondaryGreen,
    Green50 = Green50,
    DarkGray600 = DarkGray600,
    BorderColor = BorderColor,
    Red50 = Red50,
    Red400 = Red400,
    Yellow40 = Yellow40,
)

val LocalStudioColorsPalette = staticCompositionLocalOf { StudioColorsPalette() }

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Grey20
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Grey20
)

@Composable
fun PlaytimeStudioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val studioColorsPalette = if (darkTheme) DarkStudioColorsPalette else LightStudioColorsPalette

    CompositionLocalProvider(LocalStudioColorsPalette provides studioColorsPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
