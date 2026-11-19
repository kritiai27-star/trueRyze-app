package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AccessRequest
import com.example.data.model.AiChatMessage
import com.example.data.model.Enquiry
import com.example.data.model.JobApplication
import com.example.data.model.PortalMessage
import com.example.data.model.SupportTicket
import com.example.data.model.TrueRyzeCatalog
import com.example.data.model.UserProfile
import com.example.ui.components.EyebrowText
import com.example.ui.components.GlassCard
import com.example.ui.components.InteractiveLampScene
import com.example.ui.components.PrimaryPillButton
import com.example.ui.components.ProjectLifecycleTracker
import com.example.ui.components.SecondaryPillButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.CobaltBright
import com.example.ui.theme.CobaltContainer
import com.example.ui.theme.CobaltIce
import com.example.ui.theme.CobaltPrimary
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GraphiteElevated
import com.example.ui.theme.GraphiteSurface
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSilver
import com.example.ui.theme.MintContainer
import com.example.ui.theme.NeonMint
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusWarn
import com.example.ui.theme.TrustGreen
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalSignedOutScreen(
    isAuthBusy: Boolean,
    authError: String?,
    onGoogleSignIn: () -> Unit,
    onOpenEnquiry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var lampLit by rememberSaveable { mutableStateOf(false) }
    var lampTemp by rememberSaveable { mutableFloatStateOf(0.5f) }
    var roleTab by rememberSaveable { mutableIntStateOf(0) }
    var showSampleMonitor by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                EyebrowText("Portal & Client Access")
                Text(
                    text = "Choose your portal.",
                    style = MaterialTheme.typography.displayMedium,
                    color = InkPrimary
                )

                InteractiveLampScene(
                    isLit = lampLit,
                    colorTemp = lampTemp,
                    onToggleLit = { lampLit = !lampLit },
                    onColorTempChange = { lampTemp = it }
                )

                val roles = listOf("Client", "Team", "Dealer")
                SecondaryTabRow(
                    selectedTabIndex = roleTab,
                    containerColor = GraphiteSurface,
                    contentColor = CobaltIce
                ) {
                    roles.forEachIndexed { idx, label ->
                        Tab(
                            selected = roleTab == idx,
                            onClick = { roleTab = idx },
                            text = { Text(label, style = MaterialTheme.typography.labelLarge) },
                            modifier = Modifier.testTag("portal_role_${label.lowercase()}")
                        )
                    }
                }

                when (roleTab) {
                    0 -> {
                        GlassCard(
                            accentBorder = if (lampLit) CobaltBright else SilverCardBorderFallback(),
                            glowColor = if (lampLit) CobaltPrimary else null
                        ) {
                            StatusPill(label = "Google Sign-In", isLive = true)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Client Portal & Access",
                                style = MaterialTheme.typography.headlineMedium,
                                color = InkPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Sign in with Google to submit client access requests, view your project enquiries, send support tickets, message TRUE RYZE, and use the live Gemini AI scoping assistant.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkSilver
                            )
                            if (authError != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = ErrorContainer,
                                    border = BorderStroke(1.dp, StatusError)
                                ) {
                                    Text(
                                        text = authError,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = StatusError,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            PrimaryPillButton(
                                text = if (isAuthBusy) "Signing in..." else "Continue with Google →",
                                onClick = onGoogleSignIn,
                                enabled = !isAuthBusy,
                                testTag = "portal_google_signin_btn",
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            SecondaryPillButton(
                                text = if (showSampleMonitor) "Hide sample monitor" else "Preview Client Monitor (sample)",
                                onClick = { showSampleMonitor = !showSampleMonitor },
                                testTag = "toggle_sample_monitor_btn",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        AnimatedVisibility(visible = showSampleMonitor) {
                            ClientSampleMonitorCard()
                        }

                        GlassCard {
                            Text("How client access works", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            val steps = listOf(
                                "Explore TRUE RYZE services & pricing",
                                "Share your project enquiry",
                                "Confirm scope & pricing in writing",
                                "Sign in with Google & request client access",
                                "TRUE RYZE verifies and approves",
                                "Receive your unique Client ID (TR-C-######)",
                                "Track 10 project stages, loyalty points & bonds"
                            )
                            steps.forEachIndexed { idx, s ->
                                Text(
                                    text = "${idx + 1}. $s",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (idx == 3) CobaltIce else InkSilver,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            SecondaryPillButton(
                                text = "Send a project enquiry →",
                                onClick = onOpenEnquiry,
                                testTag = "portal_goto_enquiry_btn",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    1 -> TeamPreviewSection()
                    else -> DealerPreviewSection()
                }
            }
        }
    }
}

@Composable
private fun SilverCardBorderFallback() = com.example.ui.theme.SilverBorder

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PortalAuthenticatedScreen(
    userEmail: String,
    userName: String,
    userPhotoUrl: String?,
    profileState: UiState<UserProfile?>,
    enquiriesState: UiState<List<Enquiry>>,
    applicationsState: UiState<List<JobApplication>>,
    accessRequestsState: UiState<List<AccessRequest>>,
    supportTicketsState: UiState<List<SupportTicket>>,
    messagesState: UiState<List<PortalMessage>>,
    aiMessages: List<AiChatMessage>,
    aiBusy: Boolean,
    isSubmitting: Boolean,
    feedbackMessage: Pair<Boolean, String>?,
    onClearFeedback: () -> Unit,
    onUpdateBusinessName: (String) -> Unit,
    onSubmitAccessRequest: (AccessRequest, () -> Unit) -> Unit,
    onArchiveEnquiry: (String) -> Unit,
    onSubmitSupportTicket: (SupportTicket, () -> Unit) -> Unit,
    onCloseSupportTicket: (String) -> Unit,
    onSendPortalMessage: (String, () -> Unit) -> Unit,
    onAskAi: (String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var lampLit by rememberSaveable { mutableStateOf(true) }
    var lampTemp by rememberSaveable { mutableFloatStateOf(0.5f) }
    var roleTab by rememberSaveable { mutableIntStateOf(0) }
    var clientSubTab by rememberSaveable { mutableIntStateOf(0) }
    var showSampleMonitor by rememberSaveable { mutableStateOf(false) }

    var bizDraft by rememberSaveable { mutableStateOf("") }
    var reqBusiness by rememberSaveable { mutableStateOf("") }
    var reqDetails by rememberSaveable { mutableStateOf("") }
    var supCategory by rememberSaveable { mutableStateOf(TrueRyzeCatalog.supportCategories.first()) }
    var supPriority by rememberSaveable { mutableStateOf(TrueRyzeCatalog.supportPriorities.first()) }
    var supRef by rememberSaveable { mutableStateOf("") }
    var supMessage by rememberSaveable { mutableStateOf("") }
    var msgBody by rememberSaveable { mutableStateOf("") }
    var aiPrompt by rememberSaveable { mutableStateOf("") }

    val profile = (profileState as? UiState.Success)?.data

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Authenticated Header Card
                GlassCard(
                    accentBorder = NeonMint.copy(alpha = 0.45f),
                    glowColor = CobaltPrimary
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (!userPhotoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = userPhotoUrl,
                                    contentDescription = stringResource(R.string.cd_profile_avatar),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Surface(
                                    shape = CircleShape,
                                    color = CobaltContainer,
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.VerifiedUser,
                                        contentDescription = null,
                                        tint = CobaltIce,
                                        modifier = Modifier.padding(11.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = profile?.displayName ?: userName.ifBlank { "TRUE RYZE Client" },
                                    style = MaterialTheme.typography.titleLarge,
                                    color = InkPrimary
                                )
                                Text(
                                    text = userEmail,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = InkMuted
                                )
                            }
                        }
                        IconButton(
                            onClick = onSignOut,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("portal_signout_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Logout,
                                contentDescription = "Sign out",
                                tint = InkSilver
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusPill(
                            label = "Status: ${profile?.clientStatus ?: "visitor"}",
                            isLive = profile?.clientStatus == "active"
                        )
                        StatusPill(
                            label = "Tier: ${profile?.tier ?: "Starter"}",
                            isLive = false
                        )
                        StatusPill(
                            label = "${profile?.loyaltyPoints ?: 0} pts · ${profile?.bonds ?: 0} bonds",
                            isLive = false
                        )
                    }
                }

                InteractiveLampScene(
                    isLit = lampLit,
                    colorTemp = lampTemp,
                    onToggleLit = { lampLit = !lampLit },
                    onColorTempChange = { lampTemp = it }
                )

                val roles = listOf("Client Live", "Team Preview", "Dealer Preview")
                SecondaryTabRow(
                    selectedTabIndex = roleTab,
                    containerColor = GraphiteSurface,
                    contentColor = CobaltIce
                ) {
                    roles.forEachIndexed { idx, label ->
                        Tab(
                            selected = roleTab == idx,
                            onClick = { roleTab = idx },
                            text = { Text(label, style = MaterialTheme.typography.labelLarge) }
                        )
                    }
                }

                if (feedbackMessage != null) {
                    val (ok, msg) = feedbackMessage
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (ok) MintContainer else ErrorContainer,
                        border = BorderStroke(1.dp, if (ok) NeonMint else StatusError),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (ok) TrustGreen else StatusError,
                                modifier = Modifier.weight(1f)
                            )
                            SecondaryPillButton(text = "OK", onClick = onClearFeedback)
                        }
                    }
                }

                when (roleTab) {
                    0 -> {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("AI Assistant", "Access & Records", "Support & Chat", "Sample Monitor").forEachIndexed { idx, label ->
                                TrChip(
                                    label = label,
                                    selected = clientSubTab == idx,
                                    onClick = { clientSubTab = idx }
                                )
                            }
                        }

                        when (clientSubTab) {
                            // 0: Live Firebase AI Logic Assistant
                            0 -> {
                                GlassCard(
                                    accentBorder = CobaltBright.copy(alpha = 0.5f),
                                    glowColor = CobaltPrimary
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = NeonMint)
                                        Text(
                                            text = "TRUE RYZE AI Scoping Assistant",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = InkPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Powered by Gemini via Firebase AI Logic. Grounded in your submitted records and TRUE RYZE's active services.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = InkMuted
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        aiMessages.forEach { msg ->
                                            Surface(
                                                shape = RoundedCornerShape(14.dp),
                                                color = if (msg.isUser) CobaltContainer else if (msg.isError) ErrorContainer else GraphiteElevated,
                                                border = BorderStroke(
                                                    1.dp,
                                                    when {
                                                        msg.isError -> StatusError
                                                        msg.isUser -> CobaltBright.copy(alpha = 0.45f)
                                                        else -> GlassBorder
                                                    }
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Text(
                                                        text = if (msg.isUser) "You" else "TRUE RYZE Assistant",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (msg.isUser) CobaltIce else NeonMint
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    if (msg.isStreaming && msg.text.isEmpty()) {
                                                        CircularProgressIndicator(
                                                            modifier = Modifier.size(18.dp),
                                                            strokeWidth = 2.dp,
                                                            color = CobaltIce
                                                        )
                                                    } else {
                                                        Text(
                                                            text = msg.text,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = if (msg.isError) StatusError else InkPrimary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        TrTextField(
                                            value = aiPrompt,
                                            onValueChange = { aiPrompt = it },
                                            label = "Ask about plans, scope, or your enquiries",
                                            testTag = "ai_prompt_input",
                                            modifier = Modifier.weight(1f)
                                        )
                                        Surface(
                                            onClick = {
                                                val q = aiPrompt
                                                aiPrompt = ""
                                                onAskAi(q)
                                            },
                                            enabled = !aiBusy && aiPrompt.isNotBlank(),
                                            shape = CircleShape,
                                            color = CobaltPrimary,
                                            modifier = Modifier
                                                .size(52.dp)
                                                .testTag("ai_send_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Rounded.Send,
                                                contentDescription = stringResource(R.string.cd_send_message),
                                                tint = InkPrimary,
                                                modifier = Modifier.padding(14.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // 1: Client Access Requests + Saved Enquiries & Applications
                            1 -> {
                                val enquiriesList = (enquiriesState as? UiState.Success)?.data.orEmpty()
                                val accessList = (accessRequestsState as? UiState.Success)?.data.orEmpty()
                                val liveStageIndex = TrueRyzeCatalog.deriveClientLifecycleStage(
                                    clientStatus = profile?.clientStatus,
                                    hasEnquiry = enquiriesList.isNotEmpty(),
                                    hasAccessRequest = accessList.isNotEmpty()
                                )

                                GlassCard(accentBorder = CobaltBright.copy(alpha = 0.45f)) {
                                    EyebrowText("Your account lifecycle")
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "10-Stage Project Tracker",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = InkPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = when {
                                            liveStageIndex >= 2 -> "Your client profile is active. Delivery stages update as TRUE RYZE confirms milestones."
                                            liveStageIndex == 1 -> "Your client access request is saved and awaiting TRUE RYZE verification."
                                            liveStageIndex == 0 -> "Your project enquiry is logged. Submit an access request once scope is agreed."
                                            else -> "Submit an enquiry or client access request below to begin Stage 01."
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = InkSilver
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    ProjectLifecycleTracker(
                                        currentStageIndex = liveStageIndex,
                                        testTagPrefix = "live_client_lifecycle"
                                    )
                                }

                                GlassCard {
                                    Text("Business profile", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Current business: ${profile?.businessName ?: "Not set"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = CobaltIce
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TrTextField(
                                        value = bizDraft,
                                        onValueChange = { bizDraft = it },
                                        label = "Update business name",
                                        testTag = "portal_biz_input"
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    SecondaryPillButton(
                                        text = "Save business name",
                                        enabled = !isSubmitting && bizDraft.isNotBlank(),
                                        onClick = {
                                            onUpdateBusinessName(bizDraft)
                                            bizDraft = ""
                                        },
                                        testTag = "portal_save_biz_btn",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                GlassCard(accentBorder = CobaltBright.copy(alpha = 0.4f)) {
                                    Text("Request client access", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Once your deal with TRUE RYZE is done, submit your deal details to receive your TR-C-###### Client ID.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = InkSilver
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    TrTextField(
                                        value = reqBusiness,
                                        onValueChange = { reqBusiness = it },
                                        label = "Business name",
                                        testTag = "access_business_input"
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TrTextField(
                                        value = reqDetails,
                                        onValueChange = { reqDetails = it },
                                        label = "Agreed service & enquiry details *",
                                        minLines = 3,
                                        testTag = "access_details_input"
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    PrimaryPillButton(
                                        text = if (isSubmitting) "Submitting..." else "Send access request →",
                                        enabled = !isSubmitting && reqDetails.isNotBlank(),
                                        onClick = {
                                            onSubmitAccessRequest(
                                                AccessRequest(
                                                    name = userName.ifBlank { "TRUE RYZE User" },
                                                    email = userEmail,
                                                    businessName = reqBusiness,
                                                    dealDetails = reqDetails
                                                )
                                            ) {
                                                reqBusiness = ""
                                                reqDetails = ""
                                            }
                                        },
                                        testTag = "access_submit_btn",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                FirestoreEnquiriesCard(enquiriesState, onArchiveEnquiry)
                                FirestoreAccessRequestsCard(accessRequestsState)
                                FirestoreApplicationsCard(applicationsState)
                            }

                            // 2: Support Tickets & Direct Messages
                            2 -> {
                                GlassCard {
                                    Text("Support ticket", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        TrueRyzeCatalog.supportCategories.forEach { c ->
                                            TrChip(c, supCategory == c) { supCategory = c }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        TrueRyzeCatalog.supportPriorities.forEach { p ->
                                            TrChip(p, supPriority == p) { supPriority = p }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TrTextField(
                                        value = supRef,
                                        onValueChange = { supRef = it },
                                        label = "Project reference (optional)",
                                        testTag = "support_ref_input"
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TrTextField(
                                        value = supMessage,
                                        onValueChange = { supMessage = it },
                                        label = "Description *",
                                        minLines = 3,
                                        testTag = "support_msg_input"
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    PrimaryPillButton(
                                        text = "Submit support ticket",
                                        enabled = !isSubmitting && supMessage.isNotBlank(),
                                        onClick = {
                                            onSubmitSupportTicket(
                                                SupportTicket(
                                                    category = supCategory,
                                                    priority = supPriority,
                                                    projectRef = supRef,
                                                    message = supMessage
                                                )
                                            ) {
                                                supRef = ""
                                                supMessage = ""
                                            }
                                        },
                                        testTag = "support_submit_btn",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    when (supportTicketsState) {
                                        UiState.Loading -> CircularProgressIndicator(color = CobaltIce)
                                        is UiState.Error -> Text(supportTicketsState.message, color = StatusError)
                                        is UiState.Success -> {
                                            val tickets = supportTicketsState.data
                                            if (tickets.isEmpty()) {
                                                Text("No support tickets yet.", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                                            } else {
                                                tickets.forEach { t ->
                                                    Surface(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 4.dp),
                                                        shape = RoundedCornerShape(12.dp),
                                                        color = GraphiteElevated.copy(alpha = 0.6f),
                                                        border = BorderStroke(1.dp, GlassBorder)
                                                    ) {
                                                        Column(modifier = Modifier.padding(12.dp)) {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Text("${t.priority} · ${t.category}", style = MaterialTheme.typography.labelMedium, color = if (t.priority == "Urgent") StatusWarn else CobaltIce)
                                                                Text(t.status, style = MaterialTheme.typography.labelMedium, color = if (t.status == "open") TrustGreen else InkMuted)
                                                            }
                                                            Spacer(modifier = Modifier.height(4.dp))
                                                            Text(t.message, style = MaterialTheme.typography.bodyMedium, color = InkPrimary)
                                                            if (t.status == "open" && t.id.isNotBlank()) {
                                                                Spacer(modifier = Modifier.height(8.dp))
                                                                SecondaryPillButton(
                                                                    text = "Mark closed",
                                                                    onClick = { onCloseSupportTicket(t.id) }
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                GlassCard {
                                    Text("Message TRUE RYZE", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    when (messagesState) {
                                        UiState.Loading -> CircularProgressIndicator(color = CobaltIce)
                                        is UiState.Error -> Text(messagesState.message, color = StatusError)
                                        is UiState.Success -> {
                                            if (messagesState.data.isEmpty()) {
                                                Text("No messages sent yet.", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                                            } else {
                                                messagesState.data.forEach { m ->
                                                    Surface(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 3.dp),
                                                        shape = RoundedCornerShape(12.dp),
                                                        color = GraphiteElevated.copy(alpha = 0.6f)
                                                    ) {
                                                        Text(
                                                            text = m.body,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = InkPrimary,
                                                            modifier = Modifier.padding(10.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    TrTextField(
                                        value = msgBody,
                                        onValueChange = { msgBody = it },
                                        label = "Write a message to our team",
                                        minLines = 2,
                                        testTag = "portal_message_input"
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    PrimaryPillButton(
                                        text = "Send message",
                                        enabled = !isSubmitting && msgBody.isNotBlank(),
                                        onClick = {
                                            onSendPortalMessage(msgBody) { msgBody = "" }
                                        },
                                        testTag = "portal_message_send_btn",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            // 3: Sample Monitor Preview
                            else -> {
                                SecondaryPillButton(
                                    text = if (showSampleMonitor) "Collapse sample preview" else "Expand sample Client Monitor",
                                    onClick = { showSampleMonitor = !showSampleMonitor },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                ClientSampleMonitorCard()
                            }
                        }
                    }

                    1 -> TeamPreviewSection()
                    else -> DealerPreviewSection()
                }
            }
        }
    }
}

@Composable
private fun FirestoreEnquiriesCard(
    state: UiState<List<Enquiry>>,
    onArchive: (String) -> Unit
) {
    GlassCard {
        Text("Your project enquiries", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        when (state) {
            UiState.Loading -> CircularProgressIndicator(color = CobaltIce)
            is UiState.Error -> Text(state.message, color = StatusError)
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    Text("No enquiries submitted yet.", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                } else {
                    state.data.forEach { item ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = GraphiteElevated.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, GlassBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item.serviceType, style = MaterialTheme.typography.labelMedium, color = CobaltIce)
                                    Text(item.status, style = MaterialTheme.typography.labelMedium, color = if (item.status == "submitted") TrustGreen else InkMuted)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.message, style = MaterialTheme.typography.bodyMedium, color = InkPrimary)
                                if (item.status == "submitted" && item.id.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    SecondaryPillButton(text = "Archive", onClick = { onArchive(item.id) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FirestoreAccessRequestsCard(state: UiState<List<AccessRequest>>) {
    GlassCard {
        Text("Your client access requests", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        when (state) {
            UiState.Loading -> CircularProgressIndicator(color = CobaltIce)
            is UiState.Error -> Text(state.message, color = StatusError)
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    Text("No client access requests yet.", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                } else {
                    state.data.forEach { req ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = GraphiteElevated.copy(alpha = 0.6f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(req.status.replace('_', ' '), style = MaterialTheme.typography.labelMedium, color = CobaltIce)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(req.dealDetails, style = MaterialTheme.typography.bodyMedium, color = InkPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FirestoreApplicationsCard(state: UiState<List<JobApplication>>) {
    GlassCard {
        Text("Your career applications", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        when (state) {
            UiState.Loading -> CircularProgressIndicator(color = CobaltIce)
            is UiState.Error -> Text(state.message, color = StatusError)
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    Text("No applications submitted yet.", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                } else {
                    state.data.forEach { app ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = GraphiteElevated.copy(alpha = 0.6f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(app.role ?: "General application", style = MaterialTheme.typography.labelMedium, color = CobaltIce)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(app.message, style = MaterialTheme.typography.bodyMedium, color = InkPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
