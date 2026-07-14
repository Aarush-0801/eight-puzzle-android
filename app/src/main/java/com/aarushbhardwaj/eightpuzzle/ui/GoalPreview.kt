package com.aarushbhardwaj.eightpuzzle.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GoalPreview(
    visible: Boolean,
    gridSize: Int,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {

        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {

            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Goal Layout",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                for (row in 0 until gridSize) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        for (col in 0 until gridSize) {

                            val number = row * gridSize + col + 1
                            val isBlank = number == gridSize * gridSize

                            Box(
                                modifier = Modifier
                                    .size(
                                        when (gridSize) {
                                            3 -> 34.dp
                                            4 -> 28.dp
                                            else -> 22.dp
                                        }
                                    )
                                    .background(
                                        brush = if (!isBlank) {
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color(0xFF90CAF9),
                                                    Color(0xFF64B5F6),
                                                    Color(0xFF1E88E5)
                                                )
                                            )
                                        } else {
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color.Gray.copy(alpha = 0.15f),
                                                    Color.Gray.copy(alpha = 0.15f)
                                                )
                                            )
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                if (!isBlank) {
                                    Text(
                                        text = number.toString(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}