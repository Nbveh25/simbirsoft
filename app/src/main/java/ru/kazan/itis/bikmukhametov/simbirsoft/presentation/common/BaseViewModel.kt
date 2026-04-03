package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : Any, Effect : Any>(
    initialState: State
) : ViewModel() {

    private val mutableState = MutableStateFlow(initialState)
    val state: StateFlow<State> = mutableState.asStateFlow()

    private val mutableEffect = MutableSharedFlow<Effect>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect: SharedFlow<Effect> = mutableEffect.asSharedFlow()

    protected fun updateState(transform: (State) -> State) {
        mutableState.update(transform)
    }

    protected fun setState(newState: State) {
        mutableState.value = newState
    }

    protected suspend fun sendEffect(effect: Effect) {
        mutableEffect.emit(effect)
    }

    protected fun postEffect(effect: Effect) {
        viewModelScope.launch {
            mutableEffect.emit(effect)
        }
    }
}
