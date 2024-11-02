package com.example.mobv.data.services.helpers

import com.example.mobv.config.AppConfig
import com.example.mobv.data.PreferenceData
import okhttp3.Interceptor
import okhttp3.Response
import android.content.Context

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")

        val requestPath = chain.request().url.toUrl().path.lowercase()
        val user = PreferenceData.getInstance().getUser()

        // No authorization needed for user creation or login
        if (requestPath.contains("/user/create.php") ||
            requestPath.contains("/user/login.php")  ||
            requestPath.contains("/user/reset.php")) {
            // No additional headers needed
        }
        // Add user ID header for token refresh
        else if (requestPath.contains("/user/refresh.php")) {
            user?.id?.let { userId ->
                requestBuilder.header("x-user", userId)
            }
        }
        // Add authorization token for other requests
        else {
            user?.access?.let { token ->
                requestBuilder.header("Authorization", "Bearer $token")
            }
        }

        // Add API key to all requests
        requestBuilder.addHeader("x-apikey", AppConfig.API_KEY)

        return chain.proceed(requestBuilder.build())
    }
}