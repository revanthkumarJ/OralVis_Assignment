package com.example.oralvis.ui.screens.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<S, E, A>(
    initialState: S,
) : ViewModel() {
    protected val mutableStateFlow: MutableStateFlow<S> = MutableStateFlow(initialState)
    private val eventChannel: Channel<E> = Channel(capacity = Channel.UNLIMITED)
    private val internalActionChannel: Channel<A> = Channel(capacity = Channel.UNLIMITED)

    protected val state: S get() = mutableStateFlow.value

    val stateFlow: StateFlow<S> = mutableStateFlow.asStateFlow()

    val eventFlow: Flow<E> = eventChannel.receiveAsFlow()

    val actionChannel: SendChannel<A> = internalActionChannel

    init {
        viewModelScope.launch {
            internalActionChannel
                .consumeAsFlow()
                .collect { action ->
                    handleAction(action)
                }
        }
    }

    protected abstract fun handleAction(action: A): Unit

    fun trySendAction(action: A) {
        actionChannel.trySend(action)
    }

    protected suspend fun sendAction(action: A) {
        actionChannel.send(action)
    }

    protected fun sendEvent(event: E) {
        viewModelScope.launch { eventChannel.send(event) }
    }
}