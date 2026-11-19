package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.data.model.TrueRyzeCatalog
import com.example.ui.components.EyebrowText
import com.example.ui.components.GlassCard
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
import com.example.ui.theme.StatusWarn
import com.example.ui.theme.TrustGreen

@Composable
fun SamplePreviewBanner(roleLabel: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CobaltContainer.copy(alpha = 0.65f),
        border = BorderStroke(1.dp, CobaltBright.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Sample data preview — $roleLabel",
                style = MaterialTheme.typography.titleMedium,
                color = CobaltIce
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "IDs, orders, and amounts below are labelled examples showing how approved Client (TR-C-######), Team (TR-T-####), and Dealer (TR-D-###) monitors work.",
                style = MaterialTheme.typography.bodyMedium,
                color = InkSilver
            )
        }
    }
}

@Composable
fun ClientSampleMonitorCard() {
    var sampleStageIndex by rememberSaveable { mutableIntStateOf(4) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SamplePreviewBanner("Client Monitor (TR-C-000123)")

        // Active Order 10-Stage Horizontal Stepper & Timeline
        GlassCard(accentBorder = CobaltBright.copy(alpha = 0.4f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    EyebrowText("Sample order")
                    Text("Business website, Grow plan", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                    Text("Started 12 Sep · Expected 28 Nov · Team TR-T-0457", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                }
                StatusPill(label = "Sample preview", isLive = false)
            }

            Spacer(modifier = Modifier.height(14.dp))

            ProjectLifecycleTracker(
                currentStageIndex = sampleStageIndex,
                overallProgressOverride = if (sampleStageIndex == 4) 0.62f else null,
                testTagPrefix = "sample_lifecycle"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive sample stage stepper controls so users can preview stage transitions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecondaryPillButton(
                    text = "Prev stage",
                    enabled = sampleStageIndex > 0,
                    onClick = { if (sampleStageIndex > 0) sampleStageIndex-- },
                    testTag = "sample_stage_step_back",
                    modifier = Modifier.weight(1f)
                )
                SecondaryPillButton(
                    text = if (sampleStageIndex >= 9) "Reset to 05" else "Advance stage",
                    onClick = {
                        sampleStageIndex = if (sampleStageIndex >= 9) 4 else sampleStageIndex + 1
                    },
                    testTag = "sample_stage_step_forward",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "You only see the team's ID (TR-T-0457). TRUE RYZE coordinates all work and delivers to you.",
                style = MaterialTheme.typography.labelSmall,
                color = InkMuted
            )
        }

        // Loyalty Points Ring & Bonds Growth Chart
        GlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(92.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(92.dp)) {
                        drawArc(
                            color = GraphiteElevated,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 12f)
                        )
                        drawArc(
                            brush = Brush.sweepGradient(listOf(CobaltPrimary, CobaltIce)),
                            startAngle = -90f,
                            sweepAngle = 360f * 0.80f,
                            useCenter = false,
                            style = Stroke(width = 12f, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("80%", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                        Text("to 1 mo free", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Loyalty & Bonds", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                    Text("2,400 / 3,000 pts · Silver Tier · 4 Bonds", style = MaterialTheme.typography.bodyMedium, color = CobaltIce)
                    Text("600 points away from redeeming 1 month of free maintenance service.", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            // Growth chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(74.dp)
            ) {
                val series = TrueRyzeCatalog.sampleClientPointsSeries
                val maxVal = series.max().toFloat().coerceAtLeast(1f)
                val stepX = size.width / (series.size - 1).coerceAtLeast(1)
                val linePath = Path()
                series.forEachIndexed { idx, v ->
                    val x = idx * stepX
                    val y = size.height - (v / maxVal) * (size.height - 16f) - 8f
                    if (idx == 0) linePath.moveTo(x, y) else linePath.lineTo(x, y)
                }
                drawPath(
                    path = linePath,
                    color = CobaltIce,
                    style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }
}

@Composable
fun TeamPreviewSection() {
    var showEmpty by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SamplePreviewBanner("Team Portal (TR-T-0457)")

        GlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Team ID: TR-T-0457", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
                    Text("Client IDs only · Deliveries go to TRUE RYZE", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                }
                StatusPill(label = if (showEmpty) "0 works" else "3 works", isLive = !showEmpty)
            }
            Spacer(modifier = Modifier.height(12.dp))
            SecondaryPillButton(
                text = if (showEmpty) "Show sample works" else "Preview: no works yet",
                onClick = { showEmpty = !showEmpty },
                testTag = "team_empty_toggle",
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (showEmpty) {
            GlassCard {
                Text("No works assigned yet", style = MaterialTheme.typography.titleMedium, color = InkPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "New works appear here when TRUE RYZE assigns them to your Team ID.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkMuted
                )
            }
        } else {
            TrueRyzeCatalog.sampleTeamWorks.forEach { w ->
                GlassCard(accentBorder = GlassBorder) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${w.workId} · ${w.clientCode}", style = MaterialTheme.typography.labelMedium, color = CobaltIce)
                        Text(w.status, style = MaterialTheme.typography.labelMedium, color = if (w.status == "In review") TrustGreen else InkSilver)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(w.task, style = MaterialTheme.typography.titleMedium, color = InkPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Due ${w.deadline} · ${w.pay}", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                }
            }
        }
    }
}

@Composable
fun DealerPreviewSection() {
    var filterMode by remember { mutableStateOf("all") }
    val approvedSample = remember { mutableStateMapOf<Int, String>() }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SamplePreviewBanner("Dealer Command (TR-D-001)")

        GlassCard {
            Text("Sample client access approvals", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            TrueRyzeCatalog.sampleDealerRequests.forEachIndexed { idx, (applicant, summary) ->
                val issued = approvedSample[idx]
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = GraphiteElevated.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(applicant, style = MaterialTheme.typography.titleMedium, color = InkPrimary)
                            Text(summary, style = MaterialTheme.typography.labelSmall, color = InkMuted)
                            if (issued != null) {
                                Text("Issued (sample): $issued", style = MaterialTheme.typography.labelSmall, color = TrustGreen)
                            }
                        }
                        if (issued == null) {
                            SecondaryPillButton(
                                text = "Approve",
                                onClick = {
                                    approvedSample[idx] = "TR-C-00020${idx + 1}"
                                },
                                testTag = "dealer_sample_approve_$idx"
                            )
                        }
                    }
                }
            }
        }

        GlassCard {
            Text("Orders monitor", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TrChip("All", filterMode == "all") { filterMode = "all" }
                TrChip("On track", filterMode == "ontrack") { filterMode = "ontrack" }
                TrChip("Due soon", filterMode == "due") { filterMode = "due" }
            }
            Spacer(modifier = Modifier.height(10.dp))
            val shown = TrueRyzeCatalog.sampleDealerOrders.filter {
                when (filterMode) {
                    "ontrack" -> !it.isDueSoon
                    "due" -> it.isDueSoon
                    else -> true
                }
            }
            shown.forEach { order ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = GraphiteElevated.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${order.clientCode} → ${order.teamCode}", style = MaterialTheme.typography.labelMedium, color = CobaltIce)
                            Text("${order.service} · ${order.stage}", style = MaterialTheme.typography.bodyMedium, color = InkPrimary)
                            Text("Due ${order.deadline}", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                        }
                        Text(
                            text = if (order.isDueSoon) "Due soon" else "On track",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (order.isDueSoon) StatusWarn else TrustGreen
                        )
                    }
                }
            }
        }
    }
}
