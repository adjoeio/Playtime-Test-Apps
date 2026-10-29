package io.adjoe.playtimestudio.viewModels

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.adjoe.playtimestudio.Util
import io.adjoe.playtimestudio.data.InstalledCampaign
import io.adjoe.sdk.AppCategory
import io.adjoe.sdk.CampaignOptions
import io.adjoe.sdk.InstalledCampaignOptions
import io.adjoe.sdk.PlaytimeException
import io.adjoe.sdk.studio.PlaytimeCampaign
import io.adjoe.sdk.studio.PlaytimeCampaignStatus
import io.adjoe.sdk.studio.PlaytimeCampaignsListener
import io.adjoe.sdk.studio.PlaytimeCampaignsResponse
import io.adjoe.sdk.studio.PlaytimeDeeplinkListener
import io.adjoe.sdk.studio.PlaytimeStudio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    // Pull-to-refresh state. Reflects whether a user-initiated refresh is in flight.
    var isRefreshing by mutableStateOf(false)
        private set

    // Clears the refresh indicator once both campaign lists have finished loading.
    private fun updateRefreshingState() {
        if (!loadingMyCampaigns && !loadingInstalledCampaigns) {
            isRefreshing = false
        }
    }

    fun getCampaignOptions(): CampaignOptions {
        return CampaignOptions(category = AppCategory.BOTH)
    }

    fun getInstalledCampaignOptions(): InstalledCampaignOptions {
        return InstalledCampaignOptions(category = AppCategory.BOTH)
    }

    fun onGetCampaignsRecieved(
        response: PlaytimeCampaignsResponse,
    ) {
        allCampaigns = response.campaigns.toMutableList()
        campaigns =
            allCampaigns.filter { c -> c.promotion == null }
                .toMutableStateList()
        _campaignsListFlow.value = campaigns

        featuredCampaigns = allCampaigns
            .filter { c -> c.promotion != null }
            .sortedBy { it.featuredPosition }
            .toMutableStateList()

        _featuredListFlow.value = featuredCampaigns

        loadingMyCampaigns = false
        updateRefreshingState()
    }

    fun onGetInstalledCampaignsRecieved(
        response: PlaytimeCampaignsResponse,
    ) {
        installedCampaigns = mutableStateListOf()

        // Add campaigns we get from SDK
        response.campaigns.forEach { it ->
            installedCampaigns.add(
                InstalledCampaign(
                    it,
                    isInstalled = it.status == PlaytimeCampaignStatus.INSTALLED,
                    isExpired = it.status == PlaytimeCampaignStatus.FAILED
                )
            )
        }

        _installedCampaignsListFlow.value = installedCampaigns
        loadingInstalledCampaigns = false
        updateRefreshingState()
    }

    fun getCampaigns(context: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            val campaignOptions = getCampaignOptions()
            val installedCampaignOptions = getInstalledCampaignOptions()

            loadingMyCampaigns = true
            loadingInstalledCampaigns = true

            PlaytimeStudio.getCampaigns(
                campaignOptions,
                object : PlaytimeCampaignsListener {
                    override fun onReceived(response: PlaytimeCampaignsResponse) {
                        onGetCampaignsRecieved(response)
                    }

                    override fun onError(error: PlaytimeException) {
                        Util.logVisible(
                            context as Activity,
                            "Error getting campaigns: ${error.message}"
                        )
                        loadingMyCampaigns = false
                        updateRefreshingState()
                    }
                }
            )

            PlaytimeStudio.getInstalledCampaigns(
                installedCampaignOptions,
                object : PlaytimeCampaignsListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onReceived(response: PlaytimeCampaignsResponse) {
                        onGetInstalledCampaignsRecieved(response)
                    }

                    override fun onError(error: PlaytimeException) {
                        Util.logVisible(
                            context as Activity,
                            "Error getting campaigns: ${error.message}"
                        )
                        loadingInstalledCampaigns = false
                        updateRefreshingState()
                    }
                }
            )
        }
    }

    /**
     * Triggered by the pull-to-refresh gesture. Shows the refresh indicator and
     * re-fetches both campaign lists. The indicator is cleared once both requests
     * complete (see [updateRefreshingState]).
     */
    fun refreshCampaigns(context: Activity) {
        isRefreshing = true
        getCampaigns(context)
    }

    fun openChatbot(activity: Activity) {
        PlaytimeStudio.openChatbot(
            activity,
            campaign = null,
            listener = object : PlaytimeDeeplinkListener {
                override fun onError(error: PlaytimeException) {
                    Util.logVisible(activity, "Error opening chatbot: ${error.message}")
                }

                override fun onOpened() {
                    Util.logVisible(activity, "Successfully opened chatbot")
                }
            }
        )
    }

    fun showInstalledApps(activity: Activity) {
        PlaytimeStudio.showInstalledCampaigns(
            activity,
            listener = object : PlaytimeDeeplinkListener {
                override fun onError(error: PlaytimeException) {
                    Util.logVisible(activity, "Error showing installed apps: ${error.message}")
                }

                override fun onOpened() {
                    Util.logVisible(activity, "Successfully opened installed apps")
                }
            }
        )
    }

    fun deeplinkCampaign(activity: Activity, campaign: PlaytimeCampaign) {
        PlaytimeStudio.showCampaignDetails(
            activity,
            campaign,
            object : PlaytimeDeeplinkListener {
                override fun onError(error: PlaytimeException) {
                    Util.logVisible(activity, "Error opening campaign: ${error.message}")
                }

                override fun onOpened() {
                    Util.logVisible(activity, "Successfully opened campaign")
                }
            }
        )
    }
}
