package com.example.caro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caro.ui.GameScreen
import com.example.caro.ui.OnlineScreen
import com.example.caro.viewmodel.GameViewModel
import com.example.caro.ui.MenuScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF0D0D0D),
                    onPrimary = Color.White,
                    background = Color(0xFFF5F5F3),
                    surface = Color(0xFFF5F5F3),
                    onBackground = Color(0xFF0D0D0D),
                    onSurface = Color(0xFF0D0D0D),
                    outline = Color(0xFFD0D0D0),
                )
            ) {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val vm: GameViewModel = viewModel()
    var screen by remember { mutableStateOf("menu") }

    when (screen) {
        "game" -> {
            BackHandler { screen = "menu" }
            GameScreen(vm = vm, onBack = { screen = "menu" })
        }

        "online" -> {
            BackHandler { screen = "menu" }
            OnlineScreen(onBack = { screen = "menu" })
        }

        else -> MenuScreen(
            onPlayAI = { vm.resetGame(); vm.setVsAI(true); screen = "game" },
            onPlayPvP = { vm.resetGame(); vm.setVsAI(false); screen = "game" },
            onPlayOnline = { screen = "online" }
        )
    }
}