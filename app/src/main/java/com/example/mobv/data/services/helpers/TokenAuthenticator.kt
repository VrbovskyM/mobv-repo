package com.example.mobv.data.services.helpers

import android.content.Context
import com.example.mobv.data.PreferenceData
import com.example.mobv.data.models.User
import com.example.mobv.data.services.ApiService
import com.example.mobv.data.services.RefreshTokenRequest
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: okhttp3.Response): Request? {

        val requestPath = response.request.url.toUrl().path.lowercase()

        // Skip authorization if endpoint is for user creation, login, or refresh
        if (requestPath.contains("/user/create.php")  ||
            requestPath.contains("/user/login.php")   ||
            requestPath.contains("/user/refresh.php") ||
            requestPath.contains("/user/reset.php")
        ) {
            return null
        }

        // Refresh token if we received a 401 Unauthorized response
        if (response.code == 401) {
            val userItem = PreferenceData.getInstance().getUser()
            userItem?.let { user ->
                val tokenResponse = ApiService.create(context).refreshTokenBlocking(
                    RefreshTokenRequest(user.refresh)
                ).execute()

                if (tokenResponse.isSuccessful) {
                    tokenResponse.body()?.let { newToken ->
                        val updatedUser =
                            userItem.copy(access = newToken.access, refresh = newToken.refresh)
                        PreferenceData.getInstance().putUser(updatedUser)

                        // Retry the request with the new access token
                        return response.request.newBuilder()
                            .header("Authorization", "Bearer ${updatedUser.access}")
                            .build()
                    }
                }

                // Clear user data if token refresh failed
                PreferenceData.getInstance().clearData()
            }
        }
        return null
    }
}