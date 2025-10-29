package io.adjoe.playtimestudio.data

data class InstalledCampaignDO(
    val campaignUUID: String,
    val timestamp: String
)

data class InstalledCampaingnsDO(
    val campaigns: MutableList<InstalledCampaignDO>,
)
