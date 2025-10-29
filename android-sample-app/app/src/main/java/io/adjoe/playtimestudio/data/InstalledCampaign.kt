package io.adjoe.playtimestudio.data

import io.adjoe.sdk.studio.PlaytimeCampaign

data class InstalledCampaign(
    val campaign: PlaytimeCampaign,
    var isInstalled: Boolean,
    var isExpired: Boolean
)
