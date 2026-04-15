package io.adjoe.playtimestudio

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.adjoe.playtimestudio.data.InstalledCampaignDO
import io.adjoe.playtimestudio.repositories.MoshiRepository
import io.adjoe.sdk.PlaytimeOptions
import io.adjoe.sdk.internal.SharedPreferencesRepository
import io.adjoe.sdk.studio.PlaytimeCampaignsListener
import io.adjoe.sdk.studio.PlaytimeCampaignsResponse
import io.adjoe.sdk.studio.PlaytimeResponseError
import io.adjoe.sdk.studio.PlaytimeStudio
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
class InstalledGamesWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        val CAMPAIGN_UUID = "CAMPAIGN_UUID"
        const val POSTBACK_EXPIRATION = 20L
    }

    fun editInstalledAppsInSharedPreferences(installedApps: String?) {
        SharedPreferencesRepository
            .getInstance(applicationContext)
            .setInstalledApps(installedApps)
    }

    fun logVisible(message: String) {
        Log.i("WORKER", message)
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun doWork(): Result {
        val installedAppsString = SharedPreferencesRepository
            .getInstance(applicationContext)
            .getInstalledApps()

        val campaignUUID = inputData.getString(CAMPAIGN_UUID)

        if (campaignUUID.isNullOrEmpty()) {
            return Result.failure()
        }

        val jsonCampaigns = MoshiRepository
            .getInstance()
            .jsonToInstalledCampaigns(installedAppsString)
        var savedCampaign = jsonCampaigns?.campaigns?.find { it.campaignUUID == campaignUUID }

        if (savedCampaign == null) {
            savedCampaign = InstalledCampaignDO(campaignUUID, Instant.now().toString())
            jsonCampaigns?.campaigns?.add(savedCampaign)
            editInstalledAppsInSharedPreferences(
                MoshiRepository.getInstance().installedCampaignsToJson(
                    jsonCampaigns
                )
            )
        }

        var minutesPassed = Duration.between(
            Instant.parse(savedCampaign.timestamp),
            Instant.now()
        ).toMinutes()

        // Campaign is expired
        if (minutesPassed > POSTBACK_EXPIRATION) {
            logVisible(
                "ended=true, savedCampaign=$savedCampaign, minutePassed=$minutesPassed, success=false"
            )
            return Result.failure()
        }

        logVisible("started=true, savedCampaign=$savedCampaign, minutePassed=$minutesPassed")

        var appIsInstalled = false

        suspend fun checkIfAppIsInstalled(delayVal: Long) {
            if (appIsInstalled) {
                return
            }

            delay(delayVal / 2) // wait half the time

            logVisible("checkApp=true , savedCampaign=$savedCampaign, minutePassed=$minutesPassed, appIsInstalled=$appIsInstalled")

            val options: PlaytimeOptions = PlaytimeOptions().setUserId(Config.ADJOE_USER_ID)

            // Fetch the campaigns
            PlaytimeStudio.getInstalledCampaigns(
                applicationContext,
                options,
                object : PlaytimeCampaignsListener {
                    override fun onReceived(p0: PlaytimeCampaignsResponse) {
                        val campaign = p0.campaigns.find { it.campaignUUID == campaignUUID }
                        if (campaign != null) {
                            appIsInstalled = true
                            logVisible("appFound=true , savedCampaign=$savedCampaign, minutePassed=$minutesPassed")
                            minutesPassed = POSTBACK_EXPIRATION
                        }
                    }

                    override fun onError(p0: PlaytimeResponseError) {}
                }
            )

            delay(delayVal / 2) // wait half the time
            minutesPassed += delayVal / 1000 / 60
        }

        runBlocking {
            // First delay (1 minute)
            while (minutesPassed < 5) {
                checkIfAppIsInstalled(1_000 * 60)
            }

            // Second Delay (2 minutes)
            while (minutesPassed < POSTBACK_EXPIRATION) {
                checkIfAppIsInstalled(1_000 * 60 * 2)
            }
        }

        if (appIsInstalled) {
            logVisible("ended=true, savedCampaign=$savedCampaign, success=true")
            return Result.success()
        }

        logVisible("ended=true , savedCampaign=$savedCampaign, minutePassed=$minutesPassed, success=false")
        return Result.failure()
    }
}
