package com.example.market.presentation.view


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.market.model.Listing
import com.example.market.presentation.viewModel.AccountViewModel
import com.example.market.ui.theme.Beige
import com.example.market.ui.theme.Black
import com.example.market.ui.theme.DarkOrange
import com.example.market.ui.theme.MARketTheme
import com.example.market.ui.theme.Orange
import com.example.market.ui.theme.PrimaryButton
import com.example.market.ui.theme.Teal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navigate: (String) -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val showDeleteAccountDialog = viewModel.showDeleteAccountOverlay.observeAsState()
    val showSignOutDialog = viewModel.showSignOutOverlay.observeAsState()
    val showUpdateDisplayNameDialog = viewModel.showUpdateDisplayNameOverlay.observeAsState()
    val displayName = viewModel.displayName.observeAsState()

    MARketTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Account", style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp
                    ))
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
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Welcome ${displayName.value}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    text = "Sign Out",
                    onClick = { viewModel.toggleSignOutOverlay() },
                    color = Beige,
                    contentColor = Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    text = "Update Display Name",
                    onClick = { viewModel.toggleUpdateDisplayNameOverlay() },
                    color = Beige,
                    contentColor = Black
                )
                Spacer(modifier = Modifier.height(16.dp))


                PrimaryButton(
                    text = "Delete Account",
                    onClick = { viewModel.toggleDeleteAccountOverlay() },
                    color = Color.Red,
                    modifier = Modifier.shadow(elevation = 16.dp, shape = RoundedCornerShape(32))
                )
                Spacer(modifier = Modifier.height(16.dp))



                if (showDeleteAccountDialog.value == true) {
                    ConfirmDialog(
                        text = "Are you sure you want to delete your account?",
                        type = "Delete",
                        onConfirm = { viewModel.deleteAccount(navigate) },
                        onDismiss = { viewModel.toggleDeleteAccountOverlay() }
                    )
                }

                if (showSignOutDialog.value == true) {
                    ConfirmDialog(
                        text = "Are you sure you want to sign out?",
                        type = "Sign Out",
                        onConfirm = { viewModel.signOut(navigate) },
                        onDismiss = { viewModel.toggleSignOutOverlay() },
                        color = DarkOrange
                    )
                }

                if (showUpdateDisplayNameDialog.value == true) {
                    UpdateDisplayNameDialog(
                        onDismiss = { viewModel.toggleUpdateDisplayNameOverlay() },
                        onConfirm = { viewModel.updateDisplayName(it) }
                    )
                }

            }
        }
    }
}

@Composable
fun ConfirmDialog(
    text: String,
    type: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    color: Color = Color.Red
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .background(Beige)
                    .padding(16.dp)
                    .width(IntrinsicSize.Max),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$type account?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Black
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Beige,
                            contentColor = Black
                        )
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = color,
                            contentColor = Color.White
                        )
                    ) {
                        Text(type)
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateDisplayNameDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .background(Beige)
                    .padding(16.dp)
                    .width(IntrinsicSize.Max)
            ) {
                Text(
                    text = "Update Display Name",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter a new display name:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Black
                )
                Spacer(modifier = Modifier.height(16.dp))

                var newDisplayName by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = newDisplayName,
                    onValueChange = { newDisplayName = it },
                    label = { Text("New Display Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Orange,
                        unfocusedBorderColor = Black,
                        focusedLabelColor = Black,
                        unfocusedLabelColor = Black,
                        cursorColor = Black,
                        focusedTextColor = Black,
                        unfocusedTextColor = Black
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Beige,
                            contentColor = Black
                        )
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirm(newDisplayName)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkOrange,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Confirm")
                    }
                }
            }

        }
    }
}