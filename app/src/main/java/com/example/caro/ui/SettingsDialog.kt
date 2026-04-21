package com.example.caro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.window.Dialog
import com.example.caro.viewmodel.SettingsViewModel
import com.example.caro.viewmodel.SettingsViewModelFactory

@Composable
fun SettingsDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val vm: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
    val bgmVolume by vm.bgmVolume.collectAsStateWithLifecycle()
    val sfxVolume by vm.sfxVolume.collectAsStateWithLifecycle()
    val black = Color(0xFF0D0D0D)
    val mutedColor = Color(0xFF999999)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F3)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Title
                Text(
                    "SETTINGS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = black
                )

                // BGM Volume
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "MUSIC",
                            fontSize = 10.sp,
                            letterSpacing = 2.sp,
                            color = mutedColor
                        )
                        Text(
                            "${(bgmVolume * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = black
                        )
                    }
                    Slider(
                        value = bgmVolume,
                        onValueChange = { vm.setBgmVolume(it) },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = black,
                            activeTrackColor = black,
                            inactiveTrackColor = Color(0xFFDDDDDD)
                        )
                    )
                }

                // SFX Volume
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "SOUND EFFECTS",
                            fontSize = 10.sp,
                            letterSpacing = 2.sp,
                            color = mutedColor
                        )
                        Text(
                            "${(sfxVolume * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = black
                        )
                    }
                    Slider(
                        value = sfxVolume,
                        onValueChange = { vm.setSfxVolume(it) },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = black,
                            activeTrackColor = black,
                            inactiveTrackColor = Color(0xFFDDDDDD)
                        )
                    )
                }

                // Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = black,
                        contentColor = Color.White
                    )
                ) {
                    Text("DONE", fontSize = 11.sp, letterSpacing = 3.sp)
                }
            }
        }
    }
}