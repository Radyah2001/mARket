package com.example.market.presentation.view


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.market.CATEGORIES_SCREEN
import com.example.market.LISTINGS_SCREEN
import com.example.market.ui.theme.Beige
import com.example.market.ui.theme.Black
import com.example.market.ui.theme.MARketTheme
import com.example.market.ui.theme.Teal
import com.example.market.R
import com.example.market.data.AppDatabase
import com.example.market.data.ListingRepositoryImpl
import com.example.market.model.Category
import com.example.market.presentation.viewModel.ListingSharedViewModel
import com.example.market.presentation.viewModel.ListingsViewModel
import com.example.market.presentation.viewModel.ListingsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    openAndPopUp: (String, String) -> Unit,
    listingSharedViewModel: ListingSharedViewModel
) {
    MARketTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Categories", style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp
                    ))
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
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                CategoryButton(text = "Chairs", onClick = {
                    listingSharedViewModel.selectCategory(Category.CHAIRS)
                    openAndPopUp(LISTINGS_SCREEN, CATEGORIES_SCREEN)
                }, icon = ImageVector.vectorResource(R.drawable.chair_model))
                CategoryButton(text = "Tables", onClick = {
                    listingSharedViewModel.selectCategory(Category.TABLES)
                    openAndPopUp(LISTINGS_SCREEN, CATEGORIES_SCREEN)
                }, icon = ImageVector.vectorResource(R.drawable.table))
                CategoryButton(text = "Beds", onClick = {
                    listingSharedViewModel.selectCategory(Category.BEDS)
                    openAndPopUp(LISTINGS_SCREEN, CATEGORIES_SCREEN)
                }, icon = ImageVector.vectorResource(R.drawable.bed))
                CategoryButton(text = "Desks", onClick = {
                    listingSharedViewModel.selectCategory(Category.DESKS)
                    openAndPopUp(LISTINGS_SCREEN, CATEGORIES_SCREEN)
                }, icon = ImageVector.vectorResource(R.drawable.desk_model))
                CategoryButton(text = "Dressers", onClick = {
                    listingSharedViewModel.selectCategory(Category.DRESSERS)
                    openAndPopUp(LISTINGS_SCREEN, CATEGORIES_SCREEN)
                }, icon = ImageVector.vectorResource(R.drawable.closet))
                CategoryButton(text = "Couches", onClick = {
                    listingSharedViewModel.selectCategory(Category.COUCHES)
                    openAndPopUp(LISTINGS_SCREEN, CATEGORIES_SCREEN)
                }, icon = ImageVector.vectorResource(R.drawable.couches))
                CategoryButton(text = "Bookcases", onClick = {
                    listingSharedViewModel.selectCategory(Category.BOOKCASES)
                    openAndPopUp(LISTINGS_SCREEN, CATEGORIES_SCREEN)
                }, icon = ImageVector.vectorResource(R.drawable.bookcase))
            }
        }
    }
}

@Composable
fun CategoryButton(text: String, onClick: () -> Unit, icon: ImageVector? = null) {
    val textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp) // Adjust font size here
    val iconSize = 24.dp // Adjust icon size here

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Beige,
            contentColor = Black
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    modifier = Modifier.size(iconSize), // Apply icon size
                    tint = Black // Set icon color to Black
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            }
            Text(text, style = textStyle) // Apply text style
        }
    }
}

@Composable
fun SortAndFilterButtons(
    onSortClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // add some horizontal padding
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sort button
        FilledTonalButton(
            onClick = onSortClick,
            modifier = Modifier
                .padding(end = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Beige,
                contentColor = Black
            )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_sort_24),
                contentDescription = "Sort",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Sort")
        }

        // Filter button
        FilledTonalButton(
            onClick = onFilterClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Beige,
                contentColor = Black
            )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_filter_list_24),
                contentDescription = "Filter",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Filter")
        }
    }
}