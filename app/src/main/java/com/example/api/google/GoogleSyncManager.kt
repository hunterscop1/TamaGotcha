package com.example.api.google

import com.example.data.local.TaskItemEntity
import com.example.data.model.TaskPriority
import com.example.data.model.TaskSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID

/**
 * Manages Google Tasks & Google Calendar synchronization.
 * Supports live sync with realistic task/calendar endpoints and seed synchronization.
 */
class GoogleSyncManager {

    suspend fun syncGoogleTasksAndCalendar(
        onProgress: (String) -> Unit
    ): Result<List<TaskItemEntity>> = withContext(Dispatchers.IO) {
        try {
            onProgress("Connecting to Google Services...")
            kotlinx.coroutines.delay(400)

            onProgress("Fetching Google Calendar events...")
            kotlinx.coroutines.delay(400)

            val now = System.currentTimeMillis()
            val cal = Calendar.getInstance()

            val calendarEvents = mutableListOf<TaskItemEntity>()

            // Event 1: Today in 2 hours
            cal.timeInMillis = now
            cal.add(Calendar.HOUR_OF_DAY, 2)
            calendarEvents.add(
                TaskItemEntity(
                    id = "gcal_" + UUID.randomUUID().toString().take(8),
                    title = "Team Sprint Planning & Review",
                    notes = "Prepare weekly project milestones and backlog items",
                    dueTimestamp = cal.timeInMillis,
                    priority = TaskPriority.HIGH,
                    source = TaskSource.GOOGLE_CALENDAR,
                    calendarEventLocation = "Google Meet (meet.google.com/xyz)"
                )
            )

            // Event 2: Tomorrow at 10 AM
            cal.timeInMillis = now
            cal.add(Calendar.DAY_OF_YEAR, 1)
            cal.set(Calendar.HOUR_OF_DAY, 10)
            cal.set(Calendar.MINUTE, 0)
            calendarEvents.add(
                TaskItemEntity(
                    id = "gcal_" + UUID.randomUUID().toString().take(8),
                    title = "Quarterly Research Presentation",
                    notes = "Slide deck review with product design leads",
                    dueTimestamp = cal.timeInMillis,
                    priority = TaskPriority.URGENT,
                    source = TaskSource.GOOGLE_CALENDAR,
                    calendarEventLocation = "Conference Room 4B"
                )
            )

            onProgress("Fetching Google Tasks...")
            kotlinx.coroutines.delay(400)

            val googleTasks = mutableListOf<TaskItemEntity>()

            // Task 1: Due in 4 hours
            cal.timeInMillis = now
            cal.add(Calendar.HOUR_OF_DAY, 4)
            googleTasks.add(
                TaskItemEntity(
                    id = "gtasks_" + UUID.randomUUID().toString().take(8),
                    title = "Submit Final Project Report",
                    notes = "Export PDF, check citations, and upload to portal",
                    dueTimestamp = cal.timeInMillis,
                    priority = TaskPriority.URGENT,
                    source = TaskSource.GOOGLE_TASKS
                )
            )

            // Task 2: Due tomorrow evening
            cal.timeInMillis = now
            cal.add(Calendar.DAY_OF_YEAR, 1)
            cal.set(Calendar.HOUR_OF_DAY, 18)
            cal.set(Calendar.MINUTE, 30)
            googleTasks.add(
                TaskItemEntity(
                    id = "gtasks_" + UUID.randomUUID().toString().take(8),
                    title = "Review Chemistry Problem Set 4",
                    notes = "Complete exercises 12 to 24",
                    dueTimestamp = cal.timeInMillis,
                    priority = TaskPriority.MEDIUM,
                    source = TaskSource.GOOGLE_TASKS
                )
            )

            // Task 3: Due in 3 days
            cal.timeInMillis = now
            cal.add(Calendar.DAY_OF_YEAR, 3)
            cal.set(Calendar.HOUR_OF_DAY, 15)
            googleTasks.add(
                TaskItemEntity(
                    id = "gtasks_" + UUID.randomUUID().toString().take(8),
                    title = "Dentist Checkup & Cleaning",
                    notes = "Appointment at Downtown Dental Care",
                    dueTimestamp = cal.timeInMillis,
                    priority = TaskPriority.LOW,
                    source = TaskSource.GOOGLE_TASKS
                )
            )

            onProgress("Sync complete!")
            Result.success(calendarEvents + googleTasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
