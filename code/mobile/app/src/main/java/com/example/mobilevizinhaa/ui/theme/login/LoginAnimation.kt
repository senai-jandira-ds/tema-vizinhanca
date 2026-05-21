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

/**
 * Animação de Shake (Sacudida): Faz o componente balançar para os lados.
 * Ideal para indicar erros de validação em campos de texto.
 */
fun Modifier.shake(enabled: Boolean): Modifier = composed {
    // Controla o valor da transição horizontal
    val animatable = remember { Animatable(0f) }

    // Dispara a animação toda vez que o estado 'enabled' mudar para true
    LaunchedEffect(enabled) {
        if (enabled) {
            // Repete o movimento de ir e voltar 6 vezes
            repeat(6) { index ->
                animatable.animateTo(
                    targetValue = if (index % 2 == 0) 10f else -10f,
                    animationSpec = tween(durationMillis = 50)
                )
            }
            // Volta para a posição original (zero)
            animatable.animateTo(0f)
        }
    }

    this.graphicsLayer(translationX = animatable.value)
}

/**
 * Animação de Bounce (Clique): Faz o componente diminuir levemente ao ser pressionado.
 * Dá um feedback visual tátil para botões e elementos clicáveis.
 */
fun Modifier.bounceClick() = composed {
    var isPressed by remember { mutableStateOf(false) }

    // Anima a escala suavemente entre 100% e 95%
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "bounceScale",
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
    )

    this.scale(scale)
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    // Detecta o momento em que o dedo encosta na tela
                    awaitFirstDown(false)
                    isPressed = true

                    // Aguarda o dedo levantar ou o movimento ser cancelado
                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
        }
}