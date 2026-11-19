package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BrassDark
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CobaltBright
import com.example.ui.theme.CobaltContainer
import com.example.ui.theme.CobaltIce
import com.example.ui.theme.CobaltPrimary
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GraphiteBg
import com.example.ui.theme.GraphiteCard
import com.example.ui.theme.GraphiteElevated
import com.example.ui.theme.GraphiteSurface
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSilver
import com.example.ui.theme.MintContainer
import com.example.ui.theme.NeonMint
import com.example.ui.theme.SilverBorder
import com.example.ui.theme.StatusWarn
import com.example.ui.theme.TrustGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TrueRyzeBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "bg_waves")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GraphiteBg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Subtle cobalt glow orb top-right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CobaltPrimary.copy(alpha = 0.16f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.78f, h * 0.14f),
                    radius = maxOf(w, h) * 0.55f
                ),
                center = Offset(w * 0.78f, h * 0.14f),
                radius = maxOf(w, h) * 0.55f
            )

            // Liquid silver & cobalt ribbons
            val ribbons = 4
            for (i in 0 until ribbons) {
                val baseY = h * (0.18f + i * 0.21f)
                val amp = 26f + i * 8f
                val ribbonPath = Path()
                val step = (w / 18f).coerceAtLeast(16f)
                var x = 0f
                while (x <= w + step) {
                    val norm = x / w
                    val y = baseY +
                        sin(norm * 4.2f + phase + i * 1.4f) * amp +
                        cos(norm * 2.5f - phase * 0.7f + i) * (amp * 0.4f)
                    if (x == 0f) ribbonPath.moveTo(x, y) else ribbonPath.lineTo(x, y)
                    x += step
                }
                drawPath(
                    path = ribbonPath,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            CobaltBright.copy(alpha = 0.16f),
                            InkSilver.copy(alpha = 0.11f),
                            Color.Transparent
                        )
                    ),
                    style = Stroke(width = 2.2f, cap = StrokeCap.Round)
                )
            }
        }
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    accentBorder: Color = SilverBorder,
    glowColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val backgroundBrush = if (glowColor != null) {
        Brush.verticalGradient(
            colors = listOf(
                glowColor.copy(alpha = 0.13f),
                GraphiteCard.copy(alpha = 0.94f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                GraphiteElevated.copy(alpha = 0.72f),
                GraphiteCard.copy(alpha = 0.90f)
            )
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, accentBorder, MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .background(backgroundBrush)
                .padding(20.dp),
            content = content
        )
    }
}

@Composable
fun EyebrowText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = CobaltIce,
        modifier = modifier
    )
}

@Composable
fun StatusPill(
    label: String,
    isLive: Boolean,
    modifier: Modifier = Modifier
) {
    val bg = if (isLive) MintContainer else GraphiteElevated
    val fg = if (isLive) TrustGreen else InkSilver
    val border = if (isLive) NeonMint.copy(alpha = 0.45f) else GlassBorder

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = bg,
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isLive) NeonMint else InkMuted)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = fg
            )
        }
    }
}

@Composable
fun PrimaryPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CobaltPrimary,
            contentColor = InkPrimary,
            disabledContainerColor = GraphiteElevated,
            disabledContentColor = InkMuted
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .let { if (testTag != null) it.testTag(testTag) else it }
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SecondaryPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String? = null
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, SilverBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = GraphiteElevated.copy(alpha = 0.55f),
            contentColor = InkPrimary
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .let { if (testTag != null) it.testTag(testTag) else it }
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * Plays the official TRUE RYZE 8-column transparent handshake spritesheet (79 frames: 42 entrance + 37 handshake loop).
 * Plays once upon entering Home, settles cleanly, and replays the handshake loop on tap.
 */
@Composable
fun HandshakePlayer(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sheetBitmap by produceState<ImageBitmap?>(initialValue = null, context) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                context.assets.open("handshake_sheet.webp").use { input ->
                    val opts = BitmapFactory.Options().apply {
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }
                    BitmapFactory.decodeStream(input, null, opts)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }

    val cols = 8
    val frameW = 640
    val frameH = 328
    val entFrames = 42
    val loopFrames = 37
    val fps = 20

    var frameIndex by remember { mutableIntStateOf(0) }
    var replaySignal by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }

    LaunchedEffect(sheetBitmap, replaySignal) {
        if (sheetBitmap == null) return@LaunchedEffect
        isPlaying = true
        val frameDelayMs = 1000L / fps
        if (replaySignal == 0) {
            for (f in 0 until (entFrames + loopFrames)) {
                frameIndex = f
                delay(frameDelayMs)
            }
        } else {
            for (f in entFrames until (entFrames + loopFrames)) {
                frameIndex = f
                delay(frameDelayMs)
            }
        }
        frameIndex = entFrames
        isPlaying = false
    }

    val cd = stringResource(R.string.cd_handshake_animation)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(frameW.toFloat() / frameH.toFloat())
            .clip(MaterialTheme.shapes.large)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        CobaltPrimary.copy(alpha = if (isPlaying) 0.24f else 0.14f),
                        GraphiteSurface.copy(alpha = 0.85f)
                    )
                )
            )
            .border(1.dp, CobaltBright.copy(alpha = 0.28f), MaterialTheme.shapes.large)
            .clickable(role = Role.Button) {
                if (!isPlaying) replaySignal++
            }
            .testTag("handshake_player")
            .semantics { contentDescription = cd }
    ) {
        val bmp = sheetBitmap
        if (bmp != null) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val safeIdx = frameIndex.coerceIn(0, entFrames + loopFrames - 1)
                val sx = (safeIdx % cols) * frameW
                val sy = (safeIdx / cols) * frameH
                if (sx + frameW <= bmp.width && sy + frameH <= bmp.height) {
                    drawImage(
                        image = bmp,
                        srcOffset = IntOffset(sx, sy),
                        srcSize = IntSize(frameW, frameH),
                        dstOffset = IntOffset.Zero,
                        dstSize = IntSize(size.width.toInt(), size.height.toInt())
                    )
                }
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp),
            shape = RoundedCornerShape(999.dp),
            color = GraphiteBg.copy(alpha = 0.72f),
            border = BorderStroke(1.dp, GlassBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    tint = CobaltIce,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = if (isPlaying) "Shaking hands..." else "Tap to shake",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSilver
                )
            }
        }
    }
}

/**
 * Faithful Jetpack Compose Canvas recreation of the TRUE RYZE Portal luxury spiral floor lamp,
 * complete with brass stems, potted plant on upper shelf, candle, warm-to-cool color temperature slider,
 * and tactile ON/OFF switch.
 */
@Composable
fun InteractiveLampScene(
    isLit: Boolean,
    colorTemp: Float,
    onToggleLit: () -> Unit,
    onColorTempChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val lightAlpha by animateFloatAsState(
        targetValue = if (isLit) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "lamp_alpha"
    )

    val tempColor = remember(colorTemp) {
        when {
            colorTemp < 0.35f -> lerp(Color(0xFFFFA032), Color(0xFFFFD06E), colorTemp / 0.35f)
            colorTemp < 0.65f -> lerp(Color(0xFFFFD06E), Color(0xFFFFFFFF), (colorTemp - 0.35f) / 0.30f)
            else -> lerp(Color(0xFFFFFFFF), Color(0xFF3C7DFF), (colorTemp - 0.65f) / 0.35f)
        }
    }
    val tempLabel = when {
        colorTemp < 0.35f -> "Warm"
        colorTemp > 0.65f -> "Cool"
        else -> "Daylight"
    }

    val switchPlateColor by animateColorAsState(
        targetValue = if (isLit) CobaltContainer else GraphiteElevated,
        label = "switch_plate"
    )
    val cdLamp = stringResource(R.string.cd_lamp_scene)

    GlassCard(
        modifier = modifier,
        accentBorder = if (isLit) tempColor.copy(alpha = 0.55f) else SilverBorder,
        glowColor = if (isLit) tempColor else null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left: Custom Canvas Spiral Lamp
            Box(
                modifier = Modifier
                    .width(126.dp)
                    .height(228.dp)
                    .semantics { contentDescription = cdLamp }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val scaleX = size.width / 300f
                    val scaleY = size.height / 650f
                    fun px(x: Float) = x * scaleX
                    fun py(y: Float) = y * scaleY

                    // Ambient light cone & floor glow when lit
                    if (lightAlpha > 0.01f) {
                        val cone = Path().apply {
                            moveTo(px(56f), py(128f))
                            lineTo(px(244f), py(128f))
                            lineTo(px(295f), py(595f))
                            lineTo(px(5f), py(595f))
                            close()
                        }
                        drawPath(
                            path = cone,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    tempColor.copy(alpha = 0.38f * lightAlpha),
                                    tempColor.copy(alpha = 0.02f * lightAlpha)
                                ),
                                startY = py(120f),
                                endY = py(600f)
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    tempColor.copy(alpha = 0.55f * lightAlpha),
                                    Color.Transparent
                                ),
                                center = Offset(px(150f), py(110f)),
                                radius = px(135f)
                            ),
                            center = Offset(px(150f), py(110f)),
                            radius = px(135f)
                        )
                    }

                    // Dual vertical brass rods
                    val goldBrush = Brush.horizontalGradient(
                        listOf(BrassDark, ChampagneGold, Color(0xFFB38E42))
                    )
                    drawRoundRect(
                        brush = goldBrush,
                        topLeft = Offset(px(125f), py(124f)),
                        size = Size(px(6f), py(432f)),
                        cornerRadius = CornerRadius(3f, 3f)
                    )
                    drawRoundRect(
                        brush = goldBrush,
                        topLeft = Offset(px(170f), py(124f)),
                        size = Size(px(6f), py(432f)),
                        cornerRadius = CornerRadius(3f, 3f)
                    )

                    // Helical spiral ribbons
                    val spiralColor = if (isLit) {
                        lerp(InkSilver, tempColor, 0.45f)
                    } else {
                        Color(0xFF565D6D)
                    }
                    for (phaseOffset in listOf(0f, PI.toFloat())) {
                        val helixPath = Path()
                        var first = true
                        var y = 136f
                        while (y <= 524f) {
                            val theta = ((y - 136f) / (524f - 136f)) * 1.9f * 2f * PI.toFloat() + phaseOffset
                            val x = 150f + 66f * sin(theta)
                            if (first) {
                                helixPath.moveTo(px(x), py(y))
                                first = false
                            } else {
                                helixPath.lineTo(px(x), py(y))
                            }
                            y += 4f
                        }
                        drawPath(
                            path = helixPath,
                            color = spiralColor,
                            style = Stroke(width = 3.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }

                    // Three circular shelves
                    listOf(218f to 62f, 334f to 56f, 436f to 52f).forEach { (sy, rx) ->
                        drawOval(
                            color = Color(0xFF1A1D24),
                            topLeft = Offset(px(150f - rx), py(sy - 10f)),
                            size = Size(px(rx * 2f), py(20f))
                        )
                        drawOval(
                            brush = goldBrush,
                            topLeft = Offset(px(150f - rx), py(sy - 10f)),
                            size = Size(px(rx * 2f), py(20f)),
                            style = Stroke(width = 1.6f)
                        )
                    }

                    // Small plant foliage on top shelf
                    val plantGreens = listOf(Color(0xFF2F6B45), Color(0xFF3F8A57), Color(0xFF58A56B))
                    for (i in 0 until 7) {
                        val ang = -2.4f + i * 0.42f
                        drawCircle(
                            color = plantGreens[i % plantGreens.size],
                            radius = px(8f),
                            center = Offset(px(150f + cos(ang) * 16f), py(192f + sin(ang) * 10f))
                        )
                    }

                    // Amber table accent on middle shelf
                    drawRoundRect(
                        color = AmberGold,
                        topLeft = Offset(px(180f), py(304f)),
                        size = Size(px(20f), py(27f)),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Framed artwork on lower shelf
                    drawRect(
                        color = Color(0xFF2B3A52),
                        topLeft = Offset(px(124f), py(394f)),
                        size = Size(px(28f), py(32f))
                    )
                    drawRect(
                        brush = goldBrush,
                        topLeft = Offset(px(124f), py(394f)),
                        size = Size(px(28f), py(32f)),
                        style = Stroke(width = 1.6f)
                    )

                    // Linen lampshade
                    val shadeColor = if (isLit) lerp(Color(0xFFF8F2E4), tempColor, 0.35f) else Color(0xFFAFA798)
                    val shadePath = Path().apply {
                        moveTo(px(52f), py(36f))
                        lineTo(px(248f), py(36f))
                        lineTo(px(248f), py(122f))
                        lineTo(px(52f), py(122f))
                        close()
                    }
                    drawPath(path = shadePath, color = shadeColor)
                    drawLine(
                        brush = goldBrush,
                        start = Offset(px(52f), py(122f)),
                        end = Offset(px(248f), py(122f)),
                        strokeWidth = 2.4f
                    )

                    // Base pedestal + small candle
                    drawOval(
                        color = Color(0xFF14161B),
                        topLeft = Offset(px(52f), py(552f)),
                        size = Size(px(196f), py(34f))
                    )
                    drawOval(
                        brush = goldBrush,
                        topLeft = Offset(px(52f), py(552f)),
                        size = Size(px(196f), py(34f)),
                        style = Stroke(width = 2f)
                    )
                    drawRoundRect(
                        color = Color(0xFFF2EFE8),
                        topLeft = Offset(px(178f), py(536f)),
                        size = Size(px(22f), py(22f)),
                        cornerRadius = CornerRadius(3f, 3f)
                    )
                    if (isLit) {
                        drawCircle(
                            color = AmberGold,
                            radius = px(5f),
                            center = Offset(px(189f), py(528f))
                        )
                    }
                }
            }

            // Right: Quote + Tactile Rocker Switch
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isLit) {
                        "Welcome in. Your journey starts here."
                    } else {
                        "Switch on the light to light up your journey with us."
                    },
                    style = MaterialTheme.typography.titleLarge,
                    color = InkPrimary
                )
                Text(
                    text = if (isLit) {
                        "Light on ($tempLabel). Slide below to tune the studio tone."
                    } else {
                        "Flip the switch to illuminate your Client Access & Portal view."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkMuted
                )

                // Tactile switch button (meets 48dp+ minimum touch target)
                Surface(
                    onClick = onToggleLit,
                    shape = RoundedCornerShape(16.dp),
                    color = switchPlateColor,
                    border = BorderStroke(
                        width = 1.2.dp,
                        color = if (isLit) tempColor.copy(alpha = 0.7f) else SilverBorder
                    ),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 140.dp, minHeight = 52.dp)
                        .testTag("lamp_switch")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PowerSettingsNew,
                            contentDescription = null,
                            tint = if (isLit) tempColor else InkSilver,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = if (isLit) "Light ON" else "Switch ON",
                            style = MaterialTheme.typography.labelLarge,
                            color = InkPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Color Temperature Slider (Warm -> Daylight -> Cool)
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Warm", style = MaterialTheme.typography.labelSmall, color = ChampagneGold)
                Text(
                    text = if (isLit) "● $tempLabel" else "Daylight",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isLit) tempColor else InkMuted
                )
                Text("Cool", style = MaterialTheme.typography.labelSmall, color = CobaltIce)
            }
            Slider(
                value = colorTemp,
                onValueChange = onColorTempChange,
                enabled = isLit,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = tempColor,
                    activeTrackColor = tempColor,
                    inactiveTrackColor = GraphiteElevated
                ),
                modifier = Modifier.testTag("lamp_temp_slider")
            )
        }
    }
}

@Composable
fun BulletRow(text: String, iconTint: Color = NeonMint) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .padding(top = 3.dp)
                .size(15.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = InkSilver
        )
    }
}
