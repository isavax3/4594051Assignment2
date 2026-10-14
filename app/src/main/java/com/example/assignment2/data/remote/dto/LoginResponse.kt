package com.example.assignment2.data.remote.dto

// a successful login gives back a keypass, which is the topic name used for the dashboard
data class LoginResponse(
    val keypass: String
)
