package com.example.caro.ai

import com.example.caro.model.Player

class GomokuAI {
    private val boardSize = 15
    private val winLen = 5
    // Tăng depth lên 3. Nếu máy mạnh/chạy luồng phụ, bạn có thể tăng lên 4.
    private val searchDepth = 3

    fun getBestMove(board: Array<Array<Player?>>, aiPlayer: Player): Pair<Int, Int> {
        var bestScore = Int.MIN_VALUE
        var bestMove = Pair(7, 7)
        val candidates = getCandidates(board)

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

        // Thưởng/phạt điểm dựa trên depth để AI ưu tiên thắng nhanh, thua chậm
        if (winner == aiPlayer) return 10000000 + depth
        if (winner == human) return -10000000 - depth
        if (depth == 0 || isFull(board)) return evaluate(board, aiPlayer)

        var alphaVar = alpha
        var betaVar = beta
        val candidates = getCandidates(board)

        return if (isMaximizing) {
            var best = Int.MIN_VALUE
            for ((r, c) in candidates) {
                board[r][c] = aiPlayer
                best = maxOf(best, minimax(board, depth - 1, false, alphaVar, betaVar, aiPlayer))
                board[r][c] = null
                alphaVar = maxOf(alphaVar, best)
                if (betaVar <= alphaVar) break // Alpha-Beta Pruning
            }
            best
        } else {
            var best = Int.MAX_VALUE
            for ((r, c) in candidates) {
                board[r][c] = human
                best = minOf(best, minimax(board, depth - 1, true, alphaVar, betaVar, aiPlayer))
                board[r][c] = null
                betaVar = minOf(betaVar, best)
                if (betaVar <= alphaVar) break // Alpha-Beta Pruning
            }
            best
        }
    }

    private fun getCandidates(board: Array<Array<Player?>>): List<Pair<Int, Int>> {
        val candidates = mutableSetOf<Pair<Int, Int>>()
        var hasAny = false

        // Giới hạn vùng tìm kiếm: Chỉ lấy những ô trống cách các ô đã đánh tối đa 2 ô
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
        if (!hasAny) return listOf(Pair(7, 7)) // Đánh giữa bàn cờ ở nước đầu tiên

        // Mẹo tối ưu (Move Ordering): Đánh giá nhanh các ứng viên và xếp ưu tiên để thuật toán Alpha-Beta cắt tỉa nhanh hơn
        // Tuy nhiên để code đơn giản dễ hiểu, mình trả về List bình thường.
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
        // Chú ý: Cho AI ưu tiên việc phòng thủ (trọng số của đối thủ cao hơn một chút - 1.2f)
        val aiScore = scoreFor(board, aiPlayer)
        val humanScore = scoreFor(board, human)
        return aiScore - (humanScore * 1.2).toInt()
    }

    private fun scoreFor(board: Array<Array<Player?>>, player: Player): Int {
        val dirs = listOf(0 to 1, 1 to 0, 1 to 1, 1 to -1)
        var total = 0

        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                if (board[r][c] != player) continue

                for ((dr, dc) in dirs) {
                    // CỰC KỲ QUAN TRỌNG: Tránh đếm trùng lặp.
                    // Nếu ô ngay phía sau cùng hướng là quân của mình, bỏ qua (đã đếm rồi)
                    val pr = r - dr
                    val pc = c - dc
                    if (pr in 0 until boardSize && pc in 0 until boardSize && board[pr][pc] == player) {
                        continue
                    }

                    var count = 1
                    var blocks = 0 // Đếm số đầu bị chặn (0, 1, hoặc 2)
                    var nr = r + dr
                    var nc = c + dc

                    while (nr in 0 until boardSize && nc in 0 until boardSize && board[nr][nc] == player) {
                        count++
                        nr += dr
                        nc += dc
                    }

                    // Kiểm tra chặn đầu 1 (phía sau)
                    if (pr !in 0 until boardSize || pc !in 0 until boardSize || board[pr][pc] != null) blocks++
                    // Kiểm tra chặn đầu 2 (phía trước)
                    if (nr !in 0 until boardSize || nc !in 0 until boardSize || board[nr][nc] != null) blocks++

                    // Gán trọng số chuẩn hóa
                    total += when {
                        count >= 5 -> 10000000   // Thắng chắc
                        count == 4 && blocks == 0 -> 1000000 // 4 không bị chặn
                        count == 4 && blocks == 1 -> 10000   // 4 bị chặn 1 đầu
                        count == 3 && blocks == 0 -> 10000   // 3 không bị chặn
                        count == 3 && blocks == 1 -> 100     // 3 bị chặn 1 đầu
                        count == 2 && blocks == 0 -> 100     // 2 không bị chặn
                        count == 2 && blocks == 1 -> 10      // 2 bị chặn 1 đầu
                        else -> 0
                    }
                }
            }
        }
        return total
    }
}