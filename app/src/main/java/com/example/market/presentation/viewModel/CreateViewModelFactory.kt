package com.example.market.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.market.data.ListingRepository

class CreateViewModelFactory(
    private val listingRepository: ListingRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateViewModel(listingRepository) as T
    }
}