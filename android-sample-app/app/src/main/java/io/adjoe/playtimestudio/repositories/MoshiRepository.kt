package io.adjoe.playtimestudio.repositories

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.adjoe.playtimestudio.data.InstalledCampaingnsDO

class MoshiRepository {
    companion object {
        @Volatile
        private var instance: MoshiRepository? = null

        @JvmStatic
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: MoshiRepository().also {
                    instance = it
                }
            }
    }

    private val installedCampaignsJsonAdapter: JsonAdapter<InstalledCampaingnsDO?> by lazy {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        return@lazy moshi.adapter(InstalledCampaingnsDO::class.java)
    }

    fun jsonToInstalledCampaigns(value: String?): InstalledCampaingnsDO? {
        val jsonCampaigns = if (!value.isNullOrEmpty())
            installedCampaignsJsonAdapter.fromJson(value)
        else InstalledCampaingnsDO(mutableListOf())
        return jsonCampaigns
    }

    fun installedCampaignsToJson(value: InstalledCampaingnsDO?): String? {
        return installedCampaignsJsonAdapter.toJson(value)
    }
}
