package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.main

sealed interface TaskListIntent {
    data object Refresh : TaskListIntent
    data object AddTaskClicked : TaskListIntent
    data class DateSelected(val dateMillis: Long) : TaskListIntent
    data class TaskClicked(val taskId: Long) : TaskListIntent
}
