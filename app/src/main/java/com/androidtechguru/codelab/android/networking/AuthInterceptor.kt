package com.androidtechguru.codelab.android.networking

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * NETWORKING — OkHttp Auth Interceptor
 *
 * Key concepts:
 * 1. Application interceptor vs Network interceptor
 * 2. Adding auth headers to every request
 * 3. Skipping auth for specific endpoints
 *
 * APPLICATION Interceptor (.addInterceptor):
 *   - Called ONCE per request
 *   - Sees the original request (before redirects)
 *   - Can short-circuit and return a cached response
 *   - Use for: auth headers, logging, retry logic
 *
 * NETWORK Interceptor (.addNetworkInterceptor):
 *   - Called for EVERY network request (including redirects)
 *   - Sees the actual network request
 *   - Use for: network-level caching, compression
 */
class AuthInterceptor @Inject constructor(
    // In real app: inject TokenManager or DataStore to get current token
    // private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth for login/register endpoints
        if (originalRequest.url.encodedPath.contains("auth/login") ||
            originalRequest.url.encodedPath.contains("auth/register")
        ) {
            return chain.proceed(originalRequest)
        }

        // Get the current access token
        val token = getAccessToken()

        // Add Authorization header if token exists
        val authenticatedRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Content-Type", "application/json")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(authenticatedRequest)
    }

    private fun getAccessToken(): String? {
        // In real app: tokenManager.getAccessToken()
        return "sample_access_token"
    }
}

// INTERVIEW TIP — Interceptor Chain Order:
//
// Request flows:  App Interceptor → Network Interceptor → Server
// Response flows: Server → Network Interceptor → App Interceptor
//
// Add interceptors in this order:
// 1. AuthInterceptor (add token)
// 2. LoggingInterceptor (log with token visible for debugging)
// 3. CacheInterceptor (if custom caching)
