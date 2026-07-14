package com.aarushbhardwaj.eightpuzzle.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * A compact stat tile used in a 2-column grid inside StatisticsCard.
 *
 * Unlike [StatisticsRow] (a full-width row meant for a single vertical
 * list), this tile is designed to sit side-by-side with a sibling tile,
 * so it packs a small icon chip, a bold value, and a label into a tight
 * horizontal footprint with a subtly tinted background.
 *
 * @param icon Icon representing the stat.
 * @param label Human readable label, e.g. "Best Moves".
 * @param value Pre-formatted display value, e.g. "24" or "1m 42s".
 * @param accentColor Accent color used for the icon, its chip, and the tile tint.
 * @param modifier Modifier applied to the outer tile.
 * @param featured When true, renders a larger "hero" variant with a bigger
 * icon chip, bigger accent-colored value text, and a stronger tint — used
 * to draw the eye to a single standout metric (e.g. Win Rate) rather than
 * treating every stat with equal visual weight.
 */
@Composable
fun StatTile(
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    featured: Boolean = false
) {
    val chipSize = if (featured) 38.dp else 30.dp
    val iconSize = if (featured) 20.dp else 16.dp
    val tileBackgroundAlpha = if (featured) 0.14f else 0.08f
    val chipBackgroundAlpha = if (featured) 0.22f else 0.18f
    val valueStyle = if (featured) {
        MaterialTheme.typography.headlineSmall
    } else {
        MaterialTheme.typography.titleMedium
    }
    val valueColor = if (featured) accentColor else MaterialTheme.colorScheme.onSurface
    val valueWeight = if (featured) FontWeight.ExtraBold else FontWeight.Bold

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(accentColor.copy(alpha = tileBackgroundAlpha))
            .padding(
                horizontal = 12.dp,
                vertical = if (featured) 12.dp else 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(chipSize)
                .clip(RoundedCornerShape(if (featured) 12.dp else 9.dp))
                .background(accentColor.copy(alpha = chipBackgroundAlpha)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(iconSize)
            )
        }

        Spacer(modifier = Modifier.width(if (featured) 10.dp else 8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = value,
                style = valueStyle,
                fontWeight = valueWeight,
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}