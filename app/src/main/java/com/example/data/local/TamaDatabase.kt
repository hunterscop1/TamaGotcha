package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.EvolutionStage
import com.example.data.model.PetPersonality
import com.example.data.model.PetSpecies
import com.example.data.model.ShellTheme
import com.example.data.model.TaskPriority
import com.example.data.model.TaskSource

class Converters {
    @TypeConverter
    fun fromSpecies(value: PetSpecies): String = value.name
    @TypeConverter
    fun toSpecies(value: String): PetSpecies = runCatching { PetSpecies.valueOf(value) }.getOrDefault(PetSpecies.STAR_BUNNY)

    @TypeConverter
    fun fromPersonality(value: PetPersonality): String = value.name
    @TypeConverter
    fun toPersonality(value: String): PetPersonality = runCatching { PetPersonality.valueOf(value) }.getOrDefault(PetPersonality.CHEERFUL)

    @TypeConverter
    fun fromStage(value: EvolutionStage): String = value.name
    @TypeConverter
    fun toStage(value: String): EvolutionStage = runCatching { EvolutionStage.valueOf(value) }.getOrDefault(EvolutionStage.BABY)

    @TypeConverter
    fun fromPriority(value: TaskPriority): String = value.name
    @TypeConverter
    fun toPriority(value: String): TaskPriority = runCatching { TaskPriority.valueOf(value) }.getOrDefault(TaskPriority.MEDIUM)

    @TypeConverter
    fun fromSource(value: TaskSource): String = value.name
    @TypeConverter
    fun toSource(value: String): TaskSource = runCatching { TaskSource.valueOf(value) }.getOrDefault(TaskSource.MANUAL)

    @TypeConverter
    fun fromTheme(value: ShellTheme): String = value.name
    @TypeConverter
    fun toTheme(value: String): ShellTheme = runCatching { ShellTheme.valueOf(value) }.getOrDefault(ShellTheme.CYBER_MINT)
}

@Database(
    entities = [PetStateEntity::class, TaskItemEntity::class, MessageEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TamaDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun taskDao(): TaskDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: TamaDatabase? = null

        fun getDatabase(context: Context): TamaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TamaDatabase::class.java,
                    "tamagotchi_task_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
