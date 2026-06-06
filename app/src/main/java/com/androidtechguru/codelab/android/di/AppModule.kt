package com.androidtechguru.codelab.android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * DI — App Module (Dispatchers & App-wide dependencies)
 *
 * Key concepts:
 * 1. Custom qualifiers for dispatcher injection
 * 2. @Provides for types you don't own (can't annotate with @Inject)
 * 3. @Singleton scope — one instance for entire app
 */

// ── Custom Qualifiers ──
// Qualifiers disambiguate when multiple bindings of the same type exist
// Better than @Named("io") — type-safe, refactorable, IDE-friendly
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Module
@InstallIn(SingletonComponent::class)  // lives as long as the Application
object AppModule {

    // ── Dispatcher injection ──
    // Why inject dispatchers? So tests can use TestDispatcher!
    // Without this, you'd hardcode Dispatchers.IO and can't test main-safety

    @IoDispatcher
    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @DefaultDispatcher
    @Provides
    @Singleton
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @MainDispatcher
    @Provides
    @Singleton
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}

// INTERVIEW TIP — Hilt Components & Scopes:
//
// ┌─────────────────────┬───────────────────┬──────────────────────────┐
// │ Component            │ Scope             │ Lifetime                 │
// ├─────────────────────┼───────────────────┼──────────────────────────┤
// │ SingletonComponent   │ @Singleton        │ Application              │
// │ ViewModelComponent   │ @ViewModelScoped  │ ViewModel                │
// │ ActivityComponent    │ @ActivityScoped   │ Activity                 │
// │ FragmentComponent    │ @FragmentScoped   │ Fragment                 │
// │ ServiceComponent     │ @ServiceScoped    │ Service                  │
// └─────────────────────┴───────────────────┴──────────────────────────┘
//
// Default (no scope) = new instance every injection
// @Singleton = one instance for whole app
// @ViewModelScoped = one per ViewModel (useful for shared deps within a screen)
