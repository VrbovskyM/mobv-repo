package com.example.mobv.data.api

import android.content.Context
import com.example.mobv.data.api.helper.AuthInterceptor
import com.example.mobv.data.api.helper.TokenAuthenticator
import com.example.mobv.data.api.model.GeofenceResponse
import com.example.mobv.data.api.model.LoginResponse
import com.example.mobv.data.api.model.RefreshTokenRequest
import com.example.mobv.data.api.model.RefreshTokenResponse
import com.example.mobv.data.api.model.RegistrationResponse
import com.example.mobv.data.api.model.UserLoginRequest
import com.example.mobv.data.api.model.UserRegistrationRequest
import com.example.mobv.data.api.model.UserResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("user/create.php")
    suspend fun registerUser(@Body userInfo: UserRegistrationRequest): Response<RegistrationResponse>

    @POST("user/login.php")
    suspend fun loginUser(@Body userInfo: UserLoginRequest): Response<LoginResponse>

    @GET("user/get.php")
    suspend fun getUser(
        @Query("id") id: String
    ): Response<UserResponse>

    @POST("user/refresh.php")
    suspend fun refreshToken(
        @Body refreshInfo: RefreshTokenRequest
    ): Response<RefreshTokenResponse>

    @POST("user/refresh.php")
    fun refreshTokenBlocking(
        @Body refreshInfo: RefreshTokenRequest
    ): Call<RefreshTokenResponse>

    @GET("geofence/list.php")
    suspend fun listGeofence(): Response<GeofenceResponse>

    companion object {
        fun create(context: Context): ApiService {

            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .authenticator(TokenAuthenticator(context))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://zadanie.mpage.sk/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}