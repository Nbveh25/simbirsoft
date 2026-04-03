package ru.kazan.itis.bikmukhametov.simbirsoft.data.source

import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.TaskItem

fun interface TaskSeedDataSource {
    fun loadTasks(): List<TaskItem>
}
