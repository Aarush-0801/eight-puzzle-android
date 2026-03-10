package com.example.eightpuzzle.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private fun movesKey() = "best_moves_$gridSize"
    private fun timeKey() = "best_time_$gridSize"

    private var gridSize = 3

    /* SharedPreferences */
    private val prefs =
        application.getSharedPreferences("eight_puzzle_prefs", Context.MODE_PRIVATE)

    /* ---------------- GAME STATE ---------------- */

    val tiles = mutableStateListOf<Int>()

    var moves by mutableStateOf(0)
        private set

    var seconds by mutableStateOf(0)
        private set

    var isSolved by mutableStateOf(false)
        private set

    var optimalMoves by mutableStateOf(0)
        private set

    /* ---------------- BEST SCORE ---------------- */

    var bestMoves by mutableStateOf(loadBestMoves())
        private set

    var bestTime by mutableStateOf(loadBestTime())
        private set


    /* ---------------- INTERNAL ---------------- */

    private var timerJob: Job? = null
    private var timerStarted = false
    private var isProcessingMove = false

    private lateinit var solvedState: IntArray

    /* Cache empty tile position */
    private var emptyIndex = 0


    init {
        startGame(3)
    }


    /* ---------------- TIMER ---------------- */

    private fun startTimer() {

        stopTimer()

        timerJob = viewModelScope.launch {

            while (!isSolved) {
                delay(1000)
                seconds++
            }
        }
    }


    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }


    /* ---------------- SHUFFLE ---------------- */

    fun startGame(size: Int) {

        gridSize = size

        bestMoves = loadBestMoves()
        bestTime = loadBestTime()

        isSolved = false
        shufflePuzzle()

    }


    fun shufflePuzzle() {
        optimalMoves = 0
        val total = gridSize * gridSize

        solvedState = IntArray(total) { i ->
            if (i == total - 1) 0 else i + 1
        }

        var list: List<Int>

        do {
            list = (0 until total).shuffled(Random)
        } while (!isSolvable(list) || isSolvedList(list))

        tiles.clear()
        tiles.addAll(list)

        emptyIndex = tiles.indexOf(0)

        moves = 0
        seconds = 0
        isSolved = false

        timerStarted = false
        isProcessingMove = false

        stopTimer()
        if (gridSize == 3) {
            calculateOptimal()
        }

    }


    /* ---------------- MOVE ---------------- */

    fun moveTile(index: Int) {

        if (isSolved || isProcessingMove) return

        if (!isValidMove(index, emptyIndex)) return

        isProcessingMove = true

        if (!timerStarted) {
            startTimer()
            timerStarted = true
        }

        /* Swap */
        tiles.swap(index, emptyIndex)

        /* Update empty index */
        emptyIndex = index

        moves++

        checkWinFast()

        viewModelScope.launch {
            delay(16)
            isProcessingMove = false
        }
    }


    /* ---------------- VALID MOVE ---------------- */

    private fun isValidMove(a: Int, b: Int): Boolean {

        val rowA = a / gridSize
        val colA = a % gridSize

        val rowB = b / gridSize
        val colB = b % gridSize

        return abs(rowA - rowB) + abs(colA - colB) == 1
    }


    /* ---------------- SOLVABLE ---------------- */

    private fun isSolvable(list: List<Int>): Boolean {

        var inversions = 0

        for (i in list.indices) {
            for (j in i + 1 until list.size) {

                val a = list[i]
                val b = list[j]

                if (a != 0 && b != 0 && a > b) {
                    inversions++
                }
            }
        }

        return if (gridSize % 2 == 1) {
            inversions % 2 == 0
        } else {

            val blankRowFromBottom =
                gridSize - (list.indexOf(0) / gridSize)

            if (blankRowFromBottom % 2 == 0) {
                inversions % 2 == 1
            } else {
                inversions % 2 == 0
            }
        }
    }


    /* ---------------- WIN (FAST) ---------------- */

    private fun checkWinFast() {

        for (i in solvedState.indices) {

            if (tiles[i] != solvedState[i]) return
        }

        stopTimer()

        saveBestScore()

        viewModelScope.launch {
            delay(120)
            isSolved = true
        }
    }


    private fun isSolvedList(list: List<Int>): Boolean {

        for (i in list.indices) {

            if (list[i] != solvedState[i]) return false
        }

        return true
    }


    /* ---------------- BEST SCORE ---------------- */

    private fun saveBestScore() {

        val editor = prefs.edit()

        if (bestMoves == 0 || moves < bestMoves) {
            bestMoves = moves
            editor.putInt(movesKey(), moves)
        }

        if (bestTime == 0 || seconds < bestTime) {
            bestTime = seconds
            editor.putInt(timeKey(), seconds)
        }

        editor.apply()
    }


    private fun loadBestMoves(): Int {
        return prefs.getInt(movesKey(), 0)
    }


    private fun loadBestTime(): Int {
        return prefs.getInt(timeKey(), 0)
    }


    /* ---------------- TIMER CONTROL ---------------- */

    fun pauseTimer() {
        stopTimer()
    }


    fun resumeTimer() {

        if (!isSolved && timerStarted) {
            startTimer()
        }
    }


    /* ---------------- UTILITY ---------------- */

    private fun <T> MutableList<T>.swap(i: Int, j: Int) {

        val temp = this[i]

        this[i] = this[j]
        this[j] = temp
    }

    private fun calculateOptimal() {

        val startState = tiles.toList()

        viewModelScope.launch(Dispatchers.Default) {

            val solver = AStarSolver(gridSize)

            val result = solver.solve(startState)

            if (result > 0) {

                // Switch back to Main thread for UI update
                withContext(Dispatchers.Main) {
                    optimalMoves = result
                }
            }
        }
    }


}
