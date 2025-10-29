package io.adjoe.playtimestudio.repositories

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import io.adjoe.playtimestudio.InstalledGamesWorker
import io.adjoe.sdk.studio.PlaytimeCampaign

internal class WorkManagerRepository private constructor(context: Context) {
    companion object {
        @Volatile
        private var instance: WorkManagerRepository? = null

        @JvmStatic
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: WorkManagerRepository(context).also {
                    instance = it
                }
            }
    }

    private val workManagerInstance: WorkManager by lazy {
        WorkManager.getInstance(context)
    }

    private val appContext: Context by lazy {
        context
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun observeInstalledGamesWorker(
        campaign: PlaytimeCampaign,
        onFinish: () -> Unit
    ) {
        val installedGamesWorker = OneTimeWorkRequestBuilder<InstalledGamesWorker>()
            .setInputData(workDataOf(InstalledGamesWorker.CAMPAIGN_UUID to campaign.campaignUUID))
            .build()

        workManagerInstance.enqueue(installedGamesWorker)
        workManagerInstance
            .getWorkInfoByIdLiveData(installedGamesWorker.id)
            .observe(appContext as LifecycleOwner, Observer { workInfo ->
                if (workInfo != null) {
                    if (workInfo.state.isFinished && (
                                workInfo.state == WorkInfo.State.SUCCEEDED
                                        || workInfo.state == WorkInfo.State.FAILED)
                    ) {
                        onFinish()
                    }
                }
            })
    }
}
