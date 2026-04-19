package com.example.caro.model

data class GameState(
    val board: Array<Array<Player?>> = Array(15) { arrayOfNulls(15) },
    val currentPlayer: Player = Player.X,
    val winner: Player? = null,
    val winningCells: List<Pair<Int, Int>> = emptyList(),
    val isDraw: Boolean = false,
    val isVsAI: Boolean = true,
    val scoreX: Int = 0,
    val scoreO: Int = 0,
    val lastMove: Pair<Int, Int>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameState) return false
        return board.contentDeepEquals(other.board) &&
                currentPlayer == other.currentPlayer &&
                winner == other.winner &&
                isDraw == other.isDraw
    }
    override fun hashCode(): Int = board.contentDeepHashCode()
}