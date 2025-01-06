package com.example.market.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listings")
data class Listing(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val productName: String,
    val category: Category,
    val price: Double,
    val condition: Condition,
    val image: Int,
    val modelPath: String? = null
)

enum class Category {
    CHAIRS,
    TABLES,
    BEDS,
    DESKS,
    DRESSERS,
    COUCHES,
    BOOKCASES
}

enum class Condition {
    NEW,
    USED,
    FAIR
}