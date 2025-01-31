package com.example.market.presentation.viewModel

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.example.market.HOME_SCREEN
import com.example.market.SIGN_IN_SCREEN
import com.example.market.SIGN_UP_SCREEN
import com.example.market.model.service.AccountService
import com.example.market.presentation.buttons.isValidEmail
import com.example.market.presentation.buttons.isValidPassword
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService
) : mARketAppViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            try {
                if (!_email.value.isValidEmail()) {
                    throw IllegalArgumentException("Invalid email format")
                }

                if (!_password.value.isValidPassword()) {
                    throw IllegalArgumentException("Invalid password format")
                }

                if (_password.value != _confirmPassword.value) {
                    throw IllegalArgumentException("Passwords do not match")
                }

                accountService.signUp(email.value, password.value)
                openAndPopUp(HOME_SCREEN, SIGN_UP_SCREEN)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun onSignUpWithGoogle(credential: Credential, openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                accountService.linkAccountWithGoogle(googleIdTokenCredential.idToken)
                openAndPopUp(HOME_SCREEN, SIGN_UP_SCREEN)
            } else {
                Log.e(ERROR_TAG, "Unexpected type of credential")
            }
        }
    }
}