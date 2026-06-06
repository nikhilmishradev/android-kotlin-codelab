package com.androidtechguru.codelab.android.storage

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * STORAGE — Room Database
 *
 * Key concepts:
 * 1. @Database — defines entities and version
 * 2. Migration — upgrade database schema without data loss
 * 3. TypeConverters — store custom types in Room
 */
@Database(
    entities = [
        UserEntity::class,
        ContactEntity::class
    ],
    version = 2,  // increment when schema changes
    exportSchema = true  // generates schema JSON for auto-migration validation
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "codelab_database"
    }
}

// ── Type Converters ──
// Room only supports primitive types natively
// Use @TypeConverter for custom types
class Converters {

    // Store List<String> as comma-separated string
    @TypeConverter
    fun fromStringList(list: List<String>?): String? = list?.joinToString(",")

    @TypeConverter
    fun toStringList(data: String?): List<String>? = data?.split(",")?.filter { it.isNotEmpty() }

    // Store enum as string
    // @TypeConverter
    // fun fromStatus(status: Status): String = status.name
    //
    // @TypeConverter
    // fun toStatus(value: String): Status = Status.valueOf(value)
}

// ── Manual Migration ──
// Use when auto-migration can't handle the change (rename, complex transform)
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add a new column to existing table
        db.execSQL("ALTER TABLE users ADD COLUMN status TEXT NOT NULL DEFAULT 'active'")

        // Create a new table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS contacts (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                street TEXT NOT NULL,
                city TEXT NOT NULL,
                zipCode TEXT NOT NULL
            )
        """)
    }
}

// ── Database Provider (in Hilt Module) ──
// @Module
// @InstallIn(SingletonComponent::class)
// object DatabaseModule {
//     @Provides
//     @Singleton
//     fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
//         Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
//             .addMigrations(MIGRATION_1_2)
//             // .fallbackToDestructiveMigration()  // DESTROYS data on failed migration!
//             .build()
//
//     @Provides
//     fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
// }

// INTERVIEW TIP — Migrations:
// - ALWAYS provide migrations in production (users have data!)
// - fallbackToDestructiveMigration() — only for debug/development
// - Auto-migration (Room 2.4+): @AutoMigration(from = 1, to = 2)
//   Works for simple changes (add column, add table)
//   Doesn't work for: rename, delete, complex transforms
// - Test migrations with MigrationTestHelper
