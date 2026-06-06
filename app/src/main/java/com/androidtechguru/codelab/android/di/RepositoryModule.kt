package com.androidtechguru.codelab.android.di

import com.androidtechguru.codelab.android.architecture.ArticleRepository
import com.androidtechguru.codelab.android.architecture.ArticleRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DI — Repository Bindings Module
 *
 * Key concepts:
 * 1. @Binds — tells Hilt which implementation to use for an interface
 * 2. abstract class (not object) — @Binds requires abstract functions
 * 3. @Singleton scope — one repository instance for the app
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // @Binds tells Hilt: when someone injects ArticleRepository, provide ArticleRepositoryImpl
    // More efficient than @Provides — no factory class generated
    // The implementation class must have @Inject constructor
    @Binds
    @Singleton
    abstract fun bindArticleRepository(
        impl: ArticleRepositoryImpl
    ): ArticleRepository

    // Add more bindings as needed:
    // @Binds abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
    // @Binds abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}

// INTERVIEW TIP: Why @Binds over @Provides for interfaces?
// 1. @Binds is more efficient — no wrapper function generated in bytecode
// 2. @Binds is more declarative — clearly shows "this implements that"
// 3. @Provides is needed for third-party types or complex creation logic
// 4. @Binds module must be abstract class; @Provides module should be object
