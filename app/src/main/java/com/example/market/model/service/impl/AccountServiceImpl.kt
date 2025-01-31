package com.example.market.model.service.impl

import com.example.market.model.User
import com.example.market.model.service.AccountService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor() : AccountService {

    override val currentUser: Flow<User?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid) })
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override fun getUserProfile(): User {
        return Firebase.auth.currentUser.toMARketUser()
    }

    override suspend fun signIn(email: String, password: String) {
        try {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            // Handle login failure
            when (e) {
                is FirebaseAuthInvalidUserException -> {
                    // User does not exist
                    throw Exception("No user found with this email.")
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    // Invalid credentials (wrong password or malformed email)
                    throw Exception("Invalid email or password. Please try again.")
                }
                is FirebaseNetworkException -> {
                    // Network error
                    throw Exception("Network error. Please check your connection and try again.")
                }
                else -> {
                    // General error
                    throw Exception("Login failed: ${e.message}")
                }
            }
        }
    }

    override suspend fun signUp(email: String, password: String) {
        try {
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            // Handle sign-up failure
            when (e) {
                is FirebaseAuthUserCollisionException -> {
                    // Email already in use
                    throw Exception("This email is already in use.")
                }
                is FirebaseAuthWeakPasswordException -> {
                    // Weak password
                    throw Exception("The password is too weak. Please choose a stronger password.")
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    // Invalid email format
                    throw Exception("The email address is badly formatted.")
                }
                else -> {
                    // General error
                    throw Exception("Sign-up failed: ${e.message}")
                }
            }
        }
    }

    override suspend fun updateDisplayName(newDisplayName: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = newDisplayName
        }

        Firebase.auth.currentUser!!.updateProfile(profileUpdates).await()
    }

    override suspend fun linkAccountWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.currentUser!!.linkWithCredential(firebaseCredential).await()
    }

    override suspend fun signInWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(firebaseCredential).await()
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    override suspend fun deleteAccount() {
        Firebase.auth.currentUser!!.delete().await()
    }

    private fun FirebaseUser?.toMARketUser(): User {
        return if (this == null) User() else User(
            id = this.uid,
            email = this.email ?: "",
            provider = this.providerId,
            displayName = this.displayName ?: ""
        )
    }
}