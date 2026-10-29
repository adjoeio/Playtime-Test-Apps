package io.adjoe.playtimestudio.viewModels

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.adjoe.playtimestudio.Config
import io.adjoe.playtimestudio.Util
import io.adjoe.sdk.InitOptions
import io.adjoe.sdk.Playtime
import io.adjoe.sdk.PlaytimeException
import io.adjoe.sdk.PlaytimeInitListener
import io.adjoe.sdk.PlaytimeStatus

class OnboardingViewModel() : ViewModel() {
    var playtimeInit by mutableStateOf(false)
        private set
    var tosEnabled by mutableStateOf(false)
        private set
    var onboardingVisible by mutableStateOf(false)
        private set
    var onboardingStep by mutableIntStateOf(1)

    fun checkPlaytimeInit(context: Activity) {
        val currentStatus = Playtime.getStatus()

        if (currentStatus.isInitialized) {
            applyStatus(currentStatus)
            return
        }

        Playtime.initPlaytime(
            InitOptions(sdkHash = Config.ADJOE_SDK_HASH, userId = Config.ADJOE_USER_ID),
            object : PlaytimeInitListener {
                override fun onError(error: PlaytimeException) {
                    Util.logVisible(context, error.message)
                }

                override fun onSuccess() {
                    applyStatus(Playtime.getStatus())
                }
            }
        )
    }

    private fun applyStatus(status: PlaytimeStatus) {
        tosEnabled = status.isTosAccepted
        playtimeInit = true
        onboardingVisible = !(status.isTosAccepted && status.isUsagePermissionAccepted)

        if (tosEnabled) {
            onboardingStep = 2
        }
    }

    fun onOnboardingFinished() {
        val status = Playtime.getStatus()
        tosEnabled = status.isTosAccepted

        if (status.isUsagePermissionAccepted) {
            onboardingVisible = false
        }
    }
}
