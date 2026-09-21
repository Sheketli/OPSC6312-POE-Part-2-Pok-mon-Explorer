package com.pokemon.explorer.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemon.explorer.ui.components.AppLogo
import com.pokemon.explorer.ui.components.FullPokeball
import com.pokemon.explorer.ui.theme.DisplayFont
import com.pokemon.explorer.ui.theme.PokeRed
import com.pokemon.explorer.ui.theme.PokeYellow
import kotlinx.coroutines.delay

private enum class SplashPhase { Spin, Open, Logo, Tagline, Done }

/**
 * Cinematic splash: the Poké Ball spins up, opens with a burst, the logo settles in
 * and the tagline fades up, then the app hands over to Explore.
 *
 * Timings match the web app's animation beats (900 / 1600 / 2100 / 2800 / 3000 ms).
 */
@Composable
fun SplashScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var phase by remember { mutableStateOf(SplashPhase.Spin) }

    LaunchedEffect(Unit) {
        delay(900); phase = SplashPhase.Open
        delay(700); phase = SplashPhase.Logo
        delay(500); phase = SplashPhase.Tagline
        delay(700); phase = SplashPhase.Done
        delay(200)
        onComplete()
    }

    val showGlow = phase != SplashPhase.Spin
    val showLogo = phase == SplashPhase.Logo || phase == SplashPhase.Tagline || phase == SplashPhase.Done
    val showTagline = phase == SplashPhase.Tagline || phase == SplashPhase.Done

    // Speed lines and the burst are transient, driven by the phase.
    val decorationAlpha by animateFloatAsState(
        targetValue = if (showGlow) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "decoration-alpha",
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (showLogo) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "logo-alpha",
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (showTagline) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "tagline-alpha",
    )
    val ballScale by animateFloatAsState(
        targetValue = if (phase == SplashPhase.Done) 0.9f else 1f,
        animationSpec = tween(durationMillis = 400),
        label = "ball-scale",
    )

    // Continuous spin while the ball is still closed.
    val spinTransition = rememberInfiniteTransition(label = "splash-spin")
    val spinAngle by spinTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = LinearEasing),
        ),
        label = "spin-angle",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF12082A), Color(0xFF0D1020), Color(0xFF0A0616)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        SpeedLines(alpha = decorationAlpha)

        // Soft red halo behind the ball once it opens.
        Box(
            modifier = Modifier
                .size(280.dp)
                .alpha(decorationAlpha)
                .background(
                    Brush.radialGradient(
                        listOf(PokeRed.copy(alpha = 0.18f), Color.Transparent),
                    ),
                ),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            FullPokeball(
                size = 100.dp,
                opening = showGlow,
                modifier = Modifier
                    .scale(ballScale)
                    .graphicsLayer {
                        if (phase == SplashPhase.Spin) rotationZ = spinAngle
                    },
            )

            Spacer(Modifier.height(40.dp))

            AppLogo(
                modifier = Modifier.alpha(logoAlpha)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "DISCOVER · COMPARE · EXPLORE",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 12.sp,
                fontFamily = DisplayFont,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha),
            )
        }

        // Bottom edge hairline.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, PokeRed, Color.Transparent),
                    ),
                ),
        )
    }
}

/** Sixteen faint red rays radiating from the centre, as in the web app. */
@Composable
private fun SpeedLines(alpha: Float) {
    val transition = rememberInfiniteTransition(label = "speed-lines")
    val pulse by transition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
        ),
        label = "speed-line-pulse",
    )

    Canvas(Modifier.fillMaxSize()) {
        val centre = Offset(size.width / 2f, size.height / 2f)
        val tip = Offset(centre.x, centre.y - size.height * 0.55f)

        // The gradient is anchored to the ray itself: transparent at the centre,
        // brightest two-thirds of the way out, fading to nothing at the tip.
        val rayBrush = Brush.verticalGradient(
            0.0f to Color.Transparent,
            0.4f to PokeRed.copy(alpha = 0.08f),
            0.7f to PokeRed.copy(alpha = 0.15f),
            1.0f to Color.Transparent,
            startY = centre.y,
            endY = tip.y,
        )

        repeat(16) { index ->
            rotate(degrees = index * 22.5f, pivot = centre) {
                drawLine(
                    brush = rayBrush,
                    start = centre,
                    end = tip,
                    strokeWidth = 1.dp.toPx(),
                    alpha = alpha * pulse,
                )
            }
        }
    }
}
