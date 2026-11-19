package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.TrueRyzeCatalog
import com.example.ui.components.BulletRow
import com.example.ui.components.EyebrowText
import com.example.ui.components.GlassCard
import com.example.ui.components.HandshakePlayer
import com.example.ui.components.PrimaryPillButton
import com.example.ui.components.SecondaryPillButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.CobaltBright
import com.example.ui.theme.CobaltIce
import com.example.ui.theme.CobaltPrimary
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GraphiteElevated
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSilver
import com.example.ui.theme.NeonMint
import com.example.ui.theme.SilverBorder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onStartProject: () -> Unit,
    onExploreServices: () -> Unit,
    onOpenServiceDetail: (String) -> Unit,
    onOpenHowItWorks: () -> Unit,
    onOpenPortal: () -> Unit,
    onOpenCareers: () -> Unit,
    onOpenLegal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedFaq by remember { mutableIntStateOf(0) }

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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Hero Banner with official TRUE RYZE logo
                GlassCard(
                    accentBorder = CobaltBright.copy(alpha = 0.4f),
                    glowColor = CobaltPrimary
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_large),
                        contentDescription = stringResource(R.string.cd_brand_logo),
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .padding(bottom = 12.dp)
                    )
                    EyebrowText("Your growth partner · Delivered worldwide")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            append("Your dream.\nOur craft.\n")
                            withStyle(SpanStyle(color = CobaltIce)) {
                                append("Let's rise together.")
                            }
                        },
                        style = MaterialTheme.typography.displayMedium,
                        color = InkPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Every great business begins with one brave idea. TRUE RYZE is the partner who builds beside you, from your first website to your next big leap.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = InkSilver
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TrueRyzeCatalog.quotes.forEach { quote ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GraphiteElevated.copy(alpha = 0.7f),
                                border = BorderStroke(1.dp, GlassBorder)
                            ) {
                                Text(
                                    text = quote,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = InkSilver,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PrimaryPillButton(
                            text = "Start your project →",
                            onClick = onStartProject,
                            testTag = "hero_start_project_btn",
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryPillButton(
                            text = "Services",
                            onClick = onExploreServices,
                            testTag = "hero_services_btn"
                        )
                    }
                }

                // Interactive Handshake Animation
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HandshakePlayer()
                    Text(
                        text = "Websites · Maintenance · Growth (Tap animation to replay handshake)",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkMuted,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // The Challenge
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    EyebrowText("The challenge")
                    Text(
                        text = "Going digital shouldn't feel like a maze.",
                        style = MaterialTheme.typography.headlineLarge,
                        color = InkPrimary
                    )
                    TrueRyzeCatalog.challenges.forEach { (title, desc, iconKey) ->
                        val icon = when (iconKey) {
                            "store" -> Icons.Rounded.Storefront
                            "layers" -> Icons.Rounded.Layers
                            else -> Icons.Rounded.Schedule
                        }
                        GlassCard {
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = GraphiteElevated,
                                    border = BorderStroke(1.dp, SilverBorder),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = CobaltIce,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = InkPrimary
                                    )
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = InkSilver
                                    )
                                }
                            }
                        }
                    }
                }

                // Live Services Available Today
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    EyebrowText("Available now")
                    Text(
                        text = "Two services. Done properly.",
                        style = MaterialTheme.typography.headlineLarge,
                        color = InkPrimary
                    )
                    Text(
                        text = "Everything we offer today, scoped clearly and confirmed in writing before work starts.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkMuted
                    )
                    TrueRyzeCatalog.liveServices.forEach { service ->
                        GlassCard(
                            accentBorder = NeonMint.copy(alpha = 0.42f),
                            glowColor = CobaltPrimary,
                            modifier = Modifier
                                .clickable { onOpenServiceDetail(service.id) }
                                .testTag("live_service_${service.id}")
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
                                    color = CobaltIce.copy(alpha = 0.65f)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (service.id == "website-development") {
                                        Icons.Rounded.Code
                                    } else {
                                        Icons.Rounded.Build
                                    },
                                    contentDescription = null,
                                    tint = NeonMint,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = service.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = InkPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = service.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkSilver
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            service.highlights.forEach { BulletRow(it) }
                            Spacer(modifier = Modifier.height(14.dp))
                            SecondaryPillButton(
                                text = "See what's included →",
                                onClick = { onOpenServiceDetail(service.id) },
                                testTag = "detail_btn_${service.id}",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Coming Soon Ecosystem Preview
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    EyebrowText("Coming soon")
                    Text(
                        text = "The ecosystem we're building.",
                        style = MaterialTheme.typography.headlineLarge,
                        color = InkPrimary
                    )
                    Text(
                        text = "These 9 services are not yet available. We're building them step by step.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkMuted
                    )
                    TrueRyzeCatalog.comingSoonServices.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            pair.forEach { item ->
                                GlassCard(
                                    modifier = Modifier.weight(1f),
                                    accentBorder = GlassBorder
                                ) {
                                    StatusPill(label = "Coming soon", isLive = false)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = InkPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = InkMuted
                                    )
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Global Delivery Card
                GlassCard(accentBorder = SilverBorder) {
                    EyebrowText("Global delivery")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "A managed team in India, serving clients worldwide.",
                        style = MaterialTheme.typography.headlineMedium,
                        color = InkPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Projects are run remotely with clear communication, defined scope and transparent steps from first message to launch.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSilver
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TrueRyzeCatalog.deliveryGuarantees.forEach {
                        BulletRow(text = it, iconTint = CobaltIce)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    PrimaryPillButton(
                        text = "See our 10-step process →",
                        onClick = onOpenHowItWorks,
                        testTag = "home_process_btn",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // FAQ Accordion
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    EyebrowText("FAQ")
                    Text(
                        text = "Quick answers",
                        style = MaterialTheme.typography.headlineLarge,
                        color = InkPrimary
                    )
                    TrueRyzeCatalog.faqs.forEachIndexed { index, faq ->
                        val isOpen = expandedFaq == index
                        GlassCard(
                            modifier = Modifier
                                .clickable { expandedFaq = if (isOpen) -1 else index }
                                .testTag("faq_item_$index"),
                            accentBorder = if (isOpen) CobaltBright.copy(alpha = 0.5f) else GlassBorder
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = faq.question,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = InkPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (isOpen) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                    contentDescription = if (isOpen) "Collapse answer" else "Expand answer",
                                    tint = CobaltIce
                                )
                            }
                            AnimatedVisibility(visible = isOpen) {
                                Text(
                                    text = faq.answer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = InkSilver,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }
                        }
                    }
                }

                // Quick Actions & Closing CTA
                GlassCard(
                    accentBorder = CobaltBright.copy(alpha = 0.45f),
                    glowColor = CobaltPrimary
                ) {
                    Text(
                        text = "Ready to rise?",
                        style = MaterialTheme.typography.headlineLarge,
                        color = InkPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tell us about your business and what you want to build, or open the Portal with your Google account.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSilver
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    PrimaryPillButton(
                        text = "Send an enquiry →",
                        onClick = onStartProject,
                        testTag = "closing_enquiry_btn",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SecondaryPillButton(
                            text = "Client Portal",
                            onClick = onOpenPortal,
                            testTag = "quick_portal_btn",
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryPillButton(
                            text = "Careers",
                            onClick = onOpenCareers,
                            testTag = "quick_careers_btn",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Compact Footer Attribution & Privacy Link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "© 2026 TRUE RYZE",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkMuted
                    )
                    Surface(
                        onClick = onOpenLegal,
                        shape = RoundedCornerShape(999.dp),
                        color = GraphiteElevated,
                        border = BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.testTag("home_privacy_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Privacy & Terms",
                                style = MaterialTheme.typography.labelMedium,
                                color = CobaltIce,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = CobaltIce,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
