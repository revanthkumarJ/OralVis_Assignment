package com.example.oralvis.ui.screens.session_detail


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

    val sessionId = savedStateHandle.toRoute<SessionDetailRoute>().sessionId

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
                    }
                }
            }

            is SessionDetailAction.Refresh -> {
                trySendAction(SessionDetailAction.LoadSession)
            }

            is SessionDetailAction.ShowPhotoDialog -> {
                mutableStateFlow.update { it.copy(selectedPhoto = action.photo) }
            }

            SessionDetailAction.DismissPhotoDialog -> {
                mutableStateFlow.update { it.copy(selectedPhoto = null) }
            }
        }
    }
}

data class SessionDetailState(
    val loading: Boolean = true,
    val session: SessionEntity? = null,
    val photos: List<PhotoEntity> = emptyList(),
    val selectedPhoto: PhotoEntity? = null
)

sealed interface SessionDetailAction {
    object LoadSession : SessionDetailAction
    object Refresh : SessionDetailAction
    data class ShowPhotoDialog(val photo: PhotoEntity) : SessionDetailAction
    object DismissPhotoDialog : SessionDetailAction
}

sealed interface SessionDetailEvent
