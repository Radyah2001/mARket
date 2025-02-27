package com.example.market.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.market.R
import com.example.market.presentation.buttons.AuthenticationButton
import com.example.market.presentation.viewModel.SignUpViewModel
import com.example.market.ui.theme.CustomTextField
import com.example.market.ui.theme.Logo
import com.example.market.ui.theme.MARketTheme
import com.example.market.ui.theme.PrimaryButton
import com.example.market.ui.theme.Teal

@Composable
fun SignUpScreen(
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    val confirmPassword = viewModel.confirmPassword.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val spacing = 8.dp
    val spacing2 = 24.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .background(Teal)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing2)
        )

        Logo()

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing2)
        )

        CustomTextField(
            value = email.value,
            onValueChange = { viewModel.updateEmail(it) },
            placeholder = stringResource(R.string.email),
            leadingIcon = Icons.Default.Email,
            contentDescription = "Email",
        )

        CustomTextField(
            value = password.value,
            onValueChange = { viewModel.updatePassword(it) },
            placeholder = stringResource(R.string.password),
            leadingIcon = Icons.Default.Lock,
            contentDescription = "Password",
        )

        CustomTextField(
            value = confirmPassword.value,
            onValueChange = { viewModel.updateConfirmPassword(it) },
            placeholder = stringResource(R.string.confirm_password),
            leadingIcon = Icons.Default.Lock,
            contentDescription = "Confirm Password",
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing2)
        )

        PrimaryButton(
            text = stringResource(R.string.sign_up),
            onClick = { viewModel.onSignUpClick(openAndPopUp) }
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        Text(text = "-  or  -", color = Color.White, fontSize = 16.sp)
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )

        AuthenticationButton(R.string.sign_up_with_google) { credential ->
            viewModel.onSignUpWithGoogle(credential, openAndPopUp)
        }
        errorMessage?.let { error ->
            Snackbar(
                action = {
                    TextButton(onClick = { viewModel.clearErrorMessage() }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(text = error)
            }

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AuthSignUpPreview() {
    MARketTheme {
        SignUpScreen({ _, _ -> })
    }
}