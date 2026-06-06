package com.androidtechguru.codelab.android.architecture

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ARCHITECTURE — Use Case / Interactor Pattern
 *
 * Key concepts:
 * 1. Single responsibility — one use case per business operation
 * 2. operator invoke() — call like a function: getArticles()
 * 3. Combines multiple repositories if needed
 * 4. Contains business logic that doesn't belong in ViewModel or Repository
 */

// ── Use Case: Get Articles ──
// Convention: one public method (invoke), one responsibility
class GetArticlesUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    // operator invoke lets you call: getArticlesUseCase() instead of getArticlesUseCase.execute()
    operator fun invoke(): Flow<List<Article>> {
        return repository.observeArticles()
    }
}

// ── Use Case: Get Single Article ──
class GetArticleUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(id: String): Result<Article> {
        // Business logic: validate input before calling repository
        if (id.isBlank()) {
            return Result.Failure(AppError.Unknown(IllegalArgumentException("ID cannot be blank")))
        }
        return repository.getArticle(id)
    }
}

// ── Use Case: Refresh Articles ──
class RefreshArticlesUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshArticles()
    }
}

// ── Use Case: Complex business logic with multiple repositories ──
// Example: creating an article requires both saving and updating user stats
class CreateArticleUseCase @Inject constructor(
    private val articleRepository: ArticleRepository,
    // private val userRepository: UserRepository,  // combine multiple repos
    // private val analyticsRepository: AnalyticsRepository,
) {
    suspend operator fun invoke(title: String, content: String, author: String): Result<Article> {
        // Business validation
        if (title.isBlank()) {
            return Result.Failure(AppError.Unknown(IllegalArgumentException("Title required")))
        }
        if (content.length < 10) {
            return Result.Failure(AppError.Unknown(IllegalArgumentException("Content too short")))
        }

        val article = Article(
            id = System.currentTimeMillis().toString(),
            title = title.trim(),
            content = content.trim(),
            author = author
        )

        // Orchestrate multiple operations
        return when (val saveResult = articleRepository.saveArticle(article)) {
            is Result.Success -> {
                // userRepository.incrementArticleCount(author)
                // analyticsRepository.trackArticleCreated(article.id)
                Result.Success(article)
            }
            is Result.Failure -> saveResult
        }
    }
}

// INTERVIEW TIP: When to use Use Cases?
// - When business logic is needed between ViewModel and Repository
// - When multiple repositories need coordination
// - When the same logic is used across multiple ViewModels
// - Skip if it's just a pass-through (ViewModel → Repository directly is fine then)
//
// Google's official guidance: Use Cases are OPTIONAL.
// Don't create them just for pass-through — that adds complexity without value.
