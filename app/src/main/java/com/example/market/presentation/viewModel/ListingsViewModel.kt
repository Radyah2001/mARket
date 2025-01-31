package com.example.market.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.ListingRepository
import com.example.market.model.Category
import com.example.market.model.Condition
import com.example.market.model.Listing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListingsViewModel(private val listingRepository: ListingRepository) : ViewModel() {

    private val _listings = MutableLiveData<List<Listing>>()
    val listings: LiveData<List<Listing>> = _listings
    private var _filteredListings = MutableLiveData<List<Listing>>()
    val filteredListings: LiveData<List<Listing>> = _filteredListings

    private val _showSortOverlay = MutableLiveData<Boolean>(false)
    val showSortOverlay: LiveData<Boolean> = _showSortOverlay

    private val _showFilterOverlay = MutableLiveData<Boolean>(false)
    val showFilterOverlay: LiveData<Boolean> = _showFilterOverlay

    private val _listing = MutableLiveData<Listing>()
    val listing: LiveData<Listing> = _listing


    fun filterListings(currentCategory: Category?, currentCondition: Condition?) {

        viewModelScope.launch {
            val listings = listingRepository.getAllListings()
            withContext(Dispatchers.Main) {
                _filteredListings.value = if (currentCategory == null && currentCondition == null) {
                    listings // Show all listings if both are null
                } else {
                    listings.filter { listing ->
                        (currentCategory == null || listing.category == currentCategory) && // Use || for category
                                (currentCondition == null || listing.condition == currentCondition) // Use || for condition
                    }
                }
            }
        }
    }

    fun sortListings(ascending: Boolean) { // Sort by price
        viewModelScope.launch {
            val listings = listingRepository.getAllListings()
            _filteredListings.value = if (ascending) {
                listings.sortedBy { it.price }
            } else {
                listings.sortedByDescending { it.price }
            }
        }
    }

    fun toggleSortOverlay() {
        _showSortOverlay.value = _showSortOverlay.value != true
    }

    fun toggleFilterOverlay() {
        _showFilterOverlay.value = _showFilterOverlay.value != true
    }

//    val newListing = listOf(
//        Listing(
//            productName = "Old couch",
//            category = Category.COUCHES,
//            price = 199.99,
//            condition = Condition.FAIR,
//            id = 1,
//            image = R.drawable.couch,
//            modelPath = "models/old_couch.glb"
//        ),
//        Listing(
//            productName = "Viking bed",
//            category = Category.BEDS,
//            price = 399.99,
//            condition = Condition.NEW,
//            id = 2,
//            image = R.drawable.bed_model,
//            modelPath = "models/bed.glb"
//        ),
//        Listing(
//            productName = "Elegant bookcase",
//            category = Category.BOOKCASES,
//            price = 499.99,
//            condition = Condition.NEW,
//            id = 3,
//            image = R.drawable.bookcase_model,
//            modelPath = "models/bookcase.glb"
//        ),
//        Listing(
//            productName = "Simple desk",
//            category = Category.DESKS,
//            price = 299.99,
//            condition = Condition.USED,
//            id = 4,
//            image = R.drawable.desk,
//            modelPath = "models/computer_desk.glb"
//        ),
//        Listing(
//            productName = "Wood chair",
//            category = Category.CHAIRS,
//            price = 99.99,
//            condition = Condition.USED,
//            id = 5,
//            image = R.drawable.chair,
//            modelPath = "models/chair.glb"
//        )
//    )
//
//    init {
//        populateDatabase()
//    }
//
//    fun populateDatabase() {
//        viewModelScope.launch {
//            val existingListings = listingRepository.getAllListings()
//
//            // Check if new listings are identical to existing listings
//            if (newListing == existingListings) {
//                return@launch // Do nothing if listings are the same
//            }
//
//            // Otherwise, proceed with adding/updating listings
//            newListing.forEach { newListing ->
//                val existingListing = existingListings.find { it.id == newListing.id }
//                if (existingListing == null) {
//                    listingRepository.addListing(newListing)
//                } else {
//                    // Existing listing, move it to the top (if needed)
//                    val currentList = _listings.value.orEmpty().toMutableList()
//                    val existingIndex = currentList.indexOf(existingListing)
//                    if (existingIndex != -1 && existingIndex != 0) {
//                        currentList.removeAt(existingIndex)
//                        currentList.add(0, existingListing)
//                        _listings.value = currentList
//                    }
//                }
//            }
//            loadListings()
//        }
//    }

    fun loadListings() {
        viewModelScope.launch {
            _listings.value = listingRepository.getAllListings()
            _filteredListings.value = _listings.value
        }
    }

//    fun addListing(
//        productName: String,
//        category: Category,
//        price: Double,
//        condition: Condition,
//        image: Int,
//    ) {
//        viewModelScope.launch {
//            val listing = Listing(
//                productName = productName,
//                category = category,
//                price = price,
//                condition = condition,
//                image = image,
//            )
//            listingRepository.addListing(listing)
//            loadListings()
//        }
//    }

    fun deleteListing(listing: Listing) {
        viewModelScope.launch {
            listingRepository.deleteListing(listing)
            loadListings()
        }
    }

    fun onClick(navigate: (String) -> Unit, route: String) {
        navigate(route)
    }
}