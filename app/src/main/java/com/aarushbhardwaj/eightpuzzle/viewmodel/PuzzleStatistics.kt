package com.aarushbhardwaj.eightpuzzle.viewmodel

data class PuzzleStatistics(
    val gridSize: Int,
    val gamesPlayed: Int,
    val gamesWon: Int,
    val bestMoves: Int,
    val bestTime: Int,
    val averageMoves: Int,
    val averageTime: Int,
    val winRate: Float
)