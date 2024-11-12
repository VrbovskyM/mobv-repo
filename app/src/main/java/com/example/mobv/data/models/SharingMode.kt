package com.example.mobv.data.models

import com.example.mobv.MyApplication
import com.example.mobv.data.PreferenceData
import com.example.mobv.utils.Utils
import java.time.LocalTime

enum class SharingMode {
    OFF,
    ON,
    SCHEDULED;

    companion object {
        fun isSharingEnabled(): Boolean {
            val currentMode = PreferenceData.getInstance().getSharingMode()
            return Utils.hasPermissions(MyApplication.getContext()) && when (currentMode) {
                OFF -> false
                ON -> true
                SCHEDULED -> isWithinScheduledTime()
            }
        }


        private fun isWithinScheduledTime(): Boolean {
            val scheduledTime = PreferenceData.getInstance().getScheduledTime()
            val startTime = scheduledTime!!.startHour * 60 * 60 * 1000
            val endTime = scheduledTime.endHour * 60 * 60 * 1000

            val now = LocalTime.now()
            val timeOfDay = now.toSecondOfDay() * 1000  // Convert to milliseconds for comparison

            return if (startTime < endTime) {
                // Regular case within the same day, exclusive end
                timeOfDay in startTime until endTime
            } else {
                // Crosses midnight, either after startTime or before endTime
                timeOfDay >= startTime || timeOfDay < endTime
            }
        }

    }
}
