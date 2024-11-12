package com.example.mobv.data.models

import com.google.gson.Gson
import java.io.IOException

data class ScheduledTime(
    var startHour: Int,
    var endHour: Int,
){
    fun toJson(): String? {
        return try {
            Gson().toJson(this)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    companion object {
        fun fromJson(string: String): User? {
            return try {
                Gson().fromJson(string, User::class.java)
            } catch (ex: IOException) {
                ex.printStackTrace()
                null
            }
        }
    }

    // If start and end time equals, sharing is disabled
    fun isSameTime(): Boolean {
        return this.startHour == this.endHour
    }

    // Convert scheduled time to a readable format
    fun getFormattedTime(): String {
        return "From ${startHour.toString().padStart(2, '0')}" +
                "to ${endHour.toString().padStart(2, '0')}"
    }
}
