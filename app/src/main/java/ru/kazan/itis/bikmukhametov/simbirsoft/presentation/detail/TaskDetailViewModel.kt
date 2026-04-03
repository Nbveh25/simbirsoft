package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.detail

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.GetTaskByIdUseCase
import ru.kazan.itis.bikmukhametov.simbirsoft.presentation.common.BaseViewModel

class TaskDetailViewModel(
    private val getTaskByIdUseCase: GetTaskByIdUseCase
) : BaseViewModel<TaskDetailUiState, TaskDetailEffect>(TaskDetailUiState()) {

    fun onIntent(intent: TaskDetailIntent) {
        when (intent) {
            is TaskDetailIntent.Load -> load(intent.taskId)
        }
    }

    private fun load(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            runCatching { getTaskByIdUseCase(taskId) }
                .onSuccess { task ->
                    if (task == null) {
                        sendEffect(TaskDetailEffect.CloseScreen)
                        updateState { it.copy(isLoading = false) }
                        return@launch
                    }
                    updateState {
                        it.copy(task = task, isLoading = false, errorMessage = null)
                    }
                }
                .onFailure { throwable ->
                    updateState {
                        it.copy(isLoading = false, errorMessage = throwable.message)
                    }
                }
        }
    }
}
