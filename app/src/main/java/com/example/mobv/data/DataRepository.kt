package com.example.mobv.data

import com.example.mobv.data.models.User
import com.example.mobv.data.services.ApiService
import com.example.mobv.data.services.UserLogin
import com.example.mobv.data.services.UserRegistration
import java.io.IOException

class DataRepository private constructor(private val service: ApiService) {

    companion object {
        const val TAG = "DataRepository"

        @Volatile
        private var INSTANCE: DataRepository? = null
        private val lock = Any()

        fun getInstance(): DataRepository {
            return INSTANCE ?: synchronized(lock) {
                INSTANCE ?: DataRepository(ApiService.create()).also { INSTANCE = it }
            }
        }
    }

    suspend fun apiRegisterUser(username: String, email: String, password: String) : Pair<String,User?>{
        if (username.isEmpty()){
            return Pair("Username can not be empty", null)
        }
        if (email.isEmpty()){
            return Pair("Email can not be empty", null)
        }
        if (password.isEmpty()){
            return Pair("Password can not be empty", null)
        }

        try {
            val response = service.registerUser(UserRegistration(username, email, password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    return Pair("", User(username,email,json_response.uid, json_response.access, json_response.refresh))
                }
            }
            return Pair("Failed to create user", null)
        }catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to create user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return Pair("Fatal error. Failed to create user.", null)
    }

    suspend fun apiLoginUser(email: String, name: String, password: String) : Pair<String,User?>{
        if (email.isEmpty()){
            return Pair("Email can not be empty", null)
        }
        if (password.isEmpty()){
            return Pair("Password can not be empty", null)
        }

        try {
            val response = service.loginUser(UserLogin(email, name, password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    return Pair("", User("username_ph",email,json_response.uid, json_response.access, json_response.refresh))
                }
            }
            return Pair("Failed to login user", null)
        }catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to login user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return Pair("Fatal error. Failed to login user.", null)
    }
}
