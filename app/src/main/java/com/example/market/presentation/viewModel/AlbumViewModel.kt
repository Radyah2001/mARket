package com.example.market.presentation.viewModel

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.BuildConfig
import com.example.market.data.AlbumViewState
import com.example.market.data.Intent
import com.example.market.data.SelectedPicture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.CoroutineContext
import android.app.DownloadManager
import android.net.Uri
import android.os.Environment

class AlbumViewModel(private val coroutineContext: CoroutineContext): ViewModel() {
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
                    val newPictures = mutableListOf<SelectedPicture>()
                    for (eachImageUrl in intent.imageUrls) {
                        val inputStream = intent.compositionContext.contentResolver.openInputStream(eachImageUrl)
                        val bytes = inputStream?.readBytes()
                        inputStream?.close()

                        if (bytes != null) {
                            // Convert raw bytes to a Bitmap -> ImageBitmap
                            val bitmapOptions = BitmapFactory.Options().apply { inMutable = true }
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bitmapOptions)
                            val imageBitmap = bitmap.asImageBitmap()

                            // Store both the URI and the ImageBitmap
                            newPictures.add(
                                SelectedPicture(
                                    uri = eachImageUrl,
                                    imageBitmap = imageBitmap
                                )
                            )
                        } else {
                            println("Could not read bytes from $eachImageUrl")
                        }
                    }

                    val currentViewState = _albumViewState.value
                    val newCopy = currentViewState.copy(
                        selectedPictures =
                                currentViewState.selectedPictures + newPictures,
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
                    val decodedBitmap = ImageDecoder.decodeBitmap(source).asImageBitmap()

                    val currentPictures = _albumViewState.value.selectedPictures.toMutableList()
                    currentPictures.add(
                        SelectedPicture(
                            uri = tempImageUrl,
                            imageBitmap = decodedBitmap
                        )
                    )

                    _albumViewState.value = _albumViewState.value.copy(tempFileUrl = null,
                        selectedPictures = currentPictures)
                }
            }
            is Intent.OnImageSavingCanceled -> {
                _albumViewState.value = _albumViewState.value.copy(tempFileUrl = null)
            }
        }
    }

    fun toFileList(context: Context): List<File> {
        val files = mutableListOf<File>()
        for (selectedPic in _albumViewState.value.selectedPictures) {
            val inputStream = context.contentResolver.openInputStream(selectedPic.uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                // Create a temp file
                val tempFile = File.createTempFile("recap_upload_", ".jpg", context.cacheDir)
                tempFile.outputStream().use { it.write(bytes) }
                files.add(tempFile)
            }
        }
        return files
    }

    fun startDownload(context: Context, downloadUrl: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
            setTitle("Downloading 3D Model")
            setDescription("Downloading result file")
            // Set the destination for the file in the public Downloads directory
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            // Show download progress in the system notifications
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }
}