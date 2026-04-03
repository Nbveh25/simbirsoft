package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.main

import androidx.lifecycle.viewModelScope
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.GetDayHourSlotsUseCase
import ru.kazan.itis.bikmukhametov.simbirsoft.presentation.common.BaseViewModel

class TaskListViewModel(
    private val getDayHourSlotsUseCase: GetDayHourSlotsUseCase
) : BaseViewModel<TaskListUiState, TaskListEffect>(
    TaskListUiState(selectedDate = LocalDate.now(ZoneId.systemDefault()))
) {
    private val zoneId = ZoneId.systemDefault()

    init {
        onIntent(TaskListIntent.Refresh)
    }

    fun onIntent(intent: TaskListIntent) {
        when (intent) {
            is TaskListIntent.DateSelected -> {
                val date = Instant.ofEpochMilli(intent.dateMillis).atZone(zoneId).toLocalDate()
                updateState { it.copy(selectedDate = date) }
                refreshForDate(date)
            }
            is TaskListIntent.Refresh -> {
                refreshForDate(state.value.selectedDate)
            }
            is TaskListIntent.TaskClicked -> {
                postEffect(TaskListEffect.NavigateToTaskDetail(intent.taskId))
            }
            TaskListIntent.AddTaskClicked -> {
                postEffect(TaskListEffect.NavigateToCreateTask)
            }
        }
    }

    private fun refreshForDate(date: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            runCatching { getDayHourSlotsUseCase(date, zoneId) }
                .onSuccess { slots ->
                    updateState {
                        it.copy(
                            slots = slots,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { throwable ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message
                        )
                    }
                }
        }
    }
}
