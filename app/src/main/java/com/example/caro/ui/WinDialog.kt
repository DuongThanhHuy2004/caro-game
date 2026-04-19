package com.example.caro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.caro.model.GameState
import com.example.caro.model.Player

@Composable
fun WinDialog(state: GameState, onRematch: () -> Unit, onMenu: () -> Unit) {
    val message = when {
        state.winner == Player.X -> "Người chơi X thắng!"
        state.winner == Player.O -> if (state.isVsAI) "AI thắng!" else "Người chơi O thắng!"
        state.isDraw -> "Hòa!"
        else -> return
    }
    Dialog(onDismissRequest = {}) {
        Card(shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(8.dp)) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (state.isDraw) "🤝" else if (state.winner == Player.X) "🎉" else "🤖",
                    fontSize = 48.sp
                )
                Text(message, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onMenu) { Text("Menu") }
                    Button(onClick = onRematch) { Text("Chơi lại") }
                }
            }
        }
    }
}