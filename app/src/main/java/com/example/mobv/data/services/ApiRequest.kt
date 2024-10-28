package com.example.mobv.data.services

data class UserRegistration(val name: String, val email: String, val password: String)
data class UserLogin(val email: String, val name: String, val password: String)
