package com.example.mobilevizinhaa.ui.theme.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

// 1. Efeito de Sacudir (Shake) - Corrigido para usar apenas Animatable
fun Modifier.shake(enabled: Boolean): Modifier = composed {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(enabled) {
        if (enabled) {
            // Executa a sacudida
            repeat(6) { index ->
                animatable.animateTo(
                    targetValue = if (index % 2 == 0) 10f else -10f,
                    animationSpec = tween(durationMillis = 50)
                )
            }
            animatable.animateTo(0f)
        }
    }

    this.graphicsLayer(translationX = animatable.value)
}

// 2. Efeito de Clique (Bounce) - Agora com a lógica de detecção de toque
fun Modifier.bounceClick() = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "bounceScale"
    )

    this.scale(scale)
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    // Detecta quando o dedo encosta
                    awaitFirstDown(false)
                    isPressed = true

                    // Espera o dedo levantar ou o toque ser cancelado
                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
        }
}