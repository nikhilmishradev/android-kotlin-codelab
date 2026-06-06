package com.androidtechguru.codelab.android.storage

import androidx.room.*

/**
 * STORAGE — Room Entity
 *
 * Key concepts:
 * 1. @Entity — maps to a database table
 * 2. @PrimaryKey — unique identifier
 * 3. @ColumnInfo — customize column name
 * 4. @Ignore — exclude field from database
 * 5. Indices for query optimization
 * 6. TypeConverters for custom types
 */

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),  // unique index on email
        Index(value = ["name"])                     // regular index for search
    ]
)
data class UserEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "name")  // explicit column name (defaults to field name)
    val name: String,

    val email: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(defaultValue = "active")
    val status: String = "active",

    // @Ignore — not stored in database
    // Useful for transient/computed fields
    @Ignore
    val isSelected: Boolean = false
) {
    // Room needs a constructor without @Ignore fields
    // This secondary constructor is used by Room internally
    constructor(id: String, name: String, email: String, createdAt: Long, status: String)
        : this(id, name, email, createdAt, status, false)
}

// ── Embedded object — flattens nested object into parent table ──
data class Address(
    val street: String,
    val city: String,
    val zipCode: String
)

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: String,
    val name: String,

    // @Embedded flattens Address fields into contacts table
    // Columns: street, city, zipCode (no separate table!)
    @Embedded
    val address: Address
)

// INTERVIEW TIP:
// @Embedded — flattens object into same table (no join needed)
// @Relation — links to another table (requires @Transaction for consistency)
// Use @Embedded for value objects, @Relation for actual entity relationships
