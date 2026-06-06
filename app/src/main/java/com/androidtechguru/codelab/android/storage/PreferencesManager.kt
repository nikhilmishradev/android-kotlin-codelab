package com.androidtechguru.codelab.android.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * STORAGE — DataStore Preferences
 *
 * Key concepts:
 * 1. DataStore vs SharedPreferences (DataStore is the modern replacement)
 * 2. Flow-based reading — reactive, no blocking
 * 3. Type-safe keys with Preferences.Key<T>
 * 4. Transactional writes with edit {}
 *
 * DataStore advantages over SharedPreferences:
 * - Asynchronous API (Flow + suspend) — no ANR risk
 * - Type-safe keys
 * - Transactional writes (no partial updates)
 * - No runtime exceptions from parsing
 * - Safe to call on UI thread
 */

// Extension property to create DataStore instance
// Must be a top-level property (one instance per file)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_preferences"
)

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // ── Type-safe keys ──
    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val FONT_SIZE = intPreferencesKey("font_size")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val LAST_SYNC = longPreferencesKey("last_sync")
    }

    // ── Read — Flow-based (reactive) ──

    val theme: Flow<String> = context.dataStore.data
        .catch { exception ->
            // Handle read errors (corrupted file, etc.)
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[Keys.THEME] ?: "system"  // default value
        }

    val isOnboarded: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.IS_ONBOARDED] ?: false }

    val fontSize: Flow<Int> = context.dataStore.data
        .map { it[Keys.FONT_SIZE] ?: 16 }

    // ── Write — Transactional (suspend) ──

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.THEME] = theme
        }
    }

    suspend fun setOnboarded(onboarded: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.IS_ONBOARDED] = onboarded
        }
    }

    suspend fun setFontSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.FONT_SIZE] = size
        }
    }

    suspend fun setAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.AUTH_TOKEN] = token
        }
    }

    // ── Batch write — multiple values atomically ──
    suspend fun updateSettings(theme: String, fontSize: Int) {
        context.dataStore.edit { preferences ->
            // All writes in edit {} are ATOMIC — either all succeed or none
            preferences[Keys.THEME] = theme
            preferences[Keys.FONT_SIZE] = fontSize
        }
    }

    // ── Clear all preferences ──
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}

// INTERVIEW TIP — DataStore vs SharedPreferences:
//
// ┌──────────────────────┬──────────────────┬──────────────────┐
// │ Feature              │ SharedPreferences │ DataStore        │
// ├──────────────────────┼──────────────────┼──────────────────┤
// │ API                  │ Synchronous       │ Flow + suspend   │
// │ Thread safety        │ Can cause ANR     │ Safe on any      │
// │ Error handling       │ Runtime exceptions│ Flow catch       │
// │ Type safety          │ String keys       │ Typed keys       │
// │ Transactional writes │ No                │ Yes (edit{})     │
// │ Migration            │ N/A               │ Built-in         │
// └──────────────────────┴──────────────────┴──────────────────┘
