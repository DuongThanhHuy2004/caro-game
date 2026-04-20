package com.example.caro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.caro.model.GameState
import com.example.caro.model.Player

@Composable
fun WinDialog(state: GameState, onRematch: () -> Unit, onMenu: () -> Unit) {
    val black = Color(0xFF0D0D0D)
    val title = when {
        state.isDraw -> "DRAW"
        state.winner == Player.X -> "X WIN"
        state.isVsAI -> "BOT WINS"
        else -> "O WINS"
    }
    val subtitle = when {
        state.isDraw -> "No winner this time"
        state.winner == Player.X -> "Well played!"
        state.isVsAI -> "The bot got you"
        else -> "Good job!"
    }
    if (state.winner == null && !state.isDraw) return

    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F3)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = Color(0xFF999999),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onMenu,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8E8E6),
                            contentColor = black
                        )
                    ) {
                        Text("MENU", fontSize = 11.sp, letterSpacing = 2.sp)
                    }
                    Button(
                        onClick = onRematch,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("REMATCH", fontSize = 11.sp, letterSpacing = 2.sp)
                    }
                }
            }
        }
    }
}