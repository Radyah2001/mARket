package com.example.market.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
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
import com.example.market.presentation.viewModel.SignInViewModel
import com.example.market.ui.theme.CustomTextField
import com.example.market.ui.theme.Logo
import com.example.market.ui.theme.MARketTheme
import com.example.market.ui.theme.PrimaryButton
import com.example.market.ui.theme.Teal


@Composable
fun SignInScreen(
    openAndPopUp: (String, String) -> Unit,
    navigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val spacing = 8.dp
    val spacing2 = 24.dp

    MARketTheme {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(Teal)
                .padding(16.dp),
            containerColor = Teal
        ) { paddingValues ->

            Column(
                modifier = modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .wrapContentSize(align = Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                Spacer(modifier = Modifier.height(spacing2))
                Logo()
                Spacer(modifier = Modifier.height(spacing2))

                Text(
                    text = stringResource(R.string.sign_in),
                    fontSize = 24.sp,
                    color = Color.White,
                    modifier = modifier.padding(16.dp, 0.dp)
                        .align(Alignment.CenterHorizontally)
                )


                Spacer(modifier = Modifier.height(spacing2))

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

                Spacer(modifier = Modifier.height(spacing2))

                PrimaryButton(
                    text = stringResource(R.string.login),
                    onClick = { viewModel.onSignInClick(openAndPopUp) }
                )
                Spacer(
                    modifier = modifier.height(4.dp)
                )

                Text("-  or  -", color = Color.White, fontSize = 16.sp)

                Spacer(
                    modifier = modifier.height(4.dp)
                )

                AuthenticationButton(buttonText = R.string.sign_in_with_google) { credential ->
                    viewModel.onSignInWithGoogle(credential, openAndPopUp)
                }

                TextButton(modifier = modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White,
                        containerColor = Color.Transparent

                    ),
                    onClick = { viewModel.onSignUpClick(navigate) }) {
                    Text(text = stringResource(R.string.sign_up_description), fontSize = 16.sp)
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
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AuthSignInPreview() {
    MARketTheme {
        SignInScreen({ _, _ -> }, { _ -> })
    }
}