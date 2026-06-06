package com.androidtechguru.codelab.android.networking

import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * NETWORKING — Safe API Call Pattern
 *
 * Key concepts:
 * 1. Sealed class for network results (Success, Error, Exception)
 * 2. safeApiCall helper — catches exceptions, returns structured result
 * 3. No exceptions leak to ViewModel — everything is a NetworkResult
 */

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()

    // HTTP error (4xx, 5xx) — server returned an error response
    data class Error(
        val code: Int,
        val message: String,
        val errorBody: String? = null
    ) : NetworkResult<Nothing>()

    // Exception — network failure, timeout, parsing error, etc.
    data class Exception(
        val throwable: Throwable
    ) : NetworkResult<Nothing>()
}

// ── Safe API Call — wraps any suspend API call ──
suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): NetworkResult<T> {
    return try {
        val response = apiCall()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error(response.code(), "Empty response body")
            }
        } else {
            NetworkResult.Error(
                code = response.code(),
                message = response.message(),
                errorBody = response.errorBody()?.string()
            )
        }
    } catch (e: HttpException) {
        NetworkResult.Error(
            code = e.code(),
            message = e.message()
        )
    } catch (e: IOException) {
        // Network failure (no internet, timeout, DNS failure)
        NetworkResult.Exception(e)
    } catch (e: kotlin.Exception) {
        // Unexpected error (parsing, etc.)
        NetworkResult.Exception(e)
    }
}

// ── Convenience extension for direct (non-Response) API calls ──
suspend fun <T> safeCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(apiCall())
    } catch (e: HttpException) {
        NetworkResult.Error(e.code(), e.message())
    } catch (e: IOException) {
        NetworkResult.Exception(e)
    } catch (e: kotlin.Exception) {
        NetworkResult.Exception(e)
    }
}

// ── Usage in Repository ──
// suspend fun getUsers(): NetworkResult<List<UserDto>> =
//     safeApiCall { apiService.getUsersWithResponse() }
//
// suspend fun getUser(id: Int): NetworkResult<UserDto> =
//     safeCall { apiService.getUser(id) }

// INTERVIEW TIP: Why wrap API calls?
// 1. Prevents crash from uncaught exceptions in ViewModel
// 2. Forces callers to handle all cases (Success/Error/Exception)
// 3. Separates HTTP errors from network failures
// 4. Single place to add logging, analytics, retry logic
