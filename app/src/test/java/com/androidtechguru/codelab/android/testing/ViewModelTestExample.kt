package com.androidtechguru.codelab.android.testing

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.*
import com.androidtechguru.codelab.android.architecture.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*

/**
 * TESTING — ViewModel Test with JUnit5, Turbine, AssertK
 *
 * Key concepts:
 * 1. UnconfinedTestDispatcher — coroutines run eagerly (no delays)
 * 2. Turbine — test Flow emissions with awaitItem()
 * 3. Fakes over Mocks — simpler, more predictable, no framework needed
 * 4. AAA pattern — Arrange, Act, Assert
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTestExample {

    // Test dispatcher — runs coroutines immediately (no actual delay)
    private val testDispatcher = UnconfinedTestDispatcher()

    // Fake repository — simpler and more predictable than mocks
    private lateinit var fakeRepository: FakeArticleRepository

    @BeforeEach
    fun setup() {
        // Set Main dispatcher to test dispatcher
        // This is REQUIRED because viewModelScope uses Dispatchers.Main
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeArticleRepository()
    }

    @AfterEach
    fun tearDown() {
        // Reset Main dispatcher after each test
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        // Arrange — create use cases with fake repo
        val getArticles = GetArticlesUseCase(fakeRepository)
        val refreshArticles = RefreshArticlesUseCase(fakeRepository)
        val createArticle = CreateArticleUseCase(fakeRepository)

        // Act — create ViewModel
        val viewModel = ArchitectureViewModel(getArticles, refreshArticles, createArticle)

        // Assert — verify initial state
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(UiState.Success::class.java)
        }
    }

    @Test
    fun `refresh emits snackbar event on success`() = runTest {
        // Arrange
        val viewModel = createViewModel()

        // Act & Assert — test one-time events with Turbine
        viewModel.events.test {
            viewModel.onAction(ArticleAction.Refresh)
            val event = awaitItem()
            assertThat(event).isInstanceOf(UiEvent.ShowSnackbar::class.java)
            assertThat((event as UiEvent.ShowSnackbar).message).isEqualTo("Refreshed!")
        }
    }

    @Test
    fun `refresh emits error event on failure`() = runTest {
        // Arrange
        fakeRepository.shouldFail = true
        val viewModel = createViewModel()

        // Act & Assert
        viewModel.events.test {
            viewModel.onAction(ArticleAction.Refresh)
            val event = awaitItem()
            assertThat(event).isInstanceOf(UiEvent.ShowSnackbar::class.java)
            assertThat((event as UiEvent.ShowSnackbar).message).contains("Network error")
        }
    }

    // Helper
    private fun createViewModel(): ArchitectureViewModel {
        return ArchitectureViewModel(
            getArticles = GetArticlesUseCase(fakeRepository),
            refreshArticles = RefreshArticlesUseCase(fakeRepository),
            createArticle = CreateArticleUseCase(fakeRepository)
        )
    }
}

// ═══════════════════════════════════════════
// FAKE REPOSITORY — better than mocking
// ═══════════════════════════════════════════

/**
 * Fake implementation for testing.
 *
 * Why fakes over mocks?
 * 1. No framework dependency (MockK, Mockito)
 * 2. Reusable across many test classes
 * 3. Simpler to understand — it's just code
 * 4. More predictable — no verification order issues
 * 5. Catches interface contract violations (mocks always "pass")
 */
class FakeArticleRepository : ArticleRepository {

    var shouldFail = false
    val savedArticles = mutableListOf<Article>()
    var articles = listOf(
        Article("1", "Test Article", "Content", "Author")
    )

    override fun observeArticles(): Flow<List<Article>> = flow {
        emit(articles)
    }

    override suspend fun getArticle(id: String): Result<Article> {
        if (shouldFail) return Result.Failure(AppError.Network("Not found", 404))
        val article = articles.find { it.id == id }
        return if (article != null) Result.Success(article)
        else Result.Failure(AppError.Network("Not found", 404))
    }

    override suspend fun refreshArticles(): Result<Unit> {
        if (shouldFail) return Result.Failure(AppError.Network("Network error"))
        return Result.Success(Unit)
    }

    override suspend fun saveArticle(article: Article): Result<Unit> {
        if (shouldFail) return Result.Failure(AppError.Database("Save failed"))
        savedArticles.add(article)
        return Result.Success(Unit)
    }
}

// INTERVIEW TIP — Testing Best Practices:
//
// 1. Use UnconfinedTestDispatcher for ViewModel tests (eager execution)
// 2. Use Turbine for Flow testing (awaitItem, expectNoEvents, cancel)
// 3. Prefer Fakes over Mocks for repositories
// 4. Always set/reset Dispatchers.Main in setup/teardown
// 5. Test behavior, not implementation — test what the ViewModel does, not how
// 6. Name tests descriptively: `action produces expected result`
