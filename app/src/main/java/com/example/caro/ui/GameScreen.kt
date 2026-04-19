package com.example.caro.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caro.model.Player
import com.example.caro.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(vm: GameViewModel = viewModel(), onBack: () -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cờ Caro", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { vm.resetGame() }) {
                        Icon(Icons.Default.Refresh, "Reset")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Score board
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScoreCard(
                    label = "X (Bạn)",
                    score = state.scoreX,
                    isActive = state.currentPlayer == Player.X && state.winner == null,
                    color = MaterialTheme.colorScheme.errorContainer
                )
                ScoreCard(
                    label = if (state.isVsAI) "O (AI)" else "O",
                    score = state.scoreO,
                    isActive = state.currentPlayer == Player.O && state.winner == null,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }

            // Turn indicator
            if (state.winner == null && !state.isDraw) {
                val turnText = when {
                    state.isVsAI && state.currentPlayer == Player.O -> "AI đang suy nghĩ..."
                    state.currentPlayer == Player.X -> "Lượt của X"
                    else -> "Lượt của O"
                }
                Text(turnText, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Board
            BoardView(
                state = state,
                boardSize = vm.boardSize,
                onCellClick = vm::onCellClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Win dialog
        if (state.winner != null || state.isDraw) {
            WinDialog(
                state = state,
                onRematch = { vm.resetGame() },
                onMenu = { onBack() }
            )
        }
    }
}

@Composable
fun ScoreCard(label: String, score: Int, isActive: Boolean, color: androidx.compose.ui.graphics.Color) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) color else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(if (isActive) 6.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 13.sp)
            Text("$score", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}