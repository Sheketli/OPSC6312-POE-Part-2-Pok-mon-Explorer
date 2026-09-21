package com.pokemon.explorer.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.pokemon.explorer.ui.theme.PokeYellow

/**
 * The holographic trading-card sheen applied to grid cards and collection entries.
 *
 * Drawn over the card's content, which is why the effect sits on top of the artwork
 * exactly as the web app's `::after` overlay did. It is deliberately very subtle so
 * text stays readable.
 */
fun Modifier.holographicSheen(): Modifier = this.drawWithContent {
    drawContent()
    drawRect(
        brush = Brush.linearGradient(
            0.0f to Color.Transparent,
            0.3f to Color.White.copy(alpha = 0.06f),
            0.5f to PokeYellow.copy(alpha = 0.12f),
            0.7f to Color.White.copy(alpha = 0.06f),
            1.0f to Color.Transparent,
        ),
    )
}
