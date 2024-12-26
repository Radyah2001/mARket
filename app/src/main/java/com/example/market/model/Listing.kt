package com.example.market.model

import android.media.Image

data class Listing(
    val id: Int, // Unique identifier for the listing
    val productName: String,
    val category: String,
    val price: Double,
    val condition: String,
    val image: Image
)