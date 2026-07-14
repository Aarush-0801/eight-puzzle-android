package com.aarushbhardwaj.eightpuzzle.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aarushbhardwaj.eightpuzzle.viewmodel.GameViewModel
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.activity.compose.BackHandler

/* ---------------- Accent Colors ---------------- */

private val Accent3x3 = Color(0xFF2E7DF6) // Blue
private val Accent4x4 = Color(0xFF34A853) // Green
private val Accent5x5 = Color(0xFFFF8A00) // Orange

/** Fraction of the available width the centered/focused card occupies. */
private const val CenterCardWidthFraction = 0.73f

/**
 * Premium statistics screen showing a compact overall progress summary and
 * a swipeable, Play-Store / Apple-Wallet-style stacked carousel of
 * per-grid-size statistics cards.
 *
 * @param viewModel Source of statistics data via [GameViewModel.getStatistics].
 * @param onBack Invoked when the user taps the back arrow. Navigation itself
 * is intentionally left to the caller (see MainActivity integration step).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    val stats3x3 = remember(refreshTrigger) { viewModel.getStatistics(3) }
    val stats4x4 = remember(refreshTrigger) { viewModel.getStatistics(4) }
    val stats5x5 = remember(refreshTrigger) { viewModel.getStatistics(5) }

    val allStats = listOf(stats3x3, stats4x4, stats5x5)
    val accentColors = listOf(Accent3x3, Accent4x4, Accent5x5)

    val totalGamesPlayed = stats3x3.gamesPlayed + stats4x4.gamesPlayed + stats5x5.gamesPlayed
    val totalGamesWon = stats3x3.gamesWon + stats4x4.gamesWon + stats5x5.gamesWon
    val overallWinRate = if (totalGamesPlayed == 0) {
        0f
    } else {
        (totalGamesWon * 100f) / totalGamesPlayed
    }

    val pagerState = rememberPagerState(initialPage = 0) { allStats.size }

    // Side padding chosen so the focused page occupies exactly
    // CenterCardWidthFraction of the screen width, and the remaining
    // space is evenly split to reveal both neighboring cards.
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val sidePadding = screenWidth * (1f - CenterCardWidthFraction) / 2f

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Statistics")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OverallProgressCard(
                gamesPlayed = totalGamesPlayed,
                gamesWon = totalGamesWon,
                winRate = overallWinRate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = sidePadding),
                pageSpacing = 12.dp
            ) { page ->

                val pageOffset = pagerState.offsetForPage(page)
                val proximity = 1f - abs(pageOffset).coerceIn(0f, 1f)

                // Raw linear targets based on distance from the focused page.
                // A wide range across scale, alpha, elevation, and vertical
                // offset together is what sells a genuine stacked-card-deck
                // illusion rather than a subtle peek.
                val targetScale = lerpFloat(0.80f, 1f, proximity)
                val targetAlpha = lerpFloat(0.45f, 1f, proximity)
                val targetElevation = lerpFloat(2f, 28f, proximity)
                val targetTranslationY = lerpFloat(22f, 0f, proximity)

                // Spring-animate toward those targets so the center card
                // softly settles into place instead of changing linearly.
                val cardScale by animateFloatAsState(
                    targetValue = targetScale,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "card_scale"
                )
                val cardAlpha by animateFloatAsState(
                    targetValue = targetAlpha,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "card_alpha"
                )
                val cardElevation by animateDpAsState(
                    targetValue = targetElevation.dp,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "card_elevation"
                )
                val cardTranslationY by animateFloatAsState(
                    targetValue = targetTranslationY,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "card_translation_y"
                )

                StatisticsCard(
                    stats = allStats[page],
                    accentColor = accentColors[page],
                    scale = cardScale,
                    contentAlpha = cardAlpha,
                    elevation = cardElevation,
                    onResetConfirmed = {
                        viewModel.resetStatistics(allStats[page].gridSize)
                        refreshTrigger++
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            translationY = cardTranslationY.dp.toPx()
                        }
                )
            }

            PagerIndicatorDots(
                pageCount = allStats.size,
                currentPage = pagerState.currentPage,
                activeColor = accentColors[pagerState.currentPage],
                modifier = Modifier.padding(vertical = 18.dp)
            )
        }
    }
}

/* ---------------- Overall Progress Card ---------------- */

@Composable
private fun OverallProgressCard(
    gamesPlayed: Int,
    gamesWon: Int,
    winRate: Float,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Overall Progress",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatTile(
                    icon = Icons.Default.SportsEsports,
                    label = "Played",
                    value = gamesPlayed.toString(),
                    accentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    icon = Icons.Default.EmojiEvents,
                    label = "Won",
                    value = gamesWon.toString(),
                    accentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    label = "Win Rate",
                    value = "${winRate.roundToInt()}%",
                    accentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/* ---------------- Page Indicator ---------------- */

@Composable
private fun PagerIndicatorDots(
    pageCount: Int,
    currentPage: Int,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage

            val dotWidth by animateDpAsState(
                targetValue = if (isActive) 22.dp else 6.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "pager_dot_width"
            )

            val dotAlpha by animateFloatAsState(
                targetValue = if (isActive) 1f else 0.35f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "pager_dot_alpha"
            )

            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(dotWidth)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isActive) {
                            activeColor.copy(alpha = dotAlpha)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = dotAlpha)
                        }
                    )
            )
        }
    }
}

/* ---------------- Animation Helpers ---------------- */

/**
 * Returns this page's continuous offset from the currently focused page.
 * 0f means fully centered/focused. +/-1f means one full page away.
 */
private fun PagerState.offsetForPage(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
}

/**
 * Linearly interpolates between [start] and [stop] using [fraction],
 * clamping fraction to the 0f..1f range for safety.
 */
private fun lerpFloat(start: Float, stop: Float, fraction: Float): Float {
    val clamped = fraction.coerceIn(0f, 1f)
    return start + (stop - start) * clamped
}