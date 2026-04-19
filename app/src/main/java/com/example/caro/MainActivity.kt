package com.example.caro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caro.ui.GameScreen
import com.example.caro.viewmodel.GameViewModel
import com.example.caro.ui.OnlineScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val vm: GameViewModel = viewModel()
    var screen by remember { mutableStateOf("menu") } // menu, game, online

    when (screen) {
        "game" -> GameScreen(vm = vm, onBack = { screen = "menu" })
        "online" -> OnlineScreen(onBack = { screen = "menu" })
        else -> MenuScreen(
            onPlayAI = { vm.setVsAI(true); screen = "game" },
            onPlayPvP = { vm.setVsAI(false); screen = "game" },
            onPlayOnline = { screen = "online" }
        )
    }
}

@Composable
fun MenuScreen(onPlayAI: () -> Unit, onPlayPvP: () -> Unit, onPlayOnline: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("♟", fontSize = 72.sp)
            Spacer(Modifier.height(8.dp))
            Text("Cờ Caro", fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Text("Gomoku 15×15", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(48.dp))
            Button(
                onClick = onPlayAI,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Chơi vs AI", fontSize = 18.sp)
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onPlayPvP,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("2 Người chơi", fontSize = 18.sp)
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onPlayOnline,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("🌐 Chơi Online", fontSize = 18.sp)
            }
        }
    }
}