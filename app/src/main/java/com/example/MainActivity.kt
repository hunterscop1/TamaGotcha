package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.PetStateEntity
import com.example.data.local.TaskItemEntity
import com.example.ui.care.CareAndShopModal
import com.example.ui.device.TamagotchiDeviceFrame
import com.example.ui.dialogs.StatsAdjusterSheet
import com.example.ui.dialogue.PetChatDialog
import com.example.ui.dialogue.PetDialogueBubble
import com.example.ui.games.MiniGamesSheet
import com.example.ui.pet.PetSpriteCanvas
import com.example.ui.store.StoreSheet
import com.example.ui.tasks.AddTaskDialog
import com.example.ui.tasks.GoogleTasksCalendarSheet
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TamaUiState
import com.example.viewmodel.TamaViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TamaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val pet by viewModel.petState.collectAsStateWithLifecycle()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
                val allMessages by viewModel.allMessages.collectAsStateWithLifecycle()
                val upcomingTasks by viewModel.upcomingTasks.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        TamagotchiMainScreen(
                            pet = pet ?: PetStateEntity(),
                            uiState = uiState,
                            allTasks = allTasks,
                            upcomingTasks = upcomingTasks,
                            viewModel = viewModel,
                            modifier = Modifier.widthIn(max = 600.dp)
                        )
                    }

                    // Modals & Bottom Sheets
                    if (uiState.showTasksSheet) {
                        GoogleTasksCalendarSheet(
                            tasks = allTasks,
                            isSyncing = uiState.isSyncingGoogle,
                            syncStatusMessage = uiState.syncStatusMessage,
                            onDismiss = { viewModel.closeTasksSheet() },
                            onSyncNow = { viewModel.syncGoogleNow() },
                            onAddTaskClicked = { viewModel.openAddTaskDialog() },
                            onToggleTaskCompleted = { task -> viewModel.toggleTaskCompleted(task) },
                            onDeleteTask = { id -> viewModel.deleteTask(id) },
                            onAskPetAboutTask = { task ->
                                viewModel.closeTasksSheet()
                                viewModel.askPetAboutTask(task)
                            }
                        )
                    }

                    if (uiState.showChatSheet) {
                        PetChatDialog(
                            pet = pet ?: PetStateEntity(),
                            messages = allMessages,
                            isGenerating = uiState.isGeneratingAiChat,
                            onDismiss = { viewModel.closeChatSheet() },
                            onSendMessage = { msg -> viewModel.sendChatMessage(msg) },
                            onSpeakMessage = { text -> viewModel.speakText(text) }
                        )
                    }

                    if (uiState.showCareSheet) {
                        CareAndShopModal(
                            pet = pet ?: PetStateEntity(),
                            foodList = viewModel.availableFoods,
                            onDismiss = { viewModel.closeCareSheet() },
                            onFeedFood = { food -> viewModel.feedFood(food) },
                            onPetThePet = { viewModel.petThePet() },
                            onToggleSleep = { viewModel.toggleSleep() },
                            onSaveCustomization = { name, species, personality, theme ->
                                viewModel.saveCustomization(name, species, personality, theme)
                            }
                        )
                    }

                    if (uiState.showMiniGamesSheet) {
                        MiniGamesSheet(
                            pet = pet ?: PetStateEntity(),
                            activeGame = uiState.activeMiniGame,
                            onSelectGame = { gameType -> viewModel.startMiniGame(gameType) },
                            onFinishGame = { reward -> viewModel.finishMiniGame(reward) },
                            onClose = { viewModel.closeMiniGamesSheet() }
                        )
                    }

                    if (uiState.showStoreSheet) {
                        StoreSheet(
                            pet = pet ?: PetStateEntity(),
                            onBuyOrEquip = { item -> viewModel.buyOrEquipCosmetic(item) },
                            onClose = { viewModel.closeStoreSheet() }
                        )
                    }

                    if (uiState.showStatsSheet) {
                        StatsAdjusterSheet(
                            pet = pet ?: PetStateEntity(),
                            onApplyStats = { hunger, happiness, energy, focus ->
                                viewModel.adjustPetStats(hunger, happiness, energy, focus)
                            },
                            onClose = { viewModel.closeStatsSheet() }
                        )
                    }

                    if (uiState.showAddTaskDialog) {
                        AddTaskDialog(
                            onDismiss = { viewModel.closeAddTaskDialog() },
                            onSaveTask = { task -> viewModel.addNewTask(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TamagotchiMainScreen(
    pet: PetStateEntity,
    uiState: TamaUiState,
    allTasks: List<TaskItemEntity>,
    upcomingTasks: List<TaskItemEntity>,
    viewModel: TamaViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val dueSoonCount = upcomingTasks.count {
        !it.isCompleted && it.dueTimestamp != null && (it.dueTimestamp - System.currentTimeMillis()) <= 24 * 3600 * 1000L
    }
    val currentMood = pet.getComputedMood(hasUrgentTask = dueSoonCount > 0)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        // App Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF556500)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🥚", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "TamaTask",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color(0xFF1B1C17)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF8D4F00),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "${pet.name} (${pet.species.displayName}) • ${pet.personality.displayName}",
                        fontSize = 11.sp,
                        color = Color(0xFF5C6146)
                    )
                }
            }

            // Audio, Voice, & Sync Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.toggleSound() },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("sound_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (uiState.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Sound effects toggle",
                        tint = if (uiState.isSoundEnabled) Color(0xFF556500) else Color(0xFF76786C),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.syncGoogleNow() },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("top_bar_sync_btn")
                ) {
                    if (uiState.isSyncingGoogle) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFF556500),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Quick Sync Google",
                            tint = Color(0xFF556500),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Active Dialogue Speech Bubble
        PetDialogueBubble(
            petName = pet.name,
            dialogueText = pet.activeDialogue,
            isAlert = uiState.isAlertDialogue,
            onSpeakClicked = { viewModel.speakActiveDialogue() },
            onOpenChat = { viewModel.openChatSheet() },
            onViewDeadlines = { viewModel.openTasksSheet() },
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Main Hardware Tamagotchi Device Frame
        TamagotchiDeviceFrame(
            pet = pet,
            upcomingDeadlinesCount = dueSoonCount,
            isSyncing = uiState.isSyncingGoogle,
            onButtonA = { viewModel.openCareSheet() },
            onButtonB = { viewModel.openChatSheet() },
            onButtonC = { viewModel.openTasksSheet() },
            screenContent = {
                PetSpriteCanvas(
                    species = pet.species,
                    evolutionStage = pet.evolutionStage,
                    animationState = uiState.animationState,
                    happiness = pet.happiness,
                    isSleeping = pet.isSleeping,
                    mood = currentMood,
                    equippedHat = pet.equippedHat,
                    equippedAccessory = pet.equippedAccessory,
                    equippedPalette = pet.equippedPalette,
                    equippedBackground = pet.equippedBackground,
                    onPetClicked = { viewModel.petThePet() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Hub Navigation Bar (Arcade, Boutique, Mood Lab, Kitchen, Chat)
        QuickHubBar(
            onOpenArcade = { viewModel.openMiniGamesSheet() },
            onOpenStore = { viewModel.openStoreSheet() },
            onOpenStatsLab = { viewModel.openStatsSheet() },
            onOpenKitchen = { viewModel.openCareSheet() },
            onOpenChat = { viewModel.openChatSheet() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Quest & Google Sync Summary Card
        UpcomingDeadlinesCard(
            tasks = allTasks,
            onOpenSheet = { viewModel.openTasksSheet() },
            onQuickComplete = { task -> viewModel.toggleTaskCompleted(task) },
            onAskPet = { task -> viewModel.askPetAboutTask(task) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Level & Evolution Progress Card
        PetEvolutionCard(pet = pet)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun QuickHubBar(
    onOpenArcade: () -> Unit,
    onOpenStore: () -> Unit,
    onOpenStatsLab: () -> Unit,
    onOpenKitchen: () -> Unit,
    onOpenChat: () -> Unit
) {
    val scroll = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .horizontalScroll(scroll),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HubChip(
            label = "Arcade",
            emoji = "🎮",
            badge = "Games",
            onClick = onOpenArcade,
            color = Color(0xFF556500),
            testTag = "hub_arcade_chip"
        )
        HubChip(
            label = "Boutique",
            emoji = "🛍️",
            badge = "Store",
            onClick = onOpenStore,
            color = Color(0xFFD97706),
            testTag = "hub_store_chip"
        )
        HubChip(
            label = "Mood Lab",
            emoji = "🎛️",
            badge = "Vitals",
            onClick = onOpenStatsLab,
            color = Color(0xFF0284C7),
            testTag = "hub_stats_chip"
        )
        HubChip(
            label = "Kitchen",
            emoji = "🍳",
            badge = "Care",
            onClick = onOpenKitchen,
            color = Color(0xFFE11D48),
            testTag = "hub_kitchen_chip"
        )
        HubChip(
            label = "Talk",
            emoji = "💬",
            badge = "AI",
            onClick = onOpenChat,
            color = Color(0xFF7C3AED),
            testTag = "hub_chat_chip"
        )
    }
}

@Composable
private fun HubChip(
    label: String,
    emoji: String,
    badge: String,
    onClick: () -> Unit,
    color: Color,
    testTag: String
) {
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B1C17)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(color.copy(alpha = 0.12f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

@Composable
fun UpcomingDeadlinesCard(
    tasks: List<TaskItemEntity>,
    onOpenSheet: () -> Unit,
    onQuickComplete: (TaskItemEntity) -> Unit,
    onAskPet: (TaskItemEntity) -> Unit
) {
    val pending = tasks.filter { !it.isCompleted }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F4EB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .border(1.dp, Color(0xFFDDDBCF), RoundedCornerShape(20.dp))
            .testTag("upcoming_deadlines_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFF556500),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Google Tasks & Schedule",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1B1C17)
                    )
                }

                Text(
                    text = "View All (${tasks.size}) ➜",
                    color = Color(0xFF556500),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(onClick = onOpenSheet)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (pending.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎉 All tasks finished! Your pet is super proud of you!",
                        color = Color(0xFF5C6146),
                        fontSize = 12.sp
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    pending.take(3).forEach { task ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEBE9DC))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .border(2.dp, Color(0xFF556500), CircleShape)
                                        .clickable { onQuickComplete(task) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = task.title,
                                        color = Color(0xFF1B1C17),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                    if (task.notes.isNotBlank()) {
                                        Text(
                                            text = task.notes,
                                            color = Color(0xFF5C6146),
                                            fontSize = 10.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = { onAskPet(task) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Pet Reminder",
                                    tint = Color(0xFF8D4F00),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PetEvolutionCard(pet: PetStateEntity) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F4EB)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .border(1.dp, Color(0xFFDDDBCF), RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Pet Growth & Evolution",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1B1C17)
                    )
                    Text(
                        text = "${pet.evolutionStage.title} • Level ${pet.level}",
                        fontSize = 12.sp,
                        color = Color(0xFF556500),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE3EBB1))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🔥 ${pet.totalTasksCompleted} Completed",
                        color = Color(0xFF181F00),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // EXP Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Experience (EXP)", fontSize = 11.sp, color = Color(0xFF5C6146))
                Text(text = "${pet.exp} / ${pet.maxExp} EXP", fontSize = 11.sp, color = Color(0xFF1B1C17), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { (pet.exp.toFloat() / pet.maxExp.toFloat()).coerceIn(0f, 1f) },
                color = Color(0xFF556500),
                trackColor = Color(0xFFDDDBCF),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }
}
