package com.example.market.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.market.LISTING_DETAILS_SCREEN
import com.example.market.data.AppDatabase
import com.example.market.data.ListingRepositoryImpl
import com.example.market.model.Listing
import com.example.market.presentation.viewModel.ListingSharedViewModel
import com.example.market.presentation.viewModel.ListingsViewModel
import com.example.market.presentation.viewModel.ListingsViewModelFactory
import com.example.market.ui.theme.Beige
import com.example.market.ui.theme.Black
import com.example.market.ui.theme.MARketTheme
import com.example.market.ui.theme.Orange
import com.example.market.ui.theme.PrimaryButton
import com.example.market.ui.theme.Teal
import com.example.market.R
import com.example.market.model.Category
import com.example.market.model.Condition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingsScreen(
    navigate: (String) -> Unit,
    listingSharedViewModel: ListingSharedViewModel,
    listingsViewModel: ListingsViewModel = viewModel(
        factory = ListingsViewModelFactory(
            ListingRepositoryImpl(AppDatabase.getDatabase(LocalContext.current))
        )
    )
) {
    val filteredListings by listingsViewModel.filteredListings.observeAsState(emptyList())
    val showSortOverlay by listingsViewModel.showSortOverlay.observeAsState()
    val showFilterOverlay by listingsViewModel.showFilterOverlay.observeAsState()
    val selectedCategory by listingSharedViewModel.selectedCategory.observeAsState()
    val selectedCondition by listingSharedViewModel.selectedCondition.observeAsState()
    val scrollState = rememberScrollState()
    LaunchedEffect(listingSharedViewModel.selectedCategory) {
        listingsViewModel.filterListings(
            listingSharedViewModel.selectedCategory.value,
            null
        )
    }
    MARketTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                "Listings", style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 24.sp
                                )
                            )
                        }, colors = TopAppBarDefaults.topAppBarColors(
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
                        .verticalScroll(
                            state = scrollState
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (showSortOverlay == true) {
                        SortDialog(
                            onSort = { ascending -> listingsViewModel.sortListings(ascending) },
                            onDismiss = { listingsViewModel.toggleSortOverlay() }
                        )
                    }
                    if (showFilterOverlay == true) {
                        FilterDialog(
                            categories = Category.entries,
                            selectedCategory = selectedCategory,
                            onCategorySelected = { category -> listingSharedViewModel.selectCategory(category) },
                            conditions = Condition.entries,
                            selectedCondition = selectedCondition,
                            onConditionSelected = { condition -> listingSharedViewModel.selectCondition(condition) },
                            onFilter = {listingsViewModel.filterListings(selectedCategory, selectedCondition) },
                            onDismiss = { listingsViewModel.toggleFilterOverlay() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SortAndFilterButtons(
                        onSortClick = { listingsViewModel.toggleSortOverlay() },
                        onFilterClick = { listingsViewModel.toggleFilterOverlay() }
                    )

                    filteredListings.forEach {
                        ListingItem(
                            listing = it,
                            onItemClick = {
                                listingSharedViewModel.selectListing(it)
                                navigate(LISTING_DETAILS_SCREEN)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ListingItem(listing: Listing, onItemClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onItemClick),
        colors = CardDefaults.cardColors(
            containerColor = Beige
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = listing.image),
                contentDescription = listing.productName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray),
                contentScale = ContentScale.FillWidth
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = listing.productName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = listing.category.name,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${listing.price} kr.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Orange
                    )
                    Text(
                        text = listing.condition.name,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun SortDialog(
    onSort: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var ascending by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .wrapContentSize()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Sort by Price",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                Spacer(modifier = Modifier.height(8.dp))

                Text("Choose sorting order:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // Radio choices
                Row {
                    RadioButton(
                        selected = ascending,
                        onClick = { ascending = true }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ascending")
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    RadioButton(
                        selected = !ascending,
                        onClick = { ascending = false }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Descending")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onSort(ascending); onDismiss() }) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
fun FilterDialog(
    // The full list of categories user can choose from
    categories: List<Category>,
    // Currently selected category (if any)
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit,

    // The full list of conditions user can choose from
    conditions: List<Condition>,
    selectedCondition: Condition?,
    onConditionSelected: (Condition?) -> Unit,

    // Called when user presses "Apply"
    onFilter: () -> Unit,

    // Called when dialog is dismissed or canceled
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        // Material container for styling
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .width(intrinsicSize = IntrinsicSize.Min) // so it doesn't stretch too wide
                    .verticalScroll(rememberScrollState())
            ) {
                // Title row with optional close icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Filter Listings",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                // Divider or spacing
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                Spacer(Modifier.height(8.dp))

                // Category
                Text("Category", style = MaterialTheme.typography.bodyMedium)
                CategoryExposedDropdown(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected
                )

                Spacer(Modifier.height(16.dp))

                // Condition
                Text("Condition", style = MaterialTheme.typography.bodyMedium)
                ConditionExposedDropdown(
                    conditions = conditions,
                    selectedCondition = selectedCondition,
                    onConditionSelected = onConditionSelected
                )

                Spacer(Modifier.height(24.dp))

                // "Cancel" and "Apply" buttons
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onFilter()   // e.g., triggers filtering logic in your ViewModel
                            onDismiss()  // close dialog
                        }
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryExposedDropdown(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit
) {
    // The expanded state
    var expanded by remember { mutableStateOf(false) }
    // Track the text shown in the field
    val textFieldValue = selectedCategory?.name ?: "Select a category"

    // This is the M3 "Exposed" pattern
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { /* no direct text editing */ },
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            // The critical modifier that anchors the menu:
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    onCategorySelected(null)
                    expanded = false
                }
            )
            categories.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.name) },
                    onClick = {
                        onCategorySelected(cat)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionExposedDropdown(
    conditions: List<Condition>,
    selectedCondition: Condition?,
    onConditionSelected: (Condition?) -> Unit
) {
    // Whether the dropdown is open
    var expanded by remember { mutableStateOf(false) }

    // Display text in the text field
    val textFieldValue = selectedCondition?.name ?: "Select a condition"

    // The ExposedDropdownMenuBox automatically positions the menu
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        // The text field that triggers expansion
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { /* No direct editing */ },
            readOnly = true,
            label = { Text("Condition") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            // Important: menuAnchor() helps position the menu in Compose 1.5+
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Option to clear condition selection
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    onConditionSelected(null)
                    expanded = false
                }
            )
            // List all conditions
            conditions.forEach { condition ->
                DropdownMenuItem(
                    text = { Text(condition.name) },
                    onClick = {
                        onConditionSelected(condition)
                        expanded = false
                    }
                )
            }
        }
    }
}