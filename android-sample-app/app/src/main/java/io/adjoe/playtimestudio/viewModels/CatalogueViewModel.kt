package io.adjoe.playtimestudio.viewModels

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import io.adjoe.playtimestudio.InstalledGamesWorker
import io.adjoe.playtimestudio.Util
import io.adjoe.playtimestudio.data.InstalledCampaign
import io.adjoe.playtimestudio.data.InstalledCampaignDO
import io.adjoe.playtimestudio.data.InstalledCampaingnsDO
import io.adjoe.playtimestudio.repositories.MoshiRepository
import io.adjoe.playtimestudio.repositories.WorkManagerRepository
import io.adjoe.sdk.PlaytimeOptions
import io.adjoe.sdk.internal.SharedPreferencesRepository
import io.adjoe.sdk.studio.PlaytimeCampaign
import io.adjoe.sdk.studio.PlaytimeCampaignsListener
import io.adjoe.sdk.studio.PlaytimeCampaignsResponse
import io.adjoe.sdk.studio.PlaytimeResponseError
import io.adjoe.sdk.studio.PlaytimeStudio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
class CatalogueViewModel : ViewModel() {
    private var allCampaigns = mutableListOf<PlaytimeCampaign>()
    private var campaigns = mutableStateListOf<PlaytimeCampaign>()
    private val _campaignsListFlow = MutableStateFlow(mutableStateListOf<PlaytimeCampaign>())
    val campaignsListFlow: StateFlow<List<PlaytimeCampaign>> get() = _campaignsListFlow
    private var featuredCampaigns = mutableStateListOf<PlaytimeCampaign>()
    private val _featuredListFlow = MutableStateFlow(featuredCampaigns)
    val featuredCampaignsListFlow: StateFlow<List<PlaytimeCampaign>> get() = _featuredListFlow
    private var installedCampaigns = mutableStateListOf<InstalledCampaign>()
    private val _installedCampaignsListFlow = MutableStateFlow(installedCampaigns)
    val installedCampaignsListFlow: StateFlow<List<InstalledCampaign>> get() = _installedCampaignsListFlow

    val tabs = listOf("Catalog", "My Games")
    var selectedTab by mutableStateOf(tabs[0])
    var loadingMyCampaigns by mutableStateOf(true)
    var loadingInstalledCampaigns by mutableStateOf(true)

    fun startCheckInstallWorker(context: Activity, campaign: PlaytimeCampaign) {
        // If clicked game is already in installed campaigns, do nothing
        installedCampaigns.find { it.campaign.campaignUUID == campaign.campaignUUID } ?: return

        val installedGamesWorker = OneTimeWorkRequestBuilder<InstalledGamesWorker>()
            .setInputData(workDataOf(InstalledGamesWorker.CAMPAIGN_UUID to campaign.campaignUUID))
            .build()

        val installedCampaign =
            InstalledCampaign(campaign = campaign, isInstalled = false, isExpired = false)

        // add game to installed campaigns
        installedCampaigns.add(installedCampaign)
        // remove game from catalog campaigns
        campaigns.remove(campaign)
        featuredCampaigns.remove(campaign)

        WorkManagerRepository
            .getInstance(context)
            .observeInstalledGamesWorker(
                campaign,
                onFinish = {
                    getCampaigns(context)
                }
            )
    }

    fun getLocalInstalledCampaigns(context: Context): InstalledCampaingnsDO? {
        val installedAppsString = SharedPreferencesRepository
            .getInstance(context)
            .getInstalledApps()

        return MoshiRepository
            .getInstance()
            .jsonToInstalledCampaigns(installedAppsString)
    }

    fun getPlaytimeOptions(context: Context): PlaytimeOptions {
        val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(
            context.applicationContext
        )

        return PlaytimeOptions().setUserId(adInfo.id)
    }

    fun onGetCampaignsRecieved(
        p0: PlaytimeCampaignsResponse,
        localInstalledCampaigns: InstalledCampaingnsDO?
    ) {
        allCampaigns = p0.campaigns

        // Filter installed campaigns that are saved locally
        val filteredCatalogCampaigns =
            p0.campaigns.filter { c ->
                localInstalledCampaigns?.campaigns?.find {
                    it.campaignUUID == c.campaignUUID
                } == null
            }
        campaigns =
            filteredCatalogCampaigns.filter { c -> c.promotion == null }
                .toMutableStateList()
        _campaignsListFlow.value = campaigns

        featuredCampaigns = filteredCatalogCampaigns
            .filter { c -> c.promotion != null }
            .sortedBy { it.featuredPosition }
            .toMutableStateList()
        _featuredListFlow.value = featuredCampaigns

        loadingMyCampaigns = false
    }

    fun onGetInstalledCampaignsRecieved(
        p0: PlaytimeCampaignsResponse,
        localInstalledCampaigns: InstalledCampaingnsDO?
    ) {
        // Filter local installed campaigns
        val filteredLocalInstalledCampaigns =
            localInstalledCampaigns?.campaigns?.filter { c ->
                p0.campaigns.find {
                    it.campaignUUID == c.campaignUUID
                } == null
            }

        installedCampaigns = mutableStateListOf()

        // Add campaigns we get from SDK
        p0.campaigns.forEach { it ->
            installedCampaigns.add(
                InstalledCampaign(
                    it,
                    isInstalled = true,
                    isExpired = false
                )
            )
        }

        // Added local installed campaigns
        filteredLocalInstalledCampaigns?.forEach { campaign ->
            val _campaign =
                allCampaigns.find {
                    it.campaignUUID == campaign.campaignUUID
                }
            if (_campaign != null) {
                val minutesPassed = Duration.between(
                    Instant.parse(campaign.timestamp),
                    Instant.now()
                ).toMinutes()
                val expired = minutesPassed >= InstalledGamesWorker.POSTBACK_EXPIRATION
                installedCampaigns.add(
                    InstalledCampaign(
                        _campaign,
                        false,
                        expired
                    )
                )
            }
        }

        _installedCampaignsListFlow.value = installedCampaigns
        loadingInstalledCampaigns = false
    }


    fun getCampaigns(context: Activity) {
        GlobalScope.launch(Dispatchers.IO) {
            val options: PlaytimeOptions = getPlaytimeOptions(context)
            loadingMyCampaigns = true
            loadingInstalledCampaigns = true

            val localInstalledCampaigns = getLocalInstalledCampaigns(context)

            PlaytimeStudio.getCampaigns(
                context,
                options,
                object : PlaytimeCampaignsListener {
                    override fun onReceived(p0: PlaytimeCampaignsResponse) {
                        onGetCampaignsRecieved(p0, localInstalledCampaigns)

                        PlaytimeStudio.getInstalledCampaigns(
                            context,
                            options,
                            object : PlaytimeCampaignsListener {
                                @RequiresApi(Build.VERSION_CODES.O)
                                override fun onReceived(p0: PlaytimeCampaignsResponse) {
                                    onGetInstalledCampaignsRecieved(p0, localInstalledCampaigns)
                                }

                                override fun onError(p0: PlaytimeResponseError) {
                                    Util.logVisible(
                                        context as Activity,
                                        "Error getting campaigns: ${p0.message}"
                                    )
                                    loadingInstalledCampaigns = false
                                }
                            }
                        )
                    }

                    override fun onError(p0: PlaytimeResponseError) {
                        Util.logVisible(
                            context as Activity,
                            "Error getting campaigns: ${p0.message}"
                        )
                        loadingMyCampaigns = false
                    }
                }
            )
        }
    }
}
