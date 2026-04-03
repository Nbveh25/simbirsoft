package ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase

import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.TaskItem

fun interface GetTaskByIdUseCase {
    suspend operator fun invoke(taskId: Long): TaskItem?
}
