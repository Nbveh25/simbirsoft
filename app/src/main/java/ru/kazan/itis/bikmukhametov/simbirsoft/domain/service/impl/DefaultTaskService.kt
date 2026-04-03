package ru.kazan.itis.bikmukhametov.simbirsoft.domain.service.impl

import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.HourSlot
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.TaskItem
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.repository.TaskRepository
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.service.TaskService
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class DefaultTaskService(
    private val taskRepository: TaskRepository,
    private val initialDataProvider: () -> List<TaskItem>
) : TaskService {
    override suspend fun ensureInitialData() {
        if (taskRepository.count() == 0) {
            taskRepository.insertAll(initialDataProvider())
        }
    }

    override suspend fun getTaskById(id: Long): TaskItem? = taskRepository.getById(id)

    override suspend fun getTasksForDate(date: LocalDate, zoneId: ZoneId): List<TaskItem> {
        val dayStart = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val dayEnd = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        return taskRepository.getTasksInRange(dayStart, dayEnd)
    }

    override suspend fun createTask(
        name: String,
        startAtMillis: Long,
        endAtMillis: Long,
        description: String
    ): Long {
        val task = TaskItem(
            id = 0L,
            dateStart = startAtMillis,
            dateFinish = endAtMillis,
            name = name.trim(),
            description = description.trim()
        )
        return taskRepository.insert(task)
    }

    override fun buildHourSlots(tasks: List<TaskItem>, date: LocalDate, zoneId: ZoneId): List<HourSlot> {
        val dayStartMillis = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        return (0 until HOURS_IN_DAY).map { hour ->
            val slotStartMillis = dayStartMillis + hour * HOUR_MILLIS
            val slotEndMillis = slotStartMillis + HOUR_MILLIS
            val slotTasks = tasks.filter { task ->
                task.dateStart < slotEndMillis && task.dateFinish > slotStartMillis
            }.sortedBy { it.dateStart }
            HourSlot(
                hourStart = hour,
                hourEnd = hour + 1,
                tasks = slotTasks
            )
        }
    }

    override fun formatDateTimeRange(task: TaskItem, zoneId: ZoneId): String {
        val start = Instant.ofEpochMilli(task.dateStart).atZone(zoneId)
        val end = Instant.ofEpochMilli(task.dateFinish).atZone(zoneId)
        return "${start.toLocalDate()} ${start.toLocalTime().withSecond(0).withNano(0)} - " +
            "${end.toLocalDate()} ${end.toLocalTime().withSecond(0).withNano(0)}"
    }

    private companion object {
        private const val HOURS_IN_DAY = 24
        private const val HOUR_MILLIS = 60L * 60L * 1000L
    }
}
