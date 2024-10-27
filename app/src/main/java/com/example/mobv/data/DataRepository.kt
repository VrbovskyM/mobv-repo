package com.example.mobv.data

import android.content.Context
import com.example.mobv.data.api.ApiService
import com.example.mobv.data.api.model.UserLoginRequest
import com.example.mobv.data.api.model.UserRegistrationRequest
import com.example.mobv.data.db.AppRoomDatabase
import com.example.mobv.data.db.entities.UserEntity
import com.example.mobv.data.model.User
import venaka.bioapp.data.db.LocalCache
import java.io.IOException

class DataRepository private constructor(
    private val service: ApiService,
    private val cache: LocalCache
) {
    companion object {
        const val TAG = "DataRepository"

        @Volatile
        private var INSTANCE: DataRepository? = null
        private val lock = Any()

        fun getInstance(context: Context): DataRepository =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: DataRepository(
                        ApiService.create(context),
                        LocalCache(AppRoomDatabase.getInstance(context).appDao())
                    ).also { INSTANCE = it }
            }
    }

    suspend fun apiRegisterUser(
        username: String,
        email: String,
        password: String
    ): Pair<String, User?> {
        if (username.isEmpty()) {
            return Pair("Username can not be empty", null)
        }
        if (email.isEmpty()) {
            return Pair("Email can not be empty", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty", null)
        }
        try {
            val response = service.registerUser(UserRegistrationRequest(username, email, password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    return Pair(
                        "",
                        User(
                            username,
                            email,
                            json_response.uid,
                            json_response.access,
                            json_response.refresh
                        )
                    )
                }
            }
            return Pair("Failed to create user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to create user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to create user.", null)
    }

    suspend fun apiLoginUser(
        username: String,
        password: String
    ): Pair<String, User?> {
        if (username.isEmpty()) {
            return Pair("Username can not be empty", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty", null)
        }
        try {
            val response = service.loginUser(UserLoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    if (json_response.uid == "-1") {
                        return Pair("Wrong password or username.", null)
                    }
                    return Pair(
                        "",
                        User(
                            username,
                            "",
                            json_response.uid,
                            json_response.access,
                            json_response.refresh
                        )
                    )
                }
            }
            return Pair("Failed to login user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to login user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to login user.", null)
    }

    suspend fun apiGetUser(
        uid: String
    ): Pair<String, User?> {
        try {
            val response = service.getUser(uid)

            if (response.isSuccessful) {
                response.body()?.let {
                    val user = User(it.name, "", it.id, "", "", it.photo)
                    cache.insertUserItems(
                        listOf(
                            UserEntity(
                                user.id, user.username, "", 0.0, 0.0, 0.0, ""
                            )
                        )
                    )
                    return Pair("", user)
                }
            }

            return Pair("Failed to load user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to load user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to load user.", null)
    }

    suspend fun apiGeofenceUsers(): String {
        try {
            val response = service.listGeofence()

            if (response.isSuccessful) {
                response.body()?.let { resp ->
                    val users = resp.list.map {
                        UserEntity(
                            it.uid, it.name, it.updated,
                            resp.me.lat, resp.me.lon, it.radius,
                            it.photo
                        )
                    }

                    cache.insertUserItems(users)

                    return ""
                }
            }

            return "Failed to load user"
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to load user."
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to load user."
    }

    fun getUsers() = cache.getUsers()
}
