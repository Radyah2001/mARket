package com.example.market.data

import com.example.market.model.Listing

class ListingRepositoryImpl(private val db: AppDatabase) : ListingRepository {

    private val listingDao = db.listingDao()

    override suspend fun getAllListings(): List<Listing> {
        return listingDao.getAllListings()
    }

    override suspend fun getListingById(id: Int): Listing? {
        return listingDao.getListingById(id)
    }

    override suspend fun addListing(listing: Listing) {
        listingDao.addListing(listing)
    }

    override suspend fun updateListing(listing: Listing) {
        listingDao.updateListing(listing)
    }

    override suspend fun deleteListing(listing: Listing) {
        listingDao.deleteListing(listing)
    }
}