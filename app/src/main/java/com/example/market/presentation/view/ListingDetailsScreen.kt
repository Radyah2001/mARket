package com.example.market.presentation.view

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.market.AR_SCREEN
import com.example.market.MODEL_VIEWER_SCREEN
import com.example.market.R
import com.example.market.presentation.viewModel.ListingSharedViewModel
import com.example.market.presentation.viewModel.ModelConversionViewModel
import com.example.market.ui.theme.Beige
import com.example.market.ui.theme.Black
import com.example.market.ui.theme.MARketTheme
import com.example.market.ui.theme.Orange
import com.example.market.ui.theme.PrimaryButton
import com.example.market.ui.theme.Teal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailsScreen(
    listingSharedViewModel: ListingSharedViewModel,
    modelConversionViewModel: ModelConversionViewModel,
    navigate: (String) -> Unit
) {
    // State variable to hold the temporary STL file path after conversion.
    var tempSTLFilePath by remember { mutableStateOf<String?>(null) }
    var conversionMessage by remember { mutableStateOf("") }
    var conversionInProgress by remember { mutableStateOf(false) }


    val listing = listingSharedViewModel.selectedListing.collectAsState().value
    listing ?: return
    val context = LocalContext.current

    // Launcher for the Create Document contract.
    // When the user selects a destination, saveConvertedFile() is called.
    val createDocumentLauncher = rememberLauncherForActivityResult(contract = CreateDocument("model/stl")) { uri ->
        if (uri != null && tempSTLFilePath != null) {
            modelConversionViewModel.saveConvertedFile(context, uri, tempSTLFilePath!!)
            Toast.makeText(context, "File saved at: $uri", Toast.LENGTH_LONG).show()
            // Optionally reset state
            tempSTLFilePath = null
            conversionMessage = "File successfully saved!"
            conversionInProgress = false
        } else {
            Toast.makeText(context, "File save cancelled or conversion not completed.", Toast.LENGTH_LONG).show()
            conversionInProgress = false
        }
    }

    MARketTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            listing.productName,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 24.sp
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Beige,
                        titleContentColor = Black
                    )
                )
            },
            containerColor = Teal
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Beige
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(listing.imageUrl),
                            contentDescription = listing.productName,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(
                                    onClickLabel = "Open AR",
                                    onClick = { AR_SCREEN }
                                ),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Add some spacing

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            CustomIconButton(
                                imageResource = R.drawable.view_in_3d,
                                contentDescription = "Model Viewer",
                                onClick = { navigate(MODEL_VIEWER_SCREEN) }
                            )
                            Spacer(modifier = Modifier.width(32.dp))
                            CustomIconButton(
                                imageResource = R.drawable.baseline_view_in_ar_24,
                                contentDescription = "AR Button",
                                onClick = { navigate(AR_SCREEN) }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = listing.productName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = listing.category.name,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${listing.price} kr.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Orange
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = listing.condition.name,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Button for converting and immediately prompting save.
                        PrimaryButton(
                            onClick = {
                                conversionInProgress = true
                                conversionMessage = "Converting..."

                                modelConversionViewModel.convertRemoteGLTFToSTL(
                                    context = context,
                                    remoteUrl = listing.modelUrl.toString(), // The remote link
                                    onSuccess = { tempPath ->
                                        tempSTLFilePath = tempPath
                                        conversionMessage = "Conversion successful. Saving file..."
                                        // Prompt user to save the .stl
                                        createDocumentLauncher.launch("model_${System.currentTimeMillis()}.stl")
                                    },
                                    onError = { errorMsg ->
                                        conversionMessage = "Conversion failed: $errorMsg"
                                        conversionInProgress = false
                                        Toast.makeText(
                                            context,
                                            "Conversion failed: $errorMsg",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                )
                            },
                            text = if (conversionInProgress) "Exporting..." else "Export file to STL & Save"
                        )

                        // Optionally show conversion message
                        Text(text = conversionMessage)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomIconButton(
    imageResource: Int, // Image resource ID
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = imageResource),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(48.dp)
                .padding(8.dp),
            tint = Black
        )
    }
}