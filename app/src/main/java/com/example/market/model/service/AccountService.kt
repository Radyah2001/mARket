package com.example.market.model.service

import com.example.market.model.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUser: Flow<User?>
    val currentUserId: String
    fun hasUser(): Boolean
    fun getUserProfile(): User
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun updateDisplayName(newDisplayName: String)
    suspend fun linkAccountWithGoogle(idToken: String)
    suspend fun signInWithGoogle(idToken: String)
    suspend fun signOut()
    suspend fun deleteAccount()
}