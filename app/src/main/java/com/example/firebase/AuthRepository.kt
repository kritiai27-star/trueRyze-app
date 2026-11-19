package com.example.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed interface AuthResult {
    data class Success(val user: FirebaseUser) : AuthResult
    data class Cancelled(val message: String) : AuthResult
    data class Failure(val message: String) : AuthResult
}

class AuthRepository(
    private val auth: FirebaseAuth = Firebase.auth
) {
    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun signInWithGoogle(activityContext: Context): AuthResult {
        return try {
            val webClientId = activityContext.getString(R.string.default_web_client_id)
            val credentialManager = CredentialManager.create(activityContext)

            // Dedicated interactive sign-in request with GetSignInWithGoogleOption only
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )

            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                val user = authResult.user
                    ?: return AuthResult.Failure("Google sign-in completed without a user session.")
                AuthResult.Success(user)
            } else {
                AuthResult.Failure("Unsupported credential type returned by device.")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "Interactive Google Sign-In cancelled or certificate/credential flow rejected", e)
            AuthResult.Cancelled("Sign-in was cancelled. Please choose your Google account to continue.")
        } catch (e: NoCredentialException) {
            Log.w(TAG, "No Google credential available on device", e)
            AuthResult.Failure("No Google account found on this device. Add a Google account in device settings and retry.")
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential Manager error during Google Sign-In", e)
            AuthResult.Failure(e.localizedMessage ?: "Google Sign-In could not be completed.")
        } catch (e: Exception) {
            Log.e(TAG, "Firebase Google authentication failed", e)
            AuthResult.Failure(e.localizedMessage ?: "Sign-in failed. Please try again.")
        }
    }

    suspend fun trySilentSignIn(activityContext: Context): AuthResult? {
        if (auth.currentUser != null) return AuthResult.Success(auth.currentUser!!)
        return try {
            val webClientId = activityContext.getString(R.string.default_web_client_id)
            val credentialManager = CredentialManager.create(activityContext)
            // Separate request using GetGoogleIdOption only for silent auto sign-in
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val tokenCred = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCred = GoogleAuthProvider.getCredential(tokenCred.idToken, null)
                val user = auth.signInWithCredential(firebaseCred).await().user
                user?.let { AuthResult.Success(it) }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d(TAG, "Silent Google Sign-In unavailable: ${e.javaClass.simpleName}")
            null
        }
    }

    suspend fun signOut(context: Context) {
        withContext(NonCancellable) {
            runCatching {
                CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
            }.onFailure {
                Log.w(TAG, "Failed to clear credential state on sign-out", it)
            }
            auth.signOut()
        }
    }

    private companion object {
        const val TAG = "AuthRepository"
    }
}
