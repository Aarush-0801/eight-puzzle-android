package com.example.eightpuzzle.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/* 🌈 Static Background (Created Once) */
private val backgroundBrush = Brush.verticalGradient(
    listOf(
        Color(0xFF0F2027),
        Color(0xFF203A43),
        Color(0xFF2C5364)
    )
)


@Composable
fun HomeScreen(
    onPlay3x3: () -> Unit,
    onPlay4x4: () -> Unit
) {

    /* 🎈 Lightweight Floating Animation */
    val infiniteTransition = rememberInfiniteTransition()

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            /* Header Section */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = floatOffset.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            Color.White.copy(alpha = 0.15f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Games,
                        contentDescription = "Game Icon",
                        tint = Color.White,
                        modifier = Modifier.size(75.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "8 Puzzle Game",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Train Your Brain",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            /* Buttons */
            GameButton(
                text = "PLAY 3 × 3",
                onClick = onPlay3x3
            )

            Spacer(modifier = Modifier.height(16.dp))

            GameButton(
                text = "PLAY 4 × 4",
                onClick = onPlay4x4
            )

            Spacer(modifier = Modifier.height(48.dp))

            /* Footer */
            Text(
                text = "Developed by Aarush Bhardwaj",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.55f)
            )
        }
    }
}


/* ---------------- Optimized Game Button ---------------- */

@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit
) {

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    /* Lightweight Scale Animation */
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = tween(150),
        label = "btn_scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .shadow(
                if (pressed) 2.dp else 6.dp,
                RoundedCornerShape(20.dp)
            )
            .background(
                Color(0xFF00E5FF),
                RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = interaction,
                indication = null
            ) {
                onClick()
            }
            .width(220.dp)
            .height(56.dp),

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            letterSpacing = 1.sp
        )
    }
}
