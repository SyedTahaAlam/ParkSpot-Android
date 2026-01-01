package com.parkspot.app.presentation.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.parkspot.app.presentation.components.ArrowView
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScreen(
    parkingSpotId: String,
    onBackClick: () -> Unit,
    onShowMap: () -> Unit,
    onViewPhoto: () -> Unit,
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(parkingSpotId) {
        viewModel.startNavigation(parkingSpotId)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopNavigation()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Navigate to Car") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Distance Display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 32.dp)
                ) {
                    Text(
                        text = formatDistance(state.distanceToTarget),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (state.distanceToTarget < 1000) "meters away" else "kilometers away",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Accuracy Indicator
                    Spacer(modifier = Modifier.height(16.dp))
                    AccuracyIndicator(accuracy = state.locationAccuracy)
                }
                
                // Animated Arrow
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // Compass Rose Background
                    CompassRose(
                        heading = state.compassHeading,
                        modifier = Modifier.size(280.dp)
                    )
                    
                    // Navigation Arrow
                    ArrowView(
                        rotation = state.bearingToTarget - state.compassHeading,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(200.dp)
                    )
                }
                
                // Compass Heading Display
                CompassHeadingCard(heading = state.compassHeading)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onShowMap,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Icon(
                            Icons.Default.Map,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Show Map")
                    }
                    
                    if (state.hasPhoto) {
                        OutlinedButton(
                            onClick = onViewPhoto,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            Icon(
                                Icons.Default.Photo,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View Photo")
                        }
                    }
                }
            }
            
            // Loading Overlay
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            
            // Error Snackbar
            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
fun AccuracyIndicator(accuracy: Float) {
    val accuracyLevel = when {
        accuracy < 10f -> "Excellent"
        accuracy < 30f -> "Good"
        accuracy < 50f -> "Fair"
        else -> "Poor"
    }
    
    val color = when {
        accuracy < 10f -> Color(0xFF4CAF50)
        accuracy < 30f -> Color(0xFF8BC34A)
        accuracy < 50f -> Color(0xFFFFC107)
        else -> Color(0xFFFF5722)
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = "$accuracyLevel (±${accuracy.roundToInt()}m)",
                fontSize = 12.sp,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CompassRose(heading: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.rotate(-heading),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "N",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CompassHeadingCard(heading: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${heading.roundToInt()}°",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = getCardinalDirection(heading),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun formatDistance(meters: Float): String {
    return if (meters < 1000) {
        "${meters.roundToInt()}"
    } else {
        String.format("%.1f", meters / 1000)
    }
}

fun getCardinalDirection(heading: Float): String {
    val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    val index = ((heading + 22.5f) / 45f).toInt() % 8
    return directions[index]
}