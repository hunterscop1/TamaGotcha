package com.example.ui.store

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.local.PetStateEntity
import com.example.data.model.CosmeticItem
import com.example.data.model.CosmeticRegistry
import com.example.data.model.CosmeticType
import com.example.ui.pet.PetAnimationState
import com.example.ui.pet.PetSpriteCanvas

@Composable
fun StoreSheet(
    pet: PetStateEntity,
    onBuyOrEquip: (CosmeticItem) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf(CosmeticType.HAT) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("store_sheet"),
        color = Color(0xFFFDFCF4)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF556500).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🛍️", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Tama Boutique",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E3B10)
                        )
                        Text(
                            text = "Dress up & customize ${pet.name}!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5F6E4D)
                        )
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("close_store_btn")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF2E3B10))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Wallet & Avatar Preview Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        PetSpriteCanvas(
                            species = pet.species,
                            evolutionStage = pet.evolutionStage,
                            animationState = PetAnimationState.IDLE,
                            happiness = pet.happiness,
                            isSleeping = false,
                            mood = pet.getComputedMood(),
                            equippedHat = pet.equippedHat,
                            equippedAccessory = pet.equippedAccessory,
                            equippedPalette = pet.equippedPalette,
                            equippedBackground = pet.equippedBackground,
                            onPetClicked = {},
                            modifier = Modifier.size(76.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Available Balance", style = MaterialTheme.typography.labelSmall, color = Color(0xFF5F6E4D))
                        Text(
                            "🪙 ${pet.coins} Coins",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                        Text(
                            "Complete tasks to earn more!",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF854D0E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CosmeticType.values()) { type ->
                    val isSelected = selectedType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedType = type },
                        label = {
                            Text(
                                when (type) {
                                    CosmeticType.HAT -> "🎩 Hats"
                                    CosmeticType.ACCESSORY -> "👓 Accessories"
                                    CosmeticType.PALETTE -> "🎨 Palettes"
                                    CosmeticType.BACKGROUND -> "🏞️ Rooms"
                                }
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF556500),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Items List
            val itemsForCategory = remember(selectedType) {
                CosmeticRegistry.items.filter { it.type == selectedType }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(itemsForCategory, key = { it.id }) { item ->
                    val isUnlocked = pet.isItemUnlocked(item.id)
                    val isEquipped = when (item.type) {
                        CosmeticType.HAT -> pet.equippedHat == item.id
                        CosmeticType.ACCESSORY -> pet.equippedAccessory == item.id
                        CosmeticType.PALETTE -> pet.equippedPalette == item.id
                        CosmeticType.BACKGROUND -> pet.equippedBackground == item.id
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cosmetic_item_${item.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEquipped) Color(0xFFF1F5E0) else Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isEquipped) Color(0xFF556500) else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFDFCF4)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(item.emoji, fontSize = 22.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B1C17)
                                    )
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF5F6E4D)
                                    )
                                }
                            }

                            when {
                                isEquipped -> {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF556500))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("EQUIPPED", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                isUnlocked -> {
                                    OutlinedButton(
                                        onClick = { onBuyOrEquip(item) },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Equip", color = Color(0xFF556500), fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                else -> {
                                    val canAfford = pet.coins >= item.costCoins
                                    Button(
                                        onClick = { onBuyOrEquip(item) },
                                        enabled = canAfford,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFD97706),
                                            disabledContainerColor = Color(0xFFE2E8F0)
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("🪙 ${item.costCoins}", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
