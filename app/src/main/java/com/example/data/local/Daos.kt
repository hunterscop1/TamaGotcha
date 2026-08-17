package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Query("SELECT * FROM pet_state WHERE id = 1")
    fun getPetStateFlow(): Flow<PetStateEntity?>

    @Query("SELECT * FROM pet_state WHERE id = 1")
    suspend fun getPetStateDirect(): PetStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePet(pet: PetStateEntity)

    @Update
    suspend fun updatePet(pet: PetStateEntity)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueTimestamp ASC, priority DESC")
    fun getAllTasksFlow(): Flow<List<TaskItemEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND dueTimestamp IS NOT NULL ORDER BY dueTimestamp ASC")
    fun getUpcomingPendingTasksFlow(): Flow<List<TaskItemEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND dueTimestamp IS NOT NULL ORDER BY dueTimestamp ASC LIMIT 5")
    suspend fun getUpcomingPendingTasksDirect(): List<TaskItemEntity>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): TaskItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskItemEntity>)

    @Update
    suspend fun updateTask(task: TaskItemEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)

    @Query("DELETE FROM tasks WHERE source != 'MANUAL'")
    suspend fun clearSyncedTasks()
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessagesFlow(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages")
    suspend fun clearAllMessages()
}
