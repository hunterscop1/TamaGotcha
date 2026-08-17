package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.EvolutionStage
import com.example.data.model.PetMood
import com.example.data.model.PetPersonality
import com.example.data.model.PetSpecies
import com.example.data.model.ShellTheme
import com.example.data.model.TaskPriority
import com.example.data.model.TaskSource

@Entity(tableName = "pet_state")
data class PetStateEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Luna",
    val species: PetSpecies = PetSpecies.STAR_BUNNY,
    val personality: PetPersonality = PetPersonality.CHEERFUL,
    val level: Int = 1,
    val exp: Int = 20,
    val maxExp: Int = 100,
    val hunger: Float = 85f, // 0 = starving, 100 = full
    val energy: Float = 90f, // 0 = exhausted, 100 = full
    val happiness: Float = 80f, // 0 = sad, 100 = blissful
    val focus: Float = 75f, // boosted by productivity / pomodoros
    val evolutionStage: EvolutionStage = EvolutionStage.BABY,
    val shellTheme: ShellTheme = ShellTheme.MOSS_EARTH,
    val coins: Int = 150,
    val streakDays: Int = 3,
    val totalTasksCompleted: Int = 5,
    val isSleeping: Boolean = false,
    val isStudying: Boolean = false,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis(),
    val activeDialogue: String = "Hello friend! Let's conquer today's goals together! (｡♥‿♥｡)",
    // Customization & Store
    val equippedHat: String = "hat_none",
    val equippedAccessory: String = "acc_none",
    val equippedPalette: String = "pal_default",
    val equippedBackground: String = "bg_default",
    val unlockedItems: String = "hat_none,acc_none,pal_default,bg_default",
    // Mini-game high scores
    val highScoreDeadlineRush: Int = 0,
    val highScoreTaskSorter: Int = 0,
    val highScoreMemoryMatch: Int = 0
) {
    fun isItemUnlocked(itemId: String): Boolean {
        if (itemId.endsWith("_none") || itemId.endsWith("_default")) return true
        return unlockedItems.split(",").contains(itemId)
    }

    fun getComputedMood(hasUrgentTask: Boolean = false): PetMood {
        return when {
            isSleeping -> PetMood.SLEEPING
            hasUrgentTask -> PetMood.STRESSED
            isStudying || focus >= 85f -> PetMood.FOCUS_MODE
            hunger < 35f -> PetMood.HUNGRY
            energy < 30f -> PetMood.TIRED
            happiness < 35f -> PetMood.SAD
            happiness >= 80f && hunger >= 50f && energy >= 50f -> PetMood.ECSTATIC
            else -> PetMood.HAPPY
        }
    }
}

@Entity(tableName = "tasks")
data class TaskItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val notes: String = "",
    val dueTimestamp: Long? = null,
    val isCompleted: Boolean = false,
    val completedTimestamp: Long? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val source: TaskSource = TaskSource.MANUAL,
    val calendarEventLocation: String = "",
    val reminderAcknowledged: Boolean = false
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "PET" or "USER"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val moodTag: String = "HAPPY"
)

