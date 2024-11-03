package com.example.mobv.data

import android.content.Context
import android.content.SharedPreferences
import com.example.mobv.MyApplication
import com.example.mobv.config.AppConfig
import com.example.mobv.data.models.ScheduledTime
import com.example.mobv.data.models.SharingMode
import com.example.mobv.data.models.User
import com.google.gson.Gson

class PreferenceData private constructor() {
    // Using lazy initialization to get SharedPreferences only when needed
    private val sharedPreferences: SharedPreferences by lazy {
        MyApplication.getContext().getSharedPreferences(
            shpKey, Context.MODE_PRIVATE
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: PreferenceData? = null
        private val lock = Any()

        fun getInstance(): PreferenceData =
            INSTANCE ?: synchronized(lock) {
                INSTANCE ?: PreferenceData().also { INSTANCE = it }
            }

        private const val shpKey = AppConfig.SharedPreferences_KEY
        private const val userKey = "userKey"
        // Add keys for sharing preferences
        private const val sharingModeKey = "sharingModeKey"
        private const val manualSharingKey = "manualSharingEnabledKey"
        private const val scheduledSharingKey = "scheduledSharing"
    }

    fun clearData() {
        sharedPreferences.edit().clear().apply()
    }

    fun putUser(user: User?) {
        val editor = sharedPreferences.edit()
        when (user) {
            null -> editor.remove(userKey)
            else -> editor.putString(userKey, user.toJson())
        }
        editor.apply()
    }

    fun getUser(): User? {
        return sharedPreferences.getString(userKey, null)?.let { json ->
            User.fromJson(json)
        }
    }

    fun updateUser(updatedUser: User): Boolean {
        val currentUser = getUser()

        return if (currentUser?.id == updatedUser.id) {
            // Create new User object with updated fields
            val newUser = User(
                id = currentUser.id,  // keep original ID
                username = updatedUser.username,
                email = updatedUser.email,
                access = updatedUser.access,
                refresh = updatedUser.refresh,
                photo = updatedUser.photo
            )
            putUser(newUser)
            true
        } else {
            false
        }
    }

    // Sharing Modes
    fun putSharingMode(mode: SharingMode) {
        sharedPreferences.edit()
            .putString(sharingModeKey, mode.name) // Store enum name as string
            .apply()
    }
    fun getSharingMode(): SharingMode {
        val modeString = sharedPreferences.getString(sharingModeKey, SharingMode.OFF.name) // Default to MANUAL
        return SharingMode.valueOf(modeString ?: SharingMode.OFF.name)
    }

    // Scheduled Sharing
    fun putScheduledTime(scheduledTime: ScheduledTime) {
        val jsonString = Gson().toJson(scheduledTime)
        sharedPreferences.edit()
            .putString(scheduledSharingKey, jsonString)
            .apply()
    }
    fun getScheduledTime(): ScheduledTime? {
        val jsonString = sharedPreferences.getString(scheduledSharingKey, null)
        return jsonString?.let { Gson().fromJson(it, ScheduledTime::class.java) }
    }
    // New method to update the scheduled time
    fun updateScheduledTime(newScheduledTime: ScheduledTime) {
        // Retrieve the current scheduled time
        var currentScheduledTime = getScheduledTime()

        // Check if there is an existing scheduled time
        if (currentScheduledTime != null) {
            // Update properties as necessary, for example:
            currentScheduledTime.startHour = newScheduledTime.startHour // Assuming 'time' is a property of ScheduledTime
            currentScheduledTime.endHour = newScheduledTime.endHour // Update any other necessary fields

            // Save the updated scheduled time
            putScheduledTime(currentScheduledTime)
        } else {
            // If there was no existing scheduled time, you can choose to create a new one
            putScheduledTime(newScheduledTime)
        }
    }
}