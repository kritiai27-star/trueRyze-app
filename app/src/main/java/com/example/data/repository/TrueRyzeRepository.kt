package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.AccessRequest
import com.example.data.model.Enquiry
import com.example.data.model.JobApplication
import com.example.data.model.PortalMessage
import com.example.data.model.SupportTicket
import com.example.data.model.UserProfile
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class TrueRyzeRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String =
        auth.currentUser?.uid ?: error("Google Sign-In required before accessing cloud records.")

    fun observeUserProfile(userId: String): Flow<UserProfile?> =
        db.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(
                    UserProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }

    fun observeEnquiries(userId: String): Flow<List<Enquiry>> =
        db.collection("users").document(userId)
            .collection("enquiries")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    Enquiry::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt?.seconds ?: 0L }
            }

    fun observeApplications(userId: String): Flow<List<JobApplication>> =
        db.collection("users").document(userId)
            .collection("applications")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    JobApplication::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt?.seconds ?: 0L }
            }

    fun observeAccessRequests(userId: String): Flow<List<AccessRequest>> =
        db.collection("users").document(userId)
            .collection("access_requests")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    AccessRequest::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt?.seconds ?: 0L }
            }

    fun observeSupportTickets(userId: String): Flow<List<SupportTicket>> =
        db.collection("users").document(userId)
            .collection("support_tickets")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    SupportTicket::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt?.seconds ?: 0L }
            }

    fun observePortalMessages(userId: String): Flow<List<PortalMessage>> =
        db.collection("users").document(userId)
            .collection("portal_messages")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    PortalMessage::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedBy { it.createdAt?.seconds ?: Long.MAX_VALUE }
            }

    suspend fun ensureUserProfile(
        userId: String,
        displayName: String,
        email: String,
        photoUrl: String?
    ): Result<Unit> = try {
        val uid = requireUserId()
        check(uid == userId) { "Authenticated user mismatch." }
        val docRef = db.collection("users").document(userId)
        val existing = docRef.get().await()
        val safeName = displayName.ifBlank { "TRUE RYZE User" }.take(100)
        val safeEmail = email.ifBlank { "client@trueryze.app" }.take(160)
        if (!existing.exists()) {
            val profile = UserProfile(
                userId = userId,
                displayName = safeName,
                email = safeEmail,
                photoUrl = photoUrl
            )
            docRef.set(profile.toCreateMap()).await()
        } else {
            val updates = mutableMapOf<String, Any>(
                "displayName" to safeName,
                "email" to safeEmail,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (!photoUrl.isNullOrBlank()) {
                updates["photoUrl"] = photoUrl.take(500)
            }
            docRef.update(updates).await()
        }
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.WRITE, "users/$userId")
        Result.failure(e)
    }

    suspend fun updateBusinessName(userId: String, businessName: String): Result<Unit> = try {
        val uid = requireUserId()
        check(uid == userId) { "Authenticated user mismatch." }
        val clean = businessName.trim().take(120)
        require(clean.isNotBlank()) { "Business name cannot be empty." }
        db.collection("users").document(userId)
            .update(
                mapOf(
                    "businessName" to clean,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "users/$userId")
        Result.failure(e)
    }

    suspend fun submitEnquiry(userId: String, enquiry: Enquiry): Result<Unit> = try {
        val uid = requireUserId()
        check(uid == userId) { "Authenticated user mismatch." }
        require(enquiry.name.isNotBlank() && enquiry.email.contains("@") && enquiry.message.isNotBlank()) {
            "Name, valid email, and project requirements are required."
        }
        val docRef = db.collection("users").document(userId)
            .collection("enquiries").document()
        docRef.set(enquiry.toCreateMap()).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, "users/$userId/enquiries")
        Result.failure(e)
    }

    suspend fun archiveEnquiry(userId: String, enquiryId: String): Result<Unit> = try {
        val uid = requireUserId()
        check(uid == userId)
        require(enquiryId.isNotBlank())
        val path = "users/$userId/enquiries/$enquiryId"
        db.document(path).update(
            mapOf(
                "status" to "archived",
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "users/$userId/enquiries/$enquiryId")
        Result.failure(e)
    }

    suspend fun submitApplication(userId: String, application: JobApplication): Result<Unit> = try {
        val uid = requireUserId()
        check(uid == userId)
        require(application.name.isNotBlank() && application.email.contains("@") && application.message.isNotBlank()) {
            "Name, valid email, and introduction are required."
        }
        val docRef = db.collection("users").document(userId)
            .collection("applications").document()
        docRef.set(application.toCreateMap()).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, "users/$userId/applications")
        Result.failure(e)
    }

    suspend fun submitAccessRequest(userId: String, request: AccessRequest): Result<Unit> = try {
        val uid = requireUserId()
        check(uid == userId)
        require(request.name.isNotBlank() && request.email.contains("@") && request.dealDetails.isNotBlank()) {
            "Name, email, and deal details are required."
        }
        val docRef = db.collection("users").document(userId)
            .collection("access_requests").document()
        docRef.set(request.toCreateMap()).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, "users/$userId/access_requests")
        Result.failure(e)
    }

    suspend fun submitSupportTicket(userId: String, ticket: SupportTicket): Result<Unit> = try {
        val uid = requireUserId()
        check(uid == userId)
        require(ticket.message.isNotBlank()) { "Please describe your support request." }
        val docRef = db.collection("users").document(userId)
            .collection("support_tickets").document()
        docRef.set(ticket.toCreateMap()).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, "users/$userId/support_tickets")
        Result.failure(e)
    }

    suspend fun closeSupportTicket(userId: String, ticketId: String): Result<Unit> = try {
        val uid = requireUserId()
        check(uid == userId)
        require(ticketId.isNotBlank())
        val path = "users/$userId/support_tickets/$ticketId"
        db.document(path).update(
            mapOf(
                "status" to "closed",
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "users/$userId/support_tickets/$ticketId")
        Result.failure(e)
    }

    suspend fun sendPortalMessage(userId: String, body: String): Result<Unit> = try {
        val uid = requireUserId()
        check(uid == userId)
        require(body.trim().isNotBlank()) { "Message cannot be empty." }
        val docRef = db.collection("users").document(userId)
            .collection("portal_messages").document()
        docRef.set(PortalMessage(body = body).toCreateMap()).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, "users/$userId/portal_messages")
        Result.failure(e)
    }
}
