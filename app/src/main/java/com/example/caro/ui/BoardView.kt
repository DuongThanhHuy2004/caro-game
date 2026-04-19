package com.example.caro.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.caro.model.GameState
import com.example.caro.model.Player

@Composable
fun BoardView(
    state: GameState,
    boardSize: Int,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val boardColor = Color(0xFFDEB887)
    val lineColor = Color(0xFF8B6914)
    val xColor = Color(0xFFD32F2F)
    val oColor = Color(0xFF1565C0)
    val winHighlight = Color(0xFFFFEB3B).copy(alpha = 0.5f)
    val lastMoveColor = Color(0xFF4CAF50).copy(alpha = 0.35f)

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .background(boardColor)
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

        // Grid lines
        for (i in 0..boardSize) {
            val x = i * cellSize
            val y = i * cellSize
            drawLine(lineColor, Offset(0f, y), Offset(size.width, y), 1f)
            drawLine(lineColor, Offset(x, 0f), Offset(x, size.height), 1f)
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
                winHighlight,
                topLeft = Offset(wc * cellSize, wr * cellSize),
                size = Size(cellSize, cellSize)
            )
        }

        // Pieces
        val padding = cellSize * 0.2f
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                val piece = state.board[r][c] ?: continue
                val cx = c * cellSize + cellSize / 2
                val cy = r * cellSize + cellSize / 2
                val radius = cellSize / 2 - padding

                if (piece == Player.X) {
                    val strokeW = cellSize * 0.12f
                    drawLine(xColor, Offset(cx - radius, cy - radius), Offset(cx + radius, cy + radius), strokeW, StrokeCap.Round)
                    drawLine(xColor, Offset(cx + radius, cy - radius), Offset(cx - radius, cy + radius), strokeW, StrokeCap.Round)
                } else {
                    drawCircle(Color.White, radius, Offset(cx, cy))
                    drawCircle(oColor, radius, Offset(cx, cy), style = Stroke(cellSize * 0.12f))
                }
            }
        }
    }
}