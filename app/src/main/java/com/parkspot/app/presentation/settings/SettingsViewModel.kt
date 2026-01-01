package com.parkspot.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkspot.app.domain.model.Vehicle
import com.parkspot.app.domain.repository.UserPreferencesRepository
import com.parkspot.app.domain.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val autoDetectionEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val vehicles: List<Vehicle> = emptyList(),
    val isPremium: Boolean = false,
    val appVersion: String = "1.0.0",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()
    
    init {
        loadSettings()
        loadVehicles()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                combine(
                    userPreferencesRepository.autoDetectionEnabled,
                    userPreferencesRepository.notificationsEnabled,
                    userPreferencesRepository.isPremium
                ) { autoDetection, notifications, premium ->
                    Triple(autoDetection, notifications, premium)
                }.collect { (autoDetection, notifications, premium) ->
                    _state.update { it.copy(
                        autoDetectionEnabled = autoDetection,
                        notificationsEnabled = notifications,
                        isPremium = premium
                    )}
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = "Failed to load settings: ${e.message}"
                )}
            }
        }
    }
    
    private fun loadVehicles() {
        viewModelScope.launch {
            try {
                vehicleRepository.getAllVehicles()
                    .catch { e ->
                        _state.update { it.copy(
                            error = "Failed to load vehicles: ${e.message}"
                        )}
                    }
                    .collect { vehicles ->
                        _state.update { it.copy(vehicles = vehicles) }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = "Failed to load vehicles: ${e.message}"
                )}
            }
        }
    }
    
    fun toggleAutoDetection(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setAutoDetectionEnabled(enabled)
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = "Failed to update auto-detection: ${e.message}"
                )}
            }
        }
    }
    
    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setNotificationsEnabled(enabled)
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = "Failed to update notifications: ${e.message}"
                )}
            }
        }
    }
    
    fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            try {
                vehicleRepository.deleteVehicle(vehicleId)
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = "Failed to delete vehicle: ${e.message}"
                )}
            }
        }
    }
    
    fun editVehicle(vehicleId: String) {
        // Navigation to edit vehicle screen will be handled by UI
        // This is a placeholder for future implementation
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}