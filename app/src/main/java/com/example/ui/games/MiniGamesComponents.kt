package com.example.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PetStateEntity
import com.example.data.model.MiniGameReward
import com.example.data.model.MiniGameType
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun MiniGamesSheet(
    pet: PetStateEntity,
    activeGame: MiniGameType?,
    onSelectGame: (MiniGameType) -> Unit,
    onFinishGame: (MiniGameReward) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("mini_games_sheet"),
        color = Color(0xFFFDFCF4)
    ) {
        if (activeGame == null) {
            MiniGameSelectorView(
                pet = pet,
                onSelectGame = onSelectGame,
                onClose = onClose
            )
        } else {
            when (activeGame) {
                MiniGameType.DEADLINE_RUSH -> {
                    DeadlineRushGame(
                        onFinish = onFinishGame,
                        onExit = onClose
                    )
                }
                MiniGameType.TASK_SORTER -> {
                    TaskSorterGame(
                        onFinish = onFinishGame,
                        onExit = onClose
                    )
                }
                MiniGameType.MEMORY_MATCH -> {
                    ScheduleMemoryGame(
                        onFinish = onFinishGame,
                        onExit = onClose
                    )
                }
            }
        }
    }
}

@Composable
fun MiniGameSelectorView(
    pet: PetStateEntity,
    onSelectGame: (MiniGameType) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Arcade & Training",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E3B10)
                )
                Text(
                    text = "Play games with ${pet.name} to earn coins & EXP!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5F6E4D)
                )
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier.testTag("close_mini_games_btn")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF2E3B10))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // High Score / Coins Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5E0)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🪙 Wallet", style = MaterialTheme.typography.labelMedium, color = Color(0xFF5F6E4D))
                    Text("${pet.coins} Coins", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚡ Energy", style = MaterialTheme.typography.labelMedium, color = Color(0xFF5F6E4D))
                    Text("${pet.energy.toInt()}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💖 Happiness", style = MaterialTheme.typography.labelMedium, color = Color(0xFF5F6E4D))
                    Text("${pet.happiness.toInt()}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFE11D48))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                GameCard(
                    game = MiniGameType.DEADLINE_RUSH,
                    highScore = pet.highScoreDeadlineRush,
                    icon = "⚡",
                    accentColor = Color(0xFFDC2626),
                    onPlay = { onSelectGame(MiniGameType.DEADLINE_RUSH) }
                )
            }
            item {
                GameCard(
                    game = MiniGameType.TASK_SORTER,
                    highScore = pet.highScoreTaskSorter,
                    icon = "📂",
                    accentColor = Color(0xFF556500),
                    onPlay = { onSelectGame(MiniGameType.TASK_SORTER) }
                )
            }
            item {
                GameCard(
                    game = MiniGameType.MEMORY_MATCH,
                    highScore = pet.highScoreMemoryMatch,
                    icon = "🧠",
                    accentColor = Color(0xFF7C3AED),
                    onPlay = { onSelectGame(MiniGameType.MEMORY_MATCH) }
                )
            }
        }
    }
}

@Composable
private fun GameCard(
    game: MiniGameType,
    highScore: Int,
    icon: String,
    accentColor: Color,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("game_card_${game.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(icon, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = game.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1C17)
                        )
                        Text(
                            text = "Best: $highScore pts",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF854D0E),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("play_${game.name.lowercase()}_btn")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Play")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = game.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF49454F)
            )
        }
    }
}

// ----------------------------------------------------
// 1. DEADLINE RUSH (Quick-Time Target Tapping)
// ----------------------------------------------------
@Composable
fun DeadlineRushGame(
    onFinish: (MiniGameReward) -> Unit,
    onExit: () -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var timeLeftSeconds by remember { mutableIntStateOf(20) }
    var currentTargetTime by remember { mutableStateOf(1.8f) }
    var currentProgress by remember { mutableStateOf(1f) }
    var currentTaskTitle by remember { mutableStateOf("Fix Server Outage") }
    var feedbackMessage by remember { mutableStateOf("Tap DEFEND before timer runs out!") }
    var isGameOver by remember { mutableStateOf(false) }

    val taskPool = listOf(
        "Finalize Pitch Deck",
        "Deploy Security Patch",
        "Submit Quarterly Tax",
        "Respond to CEO Email",
        "Resolve Database Lock",
        "Turn in Homework",
        "Review Pull Request",
        "Ship Version 2.0"
    )

    // Game Timer Loop
    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (timeLeftSeconds > 0) {
                delay(1000L)
                timeLeftSeconds -= 1
            }
            isGameOver = true
        }
    }

    // Reaction Bar Loop
    LaunchedEffect(score, isGameOver) {
        if (!isGameOver) {
            currentTaskTitle = taskPool.random()
            currentTargetTime = maxOf(0.7f, 1.8f - (score * 0.05f))
            currentProgress = 1f

            val stepMs = 50L
            val totalSteps = (currentTargetTime * 1000 / stepMs).toInt()
            for (i in 0..totalSteps) {
                if (isGameOver) break
                currentProgress = 1f - (i.toFloat() / totalSteps)
                delay(stepMs)
            }

            if (!isGameOver && currentProgress <= 0.05f) {
                // Missed deadline!
                streak = 0
                feedbackMessage = "Missed! Streak broken! 💥"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("deadline_rush_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onExit) {
                Icon(Icons.Default.Close, contentDescription = "Exit")
            }
            Text("⚡ DEADLINE RUSH", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFDC2626))
            Text("⏱️ ${timeLeftSeconds}s", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1B1C17))
        }

        if (!isGameOver) {
            // Score & Streak
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Text("Score: $score", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF556500))
                Text("Streak: ${streak}x 🔥", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD97706))
            }

            // Central Urgent Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFEF4444))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "URGENT DEADLINE ALERT",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626)
                    )
                    Text(
                        text = currentTaskTitle,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF7F1D1D)
                    )

                    Column(modifier = Modifier.fillMaxWidth()) {
                        LinearProgressIndicator(
                            progress = { currentProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = if (currentProgress < 0.3f) Color(0xFFDC2626) else Color(0xFFF59E0B),
                            trackColor = Color(0xFFFEE2E2),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = feedbackMessage,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF991B1B)
                        )
                    }
                }
            }

            // Big Tap Button
            Button(
                onClick = {
                    if (currentProgress > 0.05f) {
                        score += 10 + (streak * 2)
                        streak += 1
                        feedbackMessage = "CRUSHED IT! +${10 + streak * 2} pts! ⚡"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .testTag("defend_deadline_tap_btn"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("DEFEND DEADLINE!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            // Game Over Summary
            val coinsEarned = score / 2
            val expEarned = score / 3
            GameOverCard(
                gameTitle = "Deadline Rush",
                score = score,
                coinsEarned = coinsEarned,
                expEarned = expEarned,
                happinessEarned = 25f,
                onClaim = {
                    onFinish(
                        MiniGameReward(
                            gameType = MiniGameType.DEADLINE_RUSH,
                            score = score,
                            coinsEarned = coinsEarned,
                            expEarned = expEarned,
                            happinessEarned = 25f,
                            focusEarned = 20f,
                            energyDelta = -5f,
                            message = "Super reflexes! Crushed the Deadline Rush!"
                        )
                    )
                }
            )
        }
    }
}

// ----------------------------------------------------
// 2. EISENHOWER TASK SORTER (Priority Sorting)
// ----------------------------------------------------
data class SorterTask(val text: String, val isHighPriority: Boolean, val emoji: String)

@Composable
fun TaskSorterGame(
    onFinish: (MiniGameReward) -> Unit,
    onExit: () -> Unit
) {
    val taskLibrary = remember {
        listOf(
            SorterTask("Client Demo in 1 Hour", true, "💼"),
            SorterTask("Doomscrolling Memes", false, "📱"),
            SorterTask("Submit Final Exam Paper", true, "📝"),
            SorterTask("Binge Watch 4 Episodes", false, "🍿"),
            SorterTask("Pay Electric Bill Today", true, "⚡"),
            SorterTask("Rearrange Desktop Icons", false, "🖥️"),
            SorterTask("Fix Critical Production Bug", true, "🐛"),
            SorterTask("Argue in Comments Section", false, "💬"),
            SorterTask("Doctor Appointment at 3 PM", true, "🩺"),
            SorterTask("Organize Sock Drawer", false, "🧦")
        ).shuffled()
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf("Sort tasks into Urgent vs Distraction!") }
    var isGameOver by remember { mutableStateOf(false) }

    val currentTask = taskLibrary.getOrNull(currentIndex)

    fun handleSort(selectedHighPriority: Boolean) {
        if (currentTask == null) return
        if (currentTask.isHighPriority == selectedHighPriority) {
            score += 15
            correctCount += 1
            feedback = "Correct! +15 pts! 🎯"
        } else {
            feedback = "Oops! Incorrect quadrant! ❌"
        }

        if (currentIndex + 1 < taskLibrary.size) {
            currentIndex += 1
        } else {
            isGameOver = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("task_sorter_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onExit) {
                Icon(Icons.Default.Close, contentDescription = "Exit")
            }
            Text("📂 EISENHOWER SORTER", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF556500))
            Text("Card ${minOf(currentIndex + 1, taskLibrary.size)}/${taskLibrary.size}", fontWeight = FontWeight.Bold, color = Color(0xFF5F6E4D))
        }

        if (!isGameOver && currentTask != null) {
            Text("Score: $score", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF556500))

            // Task Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(currentTask.emoji, fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentTask.text,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1B1C17)
                    )
                }
            }

            Text(feedback, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF5F6E4D))

            // Sorting Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Button(
                    onClick = { handleSort(false) },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .testTag("sort_distraction_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF94A3B8)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("🗑️ Distraction", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { handleSort(true) },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .testTag("sort_urgent_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF556500)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("⚡ Urgent & Key", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            val coinsEarned = score / 2
            val expEarned = score / 3
            GameOverCard(
                gameTitle = "Eisenhower Sorter",
                score = score,
                coinsEarned = coinsEarned,
                expEarned = expEarned,
                happinessEarned = 30f,
                onClaim = {
                    onFinish(
                        MiniGameReward(
                            gameType = MiniGameType.TASK_SORTER,
                            score = score,
                            coinsEarned = coinsEarned,
                            expEarned = expEarned,
                            happinessEarned = 30f,
                            focusEarned = 30f,
                            energyDelta = -5f,
                            message = "Master organizer! Sorted $correctCount items accurately!"
                        )
                    )
                }
            )
        }
    }
}

// ----------------------------------------------------
// 3. SCHEDULE MEMORY MATCH (Calendar Event Recall)
// ----------------------------------------------------
data class MemoryCardItem(val id: Int, val emoji: String, val title: String, var isFlipped: Boolean = false, var isMatched: Boolean = false)

@Composable
fun ScheduleMemoryGame(
    onFinish: (MiniGameReward) -> Unit,
    onExit: () -> Unit
) {
    val baseEvents = listOf(
        "9 AM: Team Standup" to "☕",
        "11 AM: Design Review" to "🎨",
        "2 PM: Budget Meeting" to "📊",
        "4 PM: Pet Vet Checkup" to "🩺"
    )

    val cards = remember {
        val list = mutableStateListOf<MemoryCardItem>()
        var idCounter = 0
        baseEvents.forEach { (title, emoji) ->
            list.add(MemoryCardItem(idCounter++, emoji, title))
            list.add(MemoryCardItem(idCounter++, emoji, title))
        }
        list.shuffle()
        list
    }

    var selectedFirst by remember { mutableStateOf<MemoryCardItem?>(null) }
    var selectedSecond by remember { mutableStateOf<MemoryCardItem?>(null) }
    var moves by remember { mutableIntStateOf(0) }
    var matchesFound by remember { mutableIntStateOf(0) }
    var isChecking by remember { mutableStateOf(false) }

    LaunchedEffect(selectedFirst, selectedSecond) {
        if (selectedFirst != null && selectedSecond != null) {
            isChecking = true
            delay(700L)
            if (selectedFirst!!.title == selectedSecond!!.title) {
                selectedFirst!!.isMatched = true
                selectedSecond!!.isMatched = true
                matchesFound += 1
            } else {
                selectedFirst!!.isFlipped = false
                selectedSecond!!.isFlipped = false
            }
            selectedFirst = null
            selectedSecond = null
            isChecking = false
        }
    }

    val isGameOver = matchesFound >= baseEvents.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .testTag("memory_match_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onExit) {
                Icon(Icons.Default.Close, contentDescription = "Exit")
            }
            Text("🧠 SCHEDULE MEMORY", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF7C3AED))
            Text("Pairs: $matchesFound/${baseEvents.size}", fontWeight = FontWeight.Bold, color = Color(0xFF5F6E4D))
        }

        if (!isGameOver) {
            Text("Find matching schedule cards in fewest moves!", style = MaterialTheme.typography.bodySmall, color = Color(0xFF5F6E4D))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cards) { card ->
                    val isRevealed = card.isFlipped || card.isMatched
                    Card(
                        modifier = Modifier
                            .height(82.dp)
                            .clickable(enabled = !isRevealed && !isChecking) {
                                card.isFlipped = true
                                moves += 1
                                if (selectedFirst == null) selectedFirst = card
                                else if (selectedSecond == null) selectedSecond = card
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (card.isMatched) Color(0xFFECFDF5) else if (isRevealed) Color.White else Color(0xFFEDE9FE)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (card.isMatched) Color(0xFF10B981) else if (isRevealed) Color(0xFF7C3AED) else Color(0xFFC4B5FD)
                        )
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (isRevealed) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(card.emoji, fontSize = 22.sp)
                                    Text(
                                        text = card.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = Color(0xFF1E1B4B)
                                    )
                                }
                            } else {
                                Text("❓", fontSize = 26.sp)
                            }
                        }
                    }
                }
            }

            Text("Moves: $moves", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
        } else {
            val finalScore = maxOf(40, 120 - (moves * 4))
            val coinsEarned = finalScore / 2
            val expEarned = finalScore / 3
            GameOverCard(
                gameTitle = "Schedule Memory",
                score = finalScore,
                coinsEarned = coinsEarned,
                expEarned = expEarned,
                happinessEarned = 35f,
                onClaim = {
                    onFinish(
                        MiniGameReward(
                            gameType = MiniGameType.MEMORY_MATCH,
                            score = finalScore,
                            coinsEarned = coinsEarned,
                            expEarned = expEarned,
                            happinessEarned = 35f,
                            focusEarned = 35f,
                            energyDelta = -5f,
                            message = "Incredible memory! Recalled all calendar milestones in $moves moves!"
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun GameOverCard(
    gameTitle: String,
    score: Int,
    coinsEarned: Int,
    expEarned: Int,
    happinessEarned: Float,
    onClaim: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎉 GAME COMPLETED! 🎉", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF556500))
            Spacer(modifier = Modifier.height(10.dp))
            Text("Final Score: $score pts", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1C17))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🪙 Coins", style = MaterialTheme.typography.labelSmall)
                    Text("+$coinsEarned", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⭐ EXP", style = MaterialTheme.typography.labelSmall)
                    Text("+$expEarned", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💖 Happiness", style = MaterialTheme.typography.labelSmall)
                    Text("+${happinessEarned.toInt()}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE11D48))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onClaim,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("claim_game_rewards_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF556500))
            ) {
                Text("CLAIM REWARDS & RETURN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
