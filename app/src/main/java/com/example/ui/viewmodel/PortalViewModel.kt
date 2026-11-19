package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AccessRequest
import com.example.data.model.AiChatMessage
import com.example.data.model.Enquiry
import com.example.data.model.JobApplication
import com.example.data.model.PortalMessage
import com.example.data.model.SupportTicket
import com.example.data.model.UserProfile
import com.example.data.repository.AiAssistantRepository
import com.example.data.repository.TrueRyzeRepository
import com.example.firebase.friendlyFirestoreMessage
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class PortalViewModel(
    private val repository: TrueRyzeRepository,
    private val aiRepository: AiAssistantRepository,
    private val currentUserId: String
) : ViewModel() {

    private fun <T> Flow<T>.toSafeUiState(label: String): StateFlow<UiState<T>> =
        this.map<T, UiState<T>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing $label", rootError)
                emit(UiState.Error(friendlyFirestoreMessage(rootError, "Unable to load $label.")))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val profile: StateFlow<UiState<UserProfile?>> =
        repository.observeUserProfile(currentUserId).toSafeUiState("profile")

    val enquiries: StateFlow<UiState<List<Enquiry>>> =
        repository.observeEnquiries(currentUserId).toSafeUiState("enquiries")

    val applications: StateFlow<UiState<List<JobApplication>>> =
        repository.observeApplications(currentUserId).toSafeUiState("applications")

    val accessRequests: StateFlow<UiState<List<AccessRequest>>> =
        repository.observeAccessRequests(currentUserId).toSafeUiState("access requests")

    val supportTickets: StateFlow<UiState<List<SupportTicket>>> =
        repository.observeSupportTickets(currentUserId).toSafeUiState("support tickets")

    val portalMessages: StateFlow<UiState<List<PortalMessage>>> =
        repository.observePortalMessages(currentUserId).toSafeUiState("messages")

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<Pair<Boolean, String>?>(null)
    val feedbackMessage: StateFlow<Pair<Boolean, String>?> = _feedbackMessage.asStateFlow()

    private val _aiMessages = MutableStateFlow(
        listOf(
            AiChatMessage(
                id = "welcome",
                isUser = false,
                text = "Ask about your submitted enquiries, website development vs. maintenance scope, plans, or how Client IDs & loyalty points work."
            )
        )
    )
    val aiMessages: StateFlow<List<AiChatMessage>> = _aiMessages.asStateFlow()

    private val _aiBusy = MutableStateFlow(false)
    val aiBusy: StateFlow<Boolean> = _aiBusy.asStateFlow()

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    fun ensureProfile(displayName: String, email: String, photoUrl: String?) {
        viewModelScope.launch {
            repository.ensureUserProfile(currentUserId, displayName, email, photoUrl)
                .onFailure { err ->
                    Log.w(TAG, "Profile sync note: ${err.message}")
                }
        }
    }

    fun updateBusinessName(businessName: String) {
        if (_isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            repository.updateBusinessName(currentUserId, businessName)
                .onSuccess {
                    _feedbackMessage.value = true to "Business profile updated."
                }
                .onFailure { err ->
                    _feedbackMessage.value = false to friendlyFirestoreMessage(err, "Could not update business name.")
                }
            _isSubmitting.value = false
        }
    }

    fun submitEnquiry(enquiry: Enquiry, onSuccessReset: () -> Unit) {
        if (_isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            repository.submitEnquiry(currentUserId, enquiry)
                .onSuccess {
                    _feedbackMessage.value = true to "Thank you. Your enquiry has been saved and our team will reply by email."
                    onSuccessReset()
                }
                .onFailure { err ->
                    _feedbackMessage.value = false to friendlyFirestoreMessage(err, "Could not submit enquiry.")
                }
            _isSubmitting.value = false
        }
    }

    fun archiveEnquiry(enquiryId: String) {
        viewModelScope.launch {
            repository.archiveEnquiry(currentUserId, enquiryId)
                .onSuccess {
                    _feedbackMessage.value = true to "Enquiry archived."
                }
                .onFailure { err ->
                    _feedbackMessage.value = false to friendlyFirestoreMessage(err, "Could not archive enquiry.")
                }
        }
    }

    fun submitApplication(application: JobApplication, onSuccessReset: () -> Unit) {
        if (_isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            repository.submitApplication(currentUserId, application)
                .onSuccess {
                    _feedbackMessage.value = true to "Application saved. We'll reach out if a matching role opens."
                    onSuccessReset()
                }
                .onFailure { err ->
                    _feedbackMessage.value = false to friendlyFirestoreMessage(err, "Could not submit application.")
                }
            _isSubmitting.value = false
        }
    }

    fun submitAccessRequest(request: AccessRequest, onSuccessReset: () -> Unit) {
        if (_isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            repository.submitAccessRequest(currentUserId, request)
                .onSuccess {
                    _feedbackMessage.value = true to "Client access request submitted for TRUE RYZE verification."
                    onSuccessReset()
                }
                .onFailure { err ->
                    _feedbackMessage.value = false to friendlyFirestoreMessage(err, "Could not send access request.")
                }
            _isSubmitting.value = false
        }
    }

    fun submitSupportTicket(ticket: SupportTicket, onSuccessReset: () -> Unit) {
        if (_isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            repository.submitSupportTicket(currentUserId, ticket)
                .onSuccess {
                    _feedbackMessage.value = true to "Support ticket logged in your portal."
                    onSuccessReset()
                }
                .onFailure { err ->
                    _feedbackMessage.value = false to friendlyFirestoreMessage(err, "Could not submit support ticket.")
                }
            _isSubmitting.value = false
        }
    }

    fun closeSupportTicket(ticketId: String) {
        viewModelScope.launch {
            repository.closeSupportTicket(currentUserId, ticketId)
                .onSuccess {
                    _feedbackMessage.value = true to "Support ticket marked closed."
                }
                .onFailure { err ->
                    _feedbackMessage.value = false to friendlyFirestoreMessage(err, "Could not close ticket.")
                }
        }
    }

    fun sendPortalMessage(body: String, onSuccessReset: () -> Unit) {
        if (_isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            repository.sendPortalMessage(currentUserId, body)
                .onSuccess {
                    _feedbackMessage.value = true to "Message saved to your project thread."
                    onSuccessReset()
                }
                .onFailure { err ->
                    _feedbackMessage.value = false to friendlyFirestoreMessage(err, "Could not send message.")
                }
            _isSubmitting.value = false
        }
    }

    fun askAiAssistant(question: String) {
        val clean = question.trim()
        if (clean.isEmpty() || _aiBusy.value) return

        val userMsgId = UUID.randomUUID().toString()
        val aiMsgId = UUID.randomUUID().toString()
        _aiMessages.value = _aiMessages.value +
            AiChatMessage(id = userMsgId, isUser = true, text = clean) +
            AiChatMessage(id = aiMsgId, isUser = false, text = "", isStreaming = true)
        _aiBusy.value = true

        val accountContext = buildAccountContext()

        viewModelScope.launch {
            try {
                var gotText = false
                aiRepository.streamReply(clean, accountContext).collect { partial ->
                    gotText = partial.isNotBlank()
                    updateAiMessage(aiMsgId) {
                        it.copy(text = partial, isStreaming = true, isError = false)
                    }
                }
                if (gotText) {
                    updateAiMessage(aiMsgId) { it.copy(isStreaming = false) }
                } else {
                    updateAiMessage(aiMsgId) {
                        it.copy(
                            text = "The assistant returned an empty response. Please try phrasing your question differently.",
                            isStreaming = false,
                            isError = true
                        )
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI Assistant timed out", e)
                updateAiMessage(aiMsgId) {
                    it.copy(
                        text = "Response timed out after 60 seconds. Please try again.",
                        isStreaming = false,
                        isError = true
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Assistant error", e)
                updateAiMessage(aiMsgId) {
                    it.copy(
                        text = e.localizedMessage ?: "Assistant is currently unavailable. Please check your connection.",
                        isStreaming = false,
                        isError = true
                    )
                }
            } finally {
                _aiBusy.value = false
            }
        }
    }

    private fun updateAiMessage(id: String, transform: (AiChatMessage) -> AiChatMessage) {
        _aiMessages.value = _aiMessages.value.map { msg ->
            if (msg.id == id) transform(msg) else msg
        }
    }

    private fun buildAccountContext(): String {
        val prof = (profile.value as? UiState.Success)?.data
        val enqList = (enquiries.value as? UiState.Success)?.data.orEmpty()
        val reqList = (accessRequests.value as? UiState.Success)?.data.orEmpty()
        val supList = (supportTickets.value as? UiState.Success)?.data.orEmpty()

        return buildString {
            if (prof != null) {
                appendLine("User: ${prof.displayName} | Business: ${prof.businessName ?: "Not set"} | Status: ${prof.clientStatus} | Tier: ${prof.tier} | Points: ${prof.loyaltyPoints} | Bonds: ${prof.bonds}")
            } else {
                appendLine("User signed in; profile pending creation.")
            }
            appendLine("Submitted Enquiries (${enqList.size}):")
            enqList.take(5).forEach {
                appendLine("- ${it.serviceType} (Plan: ${it.plan ?: "Not sure"}, Status: ${it.status}): ${it.message.take(120)}")
            }
            appendLine("Access Requests (${reqList.size}):")
            reqList.take(3).forEach {
                appendLine("- ${it.businessName ?: "Personal"} (${it.status}): ${it.dealDetails.take(100)}")
            }
            appendLine("Support Tickets (${supList.size}):")
            supList.take(5).forEach {
                appendLine("- [${it.priority}] ${it.category} (${it.status}): ${it.message.take(100)}")
            }
        }
    }

    private companion object {
        const val TAG = "PortalViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
