package com.example.mobv.data

import android.content.Context
import android.content.SharedPreferences
import com.example.mobv.MyApplication
import com.example.mobv.data.models.User

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

        private const val shpKey = "com.example.mobv"
        private const val userKey = "userKey"
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
}