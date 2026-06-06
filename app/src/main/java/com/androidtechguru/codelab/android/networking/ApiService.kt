package com.androidtechguru.codelab.android.networking

import retrofit2.Response
import retrofit2.http.*

/**
 * NETWORKING — Retrofit API Service
 *
 * Key concepts:
 * 1. HTTP method annotations: @GET, @POST, @PUT, @DELETE, @PATCH
 * 2. Parameter annotations: @Path, @Query, @Body, @Header, @HeaderMap
 * 3. suspend functions for coroutine support
 * 4. Response<T> wrapper for accessing HTTP status codes and headers
 */

// ── DTOs (Data Transfer Objects) ──
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String? = null
)

data class CreateUserRequest(
    val name: String,
    val email: String
)

data class PostDto(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)

// ── API Service Interface ──
interface ApiService {

    // GET — fetch a list
    @GET("users")
    suspend fun getUsers(): List<UserDto>

    // GET with Response wrapper — access status code, headers, error body
    @GET("users")
    suspend fun getUsersWithResponse(): Response<List<UserDto>>

    // GET with path parameter — /users/1
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): UserDto

    // GET with query parameters — /posts?userId=1&_limit=10
    @GET("posts")
    suspend fun getUserPosts(
        @Query("userId") userId: Int,
        @Query("_limit") limit: Int = 10
    ): List<PostDto>

    // GET with query map — for dynamic/optional params
    @GET("posts")
    suspend fun searchPosts(@QueryMap filters: Map<String, String>): List<PostDto>

    // POST — create a resource
    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): Response<UserDto>

    // PUT — full update (replace entire resource)
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body user: UserDto
    ): Response<UserDto>

    // PATCH — partial update
    @PATCH("users/{id}")
    suspend fun patchUser(
        @Path("id") userId: Int,
        @Body fields: Map<String, @JvmSuppressWildcards Any>
    ): Response<UserDto>

    // DELETE
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") userId: Int): Response<Unit>

    // Custom headers
    @GET("users")
    suspend fun getUsersWithAuth(
        @Header("Authorization") token: String,
        @Header("Accept-Language") language: String = "en"
    ): List<UserDto>

    // @Headers for static headers
    @Headers(
        "Cache-Control: max-age=300",
        "X-Custom-Header: MyApp"
    )
    @GET("users")
    suspend fun getCachedUsers(): List<UserDto>
}

// INTERVIEW TIP:
// - Always use suspend functions with Retrofit (coroutine support built-in since Retrofit 2.6)
// - Use Response<T> when you need to check HTTP status or read error bodies
// - Use bare return type (no Response) when you only care about the success body
// - Retrofit throws HttpException for non-2xx when NOT using Response<T>
// - @Body serializes the object to JSON (via GsonConverter/MoshiConverter)
