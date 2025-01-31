package com.example.market.presentation.viewModel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.ListingRepository
import com.example.market.data.RetrofitClient
import com.example.market.model.Category
import com.example.market.model.Condition
import com.example.market.model.Listing
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class CreateViewModel(private val listingRepository: ListingRepository) : ViewModel() {
    private val listingApiService = RetrofitClient.modelApi

    fun createListing(
        productName: String,
        category: Category,
        price: Double,
        condition: Condition,
        imageFile: File?,
        modelFile: File?,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                if (productName.isEmpty() || price <= 0 || imageFile == null || modelFile == null) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                // 1) Insert listing in DB (without imageUrl/modelUrl for now)
                val newListing = Listing(
                    productName = productName,
                    category = category,
                    price = price,
                    condition = condition,
                    imageUrl = null,
                    modelUrl = null
                )
                // Insert listing returns row ID
                val newId = listingRepository.addListingReturnId(newListing)
                val listingId = newId.toInt()

                // 2) Upload the image if provided
                var imageUrl: String? = null

                imageUrl = uploadListingImage(listingId, imageFile)

                // 3) Upload the 3D model if provided
                var modelUrl: String? = null

                modelUrl = uploadListingModel(listingId, modelFile)

                // 4) Update the listing in DB with the new URLs
                val updatedListing = newListing.copy(
                    id = listingId,
                    imageUrl = imageUrl,
                    modelUrl = modelUrl
                )
                listingRepository.updateListing(updatedListing)

                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    private suspend fun uploadListingImage(listingId: Int, imageFile: File): String {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData(
            "image-file",
            imageFile.name,
            requestFile
        )
        val response = listingApiService.uploadListingImage(listingId, imagePart)
        if (response.isSuccessful) {
            val body = response.body()
            return body?.imageUrl ?: ""
        } else {
            throw Exception("Failed to upload image: ${response.errorBody()?.string()}")
        }
    }

    private suspend fun uploadListingModel(listingId: Int, modelFile: File): String {
        val requestFile = modelFile.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val modelPart = MultipartBody.Part.createFormData(
            "model-file",
            modelFile.name,
            requestFile
        )
        val response = listingApiService.uploadListingModel(listingId, modelPart)
        if (response.isSuccessful) {
            val body = response.body()
            return body?.modelUrl ?: ""
        } else {
            throw Exception("Failed to upload model: ${response.errorBody()?.string()}")
        }
    }

    fun uriToFile(uri: Uri, context: Context): File? {
        // We create a temp file in the app's cache dir
        val contentResolver = context.contentResolver ?: return null
        val file = File(context.cacheDir, "${System.currentTimeMillis()}_tempfile")
        try {
            contentResolver.openInputStream(uri).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    copyStream(inputStream!!, outputStream)
                }
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // Copy the bytes from InputStream to OutputStream
    fun copyStream(input: InputStream, out: FileOutputStream) {
        val buffer = ByteArray(1024)
        var len: Int
        while (input.read(buffer).also { len = it } != -1) {
            out.write(buffer, 0, len)
        }
        out.flush()
    }
}