package com.example.market.presentation.viewModel

import com.example.market.CATEGORIES_SCREEN

class HomeViewModel : mARketAppViewModel() {
    fun onClick(navigate: (String) -> Unit, route: String ) {
        navigate(route)
    }
}