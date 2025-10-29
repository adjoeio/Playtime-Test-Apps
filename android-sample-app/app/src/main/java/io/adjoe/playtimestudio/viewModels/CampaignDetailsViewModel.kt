package io.adjoe.playtimestudio.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import io.adjoe.playtimestudio.CampaignHelper
import io.adjoe.playtimestudio.DateHelper
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette
import io.adjoe.sdk.studio.PlaytimeCampaign

@RequiresApi(Build.VERSION_CODES.S)
class CampaignDetailsViewModel() : ViewModel() {
    var activeCampaign by mutableStateOf<PlaytimeCampaign?>(null)
    val isTimeBased by derivedStateOf { CampaignHelper.getIsTimeBased(activeCampaign) }
    val rewards by derivedStateOf { CampaignHelper.getAllActions(activeCampaign) }
    val multipliers by derivedStateOf { CampaignHelper.getAllMultipliers(activeCampaign) }
    val bonusRewards by derivedStateOf { CampaignHelper.getBonusActions(activeCampaign) }
    val isInstalled by derivedStateOf { CampaignHelper.getIsInstalled(activeCampaign) }
    val durationBetween by derivedStateOf { CampaignHelper.getDurationUntilExpiration(activeCampaign) }
    val duration by derivedStateOf { DateHelper.humanizeDuration(durationBetween) }
    val video by derivedStateOf { activeCampaign?.video?.landscape }
    var showVideo by mutableStateOf(false)
        private set
    var openedRewards by mutableStateOf<List<PlaytimeCampaign.PlaytimeRewardAction?>?>(null)
        private set
    var rewardSectionsExpanded by mutableStateOf(false)
        private set

    val hasVideo by derivedStateOf { !video.isNullOrEmpty() }
    val openedReward by derivedStateOf { openedRewards?.get(1) }
    val campaignExpired by derivedStateOf { CampaignHelper.getIsExpired(activeCampaign) }
    val rewardedCoins by derivedStateOf { CampaignHelper.getRewardedCoins(activeCampaign) }

    fun toggleRewardSection() {
        rewardSectionsExpanded = !rewardSectionsExpanded
    }

    fun onRewardsClick(rewards: List<PlaytimeCampaign.PlaytimeRewardAction?>?) {
        if (isInstalled) {
            openedRewards = rewards
        }
    }

    fun onBannerClick() {
        if (!video.isNullOrEmpty()) {
            showVideo = !showVideo
        }
    }

    fun getInstalledDaysLeftText(): String {
        if (activeCampaign?.isCompleted == true) {
            return "Congratulations! You claimed all rewards."
        }

        if (duration.isNullOrEmpty()) {
            return "Offer is expired. You can't earn rewards anymore."
        }

        return "$duration left to claim rewards"
    }

    @Composable
    fun getInstalledDaysBackground(): Color {
        if (activeCampaign?.isCompleted == true) {
            return LocalStudioColorsPalette.current.Green50
        }

        if ((durationBetween?.toDays() ?: 0) <= 1) {
            return LocalStudioColorsPalette.current.Red50
        }

        return LocalStudioColorsPalette.current.BlueGrey50
    }

    @Composable
    fun getRewardColor(
        reward: PlaytimeCampaign.PlaytimeRewardAction?,
        prevReward: PlaytimeCampaign.PlaytimeRewardAction?
    ): Color {
        if (isInstalled) {
            if (!reward?.rewardedAt.isNullOrEmpty()) {
                return LocalStudioColorsPalette.current.SecondaryGreen
            }

            if (campaignExpired) {
                return LocalStudioColorsPalette.current.BlueGrey400
            }

            if (prevReward != null) {
                if (!prevReward.rewardedAt.isNullOrEmpty()) {
                    return LocalStudioColorsPalette.current.PrimaryColor
                }
            }

            if (prevReward == null) {
                return LocalStudioColorsPalette.current.PrimaryColor
            }
        }

        return LocalStudioColorsPalette.current.BlueGrey400
    }

    fun getRewardText(
        reward: PlaytimeCampaign.PlaytimeRewardAction?,
        prevReward: PlaytimeCampaign.PlaytimeRewardAction?
    ): String? {
        if (isInstalled) {
            if (!reward?.rewardedAt.isNullOrEmpty()) {
                return "Completed"
            }

            if (campaignExpired) {
                return "Not available"
            }

            if (prevReward != null) {
                if (!prevReward.rewardedAt.isNullOrEmpty()) {
                    return "In Progress"
                }
            }

            if (prevReward == null) {
                return "In progress"
            }
        }

        return null
    }

    fun getRewardLabel(reward: PlaytimeCampaign.PlaytimeRewardAction?): String {
        if (isTimeBased)
            return "Play ${DateHelper.secondsToMinutes(reward?.playDuration)}"

        return reward?.taskDescription.toString()
    }
}
