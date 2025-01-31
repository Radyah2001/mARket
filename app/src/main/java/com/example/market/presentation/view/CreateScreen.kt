package com.example.market.presentation.view


import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isTappableElementVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults.itemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.market.LISTINGS_SCREEN
import com.example.market.R
import com.example.market.data.AppDatabase
import com.example.market.data.ListingRepositoryImpl
import com.example.market.model.Category
import com.example.market.model.Condition
import com.example.market.presentation.viewModel.CreateViewModel
import com.example.market.presentation.viewModel.CreateViewModelFactory
import com.example.market.ui.theme.Beige
import com.example.market.ui.theme.Black
import com.example.market.ui.theme.CustomTextField
import com.example.market.ui.theme.MARketTheme
import com.example.market.ui.theme.PriceTextField
import com.example.market.ui.theme.PrimaryButton
import com.example.market.ui.theme.Teal
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    createViewModel: CreateViewModel = viewModel(
        factory = CreateViewModelFactory(
            ListingRepositoryImpl(AppDatabase.getDatabase(LocalContext.current))
        )
    ),
    navigate: (String) -> Unit
) {
    // Local states for the listing form
    var productName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.COUCHES) }
    var price by remember { mutableStateOf("") }
    var selectedCondition by remember { mutableStateOf(Condition.NEW) }

    var isProductNameEmpty by remember { mutableStateOf(false) }
    var isPriceEmpty by remember { mutableStateOf(false) }
    var isModelEmpty by remember { mutableStateOf(true) }
    var isImageEmpty by remember { mutableStateOf(true) }

    // Files chosen by user for image & 3D model
    var imageFile: File? by remember { mutableStateOf(null) }
    var modelFile: File? by remember { mutableStateOf(null) }

    // Track whether the Condition dropdown is expanded
    var conditionMenuExpanded by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // **Launcher for picking an image**
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // This callback is triggered when the user picks an image (or cancels).
        if (uri != null) {
            // Convert the Uri to File, store in 'imageFile'
            val file = createViewModel.uriToFile(uri, context)
            imageFile = file
        }
    }

    val pickModelLauncher = rememberLauncherForActivityResult(
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

            // Fallback: try to use MimeTypeMap if displayName is null
            val extensionFromMime = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(context.contentResolver.getType(uri))

            val extension = displayName?.substringAfterLast('.', missingDelimiterValue = extensionFromMime ?: "")?.lowercase()

            if (extension != null && extension == "glb") {
                // Convert the Uri to a File if needed
                val file = createViewModel.uriToFile(uri, context)
                modelFile = file
            } else {
                Toast.makeText(context, "Please select a .glb file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    MARketTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Create Listing",
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Product Name
                CustomTextField(
                    value = productName,
                    onValueChange = {
                        productName = it
                        isProductNameEmpty = it.isEmpty()
                    },
                    placeholder = "Product Name",
                    leadingIcon = Icons.Filled.ShoppingCart, // Replace with your desired icon
                    contentDescription = "Product Name"
                )
                if (isProductNameEmpty) {
                    Text(
                        text = "Product name cannot be empty",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(16.dp, 16.dp)
                        .background(
                            color = Beige,
                            shape = RoundedCornerShape(2)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Category Selection (ExposedDropdownMenu)
                    ExposedDropdownMenuBox(
                        expanded = categoryMenuExpanded,
                        onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded },
                    ) {
                        TextField(
                            readOnly = true,
                            value = selectedCategory.name,
                            onValueChange = {},
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionMenuExpanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedContainerColor = Beige,
                                unfocusedContainerColor = Beige,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                unfocusedTrailingIconColor = Black,
                                focusedTrailingIconColor = Black,
                                focusedLabelColor = Black,
                                unfocusedLabelColor = Black,
                                unfocusedTextColor = Black,
                                focusedTextColor = Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor() // Make the TextField the anchor for the menu
                        )
                        ExposedDropdownMenu(
                            expanded = categoryMenuExpanded,
                            onDismissRequest = { categoryMenuExpanded = false },
                            modifier = Modifier.background(Beige)
                        ) {
                            Category.entries.forEach { category ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedCategory = category
                                        categoryMenuExpanded = false
                                    },
                                    text = { Text(category.name, color = Black) },
                                    colors = itemColors(
                                        textColor = Black,
                                        disabledTextColor = Black,
                                    )
                                )
                            }
                        }
                    }
                }

                // Price
                PriceTextField(
                    value = price,
                    onValueChange = {
                        price = it
                        isPriceEmpty = it.isEmpty()
                    },
                    currency = "kr.", // Set your desired currency
                    leadingIcon = ImageVector.vectorResource(R.drawable.attach_money_24dp_e8eaed_fill0_wght400_grad0_opsz24)
                )
                // Helper text for price
                if (isPriceEmpty) {
                    Text(
                        text = "Price cannot be empty",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(16.dp, 16.dp)
                        .background(
                            color = Beige,
                            shape = RoundedCornerShape(2)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Condition Selection (ExposedDropdownMenu)
                    ExposedDropdownMenuBox(
                        expanded = conditionMenuExpanded,
                        onExpandedChange = { conditionMenuExpanded = !conditionMenuExpanded },
                    ) {
                        TextField(
                            readOnly = true,
                            value = selectedCondition.name,
                            onValueChange = {},
                            label = { Text("Condition") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionMenuExpanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedContainerColor = Beige,
                                unfocusedContainerColor = Beige,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                unfocusedTrailingIconColor = Black,
                                focusedTrailingIconColor = Black,
                                focusedLabelColor = Black,
                                unfocusedLabelColor = Black,
                                unfocusedTextColor = Black,
                                focusedTextColor = Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor() // Make the TextField the anchor for the menu
                        )
                        ExposedDropdownMenu(
                            expanded = conditionMenuExpanded,
                            onDismissRequest = { conditionMenuExpanded = false },
                            modifier = Modifier.background(Beige)
                        ) {
                            Condition.entries.forEach { condition ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedCondition = condition
                                        conditionMenuExpanded = false
                                    },
                                    text = { Text(condition.name, color = Black) },
                                    colors = itemColors(
                                        textColor = Black,
                                        disabledTextColor = Black,
                                    )
                                )
                            }
                        }
                    }
                }

                // Pictures for 3D-model section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 4.dp)
                        .background(Beige, shape = RoundedCornerShape(2)) // Add Beige background
                ) {

                    Icon(
                        modifier = Modifier.padding(16.dp, 0.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.image_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                        contentDescription = "Pictures",
                        tint = Black
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing)) // Add spacing after icon

                    Text(
                        "Product picture",
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp, 0.dp) // Add padding to match text fields
                        ,
                        color = Black
                    )
                    IconButton(
                        onClick = { pickImageLauncher.launch("image/*") },
                        modifier = Modifier.background(Beige) // Add Beige background to IconButton
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Upload Images", tint = Black)
                    }
                }
                // Show the chosen image, if any
                imageFile?.let { chosenFile ->
                    isImageEmpty = false
                    Column(modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .background(Beige, shape = RoundedCornerShape(2)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Selected Image", color = Black, modifier = Modifier.padding(16.dp))
                        ImagePreview(
                            imageFile = chosenFile,
                            modifier = Modifier
                                .padding(16.dp)
                        )
                    }
                }

                // 3D MODEL UPLOAD SECTION
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 4.dp)
                        .background(Beige, shape = RoundedCornerShape(2)) // Add Beige background
                ) {

                    Icon(
                        modifier = Modifier.padding(16.dp, 0.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.view_in_3d),
                        contentDescription = "Add 3D-model (.glb)",
                        tint = Black
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing)) // Add spacing after icon

                    if (modelFile == null) {
                        Text(
                            "Add 3D-model (.glb)",
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp, 0.dp) // Add padding to match text fields
                            ,
                            color = Black
                        )
                    } else {
                        isModelEmpty = false
                        Text(
                            "3D-Model has been added",
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp, 0.dp) // Add padding to match text fields
                            ,
                            color = Black
                        )
                    }
                    IconButton(
                        onClick = { pickModelLauncher.launch("*/*") },
                        modifier = Modifier.background(Beige) // Add Beige background to IconButton
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Upload Images", tint = Black)
                    }
                }

                // CREATE BUTTON
                PrimaryButton(
                    text = "Create Listing",
                    onClick = {
                            // Convert price to double
                            val parsedPrice = price.toDoubleOrNull() ?: 0.0
                            // Actually create the listing
                            createViewModel.createListing(
                                productName = productName,
                                category = selectedCategory,
                                price = parsedPrice,
                                condition = selectedCondition,
                                imageFile = imageFile,
                                modelFile = modelFile,
                                onSuccess = {
                                    // Listing created + image/model uploaded + DB updated
                                    navigate(LISTINGS_SCREEN)
                                },
                                onError = {
                                    // TODO: Show error message
                                },
                                context = context
                            )
                    },
                )
            }
        }
    }
}

@Composable
fun ImagePreview(
    imageFile: File,
    modifier: Modifier = Modifier
) {
    val imageUri = imageFile.toUri() // Convert File -> Uri
    val painter = rememberAsyncImagePainter(model = imageUri)

    // Wrap the Image in a Box that handles background, border, and shape
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Beige) // match your app’s background color
            .border(
                width = 1.dp,
                color = Black,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Image(
            painter = painter,
            contentDescription = "Selected image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(100.dp)
        )
    }
}