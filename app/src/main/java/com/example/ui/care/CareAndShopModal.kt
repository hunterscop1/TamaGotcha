package com.example.ui.care

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.FoodItem
import com.example.data.model.PetPersonality
import com.example.data.model.PetSpecies
import com.example.data.model.ShellTheme
import kotlinx.coroutines.delay

enum class CareTab {
    TREATS,
    FOCUS_TIMER,
    CUSTOMIZE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareAndShopModal(
    pet: PetStateEntity,
    foodList: List<FoodItem>,
    onDismiss: () -> Unit,
    onFeedFood: (FoodItem) -> Unit,
    onPetThePet: () -> Unit,
    onToggleSleep: () -> Unit,
    onSaveCustomization: (name: String, species: PetSpecies, personality: PetPersonality, theme: ShellTheme) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentTab by remember { mutableStateOf(CareTab.TREATS) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFFDFCF4),
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("care_shop_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header & Tab Navigation
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Pet Care & Playroom",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1C17)
                )

                Text(
                    text = "🪙 ${pet.coins} Coins",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF8D4F00)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Selection
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = currentTab == CareTab.TREATS,
                        onClick = { currentTab = CareTab.TREATS },
                        leadingIcon = { Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        label = { Text("Kitchen Treats") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF556500), selectedLabelColor = Color.White)
                    )
                }
                item {
                    FilterChip(
                        selected = currentTab == CareTab.FOCUS_TIMER,
                        onClick = { currentTab = CareTab.FOCUS_TIMER },
                        leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        label = { Text("Study Pomodoro") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF386663), selectedLabelColor = Color.White)
                    )
                }
                item {
                    FilterChip(
                        selected = currentTab == CareTab.CUSTOMIZE,
                        onClick = { currentTab = CareTab.CUSTOMIZE },
                        leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        label = { Text("Customization") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF5C6146), selectedLabelColor = Color.White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (currentTab) {
                CareTab.TREATS -> TreatsKitchenTab(
                    pet = pet,
                    foodList = foodList,
                    onFeed = onFeedFood,
                    onPet = onPetThePet,
                    onToggleSleep = onToggleSleep
                )
                CareTab.FOCUS_TIMER -> StudyPomodoroTab(pet = pet)
                CareTab.CUSTOMIZE -> CustomizationTab(pet = pet, onSave = onSaveCustomization)
            }
        }
    }
}

@Composable
private fun TreatsKitchenTab(
    pet: PetStateEntity,
    foodList: List<FoodItem>,
    onFeed: (FoodItem) -> Unit,
    onPet: () -> Unit,
    onToggleSleep: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Quick Action Bar (Head Pats & Nap Mode)
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onPet,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E472A)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("head_pat_button")
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Pet", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Give Head Pats (+Joy)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onToggleSleep,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pet.isSleeping) Color(0xFF386663) else Color(0xFF5C6146)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("toggle_sleep_button")
            ) {
                Icon(Icons.Default.Bedtime, contentDescription = "Sleep", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (pet.isSleeping) "Wake Up!" else "Sleep Mode", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Yummy Snacks & Feeds",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF1B1C17)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Food Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            items(foodList) { food ->
                FoodCard(
                    food = food,
                    canAfford = pet.coins >= food.costCoins,
                    onFeed = { onFeed(food) }
                )
            }
        }
    }
}

@Composable
private fun FoodCard(food: FoodItem, canAfford: Boolean, onFeed: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFDDDBCF), RoundedCornerShape(16.dp))
            .testTag("food_card_${food.id}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(text = food.emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = food.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1B1C17))
            Text(
                text = "+${food.hungerRestore.toInt()}% Hunger  +${food.happinessBoost.toInt()}% Joy",
                fontSize = 10.sp,
                color = Color(0xFF5C6146)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onFeed,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF556500)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Feed (${food.costCoins} 🪙)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StudyPomodoroTab(pet: PetStateEntity) {
    var timerRunning by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableStateOf(25 * 60) }

    LaunchedEffect(timerRunning) {
        while (timerRunning && secondsLeft > 0) {
            delay(1000L)
            secondsLeft -= 1
        }
        if (secondsLeft == 0) {
            timerRunning = false
        }
    }

    val minutes = secondsLeft / 60
    val secs = secondsLeft % 60

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Study & Focus with ${pet.name}",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF1B1C17)
        )
        Text(
            text = "Work alongside your Tamagotchi to boost Focus and earn bonus EXP!",
            fontSize = 12.sp,
            color = Color(0xFF5C6146),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Timer Display
        Surface(
            shape = CircleShape,
            color = Color(0xFFF5F4EB),
            modifier = Modifier
                .size(160.dp)
                .border(4.dp, Color(0xFF556500), CircleShape)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%02d:%02d", minutes, secs),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1B1C17)
                    )
                    Text(
                        text = if (timerRunning) "FOCUS MODE" else "25 MIN SESSION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF556500)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { timerRunning = !timerRunning },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (timerRunning) Color(0xFF9E472A) else Color(0xFF556500)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("toggle_pomodoro_btn")
            ) {
                Icon(
                    imageVector = if (timerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (timerRunning) "Pause" else "Start Session", fontWeight = FontWeight.Bold)
            }

            TextButton(
                onClick = {
                    timerRunning = false
                    secondsLeft = 25 * 60
                }
            ) {
                Text("Reset", color = Color(0xFF5C6146))
            }
        }
    }
}

@Composable
private fun CustomizationTab(
    pet: PetStateEntity,
    onSave: (name: String, species: PetSpecies, personality: PetPersonality, theme: ShellTheme) -> Unit
) {
    var petName by remember { mutableStateOf(pet.name) }
    var selectedSpecies by remember { mutableStateOf(pet.species) }
    var selectedPersonality by remember { mutableStateOf(pet.personality) }
    var selectedTheme by remember { mutableStateOf(pet.shellTheme) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = petName,
            onValueChange = { petName = it },
            label = { Text("Pet Name") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Select Species:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1B1C17))
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(PetSpecies.values()) { spec ->
                FilterChip(
                    selected = selectedSpecies == spec,
                    onClick = { selectedSpecies = spec },
                    label = { Text(spec.displayName, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF556500),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Personality / AI Voice Style:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1B1C17))
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(PetPersonality.values()) { pers ->
                FilterChip(
                    selected = selectedPersonality == pers,
                    onClick = { selectedPersonality = pers },
                    label = { Text(pers.displayName, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF386663),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Egg Shell Theme:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1B1C17))
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ShellTheme.values()) { theme ->
                FilterChip(
                    selected = selectedTheme == theme,
                    onClick = { selectedTheme = theme },
                    label = { Text(theme.displayName, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF556500),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSave(petName.trim().ifBlank { "Mochi" }, selectedSpecies, selectedPersonality, selectedTheme)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF556500)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("save_customization_btn")
        ) {
            Text("Save & Apply Changes", fontWeight = FontWeight.Bold)
        }
    }
}
