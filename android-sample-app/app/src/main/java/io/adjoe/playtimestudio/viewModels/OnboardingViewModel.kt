package io.adjoe.playtimestudio.viewModels

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.adjoe.playtimestudio.Util
import io.adjoe.sdk.Playtime
import io.adjoe.sdk.internal.SharedPreferencesRepository
import io.adjoe.sdk.studio.PlaytimePermissions
import io.adjoe.sdk.studio.PlaytimePermissionsListener
import io.adjoe.sdk.studio.PlaytimePermissionsResponse
import io.adjoe.sdk.studio.PlaytimeResponseError
import io.adjoe.sdk.studio.PlaytimeStudio
import java.util.Timer
import java.util.TimerTask

class OnboardingViewModel() : ViewModel() {
    var playtimeInit by mutableStateOf(false)
        private set
    var tosEnabled by mutableStateOf(false)
        private set
    var onboardingVisible by mutableStateOf(false)
        private set
    var onboardingStep by mutableIntStateOf(1)

    fun checkPlaytimeInit(context: Activity) {
        // Workaround used to check if playtime init is done
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val inited = Playtime.isInitialized()

                if (inited) {
                    this.cancel()
                    PlaytimeStudio.getPermissions(
                        context,
                        object : PlaytimePermissionsListener {
                            override fun onReceived(p0: PlaytimePermissionsResponse) {
                                playtimeInit = true
                                tosEnabled = SharedPreferencesRepository
                                    .getInstance(context)
                                    .getTosApproved()
                                onboardingVisible = !(tosEnabled
                                        && p0.permissions.isUsagePermissionAccepted)

                                if (tosEnabled) {
                                    onboardingStep = 2
                                }
                            }

                            override fun onError(p0: PlaytimeResponseError) {
                                Util.logVisible(context, p0.message)
                            }
                        })
                }
            }
        }, 0, 200)
    }

    fun onApproveTOSFinished(permissions: PlaytimePermissions?) {
        onboardingVisible = false

        if (permissions != null) {
            tosEnabled = permissions.isTOSAccepted
        }
    }
}
