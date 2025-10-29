package io.adjoe.playtimestudio.repositories

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient

class AdInfoRepository private constructor(context: Context) {
    companion object {
        @Volatile
        private var instance: AdInfoRepository? = null

        @JvmStatic
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: AdInfoRepository(context).also {
                    instance = it
                }
            }
    }


    private val adInfoInstance: AdvertisingIdClient.Info by lazy {
        AdvertisingIdClient.getAdvertisingIdInfo(context)
    }

    fun getAdInfo(): AdvertisingIdClient.Info {
        return adInfoInstance
    }
}
