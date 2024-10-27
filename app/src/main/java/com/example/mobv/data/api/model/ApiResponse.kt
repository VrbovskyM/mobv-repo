package com.example.mobv.data.api.model

data class RegistrationResponse(val uid: String, val access: String, val refresh: String)

data class LoginResponse(val uid: String, val access: String, val refresh: String)

data class UserResponse(val id: String, val name: String, val photo: String)

data class RefreshTokenResponse(val uid: String, val access: String, val refresh: String)

//
//
//{
//    "me": {"uid":"5","lat":"48.1555486000","lon":"17.0513821000","radius":"100"},
//    "list":[
//    {"uid":"5","radius":"100", "updated":"2023-10-28 16:24:34","name":"username",
//      "photo":"photo path without prefix, i.e. photo/1.jpeg  prefix is https://zadanie.mpage.sk/"
//      }
//    ]
//}
data class GeofenceResponse(
    val me: GeofenceMeResponse,
    val list: List<GeofenceUserResponse>
)

data class GeofenceUserResponse(
    val uid: String,
    val radius: Double,
    val updated: String,
    val name: String,
    val photo: String
)


data class GeofenceMeResponse(
    val uid: String,
    val lat: Double,
    val lon: Double,
    val radius: Double
)