package com.example.assignment2.data.remote.dto

// body of the login request. username is the student id and password is the first name
data class LoginRequest(
    val username: String,
    val password: String
)
