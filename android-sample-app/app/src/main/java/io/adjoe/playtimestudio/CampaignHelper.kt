package io.adjoe.playtimestudio

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.adjoe.playtimestudio.data.BonusAction
import io.adjoe.playtimestudio.ui.theme.LocalStudioColorsPalette
import io.adjoe.sdk.studio.PlaytimeCampaign
import java.time.Duration
import java.time.Instant

object CampaignHelper {
    fun getIsTimeBased(campaign: PlaytimeCampaign?): Boolean {
        return campaign?.campaignType == "offerwall"
    }

    fun getRewardedActions(campaign: PlaytimeCampaign?): List<PlaytimeCampaign.PlaytimeRewardAction>? {
        val isTimeBased = getIsTimeBased(campaign)

        return if (isTimeBased)
            campaign?.eventConfig?.timeBasedActions?.filter { !it.rewardedAt.isNullOrEmpty() }
        else campaign?.eventConfig?.sequentialActions?.filter { !it.rewardedAt.isNullOrEmpty() }
    }

    fun getAllActions(campaign: PlaytimeCampaign?): List<PlaytimeCampaign.PlaytimeRewardAction>? {
        val isTimeBased = getIsTimeBased(campaign)

        return if (isTimeBased)
            campaign?.eventConfig?.timeBasedActions
        // filter time-boosted events, since they are shown in bonus rewards
        else campaign?.eventConfig?.sequentialActions?.filter { !rewardIsTimeBoosted(it) }
    }

    fun getAllMultipliers(campaign: PlaytimeCampaign?): List<PlaytimeCampaign.PlaytimeRewardActionMultiplier>? {
        return campaign?.eventConfig?.multipliersActions
    }

    fun getRewardedCoins(campaign: PlaytimeCampaign?): Int {
        val rewardedActions = getRewardedActions(campaign)
        return rewardedActions?.sumOf { it.amount } ?: 0
    }

    fun getIsCompleted(campaign: PlaytimeCampaign?): Boolean {
        return campaign?.isCompleted == true
    }

    fun getMaxReward(campaign: PlaytimeCampaign?): Int? {
        val isTimeBased = getIsTimeBased(campaign)
        return if (isTimeBased)
            campaign?.eventConfig?.timeBasedActions?.find { d -> d.playDuration == 60 }?.amount
        else campaign?.eventConfig?.sequentialActions?.maxBy { d -> d.amount }?.amount
    }

    fun filterTimeBoostedEvents(it: PlaytimeCampaign.PlaytimeRewardAction): Boolean {
        it.timedCoinsDuration?.let {
            return it > 0
        }

        return false
    }

    fun getBonusActions(campaign: PlaytimeCampaign?): List<BonusAction>? {
        val sequentialTimeBoosted: List<PlaytimeCampaign.PlaytimeRewardAction>?
            = campaign?.eventConfig?.sequentialActions?.filter { filterTimeBoostedEvents(it) }
        val bonusActionsToReturn = mutableListOf<BonusAction>()
        val bonusActions = campaign?.eventConfig?.bonusActions?.filter { !it.name.isNullOrEmpty() }

        // Append Cashback
        campaign?.eventConfig?.cashbackReward?.let {
            bonusActionsToReturn.add(BonusAction(
                cashback = it,
                rewardConfig = null
            ))
        }

        // Append Time-Boosted
        sequentialTimeBoosted?.let {
            for (reward in it) {
                bonusActionsToReturn.add(BonusAction(rewardConfig = reward))
            }
        }

        // Add Bonus Actions
        bonusActions?.let {
            for (reward in it) {
                bonusActionsToReturn.add(BonusAction(rewardConfig = reward))
            }
        }

        return bonusActionsToReturn
    }

    @Composable
    fun getBonusActionBackground(action: BonusAction): Color {
        if (action.cashback != null) {
            return LocalStudioColorsPalette.current.Yellow40
        }
        return LocalStudioColorsPalette.current.PrimaryColor
    }

    fun getIsInstalled(campaign: PlaytimeCampaign?): Boolean {
        return !campaign?.installedAt.isNullOrEmpty()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getIsExpired(campaign: PlaytimeCampaign?): Boolean {
        return if (!campaign?.rewardingExpiresAt.isNullOrEmpty()) Instant.parse(
            campaign.rewardingExpiresAt
        ).isBefore(Instant.now())
        else false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDurationUntilExpiration(campaign: PlaytimeCampaign?): Duration? {
        return if (!campaign?.rewardingExpiresAt.isNullOrEmpty()) Duration.between(
            Instant.now(),
            Instant.parse(campaign.rewardingExpiresAt)
        ) else null
    }

    fun rewardIsTimeBoosted(reward: PlaytimeCampaign.PlaytimeRewardAction): Boolean {
        reward.timedCoinsDuration?.let {
            return it > 0
        }

        return false
    }

    fun rewardIsRepeatable(reward: PlaytimeCampaign.PlaytimeRewardAction): Boolean {
        reward.rewardsCount?.let {
            return it > 1
        }

        return false
    }

    fun multiplierIsPending(multiplier: PlaytimeCampaign.PlaytimeRewardActionMultiplier): Boolean {
        if (multiplier.status.isNullOrEmpty()) {
            return true
        }

        return multiplier.status == "Pending"
    }

    @Composable
    fun getMultiplierBackground(multiplier: PlaytimeCampaign.PlaytimeRewardActionMultiplier): Color {
        if (multiplierIsPending(multiplier)) {
            return LocalStudioColorsPalette.current.BlueGrey500
        }

        return LocalStudioColorsPalette.current.PrimaryColor
    }
}
