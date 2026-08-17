package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.gemini.DialogueEngine
import com.example.api.google.GoogleSyncManager
import com.example.audio.TamaAudioEngine
import com.example.audio.TamaTtsEngine
import com.example.data.local.MessageEntity
import com.example.data.local.PetStateEntity
import com.example.data.local.TamaDatabase
import com.example.data.local.TaskItemEntity
import com.example.data.model.FoodItem
import com.example.data.model.PetPersonality
import com.example.data.model.PetSpecies
import com.example.data.model.ShellTheme
import com.example.data.repository.TamaRepository
import com.example.ui.pet.PetAnimationState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TamaUiState(
    val animationState: PetAnimationState = PetAnimationState.IDLE,
    val isAlertDialogue: Boolean = false,
    val isSyncingGoogle: Boolean = false,
    val syncStatusMessage: String = "",
    val isGeneratingAiChat: Boolean = false,
    val showTasksSheet: Boolean = false,
    val showChatSheet: Boolean = false,
    val showCareSheet: Boolean = false,
    val showMiniGamesSheet: Boolean = false,
    val showStoreSheet: Boolean = false,
    val showStatsSheet: Boolean = false,
    val showAddTaskDialog: Boolean = false,
    val activeMiniGame: com.example.data.model.MiniGameType? = null,
    val isSoundEnabled: Boolean = true,
    val isVoiceEnabled: Boolean = true
)

class TamaViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TamaDatabase.getDatabase(application)
    private val googleSyncManager = GoogleSyncManager()
    private val repository = TamaRepository(
        petDao = database.petDao(),
        taskDao = database.taskDao(),
        messageDao = database.messageDao(),
        googleSyncManager = googleSyncManager
    )

    val audioEngine = TamaAudioEngine()
    val ttsEngine = TamaTtsEngine(application)
    private val dialogueEngine = DialogueEngine()

    val petState: StateFlow<PetStateEntity?> = repository.petStateFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allTasks: StateFlow<List<TaskItemEntity>> = repository.allTasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMessages: StateFlow<List<MessageEntity>> = repository.allMessagesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingTasks: StateFlow<List<TaskItemEntity>> = repository.upcomingTasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableFoods: List<FoodItem> = repository.availableFoods

    private val _uiState = MutableStateFlow(TamaUiState())
    val uiState: StateFlow<TamaUiState> = _uiState.asStateFlow()

    private var animationResetJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializePetIfNeeded()
            // Check deadline reminders
            checkUpcomingDeadlines()
        }
    }

    fun openTasksSheet() {
        audioEngine.playBleep()
        _uiState.value = _uiState.value.copy(showTasksSheet = true)
    }

    fun closeTasksSheet() {
        _uiState.value = _uiState.value.copy(showTasksSheet = false)
    }

    fun openChatSheet() {
        audioEngine.playBleep()
        _uiState.value = _uiState.value.copy(showChatSheet = true)
    }

    fun closeChatSheet() {
        _uiState.value = _uiState.value.copy(showChatSheet = false)
    }

    fun openCareSheet() {
        audioEngine.playBleep()
        _uiState.value = _uiState.value.copy(showCareSheet = true)
    }

    fun closeCareSheet() {
        _uiState.value = _uiState.value.copy(showCareSheet = false)
    }

    fun openMiniGamesSheet() {
        audioEngine.playBleep()
        _uiState.value = _uiState.value.copy(showMiniGamesSheet = true, activeMiniGame = null)
    }

    fun closeMiniGamesSheet() {
        _uiState.value = _uiState.value.copy(showMiniGamesSheet = false, activeMiniGame = null)
    }

    fun startMiniGame(gameType: com.example.data.model.MiniGameType) {
        audioEngine.playHappy()
        _uiState.value = _uiState.value.copy(activeMiniGame = gameType)
    }

    fun finishMiniGame(reward: com.example.data.model.MiniGameReward) {
        viewModelScope.launch {
            if (reward.score > 0) {
                audioEngine.playLevelUp()
                setAnimation(PetAnimationState.CELEBRATING, 3500)
            } else {
                audioEngine.playBleep()
            }
            val updated = repository.recordMiniGameReward(reward)
            _uiState.value = _uiState.value.copy(activeMiniGame = null)
            speakText(updated.activeDialogue)
        }
    }

    fun openStoreSheet() {
        audioEngine.playBleep()
        _uiState.value = _uiState.value.copy(showStoreSheet = true)
    }

    fun closeStoreSheet() {
        _uiState.value = _uiState.value.copy(showStoreSheet = false)
    }

    fun openStatsSheet() {
        audioEngine.playBleep()
        _uiState.value = _uiState.value.copy(showStatsSheet = true)
    }

    fun closeStatsSheet() {
        _uiState.value = _uiState.value.copy(showStatsSheet = false)
    }

    fun buyOrEquipCosmetic(item: com.example.data.model.CosmeticItem) {
        viewModelScope.launch {
            val res = repository.buyAndEquipCosmetic(item)
            res.onSuccess { msg ->
                audioEngine.playLevelUp()
                setAnimation(PetAnimationState.CELEBRATING, 2500)
                val pet = repository.getPetDirect()
                if (pet != null) speakText(pet.activeDialogue)
            }.onFailure { err ->
                audioEngine.playAlert()
            }
        }
    }

    fun adjustPetStats(hunger: Float, happiness: Float, energy: Float, focus: Float) {
        viewModelScope.launch {
            audioEngine.playBleep()
            val updated = repository.adjustStats(hunger, happiness, energy, focus)
            val mood = updated.getComputedMood()
            val thought = dialogueEngine.fallbackIdleThought(updated, upcomingTasks.value.firstOrNull())
            repository.updatePet(updated.copy(activeDialogue = thought))
            speakText(thought)
        }
    }

    fun renamePet(newName: String) {
        viewModelScope.launch {
            audioEngine.playHappy()
            val updated = repository.renamePet(newName)
            speakText(updated.activeDialogue)
        }
    }

    fun openAddTaskDialog() {
        audioEngine.playBleep()
        _uiState.value = _uiState.value.copy(showAddTaskDialog = true)
    }

    fun closeAddTaskDialog() {
        _uiState.value = _uiState.value.copy(showAddTaskDialog = false)
    }

    fun toggleSound() {
        val newVal = !_uiState.value.isSoundEnabled
        audioEngine.isSoundEnabled = newVal
        _uiState.value = _uiState.value.copy(isSoundEnabled = newVal)
    }

    fun toggleVoice() {
        val newVal = !_uiState.value.isVoiceEnabled
        ttsEngine.isVoiceEnabled = newVal
        _uiState.value = _uiState.value.copy(isVoiceEnabled = newVal)
    }

    fun petThePet() {
        viewModelScope.launch {
            audioEngine.playHappy()
            setAnimation(PetAnimationState.TALKING, 2500)
            val updated = repository.petThePet()
            speakText(updated.activeDialogue)
        }
    }

    fun feedFood(food: FoodItem) {
        viewModelScope.launch {
            audioEngine.playMunch()
            setAnimation(PetAnimationState.EATING, 2200)
            val res = repository.feedPet(food)
            res.onSuccess {
                val pet = repository.getPetDirect()
                if (pet != null) {
                    speakText(pet.activeDialogue)
                }
            }
        }
    }

    fun toggleSleep() {
        viewModelScope.launch {
            val pet = repository.toggleSleep()
            if (pet.isSleeping) {
                audioEngine.playZzz()
                setAnimation(PetAnimationState.SLEEPING, 0)
            } else {
                audioEngine.playHappy()
                setAnimation(PetAnimationState.IDLE, 0)
                speakText(pet.activeDialogue)
            }
        }
    }

    fun toggleTaskCompleted(task: TaskItemEntity) {
        viewModelScope.launch {
            if (!task.isCompleted) {
                audioEngine.playLevelUp()
                setAnimation(PetAnimationState.CELEBRATING, 3500)
                val pet = repository.completeTask(task)
                speakText(pet.activeDialogue)
            } else {
                repository.uncompleteTask(task)
            }
        }
    }

    fun addNewTask(task: TaskItemEntity) {
        viewModelScope.launch {
            audioEngine.playHappy()
            repository.addTask(task)
            closeAddTaskDialog()
            // Pet acknowledges new goal
            val pet = repository.getPetDirect()
            if (pet != null) {
                val dialogue = "Got it! Added '${task.title}' to our quest log! I'll keep you posted! ( •̀ᴗ•́ )و✨"
                repository.updatePet(pet.copy(activeDialogue = dialogue))
                speakText(dialogue)
            }
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }

    fun syncGoogleNow() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSyncingGoogle = true,
                syncStatusMessage = "Connecting with Google Tasks & Calendar..."
            )
            audioEngine.playBleep()

            val result = repository.syncGoogleServices { progress ->
                _uiState.value = _uiState.value.copy(syncStatusMessage = progress)
            }

            result.onSuccess { count ->
                _uiState.value = _uiState.value.copy(
                    isSyncingGoogle = false,
                    syncStatusMessage = "Successfully synced $count items from Google Calendar & Tasks!"
                )
                audioEngine.playHappy()
                checkUpcomingDeadlines()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isSyncingGoogle = false,
                    syncStatusMessage = "Sync finished with local schedule."
                )
            }
        }
    }

    fun askPetAboutTask(task: TaskItemEntity) {
        viewModelScope.launch {
            val pet = repository.getPetDirect() ?: return@launch
            _uiState.value = _uiState.value.copy(isAlertDialogue = true)
            audioEngine.playAlert()
            setAnimation(PetAnimationState.ALERT, 4000)

            val now = System.currentTimeMillis()
            val minutesUntil = task.dueTimestamp?.let { (it - now) / 60000L }
            val reminder = dialogueEngine.generateDeadlineReminder(pet, task, minutesUntil)

            repository.updatePet(pet.copy(activeDialogue = reminder))
            speakText(reminder)
        }
    }

    fun sendChatMessage(userText: String) {
        viewModelScope.launch {
            repository.saveMessage("USER", userText)
            _uiState.value = _uiState.value.copy(isGeneratingAiChat = true)
            audioEngine.playBleep()

            val pet = repository.getPetDirect() ?: return@launch
            val upcoming = repository.upcomingTasksFlow.stateIn(viewModelScope).value

            setAnimation(PetAnimationState.TALKING, 3500)
            val responseText = dialogueEngine.generatePetChatResponse(pet, userText, upcoming)

            repository.saveMessage("PET", responseText)
            repository.updatePet(pet.copy(activeDialogue = responseText))
            _uiState.value = _uiState.value.copy(isGeneratingAiChat = false)

            speakText(responseText)
        }
    }

    fun speakActiveDialogue() {
        val text = petState.value?.activeDialogue ?: return
        speakText(text)
    }

    fun speakText(text: String) {
        setAnimation(PetAnimationState.TALKING, 2500)
        ttsEngine.speak(text) {
            setAnimation(PetAnimationState.IDLE, 0)
        }
    }

    fun saveCustomization(name: String, species: PetSpecies, personality: PetPersonality, theme: ShellTheme) {
        viewModelScope.launch {
            audioEngine.playHappy()
            repository.updateCustomization(name, species, personality, theme)
            closeCareSheet()
            val pet = repository.getPetDirect()
            if (pet != null) {
                val msg = "Ta-da! My new look is all set! What do you think? (★ω★)💖"
                repository.updatePet(pet.copy(activeDialogue = msg))
                speakText(msg)
            }
        }
    }

    private fun checkUpcomingDeadlines() {
        viewModelScope.launch {
            val tasks = repository.upcomingTasksFlow.stateIn(viewModelScope).value
            val now = System.currentTimeMillis()
            val urgentTask = tasks.firstOrNull {
                !it.isCompleted && it.dueTimestamp != null && (it.dueTimestamp - now) in 0..(2 * 3600 * 1000L)
            }

            if (urgentTask != null) {
                val pet = repository.getPetDirect() ?: return@launch
                val mins = (urgentTask.dueTimestamp!! - now) / 60000L
                val reminder = dialogueEngine.generateDeadlineReminder(pet, urgentTask, mins)
                _uiState.value = _uiState.value.copy(isAlertDialogue = true)
                repository.updatePet(pet.copy(activeDialogue = reminder))
            }
        }
    }

    private fun setAnimation(state: PetAnimationState, durationMs: Long) {
        animationResetJob?.cancel()
        _uiState.value = _uiState.value.copy(animationState = state)
        if (durationMs > 0) {
            animationResetJob = viewModelScope.launch {
                delay(durationMs)
                _uiState.value = _uiState.value.copy(animationState = PetAnimationState.IDLE)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsEngine.shutdown()
        audioEngine.release()
    }
}
