package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.create

sealed interface CreateTaskEffect {
    data object TaskCreated : CreateTaskEffect
}
