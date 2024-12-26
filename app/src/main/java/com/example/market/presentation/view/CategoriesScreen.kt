package com.example.market.presentation.view


import android.R.color.black
import androidx.compose.animation.core.copy
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.market.ui.theme.Beige
import com.example.market.ui.theme.Black
import com.example.market.ui.theme.MARketTheme
import com.example.market.ui.theme.Teal
import com.example.market.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    openAndPopUp: (String, String) -> Unit
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
                CategoryButton(text = "Chairs", onClick = { openAndPopUp("Chairs", "") }, icon = ImageVector.vectorResource(R.drawable.chair))
                CategoryButton(text = "Tables", onClick = { openAndPopUp("Tables", "") }, icon = ImageVector.vectorResource(R.drawable.table))
                CategoryButton(text = "Beds", onClick = { openAndPopUp("Beds", "") }, icon = ImageVector.vectorResource(R.drawable.bed))
                CategoryButton(text = "Desks", onClick = { openAndPopUp("Desks", "") }, icon = ImageVector.vectorResource(R.drawable.desk))
                CategoryButton(text = "Dressers", onClick = { openAndPopUp("Dressers", "") }, icon = ImageVector.vectorResource(R.drawable.closet))
                CategoryButton(text = "Couches", onClick = { openAndPopUp("Couches", "") }, icon = ImageVector.vectorResource(R.drawable.couches))
                CategoryButton(text = "Bookcases", onClick = { openAndPopUp("Bookcases", "") }, icon = ImageVector.vectorResource(R.drawable.bookcase))
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