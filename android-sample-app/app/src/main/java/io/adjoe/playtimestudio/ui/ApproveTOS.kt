package io.adjoe.playtimestudio.ui

import android.app.Activity
import io.adjoe.playtimestudio.R
import androidx.compose.foundation.Image
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.adjoe.playtimestudio.Util
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette
import io.adjoe.playtimestudio.ui.theme.PlaytimeStudioTheme
import io.adjoe.sdk.PlaytimeException
import io.adjoe.sdk.studio.PlaytimePermissionsListener
import io.adjoe.sdk.studio.PlaytimeStudio
import io.adjoe.sdk.studio.PlaytimeTosListener
import io.adjoe.sdk.studio.TosConsentAction

@Composable
fun ApproveTOSStepOne(onNext: () -> Unit, onSkip: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(8.dp),
                )
                .background(Color.White)
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.onboarding_step_1_illustration),
                        contentDescription = null,
                        modifier = Modifier.width(84.dp)
                    )
                }

                Text(
                    text = "Game, Play, Win",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "Find a game you love, install it, and rack up rewards by completing levels.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        SimpleButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Accept TOS",
            onClick = onNext
        )

        LinkButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Skip",
            onClick = onSkip
        )
    }
}

@Composable
fun ApproveTOSStepTwo(onApproveTos: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(8.dp),
                )
                .background(Color.White)
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.onboarding_step_2_illustration),
                        contentDescription = null,
                        modifier = Modifier
                            .width(42.dp)
                            .height(40.dp)
                    )
                }

                Text(
                    text = "Allow Tracking and Unlock Rewards",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "To earn rewards we need permission to track your progress. " +
                            "This helps us tailor your experience and deliver rewards.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        SimpleButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Agree and Continue",
            onClick = onApproveTos
        )

        LinkButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Back",
            onClick = onBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApproveTOS(
    context: Activity,
    currentStep: Int,
    setStep: (step: Int) -> Unit = {},
    onFinish: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = {
            onFinish()
        },
        sheetState = sheetState
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Play Games,\nGet Free Rewards!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = LocalStudioColorsPalette.current.PrimaryColor,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )

        Image(
            painter = painterResource(id = R.drawable.onboarding_illustration),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )

        if (currentStep == 1) {
            ApproveTOSStepOne(
                onNext = {
                    setStep(2)
                    PlaytimeStudio.updateTosConsent(
                        action = TosConsentAction.ACCEPT,
                        object : PlaytimeTosListener {
                            override fun onError(error: PlaytimeException) {
                                Util.logError(context, error.message.toString())
                                onFinish()
                            }

                            override fun onUpdated() {
                                onFinish()
                            }
                        }
                    )
                },
                onSkip = { onFinish() }
            )
        }

        if (currentStep == 2) {
            ApproveTOSStepTwo(
                onApproveTos = {
                    PlaytimeStudio.showPermissionsPrompt(
                        context,
                        object : PlaytimePermissionsListener {
                            override fun onReceived() {
                                onFinish()
                            }

                            override fun onError(error: PlaytimeException) {
                                Util.logError(context, error.message.toString())
                                onFinish()
                            }
                        }
                    )
                },
                onBack = { setStep(1) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                color = LocalStudioColorsPalette.current.DarkGray600,
                fontSize = 12.sp,
                text = "By continuing you agree to our "
            )
            Text(
                color = LocalStudioColorsPalette.current.PrimaryColor,
                text = "Terms and Conditions",
                fontSize = 12.sp,
                textDecoration = TextDecoration.Underline
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ApproveTOSPreview() {
    PlaytimeStudioTheme {
        ApproveTOS(Activity(), 0)
    }
}
