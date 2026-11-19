package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.LifecycleStageSpec
import com.example.data.model.TrueRyzeCatalog
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CobaltBright
import com.example.ui.theme.CobaltContainer
import com.example.ui.theme.CobaltIce
import com.example.ui.theme.CobaltPrimary
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GraphiteElevated
import com.example.ui.theme.GraphiteSurface
import com.example.ui.theme.InkDim
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSilver
import com.example.ui.theme.MintContainer
import com.example.ui.theme.NeonMint
import com.example.ui.theme.SilverBorder
import com.example.ui.theme.TrustGreen
import kotlin.math.roundToInt

private enum class StageVisualState {
    COMPLETED,
    ACTIVE,
    UPCOMING
}

/**
 * Interactive horizontal stepper & timeline progress tracker for the 10-stage TRUE RYZE
 * project lifecycle.
 *
 * @param currentStageIndex 0-based index of the currently active stage (`-1` when not started yet,
 * `stages.size` when all 10 stages are completed).
 * @param overallProgressOverride Optional explicit 0f..1f progress ratio; defaults to a ratio
 * calculated cleanly from [currentStageIndex].
 * @param testTagPrefix Prefix used for `Modifier.testTag(...)` on interactive controls.
 */
@Composable
fun ProjectLifecycleTracker(
    currentStageIndex: Int,
    modifier: Modifier = Modifier,
    stages: List<LifecycleStageSpec> = TrueRyzeCatalog.lifecycleStages,
    overallProgressOverride: Float? = null,
    testTagPrefix: String = "lifecycle"
) {
    if (stages.isEmpty()) return

    val safeCurrent = currentStageIndex.coerceIn(-1, stages.size)
    val defaultInspected = if (safeCurrent in stages.indices) safeCurrent else 0
    var inspectedIndex by remember(safeCurrent, stages.size) {
        mutableIntStateOf(defaultInspected)
    }
    val safeInspected = inspectedIndex.coerceIn(0, stages.lastIndex)

    val computedProgress = remember(safeCurrent, stages.size, overallProgressOverride) {
        overallProgressOverride?.coerceIn(0f, 1f) ?: when {
            safeCurrent < 0 -> 0f
            safeCurrent >= stages.size -> 1f
            else -> ((safeCurrent + 0.5f) / stages.size.toFloat()).coerceIn(0f, 1f)
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = computedProgress,
        animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
        label = "lifecycle_progress"
    )
    val percentInt = (animatedProgress * 100f).roundToInt().coerceIn(0, 100)

    val listState = rememberLazyListState()
    LaunchedEffect(safeCurrent) {
        val target = if (safeCurrent in stages.indices) safeCurrent else 0
        listState.animateScrollToItem(index = (target - 1).coerceAtLeast(0))
    }
    LaunchedEffect(safeInspected) {
        listState.animateScrollToItem(index = (safeInspected - 1).coerceAtLeast(0))
    }

    val infiniteTransition = rememberInfiniteTransition(label = "active_stage_pulse")
    val pulseFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_fraction"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("${testTagPrefix}_tracker"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header: Current Stage Summary + Percentage Readout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val statusHeading = when {
                    safeCurrent < 0 -> "Not started · 0 of ${stages.size}"
                    safeCurrent >= stages.size -> "All ${stages.size} stages completed"
                    else -> {
                        val active = stages[safeCurrent]
                        "Stage ${active.numberLabel} of ${stages.size} · ${active.title}"
                    }
                }
                Text(
                    text = statusHeading,
                    style = MaterialTheme.typography.titleMedium,
                    color = InkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val phaseSubtitle = when {
                    safeCurrent < 0 -> "Tap any stage below to inspect the 10-stage lifecycle"
                    safeCurrent >= stages.size -> "Handover complete · Optional maintenance available"
                    else -> "${stages[safeCurrent].phase} · Tap any milestone to inspect"
                }
                Text(
                    text = phaseSubtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = InkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$percentInt%",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (percentInt >= 100) NeonMint else CobaltIce,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${safeCurrent.coerceAtLeast(0)}/${stages.size} done",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkDim
                )
            }
        }

        // Gradient Progress Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(GraphiteElevated)
        ) {
            if (animatedProgress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(NeonMint, CobaltPrimary, CobaltIce)
                            )
                        )
                )
            }
        }

        // Horizontal Stepper Timeline
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("${testTagPrefix}_timeline_row")
        ) {
            itemsIndexed(stages, key = { _, item -> item.index }) { idx, stage ->
                val state = when {
                    idx < safeCurrent -> StageVisualState.COMPLETED
                    idx == safeCurrent -> StageVisualState.ACTIVE
                    else -> StageVisualState.UPCOMING
                }
                val isInspected = idx == safeInspected
                val isFirstInPhase = idx == 0 || stages[idx - 1].phase != stage.phase

                TimelineStageNode(
                    stage = stage,
                    state = state,
                    isInspected = isInspected,
                    isFirst = idx == 0,
                    isLast = idx == stages.lastIndex,
                    showPhaseLabel = isFirstInPhase,
                    pulseFraction = pulseFraction,
                    onClick = { inspectedIndex = idx },
                    testTag = "${testTagPrefix}_stage_${idx + 1}"
                )
            }
        }

        // Selected / Inspected Stage Detail Card
        val inspectedStage = stages[safeInspected]
        val inspectedState = when {
            safeInspected < safeCurrent -> StageVisualState.COMPLETED
            safeInspected == safeCurrent -> StageVisualState.ACTIVE
            else -> StageVisualState.UPCOMING
        }

        AnimatedContent(
            targetState = inspectedStage,
            transitionSpec = {
                fadeIn(tween(220)) togetherWith fadeOut(tween(180))
            },
            label = "inspected_stage_detail"
        ) { stage ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GraphiteSurface.copy(alpha = 0.85f),
                border = BorderStroke(
                    width = 1.dp,
                    color = when (inspectedState) {
                        StageVisualState.COMPLETED -> NeonMint.copy(alpha = 0.42f)
                        StageVisualState.ACTIVE -> CobaltBright.copy(alpha = 0.65f)
                        StageVisualState.UPCOMING -> GlassBorder
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("${testTagPrefix}_stage_detail")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (inspectedState) {
                                    StageVisualState.COMPLETED -> MintContainer
                                    StageVisualState.ACTIVE -> CobaltContainer
                                    StageVisualState.UPCOMING -> GraphiteElevated
                                }
                            ) {
                                Text(
                                    text = stage.numberLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = when (inspectedState) {
                                        StageVisualState.COMPLETED -> NeonMint
                                        StageVisualState.ACTIVE -> CobaltIce
                                        StageVisualState.UPCOMING -> InkSilver
                                    },
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = stage.phase,
                                style = MaterialTheme.typography.labelMedium,
                                color = CobaltIce,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        val pillText = when {
                            inspectedState == StageVisualState.COMPLETED -> "Completed"
                            inspectedState == StageVisualState.ACTIVE -> "In progress"
                            stage.isOptional -> "Optional"
                            else -> "Upcoming"
                        }
                        StatusPill(
                            label = pillText,
                            isLive = inspectedState == StageVisualState.COMPLETED ||
                                inspectedState == StageVisualState.ACTIVE
                        )
                    }

                    Text(
                        text = stage.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = InkPrimary
                    )
                    Text(
                        text = stage.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSilver
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Milestone ${stage.index + 1} of ${stages.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkMuted
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { if (safeInspected > 0) inspectedIndex = safeInspected - 1 },
                                enabled = safeInspected > 0,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("${testTagPrefix}_prev_stage_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                                    contentDescription = "Previous lifecycle stage",
                                    tint = if (safeInspected > 0) InkPrimary else InkDim
                                )
                            }
                            IconButton(
                                onClick = {
                                    if (safeInspected < stages.lastIndex) {
                                        inspectedIndex = safeInspected + 1
                                    }
                                },
                                enabled = safeInspected < stages.lastIndex,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("${testTagPrefix}_next_stage_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                    contentDescription = "Next lifecycle stage",
                                    tint = if (safeInspected < stages.lastIndex) InkPrimary else InkDim
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
private fun TimelineStageNode(
    stage: LifecycleStageSpec,
    state: StageVisualState,
    isInspected: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    showPhaseLabel: Boolean,
    pulseFraction: Float,
    onClick: () -> Unit,
    testTag: String
) {
    val stateLabel = when (state) {
        StageVisualState.COMPLETED -> "Completed"
        StageVisualState.ACTIVE -> "In progress"
        StageVisualState.UPCOMING -> if (stage.isOptional) "Optional upcoming" else "Upcoming"
    }
    val nodeDescription = "Stage ${stage.numberLabel}: ${stage.title}, ${stage.phase}, $stateLabel"

    val fillColor by animateColorAsState(
        targetValue = when (state) {
            StageVisualState.COMPLETED -> MintContainer
            StageVisualState.ACTIVE -> CobaltContainer
            StageVisualState.UPCOMING -> GraphiteElevated
        },
        label = "node_fill"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            isInspected && state == StageVisualState.UPCOMING -> ChampagneGold
            state == StageVisualState.COMPLETED -> NeonMint
            state == StageVisualState.ACTIVE -> CobaltIce
            else -> SilverBorder
        },
        label = "node_border"
    )

    Column(
        modifier = Modifier
            .width(114.dp)
            .defaultMinSize(minHeight = 48.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 4.dp)
            .testTag(testTag)
            .semantics(mergeDescendants = true) {
                contentDescription = nodeDescription
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Phase Marker Row (aligned height across all nodes)
        Box(
            modifier = Modifier.height(20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (showPhaseLabel) {
                val shortPhase = stage.phase.substringAfter("· ").trim()
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = GraphiteElevated.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Text(
                        text = shortPhase,
                        style = MaterialTheme.typography.labelSmall,
                        color = CobaltIce,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 1.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Connector Track + Milestone Node
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val cy = size.height / 2f
                val cx = size.width / 2f
                val nodeRadiusPx = 19.dp.toPx()

                // Left connector
                if (!isFirst) {
                    val leftEnd = cx - nodeRadiusPx - 2.dp.toPx()
                    if (leftEnd > 0f) {
                        when (state) {
                            StageVisualState.COMPLETED -> drawLine(
                                color = NeonMint,
                                start = Offset(0f, cy),
                                end = Offset(leftEnd, cy),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            StageVisualState.ACTIVE -> drawLine(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(NeonMint, CobaltIce),
                                    startX = 0f,
                                    endX = leftEnd
                                ),
                                start = Offset(0f, cy),
                                end = Offset(leftEnd, cy),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            StageVisualState.UPCOMING -> drawLine(
                                color = SilverBorder,
                                start = Offset(0f, cy),
                                end = Offset(leftEnd, cy),
                                strokeWidth = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

                // Right connector
                if (!isLast) {
                    val rightStart = cx + nodeRadiusPx + 2.dp.toPx()
                    if (rightStart < size.width) {
                        when (state) {
                            StageVisualState.COMPLETED -> drawLine(
                                color = NeonMint,
                                start = Offset(rightStart, cy),
                                end = Offset(size.width, cy),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            StageVisualState.ACTIVE -> drawLine(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(CobaltBright, SilverBorder),
                                    startX = rightStart,
                                    endX = size.width
                                ),
                                start = Offset(rightStart, cy),
                                end = Offset(size.width, cy),
                                strokeWidth = 2.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 7f), 0f),
                                cap = StrokeCap.Round
                            )
                            StageVisualState.UPCOMING -> drawLine(
                                color = SilverBorder,
                                start = Offset(rightStart, cy),
                                end = Offset(size.width, cy),
                                strokeWidth = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

                // Active stage pulsing halo
                if (state == StageVisualState.ACTIVE) {
                    val haloRadius = 22.dp.toPx() + (5.dp.toPx() * pulseFraction)
                    drawCircle(
                        color = CobaltPrimary.copy(alpha = 0.28f * (1f - pulseFraction * 0.55f)),
                        radius = haloRadius,
                        center = Offset(cx, cy)
                    )
                }

                // Inspected outer selection ring
                if (isInspected) {
                    drawCircle(
                        color = if (state == StageVisualState.COMPLETED) TrustGreen else ChampagneGold,
                        radius = 23.dp.toPx(),
                        center = Offset(cx, cy),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                    )
                }
            }

            // Milestone Badge
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(fillColor)
                    .border(
                        width = if (state == StageVisualState.ACTIVE || isInspected) 2.dp else 1.2.dp,
                        color = borderColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (state == StageVisualState.COMPLETED) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = NeonMint,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = stage.numberLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (state == StageVisualState.ACTIVE) CobaltIce else InkSilver,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stage.title,
            style = MaterialTheme.typography.labelMedium,
            color = when {
                state == StageVisualState.ACTIVE || isInspected -> InkPrimary
                state == StageVisualState.COMPLETED -> InkSilver
                else -> InkMuted
            },
            fontWeight = if (state == StageVisualState.ACTIVE || isInspected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 6.dp)
        )

        if (stage.isOptional) {
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Optional",
                style = MaterialTheme.typography.labelSmall,
                color = ChampagneGold
            )
        }
    }
}
