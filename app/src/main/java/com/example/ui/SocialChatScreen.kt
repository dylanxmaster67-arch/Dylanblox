package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DylanbloxRepository
import com.example.model.ChatMessage
import com.example.model.PlayerRank

@Composable
fun SocialChatScreen(
    repository: DylanbloxRepository
) {
    val messages by repository.chatMessages.collectAsState()
    val isMicMuted by repository.isVoiceMicMuted.collectAsState()
    val voiceParticipants by repository.voiceParticipants.collectAsState()
    val rank by repository.rank.collectAsState()
    val settings by repository.serverSettings.collectAsState()

    var activeTab by remember { mutableStateOf("GLOBAL") } // GLOBAL, COOP, ADMIN, DM, VOICE
    var messageText by remember { mutableStateOf("") }
    var dmRecipient by remember { mutableStateOf("SkyCrafter_99") }
    var noiseSuppression by remember { mutableStateOf(settings.noiseSuppressionEnabled) }

    val listState = rememberLazyListState()

    val filteredMessages = messages.filter { msg ->
        when (activeTab) {
            "GLOBAL" -> msg.channel == "GLOBAL"
            "COOP" -> msg.channel == "COOP"
            "ADMIN" -> msg.channel == "ADMIN"
            "DM" -> msg.channel == "DM"
            else -> true
        }
    }

    LaunchedEffect(filteredMessages.size) {
        if (filteredMessages.isNotEmpty()) {
            listState.animateScrollToItem(filteredMessages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C1E))
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
                    Text(
                        text = "Social Hub & Voice Chat",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Real-time communication & co-op discussions",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }

                // Voice Chat Quick Mic Toggle Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isMicMuted) Color(0xFF381926) else Color(0xFF0D3E24),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isMicMuted) Color(0xFFFF5252) else Color(0xFF00E676)),
                    modifier = Modifier.clickable { repository.toggleVoiceMic() }.testTag("quick_mic_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Voice Mic",
                            tint = if (isMicMuted) Color(0xFFFF5252) else Color(0xFF00E676),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isMicMuted) "Mic Muted" else "Voice Live",
                            color = if (isMicMuted) Color(0xFFFF5252) else Color(0xFF00E676),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Tabs Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf(
                    Pair("GLOBAL", "Global Chat"),
                    Pair("COOP", "Co-Op Build"),
                    Pair("DM", "Direct Messages"),
                    Pair("VOICE", "Voice Lounge 🎙️"),
                    Pair("ADMIN", "Admin Channel")
                )

                for ((key, title) in tabs) {
                    val isSelected = activeTab == key
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF1E173A),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF322758)
                        ),
                        modifier = Modifier.clickable { activeTab = key }
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else Color.LightGray,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Main Content Area
            if (activeTab == "VOICE") {
                // Voice Chat Screen with Noise Suppression
                VoiceLoungePanel(
                    isMicMuted = isMicMuted,
                    noiseSuppression = noiseSuppression,
                    onToggleMic = { repository.toggleVoiceMic() },
                    onToggleNoiseSuppression = {
                        noiseSuppression = it
                        repository.updateServerSettings(settings.copy(noiseSuppressionEnabled = it))
                    },
                    participants = voiceParticipants
                )
            } else {
                // Chat Message List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredMessages) { msg ->
                        ChatMessageItem(msg)
                    }
                }

                // Chat Input Field
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        placeholder = {
                            Text(
                                if (activeTab == "DM") "Message @$dmRecipient..." else "Type message in #$activeTab...",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1E173A),
                            unfocusedContainerColor = Color(0xFF18122E),
                            focusedBorderColor = Color(0xFF7C4DFF),
                            unfocusedBorderColor = Color(0xFF2E2652),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                repository.addChatMessage(
                                    content = messageText,
                                    channel = activeTab,
                                    recipient = if (activeTab == "DM") dmRecipient else null
                                )
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFF7C4DFF), CircleShape)
                            .testTag("chat_send_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceLoungePanel(
    isMicMuted: Boolean,
    noiseSuppression: Boolean,
    onToggleMic: () -> Unit,
    onToggleNoiseSuppression: (Boolean) -> Unit,
    participants: List<com.example.model.VoiceParticipant>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Voice Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1637)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00E5FF))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Dylanblox Spatial Voice Chat",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Animated Waveform simulation
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val heights = if (!isMicMuted) listOf(14.dp, 28.dp, 36.dp, 22.dp, 30.dp, 16.dp) else listOf(8.dp, 8.dp, 8.dp, 8.dp, 8.dp, 8.dp)
                    for (h in heights) {
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(h)
                                .background(if (!isMicMuted) Color(0xFF00E5FF) else Color.Gray, RoundedCornerShape(3.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onToggleMic,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMicMuted) Color(0xFFFF5252) else Color(0xFF00E676)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isMicMuted) "Unmute Mic" else "Mute Mic", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    // Noise suppression feature switch
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Noise Suppression", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(if (noiseSuppression) "AI Filter ON" else "Raw Mic", color = Color(0xFF00E5FF), fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = noiseSuppression,
                            onCheckedChange = onToggleNoiseSuppression,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00E5FF),
                                checkedTrackColor = Color(0xFF004D40)
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Connected Voice Participants (${participants.size})",
            color = Color(0xFFFFD700),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Participants List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(participants) { p ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1838))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (p.isSpeaking) Color(0xFF00E676) else Color(0xFF3E3166)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (p.isMuted) Icons.Default.MicOff else Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(p.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "[${p.rank.displayName}]",
                                    color = Color(p.rank.badgeColorHex),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                if (p.isSpeaking) "Speaking..." else if (p.isMuted) "Muted" else "Listening",
                                color = if (p.isSpeaking) Color(0xFF00E676) else Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(msg: ChatMessage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (msg.isSystem) Color(0xFF381423) else Color(0xFF1C1635)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (msg.isSystem) Color(0xFFFF5252) else Color(0xFF2C2250)
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = msg.sender,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(msg.senderRank.badgeColorHex).copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(msg.senderRank.badgeColorHex))
                    ) {
                        Text(
                            text = msg.senderRank.displayName,
                            color = Color(msg.senderRank.badgeColorHex),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = "#${msg.channel}",
                    color = Color.LightGray,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = msg.content,
                color = if (msg.isSystem) Color(0xFFFF8A80) else Color(0xFFE0E0E0),
                fontSize = 13.sp
            )
        }
    }
}
