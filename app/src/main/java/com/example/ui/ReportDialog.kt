package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DylanbloxRepository

@Composable
fun ReportDialog(
    targetType: String,
    targetName: String,
    repository: DylanbloxRepository,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Inappropriate Content") }
    var reasonText by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val categories = listOf(
        "Inappropriate Content",
        "Harassment / Bullying",
        "Exploiting / Cheating",
        "Scam / Phishing",
        "Spam"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1433),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Report $targetType", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            if (submitted) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Thank you for reporting. Our moderation system and admin team will review this incident promptly.",
                        color = Color(0xFF00E676),
                        fontSize = 13.sp
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Target: $targetName", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Select Violation Category:", color = Color.LightGray, fontSize = 12.sp)

                    for (cat in categories) {
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF4A1A32) else Color(0xFF16102A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFFFF5252) else Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color(0xFFFF8A80) else Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = reasonText,
                        onValueChange = { reasonText = it },
                        label = { Text("Additional Details") },
                        placeholder = { Text("Describe what happened...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            if (submitted) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    Text("Done")
                }
            } else {
                Button(
                    onClick = {
                        repository.reportTarget(targetType, targetName, selectedCategory, reasonText)
                        submitted = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744))
                ) {
                    Text("Submit Report", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!submitted) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color.White)
                }
            }
        }
    )
}
