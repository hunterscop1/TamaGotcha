package com.example.data.repository

import com.example.api.google.GoogleSyncManager
import com.example.data.local.MessageDao
import com.example.data.local.MessageEntity
import com.example.data.local.PetDao
import com.example.data.local.PetStateEntity
import com.example.data.local.TaskDao
import com.example.data.local.TaskItemEntity
import com.example.data.model.EvolutionStage
import com.example.data.model.FoodItem
import com.example.data.model.PetPersonality
import com.example.data.model.PetSpecies
import com.example.data.model.ShellTheme
import com.example.data.model.TaskPriority
import com.example.data.model.TaskSource
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.UUID

class TamaRepository(
    private val petDao: PetDao,
    private val taskDao: TaskDao,
    private val messageDao: MessageDao,
    private val googleSyncManager: GoogleSyncManager
) {
    val petStateFlow: Flow<PetStateEntity?> = petDao.getPetStateFlow()
    val allTasksFlow: Flow<List<TaskItemEntity>> = taskDao.getAllTasksFlow()
    val allMessagesFlow: Flow<List<MessageEntity>> = messageDao.getAllMessagesFlow()
    val upcomingTasksFlow: Flow<List<TaskItemEntity>> = taskDao.getUpcomingPendingTasksFlow()

    val availableFoods = listOf(
        FoodItem("apple", "Crisp Apple", "🍎", hungerRestore = 25f, happinessBoost = 10f, costCoins = 5),
        FoodItem("onigiri", "Rice Ball", "🍙", hungerRestore = 40f, happinessBoost = 15f, costCoins = 10),
        FoodItem("boba", "Boba Milk Tea", "🧋", hungerRestore = 20f, happinessBoost = 35f, costCoins = 15),
        FoodItem("pancake", "Fluffy Pancake", "🥞", hungerRestore = 50f, happinessBoost = 30f, costCoins = 20),
        FoodItem("ramen", "Hot Ramen", "🍜", hungerRestore = 60f, happinessBoost = 25f, costCoins = 25),
        FoodItem("cupcake", "Star Cupcake", "🧁", hungerRestore = 30f, happinessBoost = 45f, costCoins = 30)
    )

    suspend fun initializePetIfNeeded() {
        val existing = petDao.getPetStateDirect()
        if (existing == null) {
            val randomSpecies = PetSpecies.values().random()
            val randomPersonality = PetPersonality.values().random()
            val randomTheme = ShellTheme.values().random()
            val initialPet = PetStateEntity(
                id = 1,
                name = randomSpecies.defaultName,
                species = randomSpecies,
                personality = randomPersonality,
                level = 1,
                exp = 30,
                maxExp = 100,
                hunger = 85f,
                energy = 90f,
                happiness = 85f,
                focus = 80f,
                evolutionStage = EvolutionStage.BABY,
                shellTheme = randomTheme,
                coins = 200,
                streakDays = 1,
                activeDialogue = "Yay! I am awake! Let's take on today's tasks together! (｡♥‿♥｡)✨"
            )
            petDao.insertOrUpdatePet(initialPet)

            // Seed initial welcoming message
            messageDao.insertMessage(
                MessageEntity(
                    sender = "PET",
                    text = "Hi there! I'm ${initialPet.name}, your ${initialPet.species.displayName}! Feed me treats, pet me, play mini-games, and I'll keep you accountable on all your tasks! (✿◠‿◠)",
                    moodTag = "HAPPY"
                )
            )

            // Seed initial example task
            val cal = Calendar.getInstance()
            cal.add(Calendar.HOUR_OF_DAY, 3)
            taskDao.insertTask(
                TaskItemEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Complete Team Milestone Goals",
                    notes = "Finalize notes and submit quarterly report",
                    dueTimestamp = cal.timeInMillis,
                    priority = TaskPriority.HIGH,
                    source = TaskSource.MANUAL
                )
            )
        }
    }

    suspend fun renamePet(newName: String): PetStateEntity {
        val pet = petDao.getPetStateDirect() ?: PetStateEntity()
        val cleaned = newName.trim().ifBlank { pet.species.defaultName }
        val updated = pet.copy(
            name = cleaned,
            activeDialogue = "Ooh, I love my name '$cleaned'! Thank you! (✿◠‿◠)💖"
        )
        petDao.insertOrUpdatePet(updated)
        return updated
    }

    suspend fun adjustStats(hunger: Float, happiness: Float, energy: Float, focus: Float): PetStateEntity {
        val pet = petDao.getPetStateDirect() ?: PetStateEntity()
        val updated = pet.copy(
            hunger = hunger.coerceIn(0f, 100f),
            happiness = happiness.coerceIn(0f, 100f),
            energy = energy.coerceIn(0f, 100f),
            focus = focus.coerceIn(0f, 100f),
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        petDao.insertOrUpdatePet(updated)
        return updated
    }

    suspend fun buyAndEquipCosmetic(item: com.example.data.model.CosmeticItem): Result<String> {
        val pet = petDao.getPetStateDirect() ?: return Result.failure(Exception("Pet not found"))
        val unlockedList = pet.unlockedItems.split(",").toMutableSet()
        val isAlreadyUnlocked = pet.isItemUnlocked(item.id)

        var newCoins = pet.coins
        if (!isAlreadyUnlocked) {
            if (pet.coins < item.costCoins) {
                return Result.failure(Exception("Not enough coins! Need ${item.costCoins} 🪙, but you have ${pet.coins} 🪙."))
            }
            newCoins -= item.costCoins
            unlockedList.add(item.id)
        }

        var newHat = pet.equippedHat
        var newAcc = pet.equippedAccessory
        var newPalette = pet.equippedPalette
        var newBg = pet.equippedBackground

        when (item.type) {
            com.example.data.model.CosmeticType.HAT -> newHat = item.id
            com.example.data.model.CosmeticType.ACCESSORY -> newAcc = item.id
            com.example.data.model.CosmeticType.PALETTE -> newPalette = item.id
            com.example.data.model.CosmeticType.BACKGROUND -> newBg = item.id
        }

        val updated = pet.copy(
            coins = newCoins,
            equippedHat = newHat,
            equippedAccessory = newAcc,
            equippedPalette = newPalette,
            equippedBackground = newBg,
            unlockedItems = unlockedList.joinToString(","),
            activeDialogue = "Look at my new style with ${item.name}! Looking fresh! (★ω★)✨"
        )
        petDao.insertOrUpdatePet(updated)
        return Result.success(if (isAlreadyUnlocked) "Equipped ${item.name}!" else "Purchased & equipped ${item.name} for ${item.costCoins} coins!")
    }

    suspend fun equipCosmetic(item: com.example.data.model.CosmeticItem) {
        val pet = petDao.getPetStateDirect() ?: return
        var newHat = pet.equippedHat
        var newAcc = pet.equippedAccessory
        var newPalette = pet.equippedPalette
        var newBg = pet.equippedBackground

        when (item.type) {
            com.example.data.model.CosmeticType.HAT -> newHat = item.id
            com.example.data.model.CosmeticType.ACCESSORY -> newAcc = item.id
            com.example.data.model.CosmeticType.PALETTE -> newPalette = item.id
            com.example.data.model.CosmeticType.BACKGROUND -> newBg = item.id
        }

        val updated = pet.copy(
            equippedHat = newHat,
            equippedAccessory = newAcc,
            equippedPalette = newPalette,
            equippedBackground = newBg
        )
        petDao.insertOrUpdatePet(updated)
    }

    suspend fun recordMiniGameReward(reward: com.example.data.model.MiniGameReward): PetStateEntity {
        val pet = petDao.getPetStateDirect() ?: PetStateEntity()
        val (newLevel, newExp, newMaxExp, newStage) = addExp(pet, reward.expEarned)
        val newHappiness = (pet.happiness + reward.happinessEarned).coerceIn(0f, 100f)
        val newFocus = (pet.focus + reward.focusEarned).coerceIn(0f, 100f)
        val newEnergy = (pet.energy + reward.energyDelta).coerceIn(0f, 100f)
        val newCoins = pet.coins + reward.coinsEarned

        var highRush = pet.highScoreDeadlineRush
        var highTask = pet.highScoreTaskSorter
        var highMemory = pet.highScoreMemoryMatch

        when (reward.gameType) {
            com.example.data.model.MiniGameType.DEADLINE_RUSH -> highRush = maxOf(highRush, reward.score)
            com.example.data.model.MiniGameType.TASK_SORTER -> highTask = maxOf(highTask, reward.score)
            com.example.data.model.MiniGameType.MEMORY_MATCH -> highMemory = maxOf(highMemory, reward.score)
        }

        val updated = pet.copy(
            level = newLevel,
            exp = newExp,
            maxExp = newMaxExp,
            evolutionStage = newStage,
            happiness = newHappiness,
            focus = newFocus,
            energy = newEnergy,
            coins = newCoins,
            highScoreDeadlineRush = highRush,
            highScoreTaskSorter = highTask,
            highScoreMemoryMatch = highMemory,
            activeDialogue = "${reward.message} +${reward.coinsEarned} Coins & +${reward.expEarned} EXP! (★ω★)🎮"
        )
        petDao.insertOrUpdatePet(updated)
        return updated
    }

    suspend fun getPetDirect(): PetStateEntity? = petDao.getPetStateDirect()

    suspend fun updatePet(pet: PetStateEntity) = petDao.insertOrUpdatePet(pet)

    suspend fun feedPet(food: FoodItem): Result<String> {
        val pet = petDao.getPetStateDirect() ?: return Result.failure(Exception("Pet not found"))
        if (pet.coins < food.costCoins) {
            return Result.failure(Exception("Not enough coins! Earn more by finishing tasks!"))
        }

        val newHunger = (pet.hunger + food.hungerRestore).coerceIn(0f, 100f)
        val newHappiness = (pet.happiness + food.happinessBoost).coerceIn(0f, 100f)
        val newCoins = pet.coins - food.costCoins
        val (newLevel, newExp, newMaxExp, newStage) = addExp(pet, 15)

        val updated = pet.copy(
            hunger = newHunger,
            happiness = newHappiness,
            coins = newCoins,
            level = newLevel,
            exp = newExp,
            maxExp = newMaxExp,
            evolutionStage = newStage,
            lastUpdatedTimestamp = System.currentTimeMillis(),
            activeDialogue = "Mmm! Yummy ${food.name}! *nom nom nom* Thank you! (っ˘ڡ˘ς)💖"
        )
        petDao.insertOrUpdatePet(updated)
        return Result.success("Fed ${food.name}!")
    }

    suspend fun petThePet(): PetStateEntity {
        val pet = petDao.getPetStateDirect() ?: PetStateEntity()
        val newHappiness = (pet.happiness + 8f).coerceIn(0f, 100f)
        val (newLevel, newExp, newMaxExp, newStage) = addExp(pet, 5)

        val updated = pet.copy(
            happiness = newHappiness,
            level = newLevel,
            exp = newExp,
            maxExp = newMaxExp,
            evolutionStage = newStage,
            lastUpdatedTimestamp = System.currentTimeMillis(),
            activeDialogue = "*Purrrrrs happily* I love head pats! You're the best! (｡♥‿♥｡)✨"
        )
        petDao.insertOrUpdatePet(updated)
        return updated
    }

    suspend fun toggleSleep(): PetStateEntity {
        val pet = petDao.getPetStateDirect() ?: PetStateEntity()
        val isNowSleeping = !pet.isSleeping
        val newEnergy = if (isNowSleeping) pet.energy else (pet.energy + 40f).coerceIn(0f, 100f)
        val dialogue = if (isNowSleeping) {
            "Zzzz... Sleep mode engaged. Wake me when you need me! (∪｡∪)｡｡zZZ"
        } else {
            "Good morning! I feel refreshed and energized for work! ٩(ˊᗜˋ*)و"
        }

        val updated = pet.copy(
            isSleeping = isNowSleeping,
            energy = newEnergy,
            activeDialogue = dialogue,
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        petDao.insertOrUpdatePet(updated)
        return updated
    }

    suspend fun completeTask(task: TaskItemEntity): PetStateEntity {
        val updatedTask = task.copy(
            isCompleted = true,
            completedTimestamp = System.currentTimeMillis()
        )
        taskDao.updateTask(updatedTask)

        val pet = petDao.getPetStateDirect() ?: PetStateEntity()
        val (newLevel, newExp, newMaxExp, newStage) = addExp(pet, 45)
        val newHappiness = (pet.happiness + 20f).coerceIn(0f, 100f)
        val newFocus = (pet.focus + 15f).coerceIn(0f, 100f)
        val newCoins = pet.coins + 30
        val newTotalCompleted = pet.totalTasksCompleted + 1

        val updatedPet = pet.copy(
            level = newLevel,
            exp = newExp,
            maxExp = newMaxExp,
            evolutionStage = newStage,
            happiness = newHappiness,
            focus = newFocus,
            coins = newCoins,
            totalTasksCompleted = newTotalCompleted,
            activeDialogue = "YAAAY! You completed '${task.title}'! +45 EXP & +30 Coins! Look at us go! (★ω★)🎉✨"
        )
        petDao.insertOrUpdatePet(updatedPet)
        return updatedPet
    }

    suspend fun uncompleteTask(task: TaskItemEntity) {
        val updatedTask = task.copy(
            isCompleted = false,
            completedTimestamp = null
        )
        taskDao.updateTask(updatedTask)
    }

    suspend fun addTask(task: TaskItemEntity) {
        taskDao.insertTask(task)
    }

    suspend fun deleteTask(id: String) {
        taskDao.deleteTaskById(id)
    }

    suspend fun syncGoogleServices(onProgress: (String) -> Unit): Result<Int> {
        val result = googleSyncManager.syncGoogleTasksAndCalendar(onProgress)
        return result.map { tasks ->
            taskDao.insertTasks(tasks)
            tasks.size
        }
    }

    suspend fun saveMessage(sender: String, text: String, mood: String = "HAPPY") {
        messageDao.insertMessage(
            MessageEntity(
                sender = sender,
                text = text,
                timestamp = System.currentTimeMillis(),
                moodTag = mood
            )
        )
    }

    suspend fun updateCustomization(
        name: String,
        species: PetSpecies,
        personality: PetPersonality,
        shellTheme: ShellTheme
    ) {
        val pet = petDao.getPetStateDirect() ?: PetStateEntity()
        val updated = pet.copy(
            name = name,
            species = species,
            personality = personality,
            shellTheme = shellTheme
        )
        petDao.insertOrUpdatePet(updated)
    }

    private fun addExp(pet: PetStateEntity, gainedExp: Int): Quadruple<Int, Int, Int, EvolutionStage> {
        var currentLevel = pet.level
        var currentExp = pet.exp + gainedExp
        var maxExp = pet.maxExp

        while (currentExp >= maxExp) {
            currentExp -= maxExp
            currentLevel += 1
            maxExp = (maxExp * 1.3f).toInt()
        }

        val stage = when {
            currentLevel >= EvolutionStage.MYTHIC.minLevel -> EvolutionStage.MYTHIC
            currentLevel >= EvolutionStage.ADULT.minLevel -> EvolutionStage.ADULT
            currentLevel >= EvolutionStage.TEEN.minLevel -> EvolutionStage.TEEN
            currentLevel >= EvolutionStage.BABY.minLevel -> EvolutionStage.BABY
            else -> EvolutionStage.EGG
        }

        return Quadruple(currentLevel, currentExp, maxExp, stage)
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
