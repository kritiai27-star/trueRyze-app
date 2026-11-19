package com.example.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.cancellation.CancellationException

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(
    exception: Throwable,
    operationType: OperationType,
    path: String?
): String {
    val causes = generateSequence(exception) { it.cause }
    val rootError = causes.firstOrNull { it is FirebaseFirestoreException }
        ?: causes.firstOrNull { it !is CancellationException }
        ?: exception

    val currentUser = runCatching { Firebase.auth.currentUser }.getOrNull()

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", rootError.message ?: rootError.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

fun friendlyFirestoreMessage(exception: Throwable, fallback: String): String {
    val firestoreException = generateSequence(exception) { it.cause }
        .filterIsInstance<FirebaseFirestoreException>()
        .firstOrNull()
    val raw = firestoreException?.message ?: exception.localizedMessage.orEmpty()
    return when {
        raw.contains("Quota exceeded", ignoreCase = true) ->
            "Daily free-tier database quota reached. Quota resets tomorrow."
        firestoreException?.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ->
            "Permission denied. Please sign in with your Google account and check your fields."
        firestoreException?.code == FirebaseFirestoreException.Code.UNAVAILABLE ->
            "Network unavailable. Your changes could not reach the server."
        else -> fallback
    }
}
