package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

/**
 * Reusable 3D Premium Modifier adding depth, metallic brush borders, and touch micro-interactions.
 */
@Composable
fun Modifier.premium3D(
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    borderBrush: Brush = Brush.linearGradient(
        colors = listOf(LuxuryGoldSoft, LuxuryBlackDeep, LuxuryGold, LuxuryBlackDeep, LuxuryGoldBright)
    ),
    borderWidth: Dp = 1.5.dp,
    glowColor: Color = LuxuryGold.copy(alpha = 0.15f),
    reduceMotion: Boolean = false,
    onClick: (() -> Unit)? = null
): Modifier {
    var isPressed by remember { mutableStateOf(false) }

    // Scale animation on tap (Micro-interaction)
    val scale by animateFloatAsState(
        targetValue = if (isPressed && !reduceMotion) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    // Rotation tilt animation (3D Card Tilt effect)
    val tiltX by animateFloatAsState(
        targetValue = if (isPressed && !reduceMotion) 3f else 0f,
        animationSpec = tween(150),
        label = "tiltX"
    )

    val baseModifier = this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            rotationX = tiltX
            cameraDistance = 12f * density
        }
        .drawBehind {
            if (!reduceMotion) {
                // Accent gold lighting glow around the element
                drawRoundRect(
                    color = glowColor,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(shape.topStart.toPx(size, this)),
                    alpha = 1f
                )
            }
        }
        .clip(shape)
        .border(BorderStroke(borderWidth, borderBrush), shape)
        .background(
            Brush.radialGradient(
                colors = listOf(LuxuryBlackElevated, LuxuryBlackDeep),
                radius = 1200f
            )
        )

    return if (onClick != null) {
        baseModifier.pointerInput(reduceMotion) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                    onClick()
                }
            )
        }
    } else {
        baseModifier
    }
}

/**
 * Silver variant of the 3D modifier for architectural elements.
 */
@Composable
fun Modifier.premium3DSilver(
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    reduceMotion: Boolean = false,
    onClick: (() -> Unit)? = null
): Modifier {
    return premium3(
        shape = shape,
        borderBrush = Brush.linearGradient(
            colors = listOf(LuxurySilver, LuxuryBlackDeep, LuxurySilverDark, LuxuryBlackDeep, LuxurySilver)
        ),
        glowColor = LuxurySilver.copy(alpha = 0.12f),
        reduceMotion = reduceMotion,
        onClick = onClick
    )
}

@Composable
fun Modifier.premium3(
    shape: RoundedCornerShape,
    borderBrush: Brush,
    glowColor: Color,
    reduceMotion: Boolean,
    onClick: (() -> Unit)?
): Modifier {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && !reduceMotion) 0.97f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val baseModifier = this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .drawBehind {
            if (!reduceMotion) {
                drawRoundRect(
                    color = glowColor,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(shape.topStart.toPx(size, this)),
                    alpha = 1f
                )
            }
        }
        .clip(shape)
        .border(BorderStroke(1.2.dp, borderBrush), shape)
        .background(LuxuryBlackElevated)

    return if (onClick != null) {
        baseModifier.pointerInput(reduceMotion) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                    onClick()
                }
            )
        }
    } else {
        baseModifier
    }
}

/**
 * 3D Rotation Transition for icons.
 */
@Composable
fun animate3DRotation(reduceMotion: Boolean = false): Float {
    if (reduceMotion) return 0f
    val infiniteTransition = rememberInfiniteTransition(label = "3D Rotate")
    val angle by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )
    return angle
}
