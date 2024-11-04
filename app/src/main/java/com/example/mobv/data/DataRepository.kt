package com.example.mobv.data

import com.example.mobv.config.AppConfig
import com.example.mobv.data.models.User
import com.example.mobv.data.services.ApiService
import com.example.mobv.data.services.RefreshTokenRequest
import com.example.mobv.data.services.UserLogin
import com.example.mobv.data.services.UserRegistration
import java.io.IOException
import android.content.Context
import com.example.mobv.data.localDb.AppRoomDatabase
import com.example.mobv.data.localDb.LocalCache
import com.example.mobv.data.localDb.entities.UserEntity
import com.example.mobv.data.services.StatusAndMessageResponse
import com.example.mobv.data.services.UpdateUserLocationRequest
import com.example.mobv.data.services.newPasswordRequest
import com.example.mobv.data.services.resetPasswordRequest
import com.google.gson.Gson

class DataRepository private constructor(private val service: ApiService, private val cache: LocalCache) {

    companion object {
        const val TAG = "DataRepository"

        @Volatile
        private var INSTANCE: DataRepository? = null
        private val lock = Any()

        fun getInstance(context: Context): DataRepository {
            return INSTANCE ?: synchronized(lock) {
                INSTANCE ?: DataRepository(ApiService.create(context),
                    LocalCache(AppRoomDatabase.getInstance(context).appDao())
                ).also { INSTANCE = it }
            }
        }
    }

    suspend fun apiRegisterUser(username: String, email: String, password: String) : Pair<String,User?> {
        // Input validation
        if (username.isEmpty()) return Pair("Username cannot be empty", null)
        if (email.isEmpty()) return Pair("Email cannot be empty", null)
        if (password.isEmpty()) return Pair("Password cannot be empty", null)

        try {
            val registerResponse = service.registerUser(UserRegistration(username, email, password))

            if (!registerResponse.isSuccessful) return Pair("Failed to register: ${registerResponse.message()}", null)

            val registerBody = registerResponse.body() ?: return Pair("Registration response was empty", null)

            // Check if registration was successful by verifying uid
            if (registerBody.uid == "-1") return Pair("Registration failed - username already exists", null)
            if (registerBody.uid == "-2") return Pair("Registration failed - email already exists", null)

            // Create user object and save to preferences
            val user = User(
                username = username,
                email = email,
                id = registerBody.uid,
                access = registerBody.access,
                refresh = registerBody.refresh
            )
            PreferenceData.getInstance().putUser(user)

            return Pair("User registered successfully", user)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to register user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return Pair("Fatal error. Failed to register user.", null)
        }
    }

    suspend fun apiLoginUser(name: String, password: String) : Pair<String,User?>{
        if (name.isEmpty()) return Pair("Name cannot be empty", null)
        if (password.isEmpty()) return Pair("Password cannot be empty", null)

        try {
            val loginResponse = service.loginUser(UserLogin(name, password))

            if (!loginResponse.isSuccessful) return Pair("Failed to login: ${loginResponse.message()}", null)

            val loginBody = loginResponse.body() ?: return Pair("Login response was empty", null)

            if (loginBody.uid == "-1") return Pair("Invalid credentials", null)

            val user = User(name, "", loginBody.uid, loginBody.access, loginBody.refresh)
            PreferenceData.getInstance().putUser(user)

            return Pair("Logged in user", user)
        }catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to login user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return Pair("Fatal error. Failed to login user.", null)
    }

    suspend fun apiGetUser(uid: String): Pair<String, User?> {
        try {
            val response = service.getUser(uid)

            if (response.isSuccessful) {
                response.body()?.let {
                    return Pair("", User(it.name, "", it.id, "", "", it.photo))
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

    suspend fun apiListGeofenceUsers(): String {
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

    suspend fun apiChangePassword(oldPassword: String, newPassword: String): StatusAndMessageResponse {
        if (oldPassword.isEmpty()) return StatusAndMessageResponse("Error","Old password cannot be empty")
        if (newPassword.isEmpty()) return StatusAndMessageResponse("Error","New password cannot be empty")

        try {
            val response = service.changePassword(newPasswordRequest(oldPassword, newPassword))

            if (!response.isSuccessful) return StatusAndMessageResponse("Error", "Failed; HTTP Code: ${response.code()}")

            val responseBody = response.body() ?: return StatusAndMessageResponse("Error", "Body is empty")

            if (responseBody.status != "success") return StatusAndMessageResponse(responseBody.status, "Body is failure")

            return StatusAndMessageResponse("success", "Password changed successfully")
        } catch (ex: IOException) {
            ex.printStackTrace()
            return StatusAndMessageResponse("Error","Check internet connection. Failed to change password.")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return StatusAndMessageResponse("Error","Fatal error. Failed to change password.")
    }

    suspend fun apiResetPassword(email: String): StatusAndMessageResponse {
        if (email.isEmpty()) return StatusAndMessageResponse("Error","Email cannot be empty")
        try {
            val response = service.resetPassword(resetPasswordRequest(email))

            if (response.isSuccessful) {
                response.body()?.let { success ->
                    return StatusAndMessageResponse(success.status, success.message)
                }
            }
            else if (response.code() == 500) {
                response.errorBody()?.let { failureBody ->
                    val errorJson = failureBody.string()
                    val gson = Gson() // Ensure Gson is imported and initialized
                    val failureResponse = gson.fromJson(errorJson, StatusAndMessageResponse::class.java)

                    return StatusAndMessageResponse(failureResponse.status, failureResponse.message)
                }
            }

            return StatusAndMessageResponse("Error", "HTTP Code: ${response.code()}")
        } catch (ex: IOException) {
            ex.printStackTrace()
            return StatusAndMessageResponse("Error","Check internet connection. Failed to reset password.")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return StatusAndMessageResponse("Error","Fatal error. Failed to reset password.")
    }

    suspend fun apiUpdateUserLocation(lat: Double, lon: Double, radius: Double): StatusAndMessageResponse {
        try {
            val response = service.updateUserLocation(UpdateUserLocationRequest(lat, lon, radius))

            if (!response.isSuccessful) return StatusAndMessageResponse("Error", "Failed; HTTP Code: ${response.code()}")

            val responseBody = response.body() ?: return StatusAndMessageResponse("Error", "Body is empty")

            if (responseBody.success != "true") return StatusAndMessageResponse(responseBody.success, "Body is failure")

            return StatusAndMessageResponse("success", "Location updated successfully")
        } catch (ex: IOException) {
            ex.printStackTrace()
            return StatusAndMessageResponse("Error","Check internet connection. Failed to update location.")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return StatusAndMessageResponse("Error","Fatal error. Failed to update location.")
    }

    suspend fun apiDeleteUserLocation(): StatusAndMessageResponse {
        try {
            val response = service.deleteUserLocation()

            if (!response.isSuccessful) return StatusAndMessageResponse("Error", "Failed; HTTP Code: ${response.code()}")

            val responseBody = response.body() ?: return StatusAndMessageResponse("Error", "Body is empty")

            if (responseBody.success != "true") return StatusAndMessageResponse(responseBody.success, "Body is failure")

            return StatusAndMessageResponse("success", "Location deleted successfully")
        } catch (ex: IOException) {
            ex.printStackTrace()
            return StatusAndMessageResponse("Error","Check internet connection. Failed to delete location.")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return StatusAndMessageResponse("Error","Fatal error. Failed to delete location.")
    }
}
