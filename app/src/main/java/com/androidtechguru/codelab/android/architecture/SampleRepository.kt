package com.androidtechguru.codelab.android.architecture

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * ARCHITECTURE — Repository Pattern
 *
 * Key concepts:
 * 1. Interface + Implementation for testability (swap with fake in tests)
 * 2. Single source of truth — local DB is the source, API syncs to it
 * 3. Flow-based observation — UI reacts to data changes
 * 4. Error handling with Result wrapper (no exceptions leaking to ViewModel)
 */

// ── Data models ──
data class Article(
    val id: String,
    val title: String,
    val content: String,
    val author: String
)

// ── Repository Interface ──
// Defined in domain layer — implementation in data layer
// ViewModel depends on this interface, NOT the implementation (dependency inversion)
interface ArticleRepository {
    fun observeArticles(): Flow<List<Article>>
    suspend fun getArticle(id: String): Result<Article>
    suspend fun refreshArticles(): Result<Unit>
    suspend fun saveArticle(article: Article): Result<Unit>
}

// ── Repository Implementation ──
class ArticleRepositoryImpl @Inject constructor(
    // In real app: inject ApiService, Dao, Dispatchers
    // private val api: ArticleApi,
    // private val dao: ArticleDao,
    // @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ArticleRepository {

    // Simulated local cache (in real app: Room DAO)
    private val cache = mutableListOf(
        Article("1", "Kotlin Coroutines", "Deep dive into coroutines...", "Alice"),
        Article("2", "Jetpack Compose", "Building UIs with Compose...", "Bob"),
        Article("3", "Hilt DI", "Dependency injection made easy...", "Charlie"),
    )

    // ── Single Source of Truth pattern ──
    // UI observes LOCAL data (Room DAO returns Flow)
    // Refresh fetches from API → saves to DB → Flow automatically emits new data
    override fun observeArticles(): Flow<List<Article>> = flow {
        // In real app: dao.observeAll() returns Flow<List<ArticleEntity>>
        emit(cache.toList())
    }

    override suspend fun getArticle(id: String): Result<Article> {
        return try {
            // In real app: check local first, then API
            val article = cache.find { it.id == id }
            if (article != null) {
                Result.Success(article)
            } else {
                Result.Failure(AppError.Network("Article not found", 404))
            }
        } catch (e: Exception) {
            Result.Failure(AppError.Unknown(e))
        }
    }

    override suspend fun refreshArticles(): Result<Unit> {
        return try {
            // In real app:
            // 1. val articles = api.getArticles()  ← fetch from network
            // 2. dao.insertAll(articles.toEntities()) ← save to local DB
            // 3. The Flow from observeArticles() auto-emits new data!
            delay(500) // simulate network
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(AppError.Network(e.message ?: "Network error"))
        }
    }

    override suspend fun saveArticle(article: Article): Result<Unit> {
        return try {
            cache.add(article)
            // In real app: dao.insert(article.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(AppError.Database(e.message ?: "Save failed"))
        }
    }
}

// INTERVIEW TIP: Repository pattern benefits:
// 1. Abstracts data sources (API, DB, cache) from business logic
// 2. Single source of truth prevents data inconsistency
// 3. Interface enables testing with fakes (no mocking needed)
// 4. Flow-based observation = reactive UI updates
