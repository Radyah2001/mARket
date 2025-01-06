package com.example.market.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.market.data.ListingRepository

class ListingsViewModelFactory(
    private val listingRepository: ListingRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ListingsViewModel(listingRepository) as T
    }
}