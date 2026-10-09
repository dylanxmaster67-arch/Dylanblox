package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DylanbloxRepository
import com.example.model.PlayerRank

@Composable
fun AdminConsoleScreen(
    repository: DylanbloxRepository
) {
    val rank by repository.rank.collectAsState()
    val isFly by repository.isFlyEnabled.collectAsState()
    val isNoclip by repository.isNoclipEnabled.collectAsState()
    val bannedUsers by repository.bannedUsers.collectAsState()
    val reports by repository.reports.collectAsState()
    val ownerSaleDiscount by repository.ownerSaleDiscountPercent.collectAsState()

    var commandInput by remember { mutableStateOf("") }
    val consoleLog = remember {
        mutableStateListOf(
            "Dylanblox Admin OS v4.2 [Owner Terminal]",
            "Type /help for all admin and owner commands.",
            "Security Integrity: Active. Anti-Cheat: Nominal."
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0A17))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Admin & Owner Console",
                            color = Color(0xFFFF1744),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = Color(0xFFFF1744), modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = "Real-time command execution & server moderation",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(rank.badgeColorHex).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(rank.badgeColorHex))
                ) {
                    Text(
                        text = rank.displayName,
                        color = Color(rank.badgeColorHex),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Quick Owner & Admin Macro Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val res = repository.executeAdminCommand("/fly")
                        consoleLog.add("> /fly\n$res")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFly) Color(0xFF00E676) else Color(0xFF241C44)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Flight, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isFly) "/fly (ON)" else "/fly (OFF)", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        val res = repository.executeAdminCommand("/noclip")
                        consoleLog.add("> /noclip\n$res")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isNoclip) Color(0xFFFF9100) else Color(0xFF241C44)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isNoclip) "/noclip (ON)" else "/noclip (OFF)", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        val res = repository.executeAdminCommand("/givekeys 100000000000")
                        consoleLog.add("> /givekeys 100B\n$res")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+100B Keys", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        val newSale = if (ownerSaleDiscount == 100) 0 else 100
                        val res = repository.executeAdminCommand("/sale $newSale")
                        consoleLog.add("> /sale $newSale\n$res")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (ownerSaleDiscount == 100) "End Sale" else "100% Free Sale!", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val res = repository.executeAdminCommand("/setrank owner")
                        consoleLog.add("> /setrank owner\n$res")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rank: Owner", fontSize = 11.sp)
                }
            }

            // Terminal Log View
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF05040A)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B2050))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(consoleLog) { line ->
                        Text(
                            text = line,
                            color = if (line.startsWith(">")) Color(0xFF00E5FF) else if (line.contains("banned", true)) Color(0xFFFF5252) else Color(0xFF00E676),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Command Prompt Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commandInput,
                    onValueChange = { commandInput = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("admin_command_input"),
                    placeholder = { Text("Enter admin cmd: /ban, /fly, /noclip, /givekeys...", color = Color.Gray, fontSize = 12.sp) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF140F26),
                        unfocusedContainerColor = Color(0xFF0F0B1E),
                        focusedBorderColor = Color(0xFFFF1744),
                        unfocusedBorderColor = Color(0xFF32235A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (commandInput.isNotBlank()) {
                            val res = repository.executeAdminCommand(commandInput)
                            consoleLog.add("> $commandInput")
                            consoleLog.add(res)
                            commandInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(0xFFFF1744), RoundedCornerShape(12.dp))
                        .testTag("admin_command_submit")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Run", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            // Moderation Reports Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Player Reports & Moderation Queue (${reports.size})",
                    color = Color(0xFFFF8A80),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                for (rep in reports.take(2)) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C132B))
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${rep.targetType}: ${rep.targetName}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("${rep.category} • Reason: ${rep.reason}", color = Color.LightGray, fontSize = 10.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = { repository.resolveReport(rep.id, actionBan = true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Ban User", fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { repository.resolveReport(rep.id, actionBan = false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF392C62)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Dismiss", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
