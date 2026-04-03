package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.main

sealed interface TaskListEffect {
    data object NavigateToCreateTask : TaskListEffect
    data class NavigateToTaskDetail(val taskId: Long) : TaskListEffect
}
