package ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.impl

import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.TaskItem
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.service.TaskService
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.GetTaskByIdUseCase

class GetTaskByIdUseCaseImpl(
    private val taskService: TaskService
) : GetTaskByIdUseCase {
    override suspend fun invoke(taskId: Long): TaskItem? = taskService.getTaskById(taskId)
}
