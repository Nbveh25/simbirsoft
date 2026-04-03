package ru.kazan.itis.bikmukhametov.simbirsoft.domain.repository

import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.TaskItem

interface TaskRepository {
    suspend fun count(): Int
    suspend fun insertAll(tasks: List<TaskItem>)
    suspend fun insert(task: TaskItem): Long
    suspend fun getById(id: Long): TaskItem?
    suspend fun getTasksInRange(startInclusive: Long, endExclusive: Long): List<TaskItem>
}
