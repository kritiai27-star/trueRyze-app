package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val businessName: String? = null,
    val clientStatus: String = "visitor",
    val tier: String = "Starter",
    val bonds: Long = 0L,
    val loyaltyPoints: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> {
        val raw = mutableMapOf<String, Any?>(
            "userId" to userId,
            "displayName" to displayName.take(100),
            "email" to email.take(160),
            "photoUrl" to photoUrl?.take(500),
            "businessName" to businessName?.takeIf { it.isNotBlank() }?.take(120),
            "clientStatus" to "visitor",
            "tier" to "Starter",
            "bonds" to 0L,
            "loyaltyPoints" to 0L,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }
}

@Keep
@IgnoreExtraProperties
data class Enquiry(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val businessName: String? = null,
    val serviceType: String = "Website development",
    val plan: String? = null,
    val budget: String? = null,
    val message: String = "",
    val status: String = "submitted",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> {
        val raw = mutableMapOf<String, Any?>(
            "name" to name.trim().take(100),
            "email" to email.trim().take(160),
            "businessName" to businessName?.trim()?.takeIf { it.isNotBlank() }?.take(120),
            "serviceType" to serviceType,
            "plan" to plan?.takeIf { it.isNotBlank() },
            "budget" to budget?.takeIf { it.isNotBlank() }?.take(60),
            "message" to message.trim().take(2000),
            "status" to "submitted",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }
}

@Keep
@IgnoreExtraProperties
data class JobApplication(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String? = null,
    val portfolioUrl: String? = null,
    val message: String = "",
    val status: String = "received",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> {
        val raw = mutableMapOf<String, Any?>(
            "name" to name.trim().take(100),
            "email" to email.trim().take(160),
            "role" to role?.trim()?.takeIf { it.isNotBlank() }?.take(120),
            "portfolioUrl" to portfolioUrl?.trim()?.takeIf { it.isNotBlank() }?.take(300),
            "message" to message.trim().take(2000),
            "status" to "received",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }
}

@Keep
@IgnoreExtraProperties
data class AccessRequest(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val businessName: String? = null,
    val dealDetails: String = "",
    val status: String = "pending_review",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> {
        val raw = mutableMapOf<String, Any?>(
            "name" to name.trim().take(100),
            "email" to email.trim().take(160),
            "businessName" to businessName?.trim()?.takeIf { it.isNotBlank() }?.take(120),
            "dealDetails" to dealDetails.trim().take(2000),
            "status" to "pending_review",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }
}

@Keep
@IgnoreExtraProperties
data class SupportTicket(
    @DocumentId val id: String = "",
    val category: String = "Website issue",
    val priority: String = "Normal",
    val projectRef: String? = null,
    val message: String = "",
    val status: String = "open",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> {
        val raw = mutableMapOf<String, Any?>(
            "category" to category,
            "priority" to priority,
            "projectRef" to projectRef?.trim()?.takeIf { it.isNotBlank() }?.take(80),
            "message" to message.trim().take(2000),
            "status" to "open",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }
}

@Keep
@IgnoreExtraProperties
data class PortalMessage(
    @DocumentId val id: String = "",
    val body: String = "",
    val sender: String = "client",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "body" to body.trim().take(2000),
        "sender" to "client",
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

data class AiChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val isStreaming: Boolean = false,
    val isError: Boolean = false
)
