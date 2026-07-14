package com.aarushbhardwaj.eightpuzzle.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aarushbhardwaj.eightpuzzle.viewmodel.PuzzleStatistics
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * A single premium statistics card for one puzzle grid size (3x3, 4x4, or 5x5).
 *
 * Intended to be placed inside a HorizontalPager. [scale], [contentAlpha], and
 * [elevation] are driven by the caller based on the card's offset from the
 * currently focused page, producing the "center card focused, side cards
 * peeking" carousel effect.
 *
 * @param stats The statistics to display for this grid size.
 * @param accentColor Accent color for this card (e.g. Blue for 3x3).
 * @param scale Uniform scale applied via graphicsLayer. 1f = full size.
 * @param contentAlpha Opacity applied via graphicsLayer. 1f = fully opaque.
 * @param elevation Shadow elevation of the card.
 * @param onResetConfirmed Invoked when the user confirms resetting this
 * grid size's statistics via the in-card reset icon and confirmation dialog.
 * @param modifier Modifier applied to the outer card.
 */
@Composable
fun StatisticsCard(
    stats: PuzzleStatistics,
    accentColor: Color,
    scale: Float = 1f,
    contentAlpha: Float = 1f,
    elevation: Dp = 18.dp,
    onResetConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetDialog by remember { mutableStateOf(false) }

    val resetInteraction = remember { MutableInteractionSource() }
    val resetPressed by resetInteraction.collectIsPressedAsState()
    val resetPressScale by animateFloatAsState(
        targetValue = if (resetPressed) 0.85f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "reset_press_scale"
    )

    var resetIconRotated by remember { mutableStateOf(false) }
    val resetIconRotation by animateFloatAsState(
        targetValue = if (resetIconRotated) -28f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "reset_icon_rotation"
    )

    val coroutineScope = rememberCoroutineScope()

    fun dismissResetDialog() {
        showResetDialog = false
        resetIconRotated = false
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = contentAlpha
            },
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            // Header: accent icon chip + grid size title + reset icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "${stats.gridSize} by ${stats.gridSize} grid",
                            tint = accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        Text(
                            text = "${stats.gridSize}\u00D7${stats.gridSize}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Classic Mode",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = {
                        resetIconRotated = true
                        coroutineScope.launch {
                            delay(180)
                            showResetDialog = true
                        }
                    },
                    interactionSource = resetInteraction,
                    modifier = Modifier
                        .size(32.dp)
                        .scale(resetPressScale)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset ${stats.gridSize} by ${stats.gridSize} statistics",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(resetIconRotation)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Compact 2-column stat grid
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatTile(
                        icon = Icons.Default.Bolt,
                        label = "Best Moves",
                        value = formatCount(stats.bestMoves),
                        accentColor = accentColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        icon = Icons.Default.Timer,
                        label = "Best Time",
                        value = formatDuration(stats.bestTime),
                        accentColor = accentColor,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatTile(
                        icon = Icons.Default.SwapHoriz,
                        label = "Average Moves",
                        value = formatCount(stats.averageMoves),
                        accentColor = accentColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        icon = Icons.Default.AvTimer,
                        label = "Average Time",
                        value = formatDuration(stats.averageTime),
                        accentColor = accentColor,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatTile(
                        icon = Icons.Default.SportsEsports,
                        label = "Games Played",
                        value = stats.gamesPlayed.toString(),
                        accentColor = accentColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        icon = Icons.Default.EmojiEvents,
                        label = "Games Won",
                        value = stats.gamesWon.toString(),
                        accentColor = accentColor,
                        modifier = Modifier.weight(1f)
                    )
                }
                StatTile(
                    icon = Icons.Default.TrendingUp,
                    label = "Win Rate",
                    value = "${stats.winRate.roundToInt()}%",
                    accentColor = accentColor,
                    modifier = Modifier.fillMaxWidth(),
                    featured = true
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { dismissResetDialog() },
            icon = {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(text = "Reset ${stats.gridSize}\u00D7${stats.gridSize} Statistics?")
            },
            text = {
                Text(
                    text = "This will permanently erase the best moves, best time, " +
                            "averages, and win history for this grid size. This cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dismissResetDialog()
                        onResetConfirmed()
                    }
                ) {
                    Text(
                        text = "Reset",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { dismissResetDialog() }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

/**
 * Formats a "best" or "average" count value (moves).
 * A value of 0 means no record exists yet, so it is shown as an em dash.
 */
private fun formatCount(value: Int): String {
    return if (value <= 0) "\u2014" else value.toString()
}

/**
 * Formats a duration stored in whole seconds into a compact, readable string.
 * A value of 0 means no record exists yet, so it is shown as an em dash.
 * Examples: 0 -> "—", 45 -> "45s", 102 -> "1m 42s".
 */
private fun formatDuration(totalSeconds: Int): String {
    if (totalSeconds <= 0) return "\u2014"
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"
}