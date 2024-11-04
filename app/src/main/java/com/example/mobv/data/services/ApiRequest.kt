package com.example.mobv.data.services

data class UserRegistration(val name: String, val email: String, val password: String)
data class UserLogin(val name: String, val password: String)
data class RefreshTokenRequest(val refresh: String)
data class newPasswordRequest(val old_password: String, val new_password: String)
data class resetPasswordRequest(val email: String)
data class UpdateUserLocationRequest(val lat: Double, val lon: Double, val radius: Double)