package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.detail

sealed interface TaskDetailIntent {
    data class Load(val taskId: Long) : TaskDetailIntent
}
