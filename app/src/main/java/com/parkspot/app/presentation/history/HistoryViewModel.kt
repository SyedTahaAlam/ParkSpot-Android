package com.parkspot.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkspot.app.domain.model.ParkingSession
import com.parkspot.app.domain.repository.ParkingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryState(
    val sessions: List<ParkingSession> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val parkingRepository: ParkingRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()
    
    init {
        loadHistory()
    }
    
    private fun loadHistory() {
        viewModelScope.launch {
            try {
                parkingRepository.getAllParkingSessions()
                    .catch { e ->
                        _state.update { it.copy(
                            isLoading = false,
                            error = "Failed to load history: ${e.message}"
                        )}
                    }
                    .collect { sessions ->
                        _state.update { it.copy(
                            sessions = sessions.sortedByDescending { session -> 
                                session.startTime 
                            },
                            isLoading = false,
                            error = null
                        )}
                    }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Failed to load history: ${e.message}"
                )}
            }
        }
    }
    
    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            try {
                parkingRepository.deleteParkingSession(sessionId)
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = "Failed to delete session: ${e.message}"
                )}
            }
        }
    }
    
    fun clearAllHistory() {
        viewModelScope.launch {
            try {
                state.value.sessions.forEach { session ->
                    parkingRepository.deleteParkingSession(session.id)
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = "Failed to clear history: ${e.message}"
                )}
            }
        }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}