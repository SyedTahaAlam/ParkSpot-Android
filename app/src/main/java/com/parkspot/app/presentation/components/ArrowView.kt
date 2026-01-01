package com.parkspot.app.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun ArrowView(
    rotation: Float,
    color: Color,
    modifier: Modifier = Modifier,
    animated: Boolean = true
) {
    // Animate the rotation smoothly
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = if (animated) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        } else {
            snap()
        },
        label = "Arrow Rotation"
    )
    
    // Pulsing animation for the arrow
    val infiniteTransition = rememberInfiniteTransition(label = "Arrow Pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Arrow Scale"
    )
    
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth / 2f
        val centerY = canvasHeight / 2f
        
        // Scale factor for the arrow
        val arrowSize = min(canvasWidth, canvasHeight) * 0.8f * scale
        
        rotate(degrees = animatedRotation, pivot = Offset(centerX, centerY)) {
            // Draw arrow path
            val arrowPath = Path().apply {
                // Arrow tip pointing up (north)
                moveTo(centerX, centerY - arrowSize / 2)
                
                // Right side of arrow
                lineTo(centerX + arrowSize / 6, centerY - arrowSize / 6)
                lineTo(centerX + arrowSize / 12, centerY - arrowSize / 6)
                
                // Arrow shaft
                lineTo(centerX + arrowSize / 12, centerY + arrowSize / 3)
                lineTo(centerX - arrowSize / 12, centerY + arrowSize / 3)
                
                // Left side of arrow
                lineTo(centerX - arrowSize / 12, centerY - arrowSize / 6)
                lineTo(centerX - arrowSize / 6, centerY - arrowSize / 6)
                
                close()
            }
            
            // Draw filled arrow
            drawPath(
                path = arrowPath,
                color = color
            )
            
            // Draw arrow outline for better visibility
            drawPath(
                path = arrowPath,
                color = color.copy(alpha = 0.5f),
                style = Stroke(width = 4f)
            )
        }
    }
}

@Composable
fun SimpleArrowView(
    rotation: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth / 2f
        val centerY = canvasHeight / 2f
        
        val arrowSize = min(canvasWidth, canvasHeight) * 0.7f
        
        rotate(degrees = rotation, pivot = Offset(centerX, centerY)) {
            // Simple triangular arrow
            val arrowPath = Path().apply {
                moveTo(centerX, centerY - arrowSize / 2) // Tip
                lineTo(centerX + arrowSize / 4, centerY + arrowSize / 4) // Bottom right
                lineTo(centerX, centerY) // Center bottom
                lineTo(centerX - arrowSize / 4, centerY + arrowSize / 4) // Bottom left
                close()
            }
            
            drawPath(
                path = arrowPath,
                color = color
            )
        }
    }
}