package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DylanbloxRepository
import com.example.model.PlayerRank
import com.example.model.ShopCategory
import com.example.model.ShopItem

@Composable
fun ShopScreen(
    repository: DylanbloxRepository
) {
    val shopItems by repository.shopItems.collectAsState()
    val keys by repository.keys.collectAsState()
    val rank by repository.rank.collectAsState()
    val unlockedItems by repository.unlockedItems.collectAsState()
    val equippedItems by repository.equippedItems.collectAsState()
    val ownerSaleDiscount by repository.ownerSaleDiscountPercent.collectAsState()

    var selectedCategory by remember { mutableStateOf<ShopCategory?>(null) }
    var showCreateUgcDialog by remember { mutableStateOf(false) }

    val filteredItems = if (selectedCategory == null) {
        shopItems
    } else {
        shopItems.filter { it.category == selectedCategory }
    }

    val isPremium = rank == PlayerRank.PREMIUM || rank == PlayerRank.GAME_OWNER || rank == PlayerRank.ADMIN

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C1E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
        ) {
            // Header: Title & Keys Balance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dylanblox Marketplace",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Customize your avatar with hats, capes & trails",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF2C2204),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = "Keys", tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$keys",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Sales Alert Banner
            if (ownerSaleDiscount > 0) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFF1744).copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalOffer, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🔥 OWNER MEGA SALE: ALL ITEMS ARE $ownerSaleDiscount% OFF!",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            } else if (isPremium) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E3A28),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "👑 PREMIUM PERK: 80% OFF ALL ITEMS ACTIVE!",
                            color = Color(0xFF00E676),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Category Bar & UGC Item Creator Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedCategory == null) Color(0xFF7C4DFF) else Color(0xFF1C1635),
                        modifier = Modifier.clickable { selectedCategory = null }
                    ) {
                        Text(
                            text = "All",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    for (cat in ShopCategory.values()) {
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF1C1635),
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat.title,
                                color = if (isSelected) Color.White else Color.LightGray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { showCreateUgcDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp).testTag("btn_create_ugc_item")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Design UGC", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Shop Items Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredItems) { item ->
                    val isOwned = unlockedItems.contains(item.id)
                    val isEquipped = equippedItems.contains(item.id)
                    val price = repository.getItemPrice(item)

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1D1739)),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isEquipped) 2.dp else 1.dp,
                            color = if (isEquipped) Color(0xFF00E676) else Color(0xFF312658)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Item Icon Box
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF281F4B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(parseHex(item.colorHex))
                                )
                                if (isEquipped) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF00E676),
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                    ) {
                                        Text("ON", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(2.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = item.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1
                            )

                            Text(
                                text = "By ${item.creator}",
                                color = Color(0xFF00E5FF),
                                fontSize = 10.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.description,
                                color = Color.LightGray,
                                fontSize = 10.sp,
                                maxLines = 2,
                                minLines = 2
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Price and Action Button
                            if (isOwned) {
                                Button(
                                    onClick = {
                                        if (isEquipped) repository.unequipItem(item.id) else repository.equipItem(item.id)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isEquipped) Color(0xFF2E7D32) else Color(0xFF4527A0)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(32.dp)
                                ) {
                                    Text(if (isEquipped) "Equipped ✓" else "Equip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { repository.buyItem(item) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(32.dp)
                                ) {
                                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    if (price < item.basePrice) {
                                        Text("${item.basePrice}", color = Color.Gray, fontSize = 10.sp, textDecoration = TextDecoration.LineThrough)
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text("$price 🔑", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Custom UGC Item Creator Dialog
        if (showCreateUgcDialog) {
            var itemName by remember { mutableStateOf("") }
            var itemDesc by remember { mutableStateOf("") }
            var selectedColor by remember { mutableStateOf("#FFD700") }
            var itemCat by remember { mutableStateOf(ShopCategory.CUSTOM_UGC) }

            AlertDialog(
                onDismissRequest = { showCreateUgcDialog = false },
                containerColor = Color(0xFF1E173E),
                title = {
                    Text("Design Custom UGC Item", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Create your custom wearable item in the Dylanblox catalog for everyone to see!",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )

                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = { Text("Item Name") },
                            placeholder = { Text("e.g. Dylan's Gamer Headset") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = itemDesc,
                            onValueChange = { itemDesc = it },
                            label = { Text("Description") },
                            placeholder = { Text("Exclusive blocky style") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Choose Color:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val colors = listOf("#FFD700", "#00E5FF", "#FF1744", "#00E676", "#E040FB", "#FF9100", "#FFFFFF")
                            for (c in colors) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(parseHex(c))
                                        .border(
                                            width = if (selectedColor == c) 2.dp else 0.dp,
                                            color = Color.White,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColor = c }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (itemName.isNotBlank()) {
                                repository.createCustomUgcItem(itemName, itemCat, selectedColor, itemDesc)
                                showCreateUgcDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                    ) {
                        Text("Create & Wear", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateUgcDialog = false }) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
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
