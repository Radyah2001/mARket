package com.example.market.data

import com.example.market.model.Listing

interface ListingRepository {
    suspend fun getAllListings(): List<Listing>
    suspend fun getListingById(id: Int): Listing?
    suspend fun addListing(listing: Listing)
    suspend fun addListingReturnId(listing: Listing): Long
    suspend fun updateListing(listing: Listing)
    suspend fun deleteListing(listing: Listing)
}