package com.parkspot.app.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkspot.app.domain.model.Location
import com.parkspot.app.domain.repository.ParkingRepository
import com.parkspot.app.data.location.LocationDataSource
import com.parkspot.app.data.sensors.CompassDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.*

data class NavigationState(
    val isLoading: Boolean = true,
    val distanceToTarget: Float = 0f,
    val bearingToTarget: Float = 0f,
    val compassHeading: Float = 0f,
    val locationAccuracy: Float = 0f,
    val hasPhoto: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val parkingRepository: ParkingRepository,
    private val locationDataSource: LocationDataSource,
    private val compassDataSource: CompassDataSource
) : ViewModel() {
    
    private val _state = MutableStateFlow(NavigationState())
    val state: StateFlow<NavigationState> = _state.asStateFlow()
    
    private var navigationJob: Job? = null
    private var targetLocation: Location? = null
    
    fun startNavigation(parkingSpotId: String) {
        navigationJob?.cancel()
        navigationJob = viewModelScope.launch {
            try {
                // Load parking spot details
                parkingRepository.getParkingSpotById(parkingSpotId)
                    .collect { parkingSpot ->
                        parkingSpot?.let {
                            targetLocation = it.location
                            _state.update { state ->
                                state.copy(
                                    isLoading = false,
                                    hasPhoto = it.photoUri != null
                                )
                            }
                            
                            // Start location and compass updates
                            startLocationUpdates()
                            startCompassUpdates()
                        } ?: run {
                            _state.update { it.copy(
                                isLoading = false,
                                error = "Parking spot not found"
                            )}
                        }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Failed to load navigation: ${e.message}"
                )}
            }
        }
    }
    
    private fun startLocationUpdates() {
        viewModelScope.launch {
            locationDataSource.getLocationUpdates()
                .catch { e ->
                    _state.update { it.copy(
                        error = "Location error: ${e.message}"
                    )}
                }
                .collect { location ->
                    targetLocation?.let { target ->
                        val distance = calculateDistance(
                            location.latitude,
                            location.longitude,
                            target.latitude,
                            target.longitude
                        )
                        
                        val bearing = calculateBearing(
                            location.latitude,
                            location.longitude,
                            target.latitude,
                            target.longitude
                        )
                        
                        _state.update { state ->
                            state.copy(
                                distanceToTarget = distance,
                                bearingToTarget = bearing,
                                locationAccuracy = location.accuracy
                            )
                        }
                    }
                }
        }
    }
    
    private fun startCompassUpdates() {
        viewModelScope.launch {
            compassDataSource.getCompassHeading()
                .catch { e ->
                    _state.update { it.copy(
                        error = "Compass error: ${e.message}"
                    )}
                }
                .collect { heading ->
                    _state.update { state ->
                        state.copy(compassHeading = heading)
                    }
                }
        }
    }
    
    fun stopNavigation() {
        navigationJob?.cancel()
        locationDataSource.stopLocationUpdates()
        compassDataSource.stopCompass()
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    /**
     * Calculate distance between two coordinates using Haversine formula
     * Returns distance in meters
     */
    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val earthRadius = 6371000.0 // meters
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return (earthRadius * c).toFloat()
    }
    
    /**
     * Calculate bearing between two coordinates
     * Returns bearing in degrees (0-360)
     */
    private fun calculateBearing(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val dLon = Math.toRadians(lon2 - lon1)
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        
        val y = sin(dLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) -
                sin(lat1Rad) * cos(lat2Rad) * cos(dLon)
        
        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360) % 360
        
        return bearing.toFloat()
    }
    
    override fun onCleared() {
        super.onCleared()
        stopNavigation()
    }
}