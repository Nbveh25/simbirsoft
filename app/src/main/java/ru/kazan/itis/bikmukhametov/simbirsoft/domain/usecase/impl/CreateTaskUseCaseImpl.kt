package ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.impl

import ru.kazan.itis.bikmukhametov.simbirsoft.domain.service.TaskService
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.CreateTaskUseCase

class CreateTaskUseCaseImpl(
    private val taskService: TaskService
) : CreateTaskUseCase {
    override suspend fun invoke(
        name: String,
        description: String,
        startAtMillis: Long,
        endAtMillis: Long
    ): Long {
        return taskService.createTask(
            name = name,
            startAtMillis = startAtMillis,
            endAtMillis = endAtMillis,
            description = description
        )
    }
}
