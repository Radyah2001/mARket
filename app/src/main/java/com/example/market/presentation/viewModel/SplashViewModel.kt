package com.example.market.presentation.viewModel

import com.example.market.HOME_SCREEN
import com.example.market.SIGN_IN_SCREEN
import com.example.market.SPLASH_SCREEN
import com.example.market.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService
) : mARketAppViewModel() {

    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        if (accountService.hasUser()) openAndPopUp(HOME_SCREEN, SPLASH_SCREEN)
        else openAndPopUp(SIGN_IN_SCREEN, SPLASH_SCREEN)
    }
}