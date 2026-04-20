package com.example.caro.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.caro.model.GameState
import com.example.caro.model.Player
import androidx.compose.ui.unit.dp

@Composable
fun BoardView(
    state: GameState,
    boardSize: Int,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val black = Color(0xFF0D0D0D)
    val bgWhite = Color(0xFFF5F5F3)
    val gridColor = Color(0xFF777777)
    val lastMoveColor = Color(0xFFEEEEEC)
    val winColor = Color(0xFFE0E0DE)

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .background(Color.White, RoundedCornerShape(8.dp))
            .pointerInput(state) {
                detectTapGestures { offset ->
                    val cellSize = size.width.toFloat() / boardSize
                    val col = (offset.x / cellSize).toInt().coerceIn(0, boardSize - 1)
                    val row = (offset.y / cellSize).toInt().coerceIn(0, boardSize - 1)
                    onCellClick(row, col)
                }
            }
    ) {
        val cellSize = size.width / boardSize

        // Grid
        for (i in 0..boardSize) {
            val x = i * cellSize
            val y = i * cellSize
            drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), 0.5f)
            drawLine(gridColor, Offset(0f, y), Offset(size.width, y), 0.5f)
        }

        // Last move highlight
        state.lastMove?.let { (lr, lc) ->
            drawRect(
                lastMoveColor,
                topLeft = Offset(lc * cellSize, lr * cellSize),
                size = Size(cellSize, cellSize)
            )
        }

        // Win highlight
        for ((wr, wc) in state.winningCells) {
            drawRect(
                winColor,
                topLeft = Offset(wc * cellSize, wr * cellSize),
                size = Size(cellSize, cellSize)
            )
        }

        // Pieces
        val padding = cellSize * 0.15f
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                val piece = state.board[r][c] ?: continue
                val cx = c * cellSize + cellSize / 2
                val cy = r * cellSize + cellSize / 2
                val radius = cellSize / 2 - padding
                if (piece == Player.X) {
                    drawCircle(black, radius, Offset(cx, cy))
                } else {
                    drawCircle(bgWhite, radius, Offset(cx, cy))
                    drawCircle(Color(0xFFBBBBBB), radius, Offset(cx, cy), style = Stroke(1.5f))
                }
            }
        }
    }
}