package com.example.market.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.R
import com.example.market.data.ListingRepository
import com.example.market.model.Category
import com.example.market.model.Condition
import com.example.market.model.Listing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.text.category

class ListingsViewModel(private val listingRepository: ListingRepository) : ViewModel() {

    private val _listings = MutableLiveData<List<Listing>>()
    val listings: LiveData<List<Listing>> = _listings
    private var _filteredListings = MutableLiveData<List<Listing>>()
    val filteredListings: LiveData<List<Listing>> = _filteredListings

    private val _showSortOverlay = MutableLiveData<Boolean>(false)
    val showSortOverlay: LiveData<Boolean> = _showSortOverlay

    private val _showFilterOverlay = MutableLiveData<Boolean>(false)
    val showFilterOverlay: LiveData<Boolean> = _showFilterOverlay

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

    val newListing = listOf(
        Listing(
            productName = "Old couch",
            category = Category.COUCHES,
            price = 199.99,
            condition = Condition.FAIR,
            id = 1,
            image = R.drawable.couch,
            modelPath = "models/couch.glb"
        ),
        Listing(
            productName = "Viking bed",
            category = Category.BEDS,
            price = 399.99,
            condition = Condition.NEW,
            id = 2,
            image = R.drawable.bed_model,
            modelPath = "models/bed.glb"
        ),
        Listing(
            productName = "Elegant bookcase",
            category = Category.BOOKCASES,
            price = 499.99,
            condition = Condition.NEW,
            id = 3,
            image = R.drawable.bookcase_model,
            modelPath = "models/bookcase.glb"
        ),
        Listing(
            productName = "Simple desk",
            category = Category.DESKS,
            price = 299.99,
            condition = Condition.USED,
            id = 4,
            image = R.drawable.desk,
            modelPath = "models/computer_desk.glb"
        ),
        Listing(
            productName = "Wood chair",
            category = Category.CHAIRS,
            price = 99.99,
            condition = Condition.USED,
            id = 5,
            image = R.drawable.chair,
            modelPath = "models/chair.glb"
        )
    )

    fun populateDatabase() {
        viewModelScope.launch {
            // Insert each Listing in the newListing list
            newListing.forEach { listing ->
                listingRepository.addListing(listing)
            }
            // Reload data so _listings LiveData is updated
            loadListings()
        }
    }

    fun loadListings() {
        viewModelScope.launch {
            _listings.value = listingRepository.getAllListings()
            _filteredListings.value = _listings.value
        }
    }

    fun addListing(listing: Listing) {
        viewModelScope.launch {
            listingRepository.addListing(listing)
            loadListings()
        }
    }

    fun onClick(navigate: (String) -> Unit, route: String) {
        navigate(route)
    }
}