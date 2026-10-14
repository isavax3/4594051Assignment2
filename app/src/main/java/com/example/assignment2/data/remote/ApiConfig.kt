package com.example.assignment2.data.remote

// everything about the nit3213 api that might need changing is kept in one place
object ApiConfig {
    const val BASE_URL = "https://nit3213apinew.onrender.com/"

    // my class location, used in the login url (/sydney/auth)
    const val CLASS_LOCATION = "sydney"

    // the api is on a free server that goes to sleep, so the first request can be slow
    const val TIMEOUT_SECONDS = 60L
}
