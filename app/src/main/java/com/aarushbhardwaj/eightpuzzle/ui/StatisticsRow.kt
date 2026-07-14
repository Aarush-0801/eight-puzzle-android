package com.aarushbhardwaj.eightpuzzle.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * A single stat row used inside a StatisticsCard.
 *
 * Displays a small icon in a tinted rounded container on the left,
 * a label in the middle, and a value on the right.
 *
 * This composable is intentionally presentation-only: it does not know
 * about [com.aarushbhardwaj.eightpuzzle.viewmodel.PuzzleStatistics] or formatting
 * rules (e.g. converting seconds into "1m 42s" or showing "—" for zero
 * values). That logic belongs to the caller (StatisticsCard.kt) so this
 * row stays fully reusable.
 *
 * @param icon Icon representing the stat (e.g. moves, timer, trophy).
 * @param label Human readable label, e.g. "Best Moves".
 * @param value Pre-formatted display value, e.g. "24" or "1m 42s".
 * @param accentColor Accent color used for the icon and its background tint.
 * @param modifier Modifier applied to the outer row.
 */
@Composable
fun StatisticsRow(
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End
        )
    }
}