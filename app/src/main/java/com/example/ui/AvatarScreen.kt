package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DylanbloxRepository
import com.example.model.AvatarConfig

@Composable
fun AvatarScreen(
    repository: DylanbloxRepository,
    onNavigateToShop: () -> Unit
) {
    val avatarConfig by repository.avatarConfig.collectAsState()
    val unlockedItems by repository.unlockedItems.collectAsState()
    val equippedItems by repository.equippedItems.collectAsState()
    val keys by repository.keys.collectAsState()

    var skinColor by remember { mutableStateOf(avatarConfig.skinColor) }
    var torsoColor by remember { mutableStateOf(avatarConfig.torsoColor) }
    var legsColor by remember { mutableStateOf(avatarConfig.legsColor) }
    var currentHat by remember { mutableStateOf(avatarConfig.hat) }
    var currentCape by remember { mutableStateOf(avatarConfig.cape ?: "Golden Wings") }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Avatar Studio",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Customize your blocky hero appearance",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onNavigateToShop,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Shop", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // 3D-Styled Avatar Preview Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1536)),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF7C4DFF))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("LIVE AVATAR PREVIEW", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF261D4C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f

                            // Cape
                            val capePath = Path().apply {
                                moveTo(centerX - 25f, centerY - 15f)
                                lineTo(centerX - 55f, centerY + 50f)
                                lineTo(centerX + 55f, centerY + 50f)
                                lineTo(centerX + 25f, centerY - 15f)
                                close()
                            }
                            drawPath(capePath, Color(0xFFFFD700))

                            // Legs
                            drawRect(parseHex(legsColor), Offset(centerX - 24f, centerY + 18f), Size(20f, 35f))
                            drawRect(parseHex(legsColor), Offset(centerX + 4f, centerY + 18f), Size(20f, 35f))

                            // Torso
                            drawRect(parseHex(torsoColor), Offset(centerX - 28f, centerY - 20f), Size(56f, 40f))

                            // Head
                            drawRect(parseHex(skinColor), Offset(centerX - 25f, centerY - 65f), Size(50f, 44f))

                            // Eyes & Smile
                            drawRect(Color(0xFF1E88E5), Offset(centerX - 16f, centerY - 52f), Size(8f, 10f))
                            drawRect(Color(0xFF1E88E5), Offset(centerX + 8f, centerY - 52f), Size(8f, 10f))
                            drawRect(Color(0xFF212121), Offset(centerX - 10f, centerY - 32f), Size(20f, 4f))

                            // Crown
                            val crownPath = Path().apply {
                                moveTo(centerX - 28f, centerY - 65f)
                                lineTo(centerX - 28f, centerY - 80f)
                                lineTo(centerX - 14f, centerY - 72f)
                                lineTo(centerX, centerY - 84f)
                                lineTo(centerX + 14f, centerY - 72f)
                                lineTo(centerX + 28f, centerY - 80f)
                                lineTo(centerX + 28f, centerY - 65f)
                                close()
                            }
                            drawPath(crownPath, Color(0xFFFFD700))
                            drawCircle(Color(0xFF00E5FF), radius = 3.5f, center = Offset(centerX, centerY - 78f))
                        }
                    }
                }
            }

            // Customization Options: Skin Tone
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF191331))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Skin Tone", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        val skinTones = listOf("#FFE0BD", "#F1C27D", "#E0AC69", "#C68642", "#8D5524", "#00E5FF")
                        for (tone in skinTones) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(parseHex(tone))
                                    .border(2.dp, if (skinColor == tone) Color.White else Color.Transparent, CircleShape)
                                    .clickable { skinColor = tone },
                                contentAlignment = Alignment.Center
                            ) {
                                if (skinColor == tone) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Customization Options: Torso Shirt Color
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF191331))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Torso Outfit Color", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        val shirtColors = listOf("#7C4DFF", "#2196F3", "#E91E63", "#00E676", "#FF9800", "#212121")
                        for (clr in shirtColors) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(parseHex(clr))
                                    .border(2.dp, if (torsoColor == clr) Color.White else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { torsoColor = clr },
                                contentAlignment = Alignment.Center
                            ) {
                                if (torsoColor == clr) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Customization Options: Legs Pants Color
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF191331))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Legs / Pants Color", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        val pantsColors = listOf("#212121", "#3F51B5", "#4CAF50", "#795548", "#607D8B", "#FFEB3B")
                        for (clr in pantsColors) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(parseHex(clr))
                                    .border(2.dp, if (legsColor == clr) Color.White else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { legsColor = clr },
                                contentAlignment = Alignment.Center
                            ) {
                                if (legsColor == clr) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseHex(hex: String): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = clean.toLong(16)
        if (clean.length == 6) Color(0xFF000000 or colorInt) else Color(colorInt)
    } catch (_: Exception) {
        Color(0xFFFFD700)
    }
}
