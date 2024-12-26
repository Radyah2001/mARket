package com.example.market.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class CreateViewModel : ViewModel() {
    val productName = MutableStateFlow("")
    val category = MutableStateFlow("")
    val price = MutableStateFlow("")
    val expanded = MutableStateFlow(false)
    val selectedCondition = MutableStateFlow("New") // State for selected condition

    val conditionOptions = listOf("New", "Used", "Fair") // Condition options

    fun updateProductName(newProductName: String) {
        productName.value = newProductName
    }

    fun updateCategory(newCategory: String) {
        category.value = newCategory
    }

    fun updatePrice(newPrice: String) {
        price.value = newPrice
    }

    fun updateCondition(newCondition: String) {
        selectedCondition.value = newCondition
    }

    fun updateExpanded(newExpanded: Boolean) {
        expanded.value = newExpanded
    }


    fun onClick(navigate: (String) -> Unit, route: String ) {
        navigate(route)
    }
}