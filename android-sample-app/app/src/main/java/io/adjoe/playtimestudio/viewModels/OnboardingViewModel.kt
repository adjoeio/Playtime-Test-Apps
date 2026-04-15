package io.adjoe.playtimestudio.viewModels

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.adjoe.playtimestudio.Config
import io.adjoe.playtimestudio.Util
import io.adjoe.sdk.Playtime
import io.adjoe.sdk.PlaytimeOptions
import io.adjoe.sdk.PlaytimeOptionsListener
import io.adjoe.sdk.internal.SharedPreferencesRepository
import io.adjoe.sdk.studio.PlaytimePermissions
import io.adjoe.sdk.studio.PlaytimePermissionsListener
import io.adjoe.sdk.studio.PlaytimePermissionsResponse
import io.adjoe.sdk.studio.PlaytimeResponseError
import io.adjoe.sdk.studio.PlaytimeStudio

class OnboardingViewModel() : ViewModel() {
    var playtimeInit by mutableStateOf(false)
        private set
    var tosEnabled by mutableStateOf(false)
        private set
    var onboardingVisible by mutableStateOf(false)
        private set
    var onboardingStep by mutableIntStateOf(1)

    fun checkPlaytimeInit(context: Activity) {
        Playtime.setPlaytimeOptions(
            PlaytimeOptions().setSDKHash(Config.ADJOE_SDK_HASH),
            object : PlaytimeOptionsListener {
                override fun onError(error: String) {
                    Util.logVisible(context, error)
                }

                override fun onSuccess() {
                    PlaytimeStudio.getPermissions(
                        context,
                        object : PlaytimePermissionsListener {
                            override fun onReceived(response: PlaytimePermissionsResponse) {
                                playtimeInit = true
                                tosEnabled = SharedPreferencesRepository
                                    .getInstance(context)
                                    .getTosApproved()
                                onboardingVisible = !(tosEnabled
                                        && response.permissions.isUsagePermissionAccepted)

                                if (tosEnabled) {
                                    onboardingStep = 2
                                }
                            }

                            override fun onError(error: PlaytimeResponseError) {
                                Util.logVisible(context, error.error.message)
                            }
                        })
                }
            }
        )
    }

    fun onApproveTOSFinished(permissions: PlaytimePermissions?) {
        onboardingVisible = false

        if (permissions != null) {
            tosEnabled = permissions.isTOSAccepted
        }
    }
}
