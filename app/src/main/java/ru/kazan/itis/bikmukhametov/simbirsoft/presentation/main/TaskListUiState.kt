package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.main

import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.HourSlot
import java.time.LocalDate

data class TaskListUiState(
    val selectedDate: LocalDate,
    val slots: List<HourSlot> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

