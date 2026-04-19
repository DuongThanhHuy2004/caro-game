package com.example.caro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caro.ai.GomokuAI
import com.example.caro.model.GameState
import com.example.caro.model.Player
import com.example.caro.model.opponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state

    private val ai = GomokuAI()
    val boardSize = 15
    val winLen = 5

    fun onCellClick(row: Int, col: Int) {
        val s = _state.value
        if (s.board[row][col] != null || s.winner != null || s.isDraw) return
        if (s.isVsAI && s.currentPlayer == Player.O) return

        val newBoard = s.board.map { it.copyOf() }.toTypedArray()
        newBoard[row][col] = s.currentPlayer
        val (winner, winCells) = checkWin(newBoard, row, col, s.currentPlayer)
        val isDraw = winner == null && newBoard.all { r -> r.all { it != null } }

        _state.value = s.copy(
            board = newBoard,
            currentPlayer = if (winner != null || isDraw) s.currentPlayer else s.currentPlayer.opponent(),
            winner = winner,
            winningCells = winCells,
            isDraw = isDraw,
            lastMove = Pair(row, col),
            scoreX = if (winner == Player.X) s.scoreX + 1 else s.scoreX,
            scoreO = if (winner == Player.O) s.scoreO + 1 else s.scoreO
        )

        if (winner == null && !isDraw && s.isVsAI) aiMove()
    }

    private fun aiMove() {
        viewModelScope.launch {
            delay(300)
            val s = _state.value
            if (s.winner != null || s.isDraw) return@launch
            val (r, c) = withContext(Dispatchers.Default) {
                ai.getBestMove(s.board.map { it.copyOf() }.toTypedArray(), Player.O)
            }
            val newBoard = s.board.map { it.copyOf() }.toTypedArray()
            newBoard[r][c] = Player.O
            val (winner, winCells) = checkWin(newBoard, r, c, Player.O)
            val isDraw = winner == null && newBoard.all { row -> row.all { it != null } }
            _state.value = s.copy(
                board = newBoard,
                currentPlayer = if (winner != null || isDraw) Player.O else Player.X,
                winner = winner,
                winningCells = winCells,
                isDraw = isDraw,
                lastMove = Pair(r, c),
                scoreO = if (winner == Player.O) s.scoreO + 1 else s.scoreO
            )
        }
    }

    fun checkWin(board: Array<Array<Player?>>, row: Int, col: Int, player: Player): Pair<Player?, List<Pair<Int, Int>>> {
        val dirs = listOf(0 to 1, 1 to 0, 1 to 1, 1 to -1)
        for ((dr, dc) in dirs) {
            val cells = mutableListOf(Pair(row, col))
            var r = row + dr; var c = col + dc
            while (r in 0 until boardSize && c in 0 until boardSize && board[r][c] == player) {
                cells.add(Pair(r, c)); r += dr; c += dc
            }
            r = row - dr; c = col - dc
            while (r in 0 until boardSize && c in 0 until boardSize && board[r][c] == player) {
                cells.add(Pair(r, c)); r -= dr; c -= dc
            }
            if (cells.size >= winLen) return Pair(player, cells)
        }
        return Pair(null, emptyList())
    }

    fun resetGame() {
        val s = _state.value
        _state.value = GameState(isVsAI = s.isVsAI, scoreX = s.scoreX, scoreO = s.scoreO)
    }

    fun setVsAI(vsAI: Boolean) {
        _state.value = GameState(isVsAI = vsAI)
    }
}