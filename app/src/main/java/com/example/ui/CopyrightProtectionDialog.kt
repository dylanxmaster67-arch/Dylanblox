package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun CopyrightProtectionDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1332),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFF1744), modifier = Modifier.size(26.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("COPYRIGHT ENFORCEMENT", color = Color(0xFFFF1744), fontWeight = FontWeight.Black)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Official Dylan avatar icon
                Image(
                    painter = painterResource(id = R.drawable.ic_dylanblox_icon),
                    contentDescription = "Dylan Icon",
                    modifier = Modifier.size(72.dp)
                )

                Text(
                    text = "nice try dude",
                    color = Color(0xFFFFD700),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2C1929),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Dylanblox is strictly protected by international copyright law.",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Created exclusively by @dylaniscool67 on YouTube. Unauthorized duplication, decompilation, or asset cloning is blocked by the anti-cheat core.",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text("Understood, Dude!", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    )
}
