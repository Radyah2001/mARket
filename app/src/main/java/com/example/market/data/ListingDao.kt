package com.example.market.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.market.model.Listing

@Dao
interface ListingDao {

    @Query("SELECT * FROM listings")
    suspend fun getAllListings(): List<Listing>

    @Query("SELECT * FROM listings WHERE id = :id")
    suspend fun getListingById(id: Int): Listing?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addListing(listing: Listing)

    @Update
    suspend fun updateListing(listing: Listing)

    @Delete
    suspend fun deleteListing(listing: Listing)
}