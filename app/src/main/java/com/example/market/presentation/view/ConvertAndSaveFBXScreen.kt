package com.example.market.presentation.view

import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.market.presentation.viewModel.ModelConversionViewModel
import com.example.market.ui.theme.Teal

@Composable
fun ConvertAndSaveFBXScreen(modelConversionViewModel: ModelConversionViewModel = viewModel()) {
    val context = LocalContext.current

    // State to hold the chosen OBJ file Uri.
    var pickedFbxUri by remember { mutableStateOf<Uri?>(null) }
    // State to hold the temporary GLB file path returned after conversion.
    var tempGLBFilePath by remember { mutableStateOf<String?>(null) }
    // State for the status message.
    var statusMessage by remember { mutableStateOf("No file chosen.") }
    // State to track whether a conversion is in progress.
    var conversionInProgress by remember { mutableStateOf(false) }

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Query the display name from the content provider
            var displayName: String? = null
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex != -1) {
                    displayName = cursor.getString(nameIndex)
                }
            }
            val mimeType = context.contentResolver.getType(uri)
            Log.d("FilePicker", "URI: $uri")
            Log.d("FilePicker", "MIME Type: $mimeType")
            Log.d("FilePicker", "Display Name: $displayName")

            // Check if the file name ends with ".fbx"
            if (displayName != null && displayName.lowercase().endsWith(".fbx")) {
                pickedFbxUri = uri
                statusMessage = "File chosen: $displayName"
            } else {
                statusMessage = "Please select an .fbx file"
                Toast.makeText(context, statusMessage, Toast.LENGTH_SHORT).show()
            }
        } else {
            statusMessage = "No file chosen."
        }
    }

    // Launcher to let the user choose where to save the converted GLB.
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = CreateDocument("model/gltf-binary")
    ) { uri: Uri? ->
        if (uri != null && tempGLBFilePath != null) {
            modelConversionViewModel.saveConvertedFile(context, uri, tempGLBFilePath!!)
            Toast.makeText(context, "File successfully saved!", Toast.LENGTH_LONG).show()
            statusMessage = "Conversion completed and file saved."
            conversionInProgress = false
            // Reset state
            tempGLBFilePath = null
            pickedFbxUri = null
        } else {
            Toast.makeText(context, "Save cancelled or conversion failed.", Toast.LENGTH_LONG).show()
            conversionInProgress = false
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize(), // Teal background
        color = Teal // Teal; if you want the background of the Surface to be teal.
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Teal background
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title text
            Text(
                text = "Export FBX to GLB",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Status message
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button to pick an FBX file (if not already chosen).
            Button(
                onClick = {
                    pickFileLauncher.launch("*/*")
                },
                enabled = !conversionInProgress,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Pick FBX File", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to convert and save.
            Button(
                onClick = {
                    if (pickedFbxUri == null) {
                        Toast.makeText(context, "Please pick an FBX file first", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    conversionInProgress = true
                    statusMessage = "Converting FBX to GLB..."
                    val fbxFile = modelConversionViewModel.uriToFile(pickedFbxUri!!, context)
                    if (fbxFile == null) {
                        statusMessage = "Failed to convert URI to file."
                        conversionInProgress = false
                        return@Button
                    }
                    modelConversionViewModel.convertFBXToGLB(context, fbxFile,
                        onSuccess = { tempPath ->
                            tempGLBFilePath = tempPath
                            statusMessage = "Conversion successful. Choose where to save the GLB file."
                            // Immediately prompt the user to pick a save location.
                            createDocumentLauncher.launch("converted_${System.currentTimeMillis()}.glb")
                        },
                        onError = { errorMsg ->
                            statusMessage = "Conversion failed: $errorMsg"
                            conversionInProgress = false
                        }
                    )
                },
                enabled = (pickedFbxUri != null && !conversionInProgress),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Convert FBX to GLB & Save", color = Color.White)
            }
        }
    }
}