package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.Enquiry
import com.example.data.model.JobApplication
import com.example.data.model.TrueRyzeCatalog
import com.example.ui.components.BulletRow
import com.example.ui.components.EyebrowText
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryPillButton
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
import com.example.ui.theme.SilverBorder
import com.example.ui.theme.StatusError
import com.example.ui.theme.TrustGreen

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConnectScreen(
    initialTab: Int,
    preselectedPlan: String?,
    isSignedIn: Boolean,
    authEmail: String?,
    authName: String?,
    isAuthBusy: Boolean,
    isSubmitting: Boolean,
    feedbackMessage: Pair<Boolean, String>?,
    onClearFeedback: () -> Unit,
    onSignInClick: () -> Unit,
    onSubmitEnquiry: (Enquiry, () -> Unit) -> Unit,
    onSubmitApplication: (JobApplication, () -> Unit) -> Unit,
    onOpenPricing: () -> Unit,
    onOpenLegal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable(initialTab) { mutableIntStateOf(initialTab.coerceIn(0, 2)) }

    // Enquiry form state
    var enqName by rememberSaveable { mutableStateOf(authName.orEmpty()) }
    var enqEmail by rememberSaveable { mutableStateOf(authEmail.orEmpty()) }
    var enqBusiness by rememberSaveable { mutableStateOf("") }
    var enqService by rememberSaveable { mutableStateOf(TrueRyzeCatalog.serviceOptions.first()) }
    var enqPlan by rememberSaveable(preselectedPlan) {
        mutableStateOf(preselectedPlan?.takeIf { it in TrueRyzeCatalog.planOptions } ?: TrueRyzeCatalog.planOptions.first())
    }
    var enqBudget by rememberSaveable { mutableStateOf(TrueRyzeCatalog.budgetOptions.first()) }
    var enqMessage by rememberSaveable { mutableStateOf("") }

    // Careers form state
    var appName by rememberSaveable { mutableStateOf(authName.orEmpty()) }
    var appEmail by rememberSaveable { mutableStateOf(authEmail.orEmpty()) }
    var appRole by rememberSaveable { mutableStateOf("") }
    var appPortfolio by rememberSaveable { mutableStateOf("") }
    var appMessage by rememberSaveable { mutableStateOf("") }

    var localError by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(authName, authEmail) {
        if (enqName.isBlank() && !authName.isNullOrBlank()) enqName = authName
        if (enqEmail.isBlank() && !authEmail.isNullOrBlank()) enqEmail = authEmail
        if (appName.isBlank() && !authName.isNullOrBlank()) appName = authName
        if (appEmail.isBlank() && !authEmail.isNullOrBlank()) appEmail = authEmail
    }

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
                EyebrowText("Connect with TRUE RYZE")
                Text(
                    text = when (selectedTab) {
                        0 -> "Tell us what you want to build."
                        1 -> "Built to help businesses rise."
                        else -> "Help us build TRUE RYZE."
                    },
                    style = MaterialTheme.typography.displayMedium,
                    color = InkPrimary
                )

                val tabs = listOf("Enquire", "About", "Careers")
                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = GraphiteSurface,
                    contentColor = CobaltIce
                ) {
                    tabs.forEachIndexed { idx, label ->
                        Tab(
                            selected = selectedTab == idx,
                            onClick = {
                                selectedTab = idx
                                localError = null
                                onClearFeedback()
                            },
                            text = { Text(label, style = MaterialTheme.typography.labelLarge) },
                            modifier = Modifier.testTag("connect_tab_${label.lowercase()}")
                        )
                    }
                }

                // Inline status banner (either local validation or Firestore response)
                val banner = localError?.let { false to it } ?: feedbackMessage
                if (banner != null) {
                    val (ok, msg) = banner
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (ok) MintContainer else ErrorContainer,
                        border = BorderStroke(1.dp, if (ok) NeonMint else StatusError),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (ok) TrustGreen else StatusError,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                when (selectedTab) {
                    0 -> {
                        GlassCard(accentBorder = CobaltBright.copy(alpha = 0.4f)) {
                            Text(
                                text = "Project enquiry",
                                style = MaterialTheme.typography.titleLarge,
                                color = InkPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            TrTextField(
                                value = enqName,
                                onValueChange = { enqName = it; localError = null },
                                label = "Name *",
                                testTag = "enquiry_name_input"
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            TrTextField(
                                value = enqEmail,
                                onValueChange = { enqEmail = it; localError = null },
                                label = "Email *",
                                testTag = "enquiry_email_input"
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            TrTextField(
                                value = enqBusiness,
                                onValueChange = { enqBusiness = it },
                                label = "Business name (optional)",
                                testTag = "enquiry_business_input"
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text("Service of interest", style = MaterialTheme.typography.labelLarge, color = InkSilver)
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TrueRyzeCatalog.serviceOptions.forEach { opt ->
                                    TrChip(
                                        label = opt,
                                        selected = enqService == opt,
                                        onClick = { enqService = opt }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Plan of interest", style = MaterialTheme.typography.labelLarge, color = InkSilver)
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TrueRyzeCatalog.planOptions.forEach { opt ->
                                    TrChip(
                                        label = opt,
                                        selected = enqPlan == opt,
                                        onClick = { enqPlan = opt }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Budget range (optional)", style = MaterialTheme.typography.labelLarge, color = InkSilver)
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TrueRyzeCatalog.budgetOptions.forEach { opt ->
                                    TrChip(
                                        label = opt,
                                        selected = enqBudget == opt,
                                        onClick = { enqBudget = opt }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            TrTextField(
                                value = enqMessage,
                                onValueChange = { enqMessage = it; localError = null },
                                label = "Project requirements *",
                                minLines = 4,
                                testTag = "enquiry_message_input"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            if (!isSignedIn) {
                                Text(
                                    text = "Sign in with Google to save your enquiry directly to your TRUE RYZE Client Portal history.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CobaltIce
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                PrimaryPillButton(
                                    text = if (isAuthBusy) "Connecting Google..." else "Continue with Google to send →",
                                    onClick = onSignInClick,
                                    enabled = !isAuthBusy,
                                    testTag = "enquiry_google_signin_btn",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                PrimaryPillButton(
                                    text = if (isSubmitting) "Sending..." else "Send enquiry →",
                                    enabled = !isSubmitting,
                                    testTag = "enquiry_submit_btn",
                                    onClick = {
                                        localError = null
                                        onClearFeedback()
                                        when {
                                            enqName.isBlank() -> localError = "Please enter your name."
                                            !enqEmail.contains("@") -> localError = "Please enter a valid email address."
                                            enqMessage.isBlank() -> localError = "Please describe your project requirements."
                                            else -> onSubmitEnquiry(
                                                Enquiry(
                                                    name = enqName,
                                                    email = enqEmail,
                                                    businessName = enqBusiness,
                                                    serviceType = enqService,
                                                    plan = enqPlan,
                                                    budget = enqBudget,
                                                    message = enqMessage
                                                )
                                            ) {
                                                enqBusiness = ""
                                                enqMessage = ""
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        GlassCard {
                            Text("What happens next", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            BulletRow("1. We read your message and review your goals.", iconTint = CobaltIce)
                            BulletRow("2. We reply by email to discuss your needs.", iconTint = CobaltIce)
                            BulletRow("3. Scope and price are confirmed in writing before any work starts.", iconTint = CobaltIce)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                SecondaryPillButton(
                                    text = "View pricing",
                                    onClick = onOpenPricing,
                                    testTag = "connect_pricing_btn",
                                    modifier = Modifier.weight(1f)
                                )
                                SecondaryPillButton(
                                    text = "Privacy & terms",
                                    onClick = onOpenLegal,
                                    testTag = "connect_privacy_btn",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    1 -> {
                        GlassCard {
                            Text("Why TRUE RYZE exists", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Many businesses want to be online but don't know where to start, or struggle to keep their site healthy. TRUE RYZE exists to make that simple and dependable.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkSilver
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "We're not just a one-off website shop. We aim to become a long-term digital partner, growing from websites and maintenance into a wider business-growth ecosystem.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkSilver
                            )
                        }
                        GlassCard(accentBorder = CobaltBright.copy(alpha = 0.35f)) {
                            Text("Mission", style = MaterialTheme.typography.titleMedium, color = CobaltIce)
                            Text(
                                text = "Help businesses establish their digital presence, modernise operations and scale through technology and strategic support.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkSilver
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Vision", style = MaterialTheme.typography.titleMedium, color = CobaltIce)
                            Text(
                                text = "A trusted, global, all-in-one platform for business growth.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkSilver
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Approach", style = MaterialTheme.typography.titleMedium, color = CobaltIce)
                            Text(
                                text = "Clear scope, honest communication and quality checks. We only promote services we can deliver today.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkSilver
                            )
                        }
                        GlassCard(accentBorder = GlassBorder) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Rounded.Groups, contentDescription = null, tint = CobaltIce)
                                    Text("Startup WhatsApp Community", style = MaterialTheme.typography.titleMedium, color = InkPrimary)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            StatusPill(label = "Coming soon", isLive = false)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "An exclusive, invitation-only group for founders. People join after sharing their idea and being verified, so every conversation stays focused on business and startups.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkSilver
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.QrCode2,
                                    contentDescription = "Community QR placeholder",
                                    tint = InkMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = "Invite link and QR code will appear when the community launches.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = InkMuted
                                )
                            }
                        }
                    }

                    else -> {
                        GlassCard {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PersonSearch,
                                    contentDescription = null,
                                    tint = CobaltIce,
                                    modifier = Modifier.size(28.dp)
                                )
                                Column {
                                    Text("No open vacancies right now", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                                    Text(
                                        text = "We're growing. Send a general application and we'll be in touch if a suitable role opens.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = InkSilver
                                    )
                                }
                            }
                        }

                        GlassCard(accentBorder = CobaltBright.copy(alpha = 0.4f)) {
                            Text("General application", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                            Spacer(modifier = Modifier.height(12.dp))
                            TrTextField(
                                value = appName,
                                onValueChange = { appName = it; localError = null },
                                label = "Name *",
                                testTag = "career_name_input"
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            TrTextField(
                                value = appEmail,
                                onValueChange = { appEmail = it; localError = null },
                                label = "Email *",
                                testTag = "career_email_input"
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            TrTextField(
                                value = appRole,
                                onValueChange = { appRole = it },
                                label = "Role of interest (e.g. Frontend developer)",
                                testTag = "career_role_input"
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            TrTextField(
                                value = appPortfolio,
                                onValueChange = { appPortfolio = it; localError = null },
                                label = "Portfolio or résumé link (https://...)",
                                testTag = "career_link_input"
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            TrTextField(
                                value = appMessage,
                                onValueChange = { appMessage = it; localError = null },
                                label = "About you *",
                                minLines = 4,
                                testTag = "career_about_input"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Share a web link to your portfolio or résumé rather than attaching device files.",
                                style = MaterialTheme.typography.labelSmall,
                                color = InkMuted
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            if (!isSignedIn) {
                                PrimaryPillButton(
                                    text = if (isAuthBusy) "Connecting Google..." else "Continue with Google to apply →",
                                    onClick = onSignInClick,
                                    enabled = !isAuthBusy,
                                    testTag = "career_google_signin_btn",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                PrimaryPillButton(
                                    text = if (isSubmitting) "Sending..." else "Send application →",
                                    enabled = !isSubmitting,
                                    testTag = "career_submit_btn",
                                    onClick = {
                                        localError = null
                                        onClearFeedback()
                                        when {
                                            appName.isBlank() -> localError = "Please enter your name."
                                            !appEmail.contains("@") -> localError = "Please enter a valid email address."
                                            appPortfolio.isNotBlank() &&
                                                !appPortfolio.startsWith("https://") &&
                                                !appPortfolio.startsWith("http://") ->
                                                localError = "Portfolio URL must start with https:// or http://"
                                            appMessage.isBlank() -> localError = "Please tell us a little about yourself."
                                            else -> onSubmitApplication(
                                                JobApplication(
                                                    name = appName,
                                                    email = appEmail,
                                                    role = appRole,
                                                    portfolioUrl = appPortfolio,
                                                    message = appMessage
                                                )
                                            ) {
                                                appRole = ""
                                                appPortfolio = ""
                                                appMessage = ""
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    testTag: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        minLines = minLines,
        maxLines = if (minLines > 1) 8 else 1,
        singleLine = minLines == 1,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CobaltBright,
            unfocusedBorderColor = SilverBorder,
            focusedLabelColor = CobaltIce,
            unfocusedLabelColor = InkMuted,
            focusedTextColor = InkPrimary,
            unfocusedTextColor = InkPrimary,
            focusedContainerColor = GraphiteElevated.copy(alpha = 0.55f),
            unfocusedContainerColor = GraphiteElevated.copy(alpha = 0.35f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .let { if (testTag != null) it.testTag(testTag) else it }
    )
}

@Composable
fun TrChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CobaltContainer,
            selectedLabelColor = CobaltIce,
            containerColor = GraphiteElevated.copy(alpha = 0.55f),
            labelColor = InkSilver
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            selectedBorderColor = CobaltBright,
            borderColor = GlassBorder
        )
    )
}
