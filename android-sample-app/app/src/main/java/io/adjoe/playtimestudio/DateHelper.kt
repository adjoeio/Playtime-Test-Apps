package io.adjoe.playtimestudio

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration

object DateHelper {
    fun secondsToMinutes(seconds: Int? = 0): String {
        val minutes = seconds?.div(60)
        return if (minutes == 1) "$minutes minute" else "$minutes minutes"
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun humanizeDuration(durationBetween: Duration?): String? {
        if (durationBetween?.isNegative == true) {
            return null
        }

        val daysLeft = durationBetween?.toDays()

        if (daysLeft == 0L) {
            val hoursLeft = durationBetween.toHours()
            val minutesLeft = durationBetween.toMinutes()
            val secondsLeft = durationBetween.toSeconds()

            if (hoursLeft == 0L) {
                if (minutesLeft == 0L) {
                    return "$secondsLeft seconds"
                }

                return "$minutesLeft minutes ${secondsLeft - (minutesLeft * 60)} seconds"
            }

            return "$hoursLeft hours ${minutesLeft - (hoursLeft * 60)} minutes"
        }

        return "$daysLeft days"
    }

    fun humanizeMinutes(minutes: Int): String {
        if (minutes > (60 * 24)) {
            val hours = minutes / 60
            val days = hours / 24
            val leftHours = hours % 24
            return "${days}d ${leftHours}h"
        }

        if (minutes > 60) {
            val hours = minutes / 60
            val leftMinutes = minutes % 60
            return "${hours}h ${leftMinutes}m"
        }

        return "${minutes}m 0s"
    }
}
