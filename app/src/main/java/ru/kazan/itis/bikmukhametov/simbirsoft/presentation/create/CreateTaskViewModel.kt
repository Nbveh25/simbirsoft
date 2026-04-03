package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.create

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.CreateTaskUseCase
import ru.kazan.itis.bikmukhametov.simbirsoft.presentation.common.BaseViewModel

class CreateTaskViewModel(
    private val createTaskUseCase: CreateTaskUseCase
) : BaseViewModel<CreateTaskUiState, CreateTaskEffect>(CreateTaskUiState()) {

    fun onIntent(intent: CreateTaskIntent) {
        when (intent) {
            is CreateTaskIntent.Submit -> submit(intent)
        }
    }

    private fun submit(intent: CreateTaskIntent.Submit) {
        viewModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                createTaskUseCase(intent.name, intent.description, intent.startAtMillis, intent.endAtMillis)
            }.onSuccess {
                updateState { it.copy(isSaving = false) }
                sendEffect(CreateTaskEffect.TaskCreated)
            }.onFailure { throwable ->
                updateState {
                    it.copy(isSaving = false, errorMessage = throwable.message)
                }
            }
        }
    }
}
