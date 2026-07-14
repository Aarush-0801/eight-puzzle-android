package com.aarushbhardwaj.eightpuzzle.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import androidx.compose.material3.Surface
import androidx.compose.foundation.BorderStroke

private data class TileSpec(
    val label: String,
    val gradient: Brush,
    val phaseShift: Float,
    val isEmpty: Boolean = false
)

@Composable
fun HomeScreen(
    onPlay3x3: () -> Unit,
    onPlay4x4: () -> Unit,
    onPlay5x5: () -> Unit,
    onViewStatistics: () -> Unit
) {
    val backgroundBrush = remember {
        Brush.verticalGradient(
            listOf(
                Color(0xFF14192B),
                Color(0xFF1B2340),
                Color(0xFF212B4A)
            )
        )
    }

    val glowTop = remember {
        Brush.radialGradient(
            listOf(Color(0xFF5B8CFF).copy(alpha = 0.30f), Color.Transparent)
        )
    }

    val glowBottom = remember {
        Brush.radialGradient(
            listOf(Color(0xFFFF7A59).copy(alpha = 0.18f), Color.Transparent)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(x = 220.dp, y = (-90).dp)
                .background(glowTop, CircleShape)
                .blur(65.dp)
        )

        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-90).dp, y = 80.dp)
                .background(glowBottom, CircleShape)
                .blur(70.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp),
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            PuzzleHero()

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Slide into focus",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pick a grid size and start training your pattern recognition, one tile at a time.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.62f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            GameModeCard(
                index = "01",
                title = "3 × 3",
                subtitle = "Beginner",
                difficultyLevel = 1,
                accentStart = Color(0xFF33D6C2),
                accentEnd = Color(0xFF1FA890),
                onClick = onPlay3x3
            )

            Spacer(modifier = Modifier.height(14.dp))

            GameModeCard(
                index = "02",
                title = "4 × 4",
                subtitle = "Intermediate",
                difficultyLevel = 2,
                accentStart = Color(0xFF8A5CFF),
                accentEnd = Color(0xFF5B3BC4),
                onClick = onPlay4x4
            )

            Spacer(modifier = Modifier.height(14.dp))

            GameModeCard(
                index = "03",
                title = "5 × 5",
                subtitle = "Expert",
                difficultyLevel = 3,
                accentStart = Color(0xFFFF8A5B),
                accentEnd = Color(0xFFE85D3D),
                onClick = onPlay5x5
            )

            Spacer(modifier = Modifier.height(28.dp))

            StatisticsRow(onClick = onViewStatistics)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PuzzleHero() {
    val density = LocalDensity.current

    val infiniteTransition = rememberInfiniteTransition(label = "hero_transition")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    val tileGradients = remember {
        listOf(
            Brush.linearGradient(listOf(Color(0xFF33D6C2), Color(0xFF1FA890))),
            Brush.linearGradient(listOf(Color(0xFF5B8CFF), Color(0xFF3A64D8))),
            Brush.linearGradient(listOf(Color(0xFF8A5CFF), Color(0xFF5B3BC4))),
            Brush.linearGradient(listOf(Color(0xFFFF8A5B), Color(0xFFE85D3D))),
            Brush.linearGradient(listOf(Color(0xFFFFC24B), Color(0xFFE8A020))),
            Brush.linearGradient(listOf(Color(0xFFFF6D9E), Color(0xFFD8437A))),
            Brush.linearGradient(listOf(Color(0xFF4DD0E1), Color(0xFF2AA6B8))),
            Brush.linearGradient(listOf(Color(0xFFB388FF), Color(0xFF8560D6)))
        )
    }

    val emptyTileGradient = remember {
        Brush.linearGradient(
            listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.03f))
        )
    }

    val tiles = remember {
        listOf(
            TileSpec("1", tileGradients[0], phaseShift = 0.0f),
            TileSpec("2", tileGradients[1], phaseShift = 0.7f),
            TileSpec("3", tileGradients[2], phaseShift = 1.4f),
            TileSpec("4", tileGradients[3], phaseShift = 2.1f),
            TileSpec("", emptyTileGradient, phaseShift = 0f, isEmpty = true),
            TileSpec("5", tileGradients[4], phaseShift = 2.8f),
            TileSpec("6", tileGradients[5], phaseShift = 3.5f),
            TileSpec("7", tileGradients[6], phaseShift = 4.2f),
            TileSpec("8", tileGradients[7], phaseShift = 4.9f)
        )
    }

    val blockAmplitudePx = remember(density) { with(density) { 3.dp.toPx() } }
    val tileAmplitudePx = remember(density) { with(density) { 1.6.dp.toPx() } }

    val cellSize = 41.dp
    val gap = 8.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .semantics { contentDescription = "8 puzzle sliding tile illustration" },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer { alpha = glowPulse }
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF5B8CFF).copy(alpha = 0.34f), Color.Transparent)
                    ),
                    CircleShape
                )
                .blur(30.dp)
        )

        Column(
            modifier = Modifier
                .graphicsLayer {
                    translationY = sin(phase) * blockAmplitudePx
                    shadowElevation = 18f
                },
            verticalArrangement = Arrangement.spacedBy(gap),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 0 until 3) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(gap)
                ) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        val tile = tiles[index]

                        if (tile.isEmpty) {
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .background(tile.gradient, RoundedCornerShape(11.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .graphicsLayer {
                                        translationY = sin(phase + tile.phaseShift) * tileAmplitudePx
                                        shadowElevation = 6f
                                    }
                                    .background(tile.gradient, RoundedCornerShape(11.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tile.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
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
private fun GameModeCard(
    index: String,
    title: String,
    subtitle: String,
    difficultyLevel: Int,
    accentStart: Color,
    accentEnd: Color,
    onClick: () -> Unit
) {
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )

    val elevationDp by animateDpAsState(
        targetValue = if (pressed) 1.dp else 6.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_elevation"
    )

    val monogramBrush = remember(accentStart, accentEnd) {
        Brush.linearGradient(listOf(accentStart, accentEnd))
    }

    val cardShape = remember { RoundedCornerShape(22.dp) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accentStart),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            ),
        shape = cardShape,
        color = Color(0xFF2B3145),
        shadowElevation = elevationDp,
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(monogramBrush, RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.62f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        for (dot in 1..3) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(
                                        color = if (dot <= difficultyLevel) {
                                            accentStart
                                        } else {
                                            Color.White.copy(alpha = 0.20f)
                                        },
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.70f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StatisticsRow(onClick: () -> Unit) {
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "stats_scale"
    )

    val elevationDp by animateDpAsState(
        targetValue = if (pressed) 0.dp else 3.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "stats_elevation"
    )

    val rowShape = remember { RoundedCornerShape(22.dp) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            ),
        shape = rowShape,
        color = Color(0xFF2B3145),
        shadowElevation = elevationDp,
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.BarChart,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.70f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "View your statistics",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.82f),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.48f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}