package com.pokemon.explorer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pokemon.explorer.data.PokeApi
import com.pokemon.explorer.data.PokemonSummary
import com.pokemon.explorer.data.typeColor
import com.pokemon.explorer.ui.theme.DisplayFont
import com.pokemon.explorer.ui.theme.MonoFont
import com.pokemon.explorer.ui.theme.PokeRed
import com.pokemon.explorer.ui.theme.Radii
import com.pokemon.explorer.ui.theme.TextHigh
import com.pokemon.explorer.ui.theme.TextLow

/** `#025` style Pokédex number. */
fun pokedexNumber(id: Int): String = "#" + id.toString().padStart(3, '0')

/**
 * A trading-card style Pokémon tile: artwork on a type-coloured backing, Pokédex
 * number, name, type badges, plus favourite and caught indicators.
 */
@Composable
fun PokemonCard(
    pokemon: PokemonSummary,
    isFavourite: Boolean,
    isCaught: Boolean,
    onToggleFavourite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val primary = pokemon.types.firstOrNull() ?: "normal"
    val secondary = pokemon.types.getOrNull(1) ?: primary
    val color = typeColor(primary)
    val color2 = typeColor(secondary)

    Box(
        modifier = modifier
            .clip(Radii.card)
            .background(
                Brush.linearGradient(
                    listOf(color.copy(alpha = 0.16f), color2.copy(alpha = 0.09f)),
                ),
            )
            .border(1.dp, color.copy(alpha = 0.21f), Radii.card)
            .holographicSheen()
            .clickable(onClick = onClick),
    ) {
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = pokedexNumber(pokemon.id),
                color = TextLow,
                fontSize = 10.sp,
                fontFamily = MonoFont,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = if (compact) 8.dp else 12.dp,
                ),
            )

            AsyncImage(
                model = PokeApi.artworkUrl(pokemon.id),
                contentDescription = pokemon.name,
                contentScale = ContentScale.Fit,
                placeholder = ColorPainter(color.copy(alpha = 0.10f)),
                error = ColorPainter(color.copy(alpha = 0.14f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(
                        start = if (compact) 12.dp else 16.dp,
                        end = if (compact) 12.dp else 16.dp,
                        top = 4.dp,
                        bottom = 8.dp,
                    ),
            )

            Text(
                text = pokemon.name,
                color = TextHigh,
                fontSize = 14.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 12.dp),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 12.dp),
            ) {
                pokemon.types.forEach { type ->
                    TypeBadge(type = type, size = BadgeSize.Small)
                }
            }
        }

        // Caught marker, top-left.
        if (isCaught) {
            PokeballIcon(
                size = 14.dp,
                caught = true,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
            )
        }

        // Favourite toggle, top-right. The hit area is a full 48dp square so it meets
        // the brief's minimum touch target; only the heart is visible.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(MinTouchTarget)
                .clip(Radii.badge)
                .clickable(onClick = onToggleFavourite),
            contentAlignment = Alignment.Center,
        ) {
            HeartIcon(
                iconSize = 16.dp,
                filled = isFavourite,
                fillColor = PokeRed,
                outlineColor = Color.White.copy(alpha = 0.40f),
            )
        }
    }
}
