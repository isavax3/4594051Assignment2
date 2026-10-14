package com.example.assignment2.data.repository

import com.example.assignment2.data.model.ApiResult
import com.example.assignment2.data.model.ErrorReason
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

// runs an api call and catches the errors, so both repositories handle errors the same way
// (instead of copying the same try/catch into each one)
suspend fun <T> safeApiCall(
    httpErrorReason: (Int) -> ErrorReason = ::defaultHttpErrorReason,
    call: suspend () -> T
): ApiResult<T> {
    return try {
        ApiResult.Success(call())
    } catch (e: CancellationException) {
        // don't swallow this one, the coroutine was cancelled on purpose
        throw e
    } catch (e: HttpException) {
        ApiResult.Error(httpErrorReason(e.code()))
    } catch (e: IOException) {
        ApiResult.Error(ErrorReason.NO_CONNECTION)
    } catch (e: Exception) {
        ApiResult.Error(ErrorReason.UNKNOWN)
    }
}

fun defaultHttpErrorReason(code: Int): ErrorReason = when (code) {
    404 -> ErrorReason.NOT_FOUND
    in 500..599 -> ErrorReason.SERVER
    else -> ErrorReason.UNKNOWN
}
