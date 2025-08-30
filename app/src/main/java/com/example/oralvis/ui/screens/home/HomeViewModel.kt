package com.example.oralvis.ui.screens.home

import androidx.lifecycle.viewModelScope
import com.example.oralvis.data.db.SessionEntity
import com.example.oralvis.data.rep.SessionRepository
import com.example.oralvis.ui.screens.utils.BaseViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: SessionRepository
) : BaseViewModel<HomeState, HomeEvent, HomeAction>(HomeState()) {

    override fun handleAction(action: HomeAction) {
        when (action) {
            is HomeAction.UpdateQuery -> {
                mutableStateFlow.update { it.copy(query = action.query) }
            }
            is HomeAction.RefreshSessions -> {
                mutableStateFlow.update { it.copy(loading = true) }
                viewModelScope.launch {
                    repository.observeAllSessions().collect { data->
                        mutableStateFlow.update { it.copy(sessions = data , loading = false) }
                    }

                }
            }
            is HomeAction.OpenSession -> {
                sendEvent(HomeEvent.NavigateToSession(action.sessionId))
            }
            is HomeAction.StartCamera -> {
                sendEvent(HomeEvent.NavigateToCamera)
            }
        }
    }

}

sealed interface HomeEvent {
    data class NavigateToSession(val sessionId: Long) : HomeEvent
    object NavigateToCamera : HomeEvent
}

sealed interface HomeAction {
    data class UpdateQuery(val query: String) : HomeAction
    object RefreshSessions : HomeAction
    data class OpenSession(val sessionId: Long) : HomeAction
    object StartCamera : HomeAction
}

data class HomeState(
    val loading: Boolean = true,
    val sessions: List<SessionEntity> = emptyList(),
    val query: String = ""
)