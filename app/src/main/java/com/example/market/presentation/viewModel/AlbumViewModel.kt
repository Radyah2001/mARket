package com.example.market.presentation.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.market.BuildConfig
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.AlbumViewState
import com.example.market.data.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import android.os.Environment
import kotlin.coroutines.CoroutineContext
import androidx.lifecycle.viewmodel.compose.viewModel

class AlbumViewModel(private val coroutineContext: CoroutineContext): mARketAppViewModel() {
    //region View State
    private val _albumViewState: MutableStateFlow<AlbumViewState> = MutableStateFlow(AlbumViewState())
    // exposes the ViewState to the composable view
    val viewStateFlow: StateFlow<AlbumViewState>
        get() = _albumViewState
    // endregion

    // region Intents
    // receives user generated events and processes them in the provided coroutine context
    fun onReceive(intent: Intent) = viewModelScope.launch(coroutineContext) {
        when(intent) {
            is Intent.OnPermissionGrantedWith -> {
                // Create an empty image file in the app's cache directory
                val tempFile = File.createTempFile(
                    "temp_image_file_", /* prefix */
                    ".jpg", /* suffix */
                    intent.compositionContext.cacheDir  /* cache directory */
                )

                // Create sandboxed url for this temp file - needed for the camera API
                val uri = FileProvider.getUriForFile(intent.compositionContext,
                    "${BuildConfig.APPLICATION_ID}.provider", /* needs to match the provider information in the manifest */
                    tempFile
                )
                _albumViewState.value = _albumViewState.value.copy(tempFileUrl = uri)
            }
            is Intent.OnPermissionDenied -> {
                // maybe log the permission denial event
                println("User did not grant permission to use the camera")
            }
            is Intent.OnFinishPickingImagesWith -> {
                if (intent.imageUrls.isNotEmpty()) {
                    // Handle picked images
                    val newImages = mutableListOf<ImageBitmap>()
                    for (eachImageUrl in intent.imageUrls) {
                        val inputStream = intent.compositionContext.contentResolver.openInputStream(eachImageUrl)
                        val bytes = inputStream?.readBytes()
                        inputStream?.close()

                        if (bytes != null) {
                            val bitmapOptions = BitmapFactory.Options()
                            bitmapOptions.inMutable = true
                            val bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bitmapOptions)
                            newImages.add(bitmap.asImageBitmap())
                        } else {
                            // error reading the bytes from the image url
                            println("The image that was picked could not be read from the device at this url: $eachImageUrl")
                        }
                    }

                    val currentViewState = _albumViewState.value
                    val newCopy = currentViewState.copy(
                        selectedPictures = (currentViewState.selectedPictures + newImages),
                        tempFileUrl = null
                    )
                    _albumViewState.value = newCopy
                } else {
                    // user did not pick anything
                }
            }
            is Intent.OnImageSavedWith -> {
                val tempImageUrl = _albumViewState.value.tempFileUrl
                if (tempImageUrl != null) {
                    val source = ImageDecoder.createSource(intent.compositionContext.contentResolver, tempImageUrl)

                    val currentPictures = _albumViewState.value.selectedPictures.toMutableList()
                    currentPictures.add(ImageDecoder.decodeBitmap(source).asImageBitmap())

                    _albumViewState.value = _albumViewState.value.copy(tempFileUrl = null,
                        selectedPictures = currentPictures)
                }
            }
            is Intent.OnImageSavingCanceled -> {
                _albumViewState.value = _albumViewState.value.copy(tempFileUrl = null)
            }
        }
    }
}