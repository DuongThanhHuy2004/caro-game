package com.example.caro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.example.caro.R

@Composable
fun MenuScreen(
    onPlayAI: () -> Unit,
    onPlayPvP: () -> Unit,
    onPlayOnline: () -> Unit
) {
    var showSettings by remember { mutableStateOf(false) }
    val black = Color(0xFF0D0D0D)
    val bgColor = Color(0xFFF5F5F3)
    val gray = Color(0xFFE8E8E6)
    val mutedColor = Color(0xFF999999)

    Surface(modifier = Modifier.fillMaxSize(), color = bgColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 28.dp)
        ) {
            // Settings icon top right
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showSettings = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.setting_alt_line_light),
                        contentDescription = "Settings",
                        tint = mutedColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "GOMOKU",
                fontSize = 64.sp,
                fontWeight = FontWeight.Thin,
                letterSpacing = 6.sp,
                color = black,
                lineHeight = 64.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "THE SILENT STRATEGY",
                fontSize = 10.sp,
                letterSpacing = 4.sp,
                color = mutedColor
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onPlayAI,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = black,
                    contentColor = Color.White
                )
            ) {
                Text("VS BOT", fontSize = 12.sp, letterSpacing = 3.sp)
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onPlayPvP,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = gray,
                    contentColor = black
                )
            ) {
                Text("LOCAL 2P", fontSize = 12.sp, letterSpacing = 3.sp)
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onPlayOnline,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = black
                )
            ) {
                Text("ONLINE", fontSize = 12.sp, letterSpacing = 3.sp)
            }
            Spacer(Modifier.height(40.dp))
        }
    }

    if (showSettings) {
        SettingsDialog(onDismiss = { showSettings = false })
    }
}