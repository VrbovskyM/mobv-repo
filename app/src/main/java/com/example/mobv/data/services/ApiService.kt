package com.example.mobv.data.services

import android.content.Context
import com.example.mobv.config.AppConfig
import com.example.mobv.data.models.User
import com.example.mobv.data.services.helpers.AuthInterceptor
import com.example.mobv.data.services.helpers.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("user/create.php")
    suspend fun registerUser(@Body userInfo: UserRegistration): Response<AuthResponse>

    @POST("user/login.php")
    suspend fun loginUser(@Body userInfo: UserLogin): Response<AuthResponse>

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

    @POST("user/password.php")
    suspend fun changePassword(
        @Body newPassword: newPasswordRequest
    ): Response<StatusAndMessageResponse>

    @POST("user/reset.php")
    suspend fun resetPassword(@Body email: resetPasswordRequest): Response<StatusAndMessageResponse>

    @POST("geofence/update.php")
    suspend fun updateUserLocation(@Body location: UpdateUserLocationRequest): Response<SuccessResponse>

    @DELETE("geofence/update.php")
    suspend fun deleteUserLocation(): Response<SuccessResponse>

    companion object{
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
