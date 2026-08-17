package com.example.ui.dialogue

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PetDialogueBubble(
    petName: String,
    dialogueText: String,
    isAlert: Boolean,
    onSpeakClicked: () -> Unit,
    onOpenChat: () -> Unit,
    onViewDeadlines: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = dialogueText.isNotBlank(),
        enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { -it / 2 },
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isAlert) Color(0xFFFBF4E8) else Color(0xFFF5F4EB),
            tonalElevation = 2.dp,
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .border(
                    width = 1.5.dp,
                    color = if (isAlert) Color(0xFF9E472A) else Color(0xFFDDDBCF),
                    shape = RoundedCornerShape(20.dp)
                )
                .testTag("pet_dialogue_bubble")
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                // Header tag with pet name and actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isAlert) Color(0xFF9E472A) else Color(0xFF556500))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isAlert) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "Deadline Alert",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = if (isAlert) "DEADLINE ALERT" else petName.uppercase(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row {
                        IconButton(
                            onClick = onSpeakClicked,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("speak_dialogue_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Voice Speech",
                                tint = Color(0xFF556500),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onOpenChat,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("open_chat_from_bubble_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubble,
                                contentDescription = "Chat with pet",
                                tint = Color(0xFF386663),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Spoken dialogue text
                Text(
                    text = dialogueText,
                    color = Color(0xFF1B1C17),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )

                if (isAlert) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF9E472A))
                            .clickable(onClick = onViewDeadlines)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "View Upcoming Deadlines & Tasks ➜",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
