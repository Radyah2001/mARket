package com.example.market.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.coroutines.CoroutineContext

class AlbumViewModelFactory(private val coroutineContext: CoroutineContext) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumViewModel(coroutineContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}