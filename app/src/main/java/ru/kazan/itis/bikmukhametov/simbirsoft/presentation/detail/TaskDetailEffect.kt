package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.detail

sealed interface TaskDetailEffect {
    data object CloseScreen : TaskDetailEffect
}
