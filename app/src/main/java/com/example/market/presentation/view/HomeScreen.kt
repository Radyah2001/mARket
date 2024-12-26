package com.example.market.presentation.view

import android.R.color.black
import androidx.compose.animation.core.copy
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.market.ACCOUNT_SCREEN
import com.example.market.AR_SCREEN
import com.example.market.CATEGORIES_SCREEN
import com.example.market.CREATE_LISTING_SCREEN
import com.example.market.presentation.viewModel.HomeViewModel
import com.example.market.ui.theme.Beige
import com.example.market.ui.theme.Black
import com.example.market.ui.theme.MARketTheme
import com.example.market.ui.theme.Teal
import com.example.market.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigate: (String) -> Unit,
    viewModel: HomeViewModel = HomeViewModel()
) {
    MARketTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Home", style = MaterialTheme.typography.headlineMedium.copy(
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    HomeButton(text = "Browse", onClick = {  }, modifier = Modifier.weight(1f), icon = Icons.Filled.Search)
                    Spacer(modifier = Modifier.width(16.dp))
                    HomeButton(text = "Categories", onClick = { viewModel.onClick(navigate, CATEGORIES_SCREEN) }, modifier = Modifier.weight(1f), icon = Icons.Filled.Menu)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    HomeButton(text = "Create Listing", onClick = { viewModel.onClick(navigate,
                        CREATE_LISTING_SCREEN
                    ) }, modifier = Modifier.weight(1f), icon = Icons.Filled.Add)
                    Spacer(modifier = Modifier.width(16.dp))
                    HomeButton(text = "Account", onClick = { viewModel.onClick(navigate, ACCOUNT_SCREEN)}, modifier = Modifier.weight(1f), icon = Icons.Filled.Person)
                }
                HomeButton(text = "AR", onClick = { viewModel.onClick(navigate, AR_SCREEN) }, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(8.dp))
                RecentlySeenSection(text = "Recently Seen", placeholderImage = R.drawable.chair)

            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HomeButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Beige,
            contentColor = Black
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(
                    ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            }
            Text(text, fontSize = 20.sp)
        }
    }
}

@Composable
fun RecentlySeenSection(text: String, placeholderImage: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Beige),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text, style = MaterialTheme.typography.headlineSmall, color = Black)
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = placeholderImage),
            contentDescription = "Placeholder",
            modifier = Modifier.size(150.dp)
        )
    }
}