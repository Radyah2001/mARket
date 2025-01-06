package com.example.market.presentation.view


import android.R.color.black
import android.view.MenuItem
import androidx.compose.animation.core.copy
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
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
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.market.ALBUM_SCREEN
import com.example.market.ui.theme.Beige
import com.example.market.ui.theme.Black
import com.example.market.ui.theme.MARketTheme
import com.example.market.ui.theme.Teal
import com.example.market.R
import com.example.market.presentation.viewModel.CreateViewModel
import com.example.market.presentation.viewModel.HomeViewModel
import com.example.market.ui.theme.CustomTextField
import com.example.market.ui.theme.Orange
import com.example.market.ui.theme.PriceTextField
import com.example.market.ui.theme.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    navigate: (String) -> Unit,
    viewModel: CreateViewModel = viewModel()
) {
    val productName = viewModel.productName.collectAsState()
    val category = viewModel.category.collectAsState()
    val price = viewModel.price.collectAsState()
    val expanded = viewModel.expanded.collectAsState()
    val selectedCondition = viewModel.selectedCondition.collectAsState() // State for selected condition

    val conditionOptions = listOf("New", "Used", "Fair") // Condition options

    MARketTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Create Listing", style = MaterialTheme.typography.headlineMedium.copy(
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
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Product Name
                CustomTextField(
                    value = productName.value,
                    onValueChange = { viewModel.updateProductName(it) },
                    placeholder = "Product Name",
                    leadingIcon = Icons.Filled.ShoppingCart, // Replace with your desired icon
                    contentDescription = "Product Name"
                )

                // Category
                CustomTextField(
                    value = category.value,
                    onValueChange = { viewModel.updateCategory(it) },
                    placeholder = "Category",
                    leadingIcon = Icons.Filled.Menu, // Replace with your desired icon
                    contentDescription = "Category"
                )

                // Price
                PriceTextField(
                    value = price.value,
                    onValueChange = { viewModel.updatePrice(it) },
                    currency = "kr.", // Set your desired currency
                    leadingIcon = ImageVector.vectorResource(R.drawable.attach_money_24dp_e8eaed_fill0_wght400_grad0_opsz24)
                )
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
                        expanded = expanded.value,
                        onExpandedChange = { viewModel.updateExpanded(it) },
                    ) {
                        TextField(
                            readOnly = true,
                            value = selectedCondition.value,
                            onValueChange = {},
                            label = { Text("Condition") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
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
                            expanded = expanded.value,
                            onDismissRequest = { viewModel.updateExpanded(false) },
                            modifier = Modifier.background(Beige)
                        ) {
                            conditionOptions.forEach { selectionOption ->
                                DropdownMenuItem(
                                    onClick = {
                                        viewModel.updateCondition(selectionOption)
                                        viewModel.updateExpanded(false)
                                    },
                                    text = { Text(selectionOption, color = Black) },
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
                        "Pictures for 3D-model",
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp, 0.dp) // Add padding to match text fields
                        ,
                        color = Black
                    )
                    IconButton(
                        onClick = { viewModel.onClick(navigate, ALBUM_SCREEN) },
                        modifier = Modifier.background(Beige) // Add Beige background to IconButton
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Upload Images", tint = Black)
                    }
                }

                // Create button
                PrimaryButton(text = "Create", onClick = { /* Handle create listing */ })
            }
        }
    }
}