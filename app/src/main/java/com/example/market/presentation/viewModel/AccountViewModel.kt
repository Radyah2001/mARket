package com.example.market.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.market.SIGN_IN_SCREEN
import com.example.market.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountService: AccountService
) : mARketAppViewModel() {

    val user = accountService.currentUserId
    val userProfile = accountService.getUserProfile()

    private val _displayName = MutableLiveData<String>(userProfile.displayName)
    val displayName: LiveData<String> = _displayName

    private val _showDeleteAccountOverlay = MutableLiveData<Boolean>(false)
    val showDeleteAccountOverlay: LiveData<Boolean> = _showDeleteAccountOverlay

    private val _showSignOutOverlay = MutableLiveData<Boolean>(false)
    val showSignOutOverlay: LiveData<Boolean> = _showSignOutOverlay

    private var _showUpdateDisplayNameOverlay = MutableLiveData<Boolean>(false)
    val showUpdateDisplayNameOverlay: LiveData<Boolean> = _showUpdateDisplayNameOverlay

    fun toggleUpdateDisplayNameOverlay() {
        _showUpdateDisplayNameOverlay.value = _showUpdateDisplayNameOverlay.value != true
    }

    fun toggleDeleteAccountOverlay() {
        _showDeleteAccountOverlay.value = _showDeleteAccountOverlay.value != true
    }

    fun toggleSignOutOverlay() {
        _showSignOutOverlay.value = _showSignOutOverlay.value != true
    }

    fun deleteAccount(clearAndNavigate: (String) -> Unit) {
        launchCatching {
            accountService.deleteAccount()
            clearAndNavigate(SIGN_IN_SCREEN)
        }
    }

    fun updateDisplayName(newDisplayName: String) {
        launchCatching {
            _displayName.value = newDisplayName
            accountService.updateDisplayName(newDisplayName)

        }
    }

    fun signOut(clearAndNavigate: (String) -> Unit) {
        launchCatching {
            accountService.signOut()
            clearAndNavigate(SIGN_IN_SCREEN)
        }
    }
}