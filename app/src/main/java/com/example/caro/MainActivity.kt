package com.example.caro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caro.ui.GameScreen
import com.example.caro.ui.OnlineScreen
import com.example.caro.viewmodel.GameViewModel
import com.example.caro.ui.MenuScreen
import com.example.caro.audio.SoundManagerProvider
import androidx.compose.runtime.collectAsState
import com.example.caro.audio.SettingsRepository

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

    override fun onPause() {
        super.onPause()
        SoundManagerProvider.get(this).stopBgm() // dừng khi vào background
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundManagerProvider.release() // giải phóng khi thoát app
    }
}

@Composable
fun AppRoot() {
    val vm: GameViewModel = viewModel()
    val context = LocalContext.current
    val settingsRepo = remember { SettingsRepository(context) }
    var screen by remember { mutableStateOf("menu") }

    // Đọc volume đã lưu và apply khi khởi động
    val bgmVolume by settingsRepo.bgmVolume.collectAsState(initial = 0.4f)
    val sfxVolume by settingsRepo.sfxVolume.collectAsState(initial = 1.0f)

    LaunchedEffect(bgmVolume) {
        SoundManagerProvider.get(context).setBgmVolume(bgmVolume)
    }

    LaunchedEffect(sfxVolume) {
        SoundManagerProvider.get(context).setSfxVolume(sfxVolume)
    }

    LaunchedEffect(screen) {
        val sound = SoundManagerProvider.get(context)
        if (screen == "menu") sound.startBgm() else sound.stopBgm()
    }

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
            onPlayAI = { difficulty -> 
                vm.setDifficulty(difficulty)
                vm.resetGame()
                vm.setVsAI(true)
                screen = "game" 
            },
            onPlayPvP = { vm.resetGame(); vm.setVsAI(false); screen = "game" },
            onPlayOnline = { screen = "online" }
        )
    }
}