package com.example.market.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.market.model.Category
import com.example.market.model.Condition
import com.example.market.model.Listing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ListingSharedViewModel : ViewModel() {

    private val _selectedListing = MutableStateFlow<Listing?>(null)
    val selectedListing = _selectedListing.asStateFlow()

    fun selectListing(listing: Listing) {
        _selectedListing.value = listing
    }

    private val _selectedCategory = MutableLiveData<Category?>(null)
    val selectedCategory: LiveData<Category?> = _selectedCategory

    private val _selectedCondition = MutableLiveData<Condition?>(null)
    val selectedCondition: LiveData<Condition?> = _selectedCondition

    fun selectCategory(category: Category?) {
        _selectedCategory.value = category
    }

    fun selectCondition(condition: Condition?) {
        _selectedCondition.value = condition
    }
}