package com.example.market.presentation.viewModel

import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    fun onClick(navigate: (String) -> Unit, route: String ) {
        navigate(route)
    }
}