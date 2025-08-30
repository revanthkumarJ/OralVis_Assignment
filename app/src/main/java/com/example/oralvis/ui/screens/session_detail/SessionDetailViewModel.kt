package com.example.oralvis.ui.screens.session_detail

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.oralvis.data.db.PhotoEntity
import com.example.oralvis.data.db.SessionEntity
import com.example.oralvis.data.rep.SessionRepository
import com.example.oralvis.ui.screens.utils.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SessionDetailViewModel(
    private val repository: SessionRepository,
    val savedStateHandle: SavedStateHandle
) : BaseViewModel<SessionDetailState, SessionDetailEvent, SessionDetailAction>(SessionDetailState()) {

    val sessionId=savedStateHandle.toRoute<SessionDetailRoute>().sessionId

    init {
        trySendAction(SessionDetailAction.LoadSession)
    }

    override fun handleAction(action: SessionDetailAction) {
        when (action) {
            is SessionDetailAction.LoadSession -> {
                mutableStateFlow.update { it.copy(loading = true) }
                viewModelScope.launch {
                    val data = repository.getSession(sessionId)
                    if (data != null) {
                        mutableStateFlow.update {
                            it.copy(
                                session = data.first,
                                photos = data.second,
                                loading = false
                            )
                        }
                    } else {
                        sendEvent(SessionDetailEvent.SessionNotFound)
                    }
                }
            }
            is SessionDetailAction.Refresh -> {
                trySendAction(SessionDetailAction.LoadSession)
            }
        }
    }
}

// State
data class SessionDetailState(
    val loading: Boolean = true,
    val session: SessionEntity? = null,
    val photos: List<PhotoEntity> = emptyList()
)

// Actions
sealed interface SessionDetailAction {
    object LoadSession : SessionDetailAction
    object Refresh : SessionDetailAction
}

// Events
sealed interface SessionDetailEvent {
    object SessionNotFound : SessionDetailEvent
}
