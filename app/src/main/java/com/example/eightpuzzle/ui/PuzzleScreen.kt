package com.example.eightpuzzle.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.eightpuzzle.viewmodel.GameViewModel
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import kotlinx.coroutines.delay
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.activity.compose.BackHandler


/* ---------------- TIME FORMAT ---------------- */

private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}


/* ---------------- MAIN SCREEN ---------------- */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PuzzleScreen(
    gridSize: Int,
    onBack: () -> Unit,
    viewModel: GameViewModel   // ✅ Pass from MainActivity
) {
    // Handle phone back button
    BackHandler {
        onBack()
    }


    val tiles by remember { derivedStateOf { viewModel.tiles } }
    val isDark = isSystemInDarkTheme()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {

        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->

            when (event) {

                androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> {
                    viewModel.pauseTimer()
                }

                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> {
                    viewModel.resumeTimer()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    /* ---------- BACKGROUND ---------- */

    val backgroundBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(
                listOf(
                    Color(0xFF02040F),
                    Color(0xFF0A192F),
                    Color(0xFF071526)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color(0xFFEEF6FF),
                    Color.White,
                    Color(0xFFE3F2FD)
                )
            )
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {

        /* ================= MAIN CONTENT ================= */

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /* ---------- HEADER ---------- */
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {

                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    /* 🔙 HEADER BAR (BALANCED) */
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // LEFT: Back + Moves
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = { onBack() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBackIosNew,
                                    contentDescription = "Back"
                                )
                            }

                            Spacer(Modifier.width(4.dp))

                            Icon(Icons.Default.DirectionsWalk, null)

                            Spacer(Modifier.width(2.dp))

                            Text(
                                text = "${viewModel.moves}",
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }


                        // CENTER: Title
                        Text(
                            text = "8 PUZZLE",
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )


                        // RIGHT: Timer
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Icon(Icons.Default.Timer, null)

                            Spacer(Modifier.width(4.dp))

                            Text(
                                text = formatTime(viewModel.seconds),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }




                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents, // Trophy
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.width(4.dp))

                            Text(
                                text = "Best: ${viewModel.bestMoves}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer, // Timer
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.width(4.dp))

                            Text(
                                text = "Best: ${formatTime(viewModel.bestTime)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }// ===== OPTIMAL MOVES (3x3 only) =====
                    // ===== OPTIMAL MOVES (STABLE) =====

                    if (gridSize == 3) {

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp), // 👈 FIXED HEIGHT (prevents jump)
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            if (viewModel.optimalMoves > 0) {

                                Text(
                                    text = "Optimal: ${viewModel.optimalMoves} moves",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // ===== END OPTIMAL =====


                }
            }

//            Spacer(modifier = Modifier.weight(1f))


            /* ---------- GRID + BUTTON (CENTERED BLOCK) ---------- */

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // 👈 balances center
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                /* ---------- GRID ---------- */

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(1f)
                    ) {

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        ) {

                            for (row in 0 until gridSize) {

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {

                                    for (col in 0 until gridSize) {

                                        val index = row * gridSize + col
                                        val tile = tiles.getOrNull(index) ?: 0

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                        ) {

                                            PuzzleTile(
                                                value = tile,
                                                onClick = {
                                                    if (!viewModel.isSolved && tile != 0) {
                                                        viewModel.moveTile(index)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                /* ---------- NEW GAME ---------- */

                val buttonInteraction = remember { MutableInteractionSource() }
                val pressed by buttonInteraction.collectIsPressedAsState()

                val scale by animateFloatAsState(
                    if (pressed) 0.95f else 1f,
                    tween(120),
                    label = "btn"
                )

                Box(Modifier.fillMaxWidth(), Alignment.Center) {

                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .shadow(
                                if (pressed) 2.dp else 4.dp,
                                RoundedCornerShape(14.dp)
                            )
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFFBBDEFB),
                                        Color(0xFF64B5F6),
                                        Color(0xFF1976D2)
                                    )
                                ),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable(
                                interactionSource = buttonInteraction,
                                indication = null,
                            ) {
                                viewModel.startGame(gridSize)
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            "New Game",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(24.dp))


            /* ---------- FOOTER ---------- */

            Text(
                "Developed by Aarush Bhardwaj",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }


        /* ================= WIN SCREEN ================= */

        if (viewModel.isSolved) {
            var showConfetti by remember { mutableStateOf(false) }

            LaunchedEffect(key1 = viewModel.isSolved) {

                if (viewModel.isSolved) {

                    showConfetti = true      // Start confetti
                    delay(6000)              // Run for 6 sec
                    showConfetti = false     // Stop confetti
                }
            }


            var startAnim by remember { mutableStateOf(false) }

            LaunchedEffect(key1 = viewModel.isSolved) {
                startAnim = false
                delay(40)
                startAnim = true
            }

            val scale by animateFloatAsState(
                if (startAnim) 1f else 0.6f,
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "scale"
            )

            val alpha by animateFloatAsState(
                if (startAnim) 1f else 0f,
                tween(300),
                label = "alpha"
            )

            val offset by animateFloatAsState(
                if (startAnim) 0f else 100f,
                tween(300, easing = FastOutSlowInEasing),
                label = "offset"
            )


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.7f))
            ) {

                /* 🎊 Confetti */
                if (showConfetti && viewModel.isSolved) {
                    ConfettiAnimation()
                }


                /* Win Card */

                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationY = offset
                                this.alpha = alpha
                            }
                            .background(
                                Color.White,
                                RoundedCornerShape(24.dp)
                            )
                            .padding(28.dp)
                            .width(260.dp)
                    ) {

                        /* Trophy */
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "You Won!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(16.dp))


                        /* Stats Card */
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5F7FA)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {

                                StatRow("Moves", viewModel.moves.toString())

                                StatRow("Time", formatTime(viewModel.seconds))

                                if (
                                    viewModel.moves == viewModel.bestMoves ||
                                    viewModel.seconds == viewModel.bestTime
                                ) {
                                    Text(
                                        "🏆 New Best!",
                                        color = Color(0xFFFF9800),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))


                        /* Play Again Button */
                        Button(
                            onClick = { viewModel.startGame(gridSize) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Play Again", fontWeight = FontWeight.Bold)
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            label,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )

        Text(
            value,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}


/* ---------------- TILE ---------------- */

@Composable
fun PuzzleTile(
    value: Int,
    onClick: () -> Unit
) {

    val tileBrush = remember {
        Brush.verticalGradient(
            listOf(
                Color(0xFF90CAF9),
                Color(0xFF64B5F6),
                Color(0xFF1E88E5)
            )
        )
    }

    if (value == 0) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Gray.copy(alpha = 0.15f),
                    RoundedCornerShape(16.dp)
                )
        )

    } else {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    tileBrush,
                    RoundedCornerShape(16.dp)
                )
                .clickable(enabled = true) {
                    onClick()
                },

            contentAlignment = Alignment.Center
        ) {

            Text(
                value.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


/* ---------------- CONFETTI ---------------- */

@Composable
fun ConfettiAnimation() {

    val count = 20

    val colors = listOf(
        Color(0xFFFF5252),
        Color(0xFFFFEB3B),
        Color(0xFF69F0AE),
        Color(0xFF40C4FF),
        Color(0xFFE040FB),
        Color(0xFFFF9800)
    )
    val transition = rememberInfiniteTransition(label = "confetti_${System.currentTimeMillis()}")

    Box(modifier = Modifier.fillMaxSize()) {

        repeat(count) { index ->

            // 🎯 Spawn from LEFT / CENTER / RIGHT (TOP only)
            val startX = remember {
                when (index % 3) {
                    0 -> (-40..40).random().toFloat()      // left
                    1 -> (140..220).random().toFloat()     // center
                    else -> (320..420).random().toFloat()  // right
                }
            }

            val size = remember { (6..14).random().dp }
            val fallDuration = remember { (3500..6000).random() }
            val delayMillis = remember { (0..1200).random() }

            // ⬇️ ALWAYS start from TOP
            val y by transition.animateFloat(
                initialValue = -120f,
                targetValue = 900f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = fallDuration,
                        delayMillis = delayMillis,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "y"
            )

            // 🌬 Side drift (small, natural)
            val driftX by transition.animateFloat(
                initialValue = 0f,
                targetValue = (-80..80).random().toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(fallDuration),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "drift"
            )

            val rotation by transition.animateFloat(
                0f,
                360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1600),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = (startX + driftX).dp,
                        y = y.dp
                    )
                    .size(size)
                    .graphicsLayer {
                        rotationZ = rotation
                    }
                    .background(
                        colors[index % colors.size],
                        RoundedCornerShape(50)
                    )
            )
        }
    }
}