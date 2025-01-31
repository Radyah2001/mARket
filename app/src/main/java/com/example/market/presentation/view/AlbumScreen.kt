package com.example.market.presentation.view

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import com.example.market.data.AlbumViewState
import com.example.market.data.Intent
import com.example.market.presentation.viewModel.AlbumViewModel
import com.example.market.presentation.viewModel.RecapViewModel
import com.example.market.ui.theme.Beige
import com.example.market.ui.theme.Black
import com.example.market.ui.theme.Orange
import com.example.market.ui.theme.PrimaryButton
import com.example.market.ui.theme.Teal
import kotlinx.coroutines.launch


@Composable
fun AlbumScreen(viewModel: AlbumViewModel, recapViewModel: RecapViewModel) {
    // collecting the flow from the view model as a state allows our ViewModel and View
    // to be in sync with each other.
    val viewState: AlbumViewState by viewModel.viewStateFlow.collectAsState()
    val progress: String? by recapViewModel.progress.observeAsState()
    val currentContext = LocalContext.current
    val downloadUrl: String? by recapViewModel.downloadUrl.observeAsState()


    // launches photo picker
    val pickImageFromAlbumLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { urls ->
        viewModel.onReceive(Intent.OnFinishPickingImagesWith(currentContext, urls))
    }

    // launches camera
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isImageSaved ->
        if (isImageSaved) {
            viewModel.onReceive(Intent.OnImageSavedWith(currentContext))
        } else {
            // handle image saving error or cancellation
            viewModel.onReceive(Intent.OnImageSavingCanceled)
        }
    }

    // launches camera permissions
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
        if (permissionGranted) {
            viewModel.onReceive(Intent.OnPermissionGrantedWith(currentContext))
        } else {
            // handle permission denied such as:
            viewModel.onReceive(Intent.OnPermissionDenied)
        }
    }




    // this ensures that the camera is launched only once when the url of the temp file changes
    LaunchedEffect(key1 = viewState.tempFileUrl) {
        viewState.tempFileUrl?.let {
            cameraLauncher.launch(it)
        }
    }

    // Number of selected pictures
    val pictureCount = viewState.selectedPictures.size

    // basic view that has 2 buttons and a grid for selected pictures
    // Main layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Teal)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ---- Buttons Row ----
        Row {
            Button(
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White)
            ) {
                Text(text = "Take a photo")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    val mediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    pickImageFromAlbumLauncher.launch(mediaRequest)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White)
            ) {
                Text(text = "Pick a picture")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---- Advisory Card ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),     // match your design's corners
            // Elevation slightly if you want some shadow
        ) {
            Column(
                modifier = Modifier
                    .background(Beige)
                    .padding(16.dp)  // internal card padding
            ) {
                // A small heading
                Text(
                    text = "Heads up!",
                    style = MaterialTheme.typography.titleMedium,
                    color = Black,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = """
                        We recommend taking at least 10 pictures from every angle to ensure a better 3D model.
                        
                        Also note that you download a fbx file, so remember to export as a .glb file through the export page
                        
                        You currently have $pictureCount picture${if (pictureCount == 1) "" else "s"} selected.
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Black
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---- Title for Selected Pictures ----
        Text(
            text = "Selected Pictures",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ---- Picture Grid ----
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 0.dp, max = 1200.dp),
            columns = GridCells.Adaptive(150.dp)
        ) {
            itemsIndexed(viewState.selectedPictures) { _, picture ->
                Image(
                    bitmap = picture.imageBitmap,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp),
                    contentScale = ContentScale.FillWidth
                )
            }
        }

        // ---- "Create 3D Model" button (if at least 10 pictures) ----
        if (pictureCount >= 10 && downloadUrl == null && progress == null) {
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                text = "Create 3D model",
                onClick = {
                    recapViewModel.createPhotoscene("myScene") { sceneId ->
                        if (sceneId != null) {
                            val filesToUpload = viewModel.toFileList(currentContext)
                            recapViewModel.viewModelScope.launch {
                                try {
                                    recapViewModel.uploadPhotosInBatches(filesToUpload, 10)
                                    recapViewModel.getProgress()
                                } catch (e: Exception) {
                                    Log.e("RECAP", "Could not upload photos: $e")
                                }
                            }
                        } else {
                            Log.e("RECAP", "Could not create scene")
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---- Progress text if available ----
        progress?.let { prog ->
            Text(
                text = "Progress: $prog%",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        downloadUrl?.let { url ->
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.startDownload(currentContext, url, "result.fbx")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White)
            ) {
                Text(text = "Download Result")
            }
        }

    }
}