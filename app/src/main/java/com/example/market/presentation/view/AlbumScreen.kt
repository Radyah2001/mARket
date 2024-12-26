package com.example.market.presentation.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.market.data.AlbumViewState
import com.example.market.data.Intent
import com.example.market.presentation.viewModel.AlbumViewModel
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.market.ui.theme.Orange
import com.example.market.ui.theme.Teal


@Composable
fun AlbumScreen(viewModel: AlbumViewModel) {
    // collecting the flow from the view model as a state allows our ViewModel and View
    // to be in sync with each other.
    val viewState: AlbumViewState by viewModel.viewStateFlow.collectAsState()
    val currentContext = LocalContext.current

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

    // basic view that has 2 buttons and a grid for selected pictures
    Column(modifier = Modifier.fillMaxSize().background(Teal).padding(20.dp)
        .verticalScroll(rememberScrollState()).then(Modifier),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = Color.White
                ),
                onClick = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Text(text = "Take a photo")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = Color.White
                ),
                onClick = {
                val mediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                pickImageFromAlbumLauncher.launch(mediaRequest)
            }) {
                Text(text = "Pick a picture")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Selected Pictures",
            color = Color.White
        )
        LazyVerticalGrid(modifier = Modifier.fillMaxWidth().heightIn(0.dp, 1200.dp),
            columns = GridCells.Adaptive(150.dp),
            userScrollEnabled = false) {
            itemsIndexed(viewState.selectedPictures) { index, picture ->
                Image(modifier = Modifier.padding(8.dp),
                    bitmap = picture,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}