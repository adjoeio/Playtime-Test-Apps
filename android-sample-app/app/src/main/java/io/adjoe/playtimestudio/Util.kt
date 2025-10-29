package io.adjoe.playtimestudio

import android.app.Activity
import android.util.Log
import android.widget.Toast

object Util {
    const val TAG: String = "PlaytimeStudio"

    @JvmStatic
    fun logVisible(activity: Activity, message: String) {
        Log.i(TAG, message)
        activity.runOnUiThread {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    @JvmStatic
    fun logError(activity: Activity, message: String) {
        Log.e(TAG, message)
        activity.runOnUiThread {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }
}
