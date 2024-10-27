package com.example.mobv.data.services

data class RegistrationResponse(val uid: String, val access: String, val refresh: String)
data class LoginResponse(val uid: String, val access: String, val refresh: String)