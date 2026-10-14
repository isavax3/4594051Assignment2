package com.example.assignment2.ui.common

import androidx.annotation.StringRes
import com.example.assignment2.R
import com.example.assignment2.data.model.ErrorReason

// picks the message to show the user for each kind of error
@StringRes
fun ErrorReason.toMessage(): Int = when (this) {
    ErrorReason.WRONG_DETAILS -> R.string.error_wrong_details
    ErrorReason.NOT_FOUND -> R.string.error_not_found
    ErrorReason.NO_CONNECTION -> R.string.error_no_connection
    ErrorReason.SERVER -> R.string.error_server
    ErrorReason.UNKNOWN -> R.string.error_unknown
}
