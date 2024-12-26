package com.example.market

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.firebase.ui.auth.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureFirebaseServices()
        setContent {
            mARketApp()
        }
    }
}

private fun configureFirebaseServices() {
    if (BuildConfig.DEBUG) {
        Firebase.auth.useEmulator(LOCALHOST, AUTH_PORT)
    }
}
