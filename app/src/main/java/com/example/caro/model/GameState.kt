package com.example.caro.model

data class GameState(
    val board: Array<Array<Player?>> = Array(15) { arrayOfNulls(15) },
    val currentPlayer: Player = Player.X,
    val winner: Player? = null,
    val winningCells: List<Pair<Int, Int>> = emptyList(),
    val isDraw: Boolean = false,
    val isVsAI: Boolean = true,
    val difficulty: Difficulty = Difficulty.NORMAL,
    val scoreX: Int = 0,
    val scoreO: Int = 0,
    val lastMove: Pair<Int, Int>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameState) return false

        if (!board.contentDeepEquals(other.board)) return false
        if (currentPlayer != other.currentPlayer) return false
        if (winner != other.winner) return false
        if (winningCells != other.winningCells) return false
        if (isDraw != other.isDraw) return false
        if (isVsAI != other.isVsAI) return false
        if (difficulty != other.difficulty) return false
        if (scoreX != other.scoreX) return false
        if (scoreO != other.scoreO) return false
        if (lastMove != other.lastMove) return false

        return true
    }

    override fun hashCode(): Int {
        var result = board.contentDeepHashCode()
        result = 31 * result + currentPlayer.hashCode()
        result = 31 * result + (winner?.hashCode() ?: 0)
        result = 31 * result + winningCells.hashCode()
        result = 31 * result + isDraw.hashCode()
        result = 31 * result + isVsAI.hashCode()
        result = 31 * result + difficulty.hashCode()
        result = 31 * result + scoreX
        result = 31 * result + scoreO
        result = 31 * result + (lastMove?.hashCode() ?: 0)
        return result
    }
}