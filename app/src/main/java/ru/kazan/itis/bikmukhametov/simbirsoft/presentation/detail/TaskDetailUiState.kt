package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.detail

import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.TaskItem

data class TaskDetailUiState(
    val task: TaskItem? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

