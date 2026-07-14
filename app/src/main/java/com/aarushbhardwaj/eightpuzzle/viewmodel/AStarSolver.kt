package com.aarushbhardwaj.eightpuzzle.viewmodel

import java.util.PriorityQueue
import kotlin.math.abs


class AStarSolver(private val size: Int) {

    private val goal: List<Int> =
        (1 until size * size).toList() + 0


    // Node for A*
    private data class Node(
        val state: List<Int>,
        val g: Int, // cost so far
        val h: Int  // heuristic
    ) {
        val f: Int get() = g + h
    }


    // Manhattan Distance (heuristic)
    private fun heuristic(state: List<Int>): Int {

        var dist = 0

        for (i in state.indices) {

            val value = state[i]

            if (value == 0) continue

            val target = value - 1

            val r1 = i / size
            val c1 = i % size

            val r2 = target / size
            val c2 = target % size

            dist += abs(r1 - r2) + abs(c1 - c2)
        }

        return dist
    }


    // Generate neighbors
    private fun neighbors(state: List<Int>): List<List<Int>> {

        val result = mutableListOf<List<Int>>()

        val zero = state.indexOf(0)

        val r = zero / size
        val c = zero % size

        val moves = listOf(
            r - 1 to c,
            r + 1 to c,
            r to c - 1,
            r to c + 1
        )

        for ((nr, nc) in moves) {

            if (nr in 0 until size && nc in 0 until size) {

                val ni = nr * size + nc

                val newState = state.toMutableList()

                newState[zero] = newState[ni]
                newState[ni] = 0

                result.add(newState)
            }
        }

        return result
    }


    // MAIN SOLVER
    fun solve(start: List<Int>): Int {

        val open = PriorityQueue<Node> { a, b ->
            a.f - b.f
        }

        val closed = HashSet<List<Int>>()

        val startNode = Node(
            start,
            0,
            heuristic(start)
        )

        open.add(startNode)

        while (open.isNotEmpty()) {

            val current = open.poll()

            if (current.state == goal) {
                return current.g   // shortest moves
            }

            if (current.state in closed) continue

            closed.add(current.state)

            for (next in neighbors(current.state)) {

                if (next !in closed) {

                    val node = Node(
                        next,
                        current.g + 1,
                        heuristic(next)
                    )

                    open.add(node)
                }
            }
        }

        return -1 // not found
    }
}
