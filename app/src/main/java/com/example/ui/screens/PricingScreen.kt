package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.TrueRyzeCatalog
import com.example.ui.components.BulletRow
import com.example.ui.components.EyebrowText
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryPillButton
import com.example.ui.components.SecondaryPillButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.CobaltBright
import com.example.ui.theme.CobaltIce
import com.example.ui.theme.CobaltPrimary
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSilver
import com.example.ui.theme.NeonMint
import com.example.ui.theme.SilverBorder

@Composable
fun PricingScreen(
    onSelectPlanForEnquiry: (String) -> Unit,
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
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    EyebrowText("Pricing")
                    Text(
                        text = "Three plans, clear scope.",
                        style = MaterialTheme.typography.displayMedium,
                        color = InkPrimary
                    )
                    Text(
                        text = "Proposed structure only. Final prices and inclusions are confirmed with you before any project starts. Development is a one-time charge; maintenance is a separate recurring option.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSilver
                    )
                }

                TrueRyzeCatalog.plans.forEach { plan ->
                    GlassCard(
                        accentBorder = if (plan.isHighlighted) CobaltBright else SilverBorder,
                        glowColor = if (plan.isHighlighted) CobaltPrimary else null
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = plan.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = InkPrimary
                            )
                            if (plan.isHighlighted) {
                                StatusPill(label = "Most balanced", isLive = true)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = plan.audience,
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = plan.priceLabel,
                            style = MaterialTheme.typography.titleLarge,
                            color = CobaltIce,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        plan.items.forEach { BulletRow(it) }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = plan.note,
                            style = MaterialTheme.typography.labelSmall,
                            color = InkMuted
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        if (plan.isHighlighted) {
                            PrimaryPillButton(
                                text = "Enquire about ${plan.name}",
                                onClick = { onSelectPlanForEnquiry(plan.name) },
                                testTag = "pricing_enquire_${plan.name.lowercase()}",
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            SecondaryPillButton(
                                text = "Enquire about ${plan.name}",
                                onClick = { onSelectPlanForEnquiry(plan.name) },
                                testTag = "pricing_enquire_${plan.name.lowercase()}",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Comparison Matrix
                GlassCard {
                    Text(
                        text = "Compare plans",
                        style = MaterialTheme.typography.titleLarge,
                        color = InkPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Feature",
                            style = MaterialTheme.typography.labelMedium,
                            color = InkMuted,
                            modifier = Modifier.weight(1.9f)
                        )
                        listOf("Launch", "Grow", "Scale").forEach { col ->
                            Text(
                                text = col,
                                style = MaterialTheme.typography.labelMedium,
                                color = CobaltIce,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(0.7f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = GlassBorder)
                    TrueRyzeCatalog.comparisonRows.forEach { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = row.feature,
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkSilver,
                                modifier = Modifier.weight(1.9f)
                            )
                            listOf(row.launchIncluded, row.growIncluded, row.scaleIncluded).forEach { inc ->
                                Icon(
                                    imageVector = if (inc) Icons.Rounded.Check else Icons.Rounded.Remove,
                                    contentDescription = if (inc) "Included" else "Not included",
                                    tint = if (inc) NeonMint else InkMuted,
                                    modifier = Modifier
                                        .weight(0.7f)
                                        .size(18.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = GlassBorder.copy(alpha = 0.4f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Proposed structure only, to be confirmed before publication.",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkMuted
                    )
                }

                GlassCard(accentBorder = GlassBorder) {
                    Text(
                        text = "Maintenance is quoted separately as a recurring plan. Currency options will be confirmed with your written quote.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSilver
                    )
                }
            }
        }
    }
}
