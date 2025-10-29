package io.adjoe.playtimestudio.data

import io.adjoe.sdk.studio.PlaytimeCampaign

data class BonusAction(
    val rewardConfig: PlaytimeCampaign.PlaytimeRewardAction? = null,
    val cashback: PlaytimeCampaign.PlaytimeCashbackConfig? = null
)
