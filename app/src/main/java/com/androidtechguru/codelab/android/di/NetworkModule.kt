package com.androidtechguru.codelab.android.di

import com.androidtechguru.codelab.android.networking.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * DI — Network Module
 *
 * Key concepts:
 * 1. @Provides for third-party classes (can't add @Inject to OkHttp/Retrofit)
 * 2. @Singleton — one Retrofit instance for the entire app
 * 3. OkHttpClient configuration (timeouts, interceptors, logging)
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            // Use BODY for development (shows request/response bodies)
            // Use NONE or BASIC for production (performance + security)
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        // authInterceptor: AuthInterceptor,       // add auth header
        // tokenAuthenticator: TokenAuthenticator,  // auto-refresh on 401
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        // Order matters for interceptors!
        // .addInterceptor(authInterceptor)        // application interceptor (runs once)
        .addInterceptor(loggingInterceptor)        // log after auth header added
        // .authenticator(tokenAuthenticator)       // handles 401 responses
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}

// INTERVIEW TIP — @Provides vs @Binds:
//
// @Provides → for types you DON'T own (can't add @Inject)
//   - Retrofit, OkHttpClient, Room Database, third-party classes
//   - Must be in 'object' module (concrete function)
//
// @Binds → for types you DO own (interface → implementation)
//   - Repository interface → RepositoryImpl
//   - Must be in 'abstract class' module (abstract function)
//   - More efficient: doesn't generate a factory class
