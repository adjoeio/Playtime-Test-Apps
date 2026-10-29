package io.adjoe.playtimestudio.data

import io.adjoe.sdk.studio.PlaytimeRewardAction
import io.adjoe.sdk.studio.PlaytimeCashbackConfig

data class BonusAction(
    val rewardConfig: PlaytimeRewardAction? = null,
    val cashback: PlaytimeCashbackConfig? = null
)
