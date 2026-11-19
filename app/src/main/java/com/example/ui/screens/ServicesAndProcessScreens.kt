package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.TrueRyzeCatalog
import com.example.ui.components.BulletRow
import com.example.ui.components.EyebrowText
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryPillButton
import com.example.ui.components.ProjectLifecycleTracker
import com.example.ui.components.SecondaryPillButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.CobaltBright
import com.example.ui.theme.CobaltContainer
import com.example.ui.theme.CobaltIce
import com.example.ui.theme.CobaltPrimary
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GraphiteElevated
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSilver
import com.example.ui.theme.NeonMint
import com.example.ui.theme.StatusWarn

@Composable
fun ServicesScreen(
    onOpenServiceDetail: (String) -> Unit,
    onOpenHowItWorks: () -> Unit,
    onEnquire: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    EyebrowText("Services")
                    Text(
                        text = "What we do today.",
                        style = MaterialTheme.typography.displayMedium,
                        color = InkPrimary
                    )
                    Text(
                        text = "Two core services available right now, delivered with clear written scope.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = InkSilver
                    )
                }

                TrueRyzeCatalog.liveServices.forEach { service ->
                    GlassCard(
                        accentBorder = NeonMint.copy(alpha = 0.42f),
                        glowColor = CobaltPrimary,
                        modifier = Modifier.clickable { onOpenServiceDetail(service.id) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusPill(label = "Available", isLive = true)
                            Text(
                                text = service.number,
                                style = MaterialTheme.typography.titleLarge,
                                color = CobaltIce
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = service.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = InkPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        service.included.forEach { BulletRow(it) }
                        Spacer(modifier = Modifier.height(14.dp))
                        PrimaryPillButton(
                            text = "Full details →",
                            onClick = { onOpenServiceDetail(service.id) },
                            testTag = "svc_detail_${service.id}",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                GlassCard(accentBorder = CobaltBright.copy(alpha = 0.4f)) {
                    EyebrowText("Workflow")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "From hello to live site in 10 steps",
                        style = MaterialTheme.typography.titleLarge,
                        color = InkPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Review and feedback are built into every project before launch.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSilver
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SecondaryPillButton(
                        text = "View How It Works →",
                        onClick = onOpenHowItWorks,
                        testTag = "svc_how_it_works_btn",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    EyebrowText("Coming soon")
                    Text(
                        text = "Not available yet",
                        style = MaterialTheme.typography.headlineLarge,
                        color = InkPrimary
                    )
                    TrueRyzeCatalog.comingSoonServices.forEach { item ->
                        GlassCard(accentBorder = GlassBorder) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = InkPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                StatusPill(label = "Coming soon", isLive = false)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkMuted
                            )
                        }
                    }
                    Text(
                        text = "Want to hear when these launch? Mention it in your enquiry.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSilver
                    )
                    PrimaryPillButton(
                        text = "Send an enquiry →",
                        onClick = onEnquire,
                        testTag = "svc_enquire_btn",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceDetailScreen(
    serviceId: String,
    onBack: () -> Unit,
    onEnquire: () -> Unit,
    onViewPlans: () -> Unit,
    modifier: Modifier = Modifier
) {
    val service = TrueRyzeCatalog.liveServices.firstOrNull { it.id == serviceId }
        ?: TrueRyzeCatalog.liveServices.first()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("service_detail_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = InkPrimary
                        )
                    }
                    StatusPill(label = "Available now", isLive = true)
                }

                Text(
                    text = service.title,
                    style = MaterialTheme.typography.displayMedium,
                    color = InkPrimary
                )
                Text(
                    text = service.subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = InkSilver
                )

                GlassCard(accentBorder = NeonMint.copy(alpha = 0.35f)) {
                    Text("What's included", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    service.included.forEach { BulletRow(it) }
                }

                GlassCard {
                    Text("Who it's for", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(service.whoItsFor, style = MaterialTheme.typography.bodyMedium, color = InkSilver)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("What we need from you", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    service.whatWeNeed.forEach { BulletRow(it, iconTint = CobaltIce) }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Not included", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    service.notIncluded.forEach { BulletRow(it, iconTint = StatusWarn) }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    EyebrowText("Process")
                    service.steps.forEachIndexed { idx, (stepTitle, stepDesc) ->
                        GlassCard(accentBorder = GlassBorder) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = CobaltContainer,
                                    border = BorderStroke(1.dp, CobaltBright.copy(alpha = 0.45f))
                                ) {
                                    Text(
                                        text = "0${idx + 1}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = CobaltIce,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(stepTitle, style = MaterialTheme.typography.titleMedium, color = InkPrimary)
                                    Text(stepDesc, style = MaterialTheme.typography.bodyMedium, color = InkSilver)
                                }
                            }
                        }
                    }
                }

                GlassCard(accentBorder = CobaltBright.copy(alpha = 0.3f)) {
                    service.notes.forEach { note ->
                        Text(note, style = MaterialTheme.typography.bodyMedium, color = InkSilver)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    PrimaryPillButton(
                        text = "Enquire about this →",
                        onClick = onEnquire,
                        testTag = "service_detail_enquire",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SecondaryPillButton(
                        text = "View plans",
                        onClick = onViewPlans,
                        testTag = "service_detail_plans",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun HowItWorksScreen(
    onBack: () -> Unit,
    onStartProject: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("how_it_works_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = InkPrimary
                        )
                    }
                    EyebrowText("How it works")
                }

                Text(
                    text = "From hello to live site.",
                    style = MaterialTheme.typography.displayMedium,
                    color = InkPrimary
                )
                Text(
                    text = "Ten clear steps in four phases. Scope and pricing are confirmed with you before any work begins.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = InkSilver
                )

                GlassCard(
                    accentBorder = NeonMint.copy(alpha = 0.38f),
                    glowColor = CobaltPrimary
                ) {
                    EyebrowText("10-stage lifecycle timeline")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Interactive project roadmap",
                        style = MaterialTheme.typography.titleLarge,
                        color = InkPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ProjectLifecycleTracker(
                        currentStageIndex = 0,
                        overallProgressOverride = 0.1f,
                        testTagPrefix = "process_lifecycle"
                    )
                }

                TrueRyzeCatalog.phases.forEach { phase ->
                    GlassCard(accentBorder = CobaltBright.copy(alpha = 0.35f)) {
                        EyebrowText(phase.phaseLabel)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = phase.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = InkPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        phase.steps.forEach { step ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = GraphiteElevated.copy(alpha = 0.65f),
                                border = BorderStroke(1.dp, GlassBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = step.number.toString().padStart(2, '0'),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = CobaltIce,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = step.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = InkPrimary
                                            )
                                            if (step.isOptional) {
                                                StatusPill(label = "Optional", isLive = false)
                                            }
                                        }
                                        Text(
                                            text = step.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = InkSilver
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                GlassCard {
                    Text(
                        text = "Review and feedback happen during development (steps 6 and 7), so you can steer the result before launch.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSilver
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    PrimaryPillButton(
                        text = "Start your project →",
                        onClick = onStartProject,
                        testTag = "process_start_project_btn",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
