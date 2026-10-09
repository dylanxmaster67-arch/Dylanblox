package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DylanbloxRepository

@Composable
fun SettingsScreen(
    repository: DylanbloxRepository,
    onTriggerCopyrightAttempt: () -> Unit
) {
    val settings by repository.serverSettings.collectAsState()

    var targetFps by remember { mutableFloatStateOf(settings.targetFps.toFloat()) }
    var selectedRegion by remember { mutableStateOf(settings.region) }
    var selectedInputMode by remember { mutableStateOf(settings.inputMode) }
    var selectedTheme by remember { mutableStateOf(settings.themeName) }
    var hapticsEnabled by remember { mutableStateOf(settings.hapticFeedback) }
    var lowLatency by remember { mutableStateOf(settings.lowLatencyNetcode) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C1E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "Settings & Server Preferences",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
                Text(
                    text = "Cross-platform controls, FPS limiter & anti-cheat system",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }

            // Cross-Platform Controls
            SettingsSectionCard(title = "Cross-Platform Controls GUI", icon = Icons.Default.Gamepad) {
                Text("Select active control profile for Mobile, Console or PC:", color = Color.LightGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                val inputModes = listOf("Touch / Mobile", "Console Gamepad", "PC Keyboard (WASD)")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (mode in inputModes) {
                        val isSelected = selectedInputMode == mode
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF221A44),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.Transparent),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedInputMode = mode
                                    repository.updateServerSettings(settings.copy(inputMode = mode))
                                }
                        ) {
                            Text(
                                text = mode,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // FPS Limiter & Performance (1 to 60 FPS range)
            SettingsSectionCard(title = "Performance & FPS Limiter", icon = Icons.Default.Speed) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Frame Rate Target", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Prevents thermal throttling & eliminates lagging", color = Color.LightGray, fontSize = 11.sp)
                    }
                    Text(
                        text = "${targetFps.toInt()} FPS",
                        color = Color(0xFF00E5FF),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }

                Slider(
                    value = targetFps,
                    onValueChange = {
                        targetFps = it
                        repository.updateServerSettings(settings.copy(targetFps = it.toInt()))
                    },
                    valueRange = 1f..60f,
                    steps = 59,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF00E5FF),
                        activeTrackColor = Color(0xFF7C4DFF)
                    ),
                    modifier = Modifier.testTag("fps_slider")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Server & Netcode Preferences
            SettingsSectionCard(title = "Server Preferences", icon = Icons.Default.Lan) {
                Text("Select Region Server:", color = Color.LightGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                val regions = listOf("North America (US-East)", "Europe (Frankfurt)", "Asia (Tokyo)", "South America")
                for (reg in regions) {
                    val isSelected = selectedRegion == reg
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFF281E4E) else Color(0xFF1E173A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                selectedRegion = reg
                                repository.updateServerSettings(settings.copy(region = reg))
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = reg,
                                color = if (isSelected) Color.White else Color.LightGray,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Low-Latency Co-Op Netcode", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Sub-15ms packet sync for co-building", color = Color.LightGray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = lowLatency,
                        onCheckedChange = {
                            lowLatency = it
                            repository.updateServerSettings(settings.copy(lowLatencyNetcode = it))
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mod Accessibility & Custom Themes
            SettingsSectionCard(title = "Mod Accessibility & Custom Themes", icon = Icons.Default.Palette) {
                Text("Select Custom Theme:", color = Color.LightGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                val themes = listOf("Neon Cyber", "Classic Brick", "Golden Luxury", "Deep Void")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (thm in themes) {
                        val isSelected = selectedTheme == thm
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF1E173A),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedTheme = thm
                                    repository.updateServerSettings(settings.copy(themeName = thm))
                                }
                        ) {
                            Text(
                                text = thm,
                                color = Color.White,
                                fontSize = 10.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Haptic Feedback & Mod Accessibility", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Tactile feedback on jumps and bounces", color = Color.LightGray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = {
                            hapticsEnabled = it
                            repository.updateServerSettings(settings.copy(hapticFeedback = it))
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Anti-Cheat & Copyright Protection
            SettingsSectionCard(title = "Integrated Anti-Cheat & Security", icon = Icons.Default.Security) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Anti-Cheat Engine 2026", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Active memory validation, speedhack blocker & fair play", color = Color.LightGray, fontSize = 11.sp)
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF0A3319)) {
                        Text("SECURE", color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Copyright Protection Trigger Button
                Button(
                    onClick = onTriggerCopyrightAttempt,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C1E4A)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252)),
                    modifier = Modifier.fillMaxWidth().testTag("copy_game_btn")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Attempt Copy / Steal Dylanblox Code", color = Color(0xFFFF5252), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF191331)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2452))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
