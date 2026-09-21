package com.pokemon.explorer.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemon.explorer.data.STAT_LABELS
import com.pokemon.explorer.ui.theme.DisplayFont
import com.pokemon.explorer.ui.theme.MonoFont
import com.pokemon.explorer.ui.theme.Surface10
import com.pokemon.explorer.ui.theme.TextHigh
import com.pokemon.explorer.ui.theme.TextLow

/** Higher base stats read hotter, so the bars feel like HP counters. */
fun statColor(value: Int): Color = when {
    value >= 100 -> Color(0xFF7AC74C)
    value >= 70 -> Color(0xFFF7D02C)
    value >= 45 -> Color(0xFFEE8130)
    else -> Color(0xFFEE1515)
}

/**
 * One stat row: label, animated fill bar, numeric value.
 *
 * The bar always animates up from empty on entry, and [delay] staggers the six rows
 * so they cascade rather than snapping in together.
 */
@Composable
fun StatBar(
    name: String,
    value: Int,
    modifier: Modifier = Modifier,
    max: Int = 255,
    delay: Int = 0,
) {
    val target = (value.toFloat() / max).coerceIn(0f, 1f)

    // Start at zero, then animate to the real value so the bar visibly fills.
    var progress by remember { mutableStateOf(0f) }
    LaunchedEffect(target) { progress = target }

    val animated by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 700,
            delayMillis = 80 + delay,
            easing = FastOutSlowInEasing,
        ),
        label = "stat-fill",
    )

    val color = statColor(value)
    val label = STAT_LABELS[name] ?: name.uppercase()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = label,
            color = TextLow,
            fontSize = 12.sp,
            fontFamily = MonoFont,
            maxLines = 1,
            modifier = Modifier.width(52.dp),
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(Surface10),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animated)
                    .height(10.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(color),
            )
        }

        Text(
            text = value.toString(),
            color = TextHigh,
            fontSize = 14.sp,
            fontFamily = MonoFont,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.width(32.dp),
        )
    }
}

/** Small uppercase section label used above Abilities, Base Stats and Moves. */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        color = TextLow,
        fontSize = 12.sp,
        fontFamily = DisplayFont,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        modifier = modifier,
    )
}
