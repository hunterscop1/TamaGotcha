package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.model.PetMood

@Composable
fun StatsAdjusterSheet(
    pet: PetStateEntity,
    onApplyStats: (hunger: Float, happiness: Float, energy: Float, focus: Float) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tempHunger by remember { mutableFloatStateOf(pet.hunger) }
    var tempHappiness by remember { mutableFloatStateOf(pet.happiness) }
    var tempEnergy by remember { mutableFloatStateOf(pet.energy) }
    var tempFocus by remember { mutableFloatStateOf(pet.focus) }

    // Live preview of computed mood based on adjusted sliders
    val previewMood = when {
        pet.isSleeping -> PetMood.SLEEPING
        tempHunger < 35f -> PetMood.HUNGRY
        tempEnergy < 30f -> PetMood.TIRED
        tempHappiness < 35f -> PetMood.SAD
        tempHappiness >= 80f && tempHunger >= 50f && tempEnergy >= 50f -> PetMood.ECSTATIC
        tempFocus >= 85f -> PetMood.FOCUS_MODE
        else -> PetMood.HAPPY
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("stats_adjuster_sheet"),
        color = Color(0xFFFDFCF4)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
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
                                .background(Color(0xFF556500).copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFF556500))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Mood & Vitals Lab",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E3B10)
                            )
                            Text(
                                text = "Adjust stats to influence pet personality & behavior",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF5F6E4D)
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("close_stats_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF2E3B10))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Live Mood Preview Badge
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current Mood Outcome", style = MaterialTheme.typography.labelSmall, color = Color(0xFF5F6E4D))
                            Text(
                                "${previewMood.emoji} ${previewMood.displayName}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B1C17)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5E0))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(pet.personality.displayName, color = Color(0xFF556500), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Sliders
                StatSliderRow(
                    name = "Hunger (Satiety)",
                    emoji = "🍙",
                    value = tempHunger,
                    onValueChange = { tempHunger = it },
                    activeColor = Color(0xFFF59E0B),
                    tag = "slider_hunger"
                )

                StatSliderRow(
                    name = "Happiness",
                    emoji = "💖",
                    value = tempHappiness,
                    onValueChange = { tempHappiness = it },
                    activeColor = Color(0xFFE11D48),
                    tag = "slider_happiness"
                )

                StatSliderRow(
                    name = "Energy",
                    emoji = "⚡",
                    value = tempEnergy,
                    onValueChange = { tempEnergy = it },
                    activeColor = Color(0xFF0284C7),
                    tag = "slider_energy"
                )

                StatSliderRow(
                    name = "Focus & Productivity",
                    emoji = "🎯",
                    value = tempFocus,
                    onValueChange = { tempFocus = it },
                    activeColor = Color(0xFF0D9488),
                    tag = "slider_focus"
                )
            }

            // Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        onApplyStats(tempHunger, tempHappiness, tempEnergy, tempFocus)
                        onClose()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("apply_stats_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF556500)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("APPLY STATS & OBSERVE REACTION", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        tempHunger = 100f
                        tempHappiness = 100f
                        tempEnergy = 100f
                        tempFocus = 100f
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Restore All Vitals (100%)", color = Color(0xFF556500))
                }
            }
        }
    }
}

@Composable
private fun StatSliderRow(
    name: String,
    emoji: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    activeColor: Color,
    tag: String
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$emoji $name", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF1B1C17))
            Text("${value.toInt()}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = activeColor)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth().testTag(tag),
            colors = SliderDefaults.colors(
                thumbColor = activeColor,
                activeTrackColor = activeColor,
                inactiveTrackColor = Color(0xFFE2E8F0)
            )
        )
    }
}
