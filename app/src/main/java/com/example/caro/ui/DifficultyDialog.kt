package com.example.caro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.caro.model.Difficulty

@Composable
fun DifficultyDialog(
    onDifficultySelected: (Difficulty) -> Unit,
    onDismiss: () -> Unit
) {
    val black = Color(0xFF0D0D0D)
    val bgColor = Color(0xFFF5F5F3)
    val gray = Color(0xFFE8E8E6)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "SELECT DIFFICULTY",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    color = black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                DifficultyButton("EASY", Difficulty.EASY, onDifficultySelected)
                DifficultyButton("NORMAL", Difficulty.NORMAL, onDifficultySelected)
                DifficultyButton("HARD", Difficulty.HARD, onDifficultySelected)
            }
        }
    }
}

@Composable
private fun DifficultyButton(
    text: String,
    difficulty: Difficulty,
    onSelected: (Difficulty) -> Unit
) {
    val black = Color(0xFF0D0D0D)
    val gray = Color(0xFFE8E8E6)

    Button(
        onClick = { onSelected(difficulty) },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (difficulty == Difficulty.NORMAL) gray else gray,
            contentColor = if (difficulty == Difficulty.NORMAL) black else black
        )
    ) {
        Text(text, fontSize = 11.sp, letterSpacing = 2.sp)
    }
}