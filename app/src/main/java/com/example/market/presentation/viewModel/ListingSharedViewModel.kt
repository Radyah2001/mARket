package com.example.market.presentation.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.market.model.Category
import com.example.market.model.Condition
import com.example.market.model.Listing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream

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

    private val _recentlySeenListings = MutableLiveData<List<Listing>>()
    val recentlySeenListings: LiveData<List<Listing>> = _recentlySeenListings

    fun addRecentlySeenListing(listing: Listing) {
        val currentList = _recentlySeenListings.value.orEmpty().toMutableList()
        val existingIndex = currentList.indexOf(listing)

        if (existingIndex != -1) {
            // Listing already exists, move it to the top
            currentList.removeAt(existingIndex)
        }

        currentList.add(0, listing) // Add listing to the beginning
        _recentlySeenListings.value = currentList
    }

    fun clearRecentlySeenListings() {
        _recentlySeenListings.value = emptyList()
    }

    fun removeRecentlySeenListing(listing: Listing) {
        val currentList = _recentlySeenListings.value.orEmpty().toMutableList()
        currentList.remove(listing)
        _recentlySeenListings.value = currentList
    }


    fun selectCategory(category: Category?) {
        _selectedCategory.value = category
    }

    fun selectCondition(condition: Condition?) {
        _selectedCondition.value = condition
    }

    fun copyAssetToTempFile(context: Context, assetPath: String, outputFileName: String): File? {
        return try {
            val assetManager = context.assets
            // Open the asset as an InputStream
            assetManager.open(assetPath).use { inputStream ->
                // Create a temporary file in the cache directory
                val tempFile = File(context.cacheDir, outputFileName)
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                tempFile
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}