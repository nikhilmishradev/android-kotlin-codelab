package com.androidtechguru.codelab.android.storage

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * STORAGE — Room DAO (Data Access Object)
 *
 * Key concepts:
 * 1. @Query with Flow — reactive data observation
 * 2. suspend functions for one-shot operations
 * 3. @Transaction for multi-table operations
 * 4. Conflict strategies for inserts/updates
 */
@Dao
interface UserDao {

    // ── READ operations ──

    // Flow-based query — emits new list whenever data changes
    // UI collects this and automatically updates
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun observeAll(): Flow<List<UserEntity>>

    // Flow for single item
    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeById(userId: String): Flow<UserEntity?>

    // Suspend — one-shot query (doesn't observe changes)
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getById(userId: String): UserEntity?

    // Search with LIKE
    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<UserEntity>>

    // Count
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    // ── WRITE operations ──

    // Insert with conflict strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)  // overwrites if same PK
    suspend fun insert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)    // skips if same PK exists
    suspend fun insertIfNotExists(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    // Update
    @Update
    suspend fun update(user: UserEntity)

    // Partial update via query
    @Query("UPDATE users SET status = :status WHERE id = :userId")
    suspend fun updateStatus(userId: String, status: String)

    // Delete
    @Delete
    suspend fun delete(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteById(userId: String)

    @Query("DELETE FROM users")
    suspend fun deleteAll()

    // ── Transaction — atomic multi-operation ──
    // If any operation fails, ALL are rolled back
    @Transaction
    suspend fun replaceAll(users: List<UserEntity>) {
        deleteAll()
        insertAll(users)
    }
}

// INTERVIEW TIP — Room + Flow:
//
// Query returns Flow<T>:
//   - Automatically re-emits when underlying table changes
//   - No manual refresh needed — truly reactive
//   - Pair with collectAsStateWithLifecycle in Compose
//
// Query returns suspend T:
//   - One-shot query, no observation
//   - Use for: existence checks, counts, writes
//
// @Transaction:
//   - Ensures atomicity (all-or-nothing)
//   - Required for @Relation queries (prevents inconsistent reads)
//   - Use for: delete + insert (refresh), multi-table writes
