package com.example.ui.device

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PetStateEntity
import com.example.data.model.ShellTheme

@Composable
fun TamagotchiDeviceFrame(
    pet: PetStateEntity,
    upcomingDeadlinesCount: Int,
    isSyncing: Boolean,
    onButtonA: () -> Unit, // Feed / Care
    onButtonB: () -> Unit, // Talk / Chat
    onButtonC: () -> Unit, // Google Tasks / Calendar
    screenContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = pet.shellTheme
    val shellPrimary = Color(theme.primaryHex)
    val shellAccent = Color(theme.accentHex)

    // Outer Egg Device Shell
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(16.dp, RoundedCornerShape(44.dp))
            .clip(RoundedCornerShape(44.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        shellPrimary,
                        shellAccent,
                        shellPrimary.copy(alpha = 0.9f)
                    )
                )
            )
            .border(4.dp, Color(0x44FFFFFF), RoundedCornerShape(44.dp))
            .padding(14.dp)
            .testTag("tamagotchi_device_frame")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Top Device Hardware Header (Keychain ring loop + Brand Title)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
                // Speaker Grills
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(4) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(Color(0x55000000))
                        )
                    }
                }

                // Brand Branding Text
                Text(
                    text = "✦ TAMATASK ✦",
                    color = Color(0xDDFFFFFF),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontSize = 12.sp
                )

                // Sync status indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSyncing) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Syncing Google Tasks",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    } else if (upcomingDeadlinesCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "$upcomingDeadlinesCount Due",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Inner LCD Virtual Glass Screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF161A12))
                    .border(3.dp, Color(0xFF3B4430), RoundedCornerShape(24.dp))
                    .padding(10.dp)
            ) {
                // Screen Glass Ambient Tint
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF22291B),
                                    Color(0xFF141910)
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Top LCD Status Bar
                        LcdStatusBar(pet = pet, upcomingDeadlines = upcomingDeadlinesCount)

                        // Main Viewport (Animated Pet & Dialogue)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            screenContent()
                        }

                        // Bottom LCD Meter Bar (Hunger, Joy, Energy, Focus)
                        LcdMeterBar(pet = pet)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Tactile Physical Buttons (A, B, C)
            TamagotchiThreeButtons(
                onButtonA = onButtonA,
                onButtonB = onButtonB,
                onButtonC = onButtonC
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun LcdStatusBar(pet: PetStateEntity, upcomingDeadlines: Int) {
    val mood = pet.getComputedMood(hasUrgentTask = upcomingDeadlines > 0)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33000000))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        // Level, Name & Personality Badge
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "LV.${pet.level}",
                color = Color(0xFFE3EBB1),
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = pet.name,
                color = Color(0xFFFDFCF4),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "• ${pet.personality.displayName}",
                color = Color(0xFFC7C7BC),
                fontSize = 10.sp
            )
        }

        // Mood & Coins Status
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${mood.emoji} ${mood.displayName}",
                color = Color(0xFFE3EBB1),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "🪙 ${pet.coins}",
                color = Color(0xFFFDF0CC),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            if (upcomingDeadlines > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = "Deadlines",
                    tint = Color(0xFFD48B47),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun LcdMeterBar(pet: PetStateEntity) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x44000000))
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        StatMiniMeter("Hunger", pet.hunger, Color(0xFFD48B47), Icons.Default.Restaurant)
        StatMiniMeter("Joy", pet.happiness, Color(0xFFBCCC6F), Icons.Default.Favorite)
        StatMiniMeter("Energy", pet.energy, Color(0xFF86B0A8), Icons.Default.Bedtime)
        StatMiniMeter("Focus", pet.focus, Color(0xFFE3EBB1), Icons.Default.Bolt)
    }
}

@Composable
private fun StatMiniMeter(
    label: String,
    value: Float,
    barColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(62.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = barColor,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "${value.toInt()}%",
                color = Color(0xFFC7C7BC),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { (value / 100f).coerceIn(0f, 1f) },
            color = barColor,
            trackColor = Color(0xFF2E3524),
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
        )
    }
}

@Composable
private fun TamagotchiThreeButtons(
    onButtonA: () -> Unit,
    onButtonB: () -> Unit,
    onButtonC: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        HardwareTactileButton(
            label = "A: CARE",
            subLabel = "Feed/Pet",
            buttonColor = Color(0xFF9E472A),
            onClick = onButtonA,
            testTag = "button_a_care"
        )

        HardwareTactileButton(
            label = "B: TALK",
            subLabel = "AI Chat",
            buttonColor = Color(0xFF556500),
            onClick = onButtonB,
            testTag = "button_b_talk"
        )

        HardwareTactileButton(
            label = "C: SYNC",
            subLabel = "Calendar",
            buttonColor = Color(0xFF386663),
            onClick = onButtonC,
            testTag = "button_c_sync"
        )
    }
}

@Composable
private fun HardwareTactileButton(
    label: String,
    subLabel: String,
    buttonColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = buttonColor,
            shadowElevation = 8.dp,
            tonalElevation = 4.dp,
            modifier = Modifier
                .size(48.dp)
                .border(2.dp, Color(0x66FFFFFF), CircleShape)
                .testTag(testTag)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 10.sp
        )
        Text(
            text = subLabel,
            color = Color(0xCCFFFFFF),
            fontSize = 9.sp
        )
    }
}
