package com.example.market.presentation.viewModel

import com.example.market.ACCOUNT_SCREEN
import com.example.market.HOME_SCREEN
import com.example.market.SIGN_IN_SCREEN
import com.example.market.SIGN_UP_SCREEN
import com.example.market.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountService: AccountService
) : mARketAppViewModel() {

    val user = accountService.currentUserId

    fun deleteAccount(clearAndNavigate: (String) -> Unit) {
        launchCatching {
            accountService.deleteAccount()
            clearAndNavigate(SIGN_IN_SCREEN)
        }
    }

    fun signOut(clearAndNavigate: (String) -> Unit) {
        launchCatching {
            accountService.signOut()
            clearAndNavigate(SIGN_IN_SCREEN)
        }
    }
}