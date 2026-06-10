package com.example.caro.ai

import com.example.caro.model.Difficulty
import com.example.caro.model.Player
import kotlin.math.abs

class GomokuAI {
    private val boardSize = 15
    private val winLen = 5

    fun getBestMove(board: Array<Array<Player?>>, aiPlayer: Player, difficulty: Difficulty): Pair<Int, Int> {
        val searchDepth = when (difficulty) {
            Difficulty.EASY -> 1
            Difficulty.NORMAL -> 3
            Difficulty.HARD -> 4
        }

        var bestScore = Int.MIN_VALUE
        var bestMove = Pair(7, 7)
        
        // Requirement 2: Move Ordering for the initial search
        val candidates = getCandidates(board)
            .sortedByDescending { scoreMoveHeuristic(board, it.first, it.second, aiPlayer) }

        for ((r, c) in candidates) {
            board[r][c] = aiPlayer
            val score = minimax(board, searchDepth - 1, false, Int.MIN_VALUE, Int.MAX_VALUE, aiPlayer)
            board[r][c] = null
            if (score > bestScore) {
                bestScore = score
                bestMove = Pair(r, c)
            }
        }
        return bestMove
    }

    private fun minimax(
        board: Array<Array<Player?>>,
        depth: Int, isMaximizing: Boolean,
        alpha: Int, beta: Int,
        aiPlayer: Player
    ): Int {
        val human = if (aiPlayer == Player.X) Player.O else Player.X
        val winner = checkWinner(board)

        if (winner == aiPlayer) return 10000000 + depth
        if (winner == human) return -10000000 - depth
        if (depth == 0 || isFull(board)) return evaluate(board, aiPlayer)

        var alphaVar = alpha
        var betaVar = beta
        
        // Requirement 2: Move Ordering in minimax to trigger Alpha-Beta cutoffs earlier
        val currentPlayer = if (isMaximizing) aiPlayer else human
        val candidates = getCandidates(board)
            .sortedByDescending { scoreMoveHeuristic(board, it.first, it.second, currentPlayer) }

        return if (isMaximizing) {
            var best = Int.MIN_VALUE
            for ((r, c) in candidates) {
                board[r][c] = aiPlayer
                best = maxOf(best, minimax(board, depth - 1, false, alphaVar, betaVar, aiPlayer))
                board[r][c] = null
                alphaVar = maxOf(alphaVar, best)
                if (betaVar <= alphaVar) break
            }
            best
        } else {
            var best = Int.MAX_VALUE
            for ((r, c) in candidates) {
                board[r][c] = human
                best = minOf(best, minimax(board, depth - 1, true, alphaVar, betaVar, aiPlayer))
                board[r][c] = null
                betaVar = minOf(betaVar, best)
                if (betaVar <= alphaVar) break
            }
            best
        }
    }

    private fun getCandidates(board: Array<Array<Player?>>): List<Pair<Int, Int>> {
        val candidates = mutableSetOf<Pair<Int, Int>>()
        var hasAny = false

        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                if (board[r][c] == null) continue
                hasAny = true
                for (dr in -2..2) {
                    for (dc in -2..2) {
                        val nr = r + dr
                        val nc = c + dc
                        if (nr in 0 until boardSize && nc in 0 until boardSize && board[nr][nc] == null) {
                            candidates.add(Pair(nr, nc))
                        }
                    }
                }
            }
        }
        if (!hasAny) return listOf(Pair(7, 7))

        return candidates.toList()
    }

    private fun checkWinner(board: Array<Array<Player?>>): Player? {
        val dirs = listOf(0 to 1, 1 to 0, 1 to 1, 1 to -1)
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                val p = board[r][c] ?: continue
                for ((dr, dc) in dirs) {
                    var count = 1
                    var nr = r + dr
                    var nc = c + dc
                    while (nr in 0 until boardSize && nc in 0 until boardSize && board[nr][nc] == p) {
                        count++
                        nr += dr
                        nc += dc
                    }
                    if (count >= winLen) return p
                }
            }
        }
        return null
    }

    private fun isFull(board: Array<Array<Player?>>): Boolean =
        board.all { row -> row.all { it != null } }

    private fun evaluate(board: Array<Array<Player?>>, aiPlayer: Player): Int {
        val human = if (aiPlayer == Player.X) Player.O else Player.X
        val aiScore = scoreFor(board, aiPlayer)
        val humanScore = scoreFor(board, human)
        // AI prioritizes blocking human threats slightly more (weight 1.4)
        return aiScore - (humanScore * 1.4).toInt()
    }

    /**
     * Requirement 1: Improved heuristic using a sliding window of size 5.
     * This naturally recognizes gaps/broken lines (e.g. X_XX or X_X_X)
     * because it counts pieces in a potential winning span regardless of order.
     */
    private fun scoreFor(board: Array<Array<Player?>>, player: Player): Int {
        val opponent = if (player == Player.X) Player.O else Player.X
        val dirs = listOf(0 to 1, 1 to 0, 1 to 1, 1 to -1)
        var total = 0

        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                for ((dr, dc) in dirs) {
                    val endR = r + 4 * dr
                    val endC = c + 4 * dc
                    if (endR !in 0 until boardSize || endC !in 0 until boardSize) continue

                    var playerCount = 0
                    var opponentCount = 0
                    
                    // Sliding window of 5 cells
                    for (i in 0 until 5) {
                        val p = board[r + i * dr][c + i * dc]
                        if (p == player) playerCount++
                        else if (p == opponent) {
                            opponentCount++
                            break // Blocked window
                        }
                    }

                    if (opponentCount == 0 && playerCount > 0) {
                        var windowScore = when (playerCount) {
                            5 -> 10000000
                            4 -> 100000   // Strong threat even with a gap
                            3 -> 1000
                            2 -> 100
                            1 -> 5
                            else -> 0
                        }
                        
                        // Bonus for open-ended threats (open at both ends)
                        if (playerCount in 2..4) {
                            val beforeR = r - dr
                            val beforeC = c - dc
                            val afterR = r + 5 * dr
                            val afterC = c + 5 * dc
                            val beforeEmpty = beforeR in 0 until boardSize && beforeC in 0 until boardSize && board[beforeR][beforeC] == null
                            val afterEmpty = afterR in 0 until boardSize && afterC in 0 until boardSize && board[afterR][afterC] == null
                            
                            if (beforeEmpty && afterEmpty) windowScore *= 10 // e.g., _XXX_ or _XXXX_
                            else if (beforeEmpty || afterEmpty) windowScore *= 2 // Semi-open
                        }
                        total += windowScore
                    }
                }
            }
        }
        return total
    }

    /**
     * Requirement 2: Heuristic to score potential moves for ordering.
     * High priority to moves that complete threats or block opponent's threats.
     */
    private fun scoreMoveHeuristic(board: Array<Array<Player?>>, r: Int, c: Int, player: Player): Int {
        val opponent = if (player == Player.X) Player.O else Player.X
        var score = 0
        val dirs = listOf(0 to 1, 1 to 0, 1 to 1, 1 to -1)
        
        for ((dr, dc) in dirs) {
            // Check windows of 5 that include the potential move at (r, c)
            for (offset in -4..0) {
                val startR = r + offset * dr
                val startC = c + offset * dc
                val endR = startR + 4 * dr
                val endC = startC + 4 * dc
                
                if (startR in 0 until boardSize && startC in 0 until boardSize &&
                    endR in 0 until boardSize && endC in 0 until boardSize) {
                    
                    var pCount = 0
                    var oCount = 0
                    for (i in 0 until 5) {
                        val currR = startR + i * dr
                        val currC = startC + i * dc
                        val p = if (currR == r && currC == c) player else board[currR][currC]
                        if (p == player) pCount++
                        else if (p == opponent) oCount++
                    }
                    
                    // Offensive contribution
                    if (oCount == 0) {
                        score += when(pCount) {
                            5 -> 1000000
                            4 -> 50000
                            3 -> 5000
                            2 -> 500
                            else -> 10
                        }
                    }
                    
                    // Defensive contribution (blocking opponent)
                    var opCountInWin = 0
                    var meCountInWin = 0
                    for (i in 0 until 5) {
                        val currR = startR + i * dr
                        val currC = startC + i * dc
                        val p = if (currR == r && currC == c) player else board[currR][currC]
                        if (p == opponent) opCountInWin++
                        else if (p == player) meCountInWin++
                    }
                    if (meCountInWin == 1) { // Only our simulated piece is there
                        score += when(opCountInWin) {
                            4 -> 300000 // Blocking a 4 is high priority
                            3 -> 10000  // Blocking a 3
                            2 -> 1000
                            else -> 0
                        }
                    }
                }
            }
        }
        // Center proximity bonus (moves closer to center are evaluated first)
        score += (14 - (abs(r - 7) + abs(c - 7)))
        return score
    }
}
