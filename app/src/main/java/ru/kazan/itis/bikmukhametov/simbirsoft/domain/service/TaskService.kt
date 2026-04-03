package ru.kazan.itis.bikmukhametov.simbirsoft.domain.service

import java.time.LocalDate
import java.time.ZoneId
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.HourSlot
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.TaskItem

interface TaskService {
    suspend fun ensureInitialData()
    suspend fun getTaskById(id: Long): TaskItem?
    suspend fun getTasksForDate(date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): List<TaskItem>
    suspend fun createTask(
        name: String,
        startAtMillis: Long,
        endAtMillis: Long,
        description: String
    ): Long
    fun buildHourSlots(tasks: List<TaskItem>, date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): List<HourSlot>
    fun formatDateTimeRange(task: TaskItem, zoneId: ZoneId = ZoneId.systemDefault()): String
}
