package com.example.assignment2.data.model

// the repositories return this instead of throwing, so the viewmodels only have to
// check if it worked or not
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val reason: ErrorReason) : ApiResult<Nothing>()
}

// the different ways an api call can fail
enum class ErrorReason {
    WRONG_DETAILS,   // login rejected (wrong student id or first name)
    NOT_FOUND,       // the keypass/topic doesn't exist
    NO_CONNECTION,   // no internet or the server couldn't be reached
    SERVER,          // the server returned a 5xx error
    UNKNOWN          // anything else, like a response we couldn't read
}
