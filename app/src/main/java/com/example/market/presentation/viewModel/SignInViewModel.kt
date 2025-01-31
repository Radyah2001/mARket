package com.example.market.presentation.viewModel

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.example.market.HOME_SCREEN
import com.example.market.SIGN_IN_SCREEN
import com.example.market.SIGN_UP_SCREEN
import com.example.market.model.service.AccountService
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountService: AccountService
) : mARketAppViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            try {
                accountService.signIn(email.value, password.value)
                openAndPopUp(HOME_SCREEN, SIGN_IN_SCREEN)
            } catch (e: Exception) {
                if (e.message.equals("Login failed: Given String is empty or null") ) {
                    _errorMessage.value = "Email and password cannot be empty."
                } else {
                    _errorMessage.value = e.message
                }
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun onSignInWithGoogle(credential: Credential, openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                openAndPopUp(HOME_SCREEN, SIGN_IN_SCREEN)
            } else {
                Log.e(ERROR_TAG, "Unexpected type of credential")
            }
        }
    }

    fun onSignUpClick(navigate: (String) -> Unit) {
        navigate(SIGN_UP_SCREEN)
    }
}