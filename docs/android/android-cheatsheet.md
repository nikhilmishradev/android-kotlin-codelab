# Android Cheatsheet

Quick reference for Android components, APIs, and patterns.

---

## Activity Lifecycle

```
onCreate() → onStart() → onResume()
                ↕
         onPause() → onStop() → onDestroy()
                        ↓
                   onRestart() → onStart()
```

## Fragment Lifecycle

```
onAttach() → onCreate() → onCreateView() → onViewCreated() → onStart() → onResume()
                                                                  ↕
                                              onPause() → onStop() → onDestroyView() → onDestroy() → onDetach()
```

## ViewModel

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()

    fun onAction(action: MyAction) {
        viewModelScope.launch {
            // handle action
        }
    }
}
```

## UI State Pattern

```kotlin
sealed interface MyUiState {
    data object Loading : MyUiState
    data class Success(val data: List<Item>) : MyUiState
    data class Error(val message: String) : MyUiState
}
```

## Hilt Setup

```kotlin
@HiltAndroidApp
class MyApp : Application()

@AndroidEntryPoint
class MyActivity : ComponentActivity()

@HiltViewModel
class MyViewModel @Inject constructor(...) : ViewModel()

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepo(impl: MyRepositoryImpl): MyRepository
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
```

## Retrofit

```kotlin
interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): Response<UserDto>

    @POST("users")
    suspend fun createUser(@Body user: CreateUserRequest): Response<UserDto>

    @GET("users")
    suspend fun searchUsers(@Query("name") name: String): Response<List<UserDto>>
}
```

## Room

```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String
)

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun observeAll(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)
}

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
```

## WorkManager

```kotlin
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // do background work
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

// Enqueue
val request = OneTimeWorkRequestBuilder<SyncWorker>()
    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
    .build()
WorkManager.getInstance(context).enqueue(request)
```

## Coroutines in Android

```kotlin
// ViewModel scope — auto-cancelled on ViewModel clear
viewModelScope.launch { }

// Lifecycle scope — collect flows safely
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState.collect { state -> }
    }
}
```

---

> Add your own patterns and snippets as you practice.
