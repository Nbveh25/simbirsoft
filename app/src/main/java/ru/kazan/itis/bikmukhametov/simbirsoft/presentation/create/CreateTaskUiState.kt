package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.create

data class CreateTaskUiState(
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
