package com.pokemon.explorer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Hand-drawn icons.
 *
 * The web app used bespoke inline SVGs rather than an icon font, so these are the
 * same shapes redrawn on a Compose [Canvas]. Keeping them here means the app needs
 * no icon dependency and every glyph scales crisply.
 *
 * All shapes are authored in a 24x24 coordinate space, then scaled to [iconSize].
 */
@Composable
private fun ScaledIcon(
    iconSize: Dp,
    modifier: Modifier = Modifier,
    content: DrawScope.(scale: Float) -> Unit,
) {
    Canvas(modifier = modifier.size(iconSize)) {
        content(size.minDimension / 24f)
    }
}

@Composable
fun SearchIcon(
    iconSize: Dp = 16.dp,
    color: Color = Color.White,
    strokeWidth: Float = 2.5f,
    modifier: Modifier = Modifier,
) = ScaledIcon(iconSize, modifier) { s ->
    val stroke = strokeWidth * s
    drawCircle(color, 8f * s, Offset(11f * s, 11f * s), style = Stroke(stroke))
    drawLine(color, Offset(21f * s, 21f * s), Offset(16.65f * s, 16.65f * s), stroke, StrokeCap.Round)
}

@Composable
fun SettingsIcon(
    iconSize: Dp = 18.dp,
    color: Color = Color.White,
    modifier: Modifier = Modifier,
) = ScaledIcon(iconSize, modifier) { s ->
    val centre = Offset(12f * s, 12f * s)
    // Eight teeth radiating from the ring.
    repeat(8) { index ->
        val angle = Math.toRadians((index * 45).toDouble())
        val direction = Offset(cos(angle).toFloat(), sin(angle).toFloat())
        drawLine(
            color = color,
            start = centre + direction * (7.4f * s),
            end = centre + direction * (10.4f * s),
            strokeWidth = 2.4f * s,
            cap = StrokeCap.Round,
        )
    }
    drawCircle(color, 7f * s, centre, style = Stroke(2f * s))
    drawCircle(color, 3f * s, centre, style = Stroke(2f * s))
}

@Composable
fun BackArrowIcon(
    iconSize: Dp = 18.dp,
    color: Color = Color.White,
    modifier: Modifier = Modifier,
) = ScaledIcon(iconSize, modifier) { s ->
    val stroke = 2.5f * s
    drawLine(color, Offset(19f * s, 12f * s), Offset(5f * s, 12f * s), stroke, StrokeCap.Round)
    drawPath(
        Path().apply {
            moveTo(12f * s, 5f * s)
            lineTo(5f * s, 12f * s)
            lineTo(12f * s, 19f * s)
        },
        color,
        style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
}

@Composable
fun SwapIcon(
    iconSize: Dp = 18.dp,
    color: Color = Color.White,
    modifier: Modifier = Modifier,
) = ScaledIcon(iconSize, modifier) { s ->
    val stroke = 2f * s
    val style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
    drawPath(
        Path().apply {
            moveTo(18f * s, 4f * s); lineTo(22f * s, 8f * s); lineTo(18f * s, 12f * s)
        },
        color,
        style = style,
    )
    drawPath(
        Path().apply {
            moveTo(6f * s, 20f * s); lineTo(2f * s, 16f * s); lineTo(6f * s, 12f * s)
        },
        color,
        style = style,
    )
    drawLine(color, Offset(14f * s, 8f * s), Offset(2f * s, 8f * s), stroke, StrokeCap.Round)
    drawLine(color, Offset(22f * s, 16f * s), Offset(10f * s, 16f * s), stroke, StrokeCap.Round)
}

@Composable
fun ChevronRightIcon(
    iconSize: Dp = 16.dp,
    color: Color = Color.White,
    modifier: Modifier = Modifier,
) = ScaledIcon(iconSize, modifier) { s ->
    drawPath(
        Path().apply {
            moveTo(9f * s, 18f * s); lineTo(15f * s, 12f * s); lineTo(9f * s, 6f * s)
        },
        color,
        style = Stroke(2.5f * s, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
}

@Composable
fun CloseIcon(
    iconSize: Dp = 14.dp,
    color: Color = Color.White,
    modifier: Modifier = Modifier,
) = ScaledIcon(iconSize, modifier) { s ->
    val stroke = 2.5f * s
    drawLine(color, Offset(6f * s, 6f * s), Offset(18f * s, 18f * s), stroke, StrokeCap.Round)
    drawLine(color, Offset(18f * s, 6f * s), Offset(6f * s, 18f * s), stroke, StrokeCap.Round)
}

@Composable
fun UserIcon(
    iconSize: Dp = 16.dp,
    color: Color = Color.White,
    modifier: Modifier = Modifier,
) = ScaledIcon(iconSize, modifier) { s ->
    drawCircle(color, 4f * s, Offset(12f * s, 8f * s), style = Stroke(2.5f * s))
    drawPath(
        Path().apply {
            moveTo(4f * s, 21f * s)
            cubicTo(4f * s, 17f * s, 8f * s, 14f * s, 12f * s, 14f * s)
            cubicTo(16f * s, 14f * s, 20f * s, 17f * s, 20f * s, 21f * s)
        },
        color,
        style = Stroke(2.5f * s, cap = StrokeCap.Round),
    )
}

@Composable
fun EyeIcon(
    visible: Boolean,
    iconSize: Dp = 18.dp,
    color: Color = Color.White,
    modifier: Modifier = Modifier,
) = ScaledIcon(iconSize, modifier) { s ->
    val stroke = 2f * s
    val style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
    
    // Eye shape
    drawPath(
        Path().apply {
            moveTo(2f * s, 12f * s)
            quadraticTo(12f * s, 4f * s, 22f * s, 12f * s)
            quadraticTo(12f * s, 20f * s, 2f * s, 12f * s)
        },
        color = color,
        style = style
    )
    
    // Pupil
    drawCircle(color, 3f * s, Offset(12f * s, 12f * s), style = if (visible) Fill else Stroke(stroke))

    if (!visible) {
        // Slash for hidden state
        drawLine(
            color = color,
            start = Offset(5f * s, 5f * s),
            end = Offset(19f * s, 19f * s),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun LockIcon(
    iconSize: Dp = 16.dp,
    color: Color = Color.White,
    modifier: Modifier = Modifier,
) = ScaledIcon(iconSize, modifier) { s ->
    drawRoundRect(
        color = color,
        topLeft = Offset(5f * s, 11f * s),
        size = Size(14f * s, 10f * s),
        cornerRadius = CornerRadius(2f * s),
        style = Stroke(2.5f * s),
    )
    drawPath(
        Path().apply {
            moveTo(8f * s, 11f * s)
            lineTo(8f * s, 7f * s)
            cubicTo(8f * s, 4.8f * s, 9.8f * s, 3f * s, 12f * s, 3f * s)
            cubicTo(14.2f * s, 3f * s, 16f * s, 4.8f * s, 16f * s, 7f * s)
            lineTo(16f * s, 11f * s)
        },
        color,
        style = Stroke(2.5f * s, cap = StrokeCap.Round),
    )
}

/** A heart, matching the web app's favourite glyph. */
private fun heartPath(s: Float): Path = Path().apply {
    moveTo(12f * s, 21f * s)
    cubicTo(2f * s, 14f * s, 1f * s, 6f * s, 6f * s, 3.6f * s)
    cubicTo(9f * s, 2.4f * s, 11.3f * s, 4f * s, 12f * s, 6.4f * s)
    cubicTo(12.7f * s, 4f * s, 15f * s, 2.4f * s, 18f * s, 3.6f * s)
    cubicTo(23f * s, 6f * s, 22f * s, 14f * s, 12f * s, 21f * s)
    close()
}

/**
 * Heart glyph. When [filled] is false the outline is drawn in [outlineColor]; when
 * true the shape is filled with [fillColor].
 */
@Composable
fun HeartIcon(
    iconSize: Dp = 18.dp,
    filled: Boolean,
    fillColor: Color,
    outlineColor: Color = fillColor,
    modifier: Modifier = Modifier,
) = ScaledIcon(iconSize, modifier) { s ->
    val path = heartPath(s)
    if (filled) {
        drawPath(path, fillColor, style = Fill)
    } else {
        drawPath(
            path,
            outlineColor,
            style = Stroke(2f * s, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

// ------------------------------------------------------------ bottom nav icons

@Composable
fun ExploreNavIcon(active: Boolean, iconSize: Dp = 24.dp, modifier: Modifier = Modifier) {
    val tint = if (active) Color(0xFFEE1515) else Color.White.copy(alpha = 0.45f)
    ScaledIcon(iconSize, modifier) { s ->
        val centre = Offset(12f * s, 12f * s)
        if (active) drawCircle(tint.copy(alpha = 0.15f), 9.5f * s, centre)
        drawCircle(tint, 9.5f * s, centre, style = Stroke(2f * s))
        drawLine(tint, Offset(2.5f * s, 12f * s), Offset(21.5f * s, 12f * s), 2f * s)
        drawCircle(tint, 3f * s, centre)
        if (active) drawCircle(Color.White, 3f * s, centre, style = Stroke(1f * s))
    }
}

@Composable
fun CollectionNavIcon(active: Boolean, iconSize: Dp = 24.dp, modifier: Modifier = Modifier) {
    val tint = if (active) Color(0xFFFFCB05) else Color.White.copy(alpha = 0.45f)
    ScaledIcon(iconSize, modifier) { s ->
        val topLeft = Offset(3f * s, 4f * s)
        val boxSize = Size(18f * s, 16f * s)
        val corners = CornerRadius(2f * s)
        if (active) {
            drawRoundRect(tint.copy(alpha = 0.12f), topLeft, boxSize, corners)
        }
        drawRoundRect(tint, topLeft, boxSize, corners, style = Stroke(2f * s))
        drawLine(tint, Offset(7f * s, 9f * s), Offset(17f * s, 9f * s), 2f * s, StrokeCap.Round)
        drawLine(tint, Offset(7f * s, 13f * s), Offset(14f * s, 13f * s), 2f * s, StrokeCap.Round)
        drawCircle(tint, 2f * s, Offset(17f * s, 16f * s))
    }
}

@Composable
fun CompareNavIcon(active: Boolean, iconSize: Dp = 24.dp, modifier: Modifier = Modifier) {
    val tint = if (active) Color(0xFF6390F0) else Color.White.copy(alpha = 0.45f)
    ScaledIcon(iconSize, modifier) { s ->
        val boxSize = Size(8f * s, 14f * s)
        val corners = CornerRadius(2f * s)
        listOf(2f, 14f).forEach { x ->
            val topLeft = Offset(x * s, 5f * s)
            if (active) {
                drawRoundRect(tint.copy(alpha = 0.15f), topLeft, boxSize, corners)
            }
            drawRoundRect(tint, topLeft, boxSize, corners, style = Stroke(2f * s))
        }
        drawLine(tint, Offset(11f * s, 12f * s), Offset(13f * s, 12f * s), 2.5f * s)
    }
}

@Composable
fun GuideNavIcon(active: Boolean, iconSize: Dp = 24.dp, modifier: Modifier = Modifier) {
    val tint = if (active) Color(0xFF7AC74C) else Color.White.copy(alpha = 0.45f)
    ScaledIcon(iconSize, modifier) { s ->
        val centre = Offset(12f * s, 12f * s)
        if (active) drawCircle(tint.copy(alpha = 0.10f), 9.5f * s, centre)
        drawCircle(tint, 9.5f * s, centre, style = Stroke(2f * s))
        val cross = tint.copy(alpha = 0.4f)
        drawLine(cross, Offset(12f * s, 2.5f * s), Offset(12f * s, 21.5f * s), 1f * s)
        drawLine(cross, Offset(2.5f * s, 12f * s), Offset(21.5f * s, 12f * s), 1f * s)
        drawPath(
            Path().apply {
                moveTo(12f * s, 5f * s); lineTo(16f * s, 8f * s)
                lineTo(12f * s, 11f * s); lineTo(8f * s, 8f * s); close()
            },
            tint,
            style = Fill,
        )
        drawCircle(tint, 1.5f * s, Offset(12f * s, 17f * s))
    }
}
