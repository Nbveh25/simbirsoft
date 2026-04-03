package ru.kazan.itis.bikmukhametov.simbirsoft.data.repository

import ru.kazan.itis.bikmukhametov.simbirsoft.data.local.TaskDao
import ru.kazan.itis.bikmukhametov.simbirsoft.data.local.TaskEntity
import ru.kazan.itis.bikmukhametov.simbirsoft.data.mapper.toDomain
import ru.kazan.itis.bikmukhametov.simbirsoft.data.mapper.toEntity
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.TaskItem
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.repository.TaskRepository

class RoomTaskRepository(
    private val taskDao: TaskDao
) : TaskRepository {
    override suspend fun count(): Int = taskDao.count()

    override suspend fun insertAll(tasks: List<TaskItem>) {
        taskDao.insertAll(tasks.map { it.toEntity() })
    }

    override suspend fun insert(task: TaskItem): Long = taskDao.insert(task.toEntity())

    override suspend fun getById(id: Long): TaskItem? = taskDao.getById(id)?.toDomain()

    override suspend fun getTasksInRange(startInclusive: Long, endExclusive: Long): List<TaskItem> {
        return taskDao.getTasksInRange(startInclusive, endExclusive).map { it.toDomain() }
    }
}


