package com.example.caro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caro.model.Player
import com.example.caro.viewmodel.GameViewModel

@Composable
fun GameScreen(vm: GameViewModel = viewModel(), onBack: () -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    val black = Color(0xFF0D0D0D)
    val bgColor = Color(0xFFF5F5F3)
    val mutedColor = Color(0xFF999999)

    Surface(modifier = Modifier.fillMaxSize(), color = bgColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                    Text("←", fontSize = 22.sp, color = black)
                }
                Spacer(Modifier.weight(1f))
                Text(
                    if (state.isVsAI) "VS BOT" else "LOCAL 2P",
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    color = mutedColor
                )
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = { vm.resetGame() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = black)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "SESSION STATUS",
                    fontSize = 10.sp,
                    letterSpacing = 3.sp,
                    color = mutedColor
                )
                Spacer(Modifier.height(3.dp))
                val statusText = when {
                    state.winner == Player.X -> "X WIN"
                    state.winner == Player.O -> if (state.isVsAI) "BOT WINS" else "O WINS"
                    state.isDraw -> "DRAW"
                    state.isVsAI && state.currentPlayer == Player.O -> "THINKING"
                    state.currentPlayer == Player.X -> "X TURN"
                    else -> "O TURN"
                }
                Text(
                    statusText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = black
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                BoardView(
                    modifier = Modifier.fillMaxWidth(),
                    state = state,
                    boardSize = vm.boardSize,
                    onCellClick = { row, col -> vm.onCellClick(row, col) }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        "X (PLAYER)",
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = mutedColor
                    )
                    Text(
                        state.scoreX.toString().padStart(2, '0'),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = black,
                        lineHeight = 52.sp
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val moveCount = state.board.sumOf { row -> row.count { it != null } }
                    Text(
                        "MOVE ${moveCount.toString().padStart(2, '0')}",
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = mutedColor
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    if (state.isVsAI && state.currentPlayer == Player.O && state.winner == null)
                                        black else Color(0xFFCCCCCC),
                                    RoundedCornerShape(50)
                                )
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            if (state.isVsAI && state.currentPlayer == Player.O && state.winner == null)
                                "THINKING" else "PLAYING",
                            fontSize = 9.sp,
                            letterSpacing = 1.sp,
                            color = mutedColor
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        if (state.isVsAI) "O (BOT)" else "O (PLAYER)",
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = mutedColor
                    )
                    Text(
                        state.scoreO.toString().padStart(2, '0'),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = black,
                        lineHeight = 52.sp
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    WinDialog(
        state = state,
        onRematch = { vm.resetGame() },
        onMenu = onBack
    )
}