package com.pokemon.explorer.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val BallRed = Color(0xFFEE1515)
private val BallShell = Color(0xFF1A1020)
private val BallButton = Color(0xFFF0F0F0)
private val BallYellow = Color(0xFFFFCB05)

/**
 * The small Poké Ball glyph used as a caught indicator, tab icon and button icon.
 *
 * @param filled paints the whole ball red — used to mark a favourite
 * @param caught draws a closed, caught ball: red upper shell, white lower shell, no seam
 */
@Composable
fun PokeballIcon(
    size: Dp = 24.dp,
    filled: Boolean = false,
    caught: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = this.size.minDimension * 0.08f
        val radius = this.size.minDimension / 2f - stroke / 2f
        val centre = Offset(this.size.width / 2f, this.size.height / 2f)
        val arcTopLeft = Offset(centre.x - radius, centre.y - radius)
        val arcSize = Size(radius * 2f, radius * 2f)

        // Body fill. A caught ball shows a white lower shell; a merely "filled" ball
        // is solid red and keeps its seam.
        if (filled || caught) {
            drawCircle(BallRed, radius, centre)
        }
        if (caught) {
            drawArc(
                color = Color.White,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = arcTopLeft,
                size = arcSize,
            )
        }

        // Shell outline.
        drawCircle(Color.White, radius, centre, style = Stroke(stroke))

        // Seam between the shells.
        if (!caught) {
            drawLine(
                color = Color.White,
                start = Offset(arcTopLeft.x, centre.y),
                end = Offset(arcTopLeft.x + arcSize.width, centre.y),
                strokeWidth = stroke,
            )
        }

        // Release button: filled once the Pokémon is caught or the ball is marked.
        drawCircle(
            color = Color.White,
            radius = radius * 0.32f,
            center = centre,
            style = if (filled || caught) Fill else Stroke(stroke),
        )
    }
}

/**
 * The illustrated, front-facing Poké Ball used on the splash screen. [opening]
 * lights the release button yellow, as if the ball is about to open.
 */
@Composable
fun FullPokeball(
    size: Dp = 100.dp,
    opening: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = this.size.minDimension * 0.04f
        val radius = this.size.minDimension / 2f - stroke / 2f
        val centre = Offset(this.size.width / 2f, this.size.height / 2f)
        val arcTopLeft = Offset(centre.x - radius, centre.y - radius)
        val arcSize = Size(radius * 2f, radius * 2f)

        // Upper shell (red) and lower shell (white).
        drawArc(BallRed, 180f, 180f, true, arcTopLeft, arcSize)
        drawArc(Color.White, 0f, 180f, true, arcTopLeft, arcSize)

        // Centre band, then the dark shell outline over it.
        val bandHeight = this.size.minDimension * 0.12f
        drawRect(
            color = BallShell,
            topLeft = Offset(arcTopLeft.x, centre.y - bandHeight / 2f),
            size = Size(arcSize.width, bandHeight),
        )
        drawCircle(BallShell, radius, centre, style = Stroke(stroke))

        // Release button.
        val outer = this.size.minDimension * 0.16f
        val inner = this.size.minDimension * 0.08f
        drawCircle(Color.White, outer, centre)
        drawCircle(BallShell, outer, centre, style = Stroke(stroke))
        drawCircle(if (opening) BallYellow else BallButton, inner, centre)
    }
}

/** Continuous spinning Poké Ball, used for full-screen loading states. */
@Composable
fun PokeballSpinner(
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "pokeball-spin")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "angle",
    )

    PokeballIcon(
        size = size,
        modifier = modifier.graphicsLayer { rotationZ = angle },
    )
}
