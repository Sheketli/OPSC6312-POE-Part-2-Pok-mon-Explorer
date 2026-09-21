package com.pokemon.explorer.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemon.explorer.data.PokeApi
import com.pokemon.explorer.state.AppState
import com.pokemon.explorer.state.Screen
import com.pokemon.explorer.ui.components.CountBadge
import com.pokemon.explorer.ui.components.EmptyState
import com.pokemon.explorer.ui.components.HeartIcon
import com.pokemon.explorer.ui.components.PokeballIcon
import com.pokemon.explorer.ui.components.PokemonCard
import com.pokemon.explorer.ui.components.dataOrNull
import com.pokemon.explorer.ui.components.pokedexNumber
import com.pokemon.explorer.ui.components.rememberLoadState
import com.pokemon.explorer.ui.theme.DisplayFont
import com.pokemon.explorer.ui.theme.MonoFont
import com.pokemon.explorer.ui.theme.PokeRed
import com.pokemon.explorer.ui.theme.PokeYellow
import com.pokemon.explorer.ui.theme.Radii
import com.pokemon.explorer.ui.theme.Surface05
import com.pokemon.explorer.ui.theme.Surface08
import com.pokemon.explorer.ui.theme.TextHigh
import com.pokemon.explorer.ui.theme.TextLow

/** Total species in the National Pokédex, used for the completion percentage. */
private const val TOTAL_POKEMON = 1025

private enum class CollectionTab(val label: String) {
    Favourites("Favourites"),
    Caught("Caught"),
}

/**
 * Gamified collection dashboard: a Pokédex completion ring over Favourites / Caught
 * grids of trading cards.
 */
@Composable
fun CollectionScreen(
    state: AppState,
    modifier: Modifier = Modifier,
) {
    var tab by remember { mutableStateOf(CollectionTab.Favourites) }

    // Sort so the grid order is stable between recompositions.
    val favouriteIds = state.favourites.sorted()
    val caughtIds = state.caught.sorted()
    val ids = if (tab == CollectionTab.Favourites) favouriteIds else caughtIds

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = buildAnnotatedString {
                    append("My ")
                    withStyle(SpanStyle(color = PokeYellow)) { append("Collection") }
                },
                color = TextHigh,
                fontSize = 20.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            ProgressCard(caught = caughtIds.size, total = TOTAL_POKEMON)
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            CollectionTabs(
                selected = tab,
                favouriteCount = favouriteIds.size,
                caughtCount = caughtIds.size,
                onSelect = { tab = it },
            )
        }

        if (ids.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                if (tab == CollectionTab.Favourites) {
                    EmptyState(
                        title = "No Favourites Yet",
                        message = "Tap the heart icon on any Pokémon to add it here.",
                        glyph = {
                            HeartIcon(
                                iconSize = 36.dp,
                                filled = false,
                                fillColor = TextLow,
                                outlineColor = Color.White.copy(alpha = 0.30f),
                            )
                        },
                    )
                } else {
                    EmptyState(
                        title = "No Pokémon Caught",
                        message = "Mark Pokémon as caught from their detail page.",
                        glyph = { PokeballIcon(size = 36.dp) },
                    )
                }
            }
        } else {
            items(ids, key = { it }) { id ->
                CollectionEntry(
                    id = id,
                    isFavourite = state.favourites.contains(id),
                    isCaught = state.caught.contains(id),
                    onClick = { state.navigate(Screen.DETAIL, id = id) },
                    onToggleFavourite = { state.toggleFavourite(id) },
                )
            }
        }
    }
}

/**
 * Pokédex progress card with a circular completion ring.
 *
 * The ring is drawn with two arcs: a faint full-circle track and the progress arc,
 * which sweeps clockwise from the top.
 */
@Composable
private fun ProgressCard(caught: Int, total: Int) {
    val percentage = (caught.toFloat() / total).coerceIn(0f, 1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.hero)
            .background(
                Brush.linearGradient(
                    listOf(PokeRed.copy(alpha = 0.18f), PokeYellow.copy(alpha = 0.08f)),
                ),
            )
            .border(1.dp, PokeRed.copy(alpha = 0.25f), Radii.hero)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(112.dp)) {
                val strokeWidth = 10.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2f
                val centre = Offset(size.width / 2f, size.height / 2f)
                val arcSize = Size(radius * 2f, radius * 2f)
                val arcTopLeft = Offset(centre.x - radius, centre.y - radius)

                // Track.
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = radius,
                    center = centre,
                    style = Stroke(strokeWidth),
                )

                // Progress: starts at 12 o'clock, sweeps clockwise.
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(PokeRed, PokeYellow),
                        start = Offset(arcTopLeft.x, centre.y),
                        end = Offset(arcTopLeft.x + arcSize.width, centre.y),
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * percentage,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )

                // Faint crosshair and hub, echoing a Poké Ball.
                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = Offset(centre.x, centre.y - radius),
                    end = Offset(centre.x, centre.y + radius),
                    strokeWidth = 1.dp.toPx(),
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = Offset(centre.x - radius, centre.y),
                    end = Offset(centre.x + radius, centre.y),
                    strokeWidth = 1.dp.toPx(),
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = 10.dp.toPx(),
                    center = centre,
                )
            }

            Text(
                text = "${(percentage * 100).toInt()}%",
                color = TextHigh,
                fontSize = 18.sp,
                fontFamily = MonoFont,
                fontWeight = FontWeight.Black,
            )
        }

        Column(Modifier.weight(1f)) {
            Text(
                text = "Pokédex Progress",
                color = TextHigh,
                fontSize = 16.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = "Your Pokémon collection",
                color = TextLow,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp),
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(top = 12.dp),
            ) {
                Text(
                    text = caught.toString(),
                    color = TextHigh,
                    fontSize = 24.sp,
                    fontFamily = MonoFont,
                    fontWeight = FontWeight.Black,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "/ $total",
                    color = TextLow,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 2.dp),
                )
            }
            Text(
                text = "Pokémon caught",
                color = TextLow,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun CollectionTabs(
    selected: CollectionTab,
    favouriteCount: Int,
    caughtCount: Int,
    onSelect: (CollectionTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.card)
            .background(Surface05)
            .border(1.dp, Surface08, Radii.card)
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        CollectionTab.entries.forEach { tab ->
            val active = tab == selected
            val count = if (tab == CollectionTab.Favourites) favouriteCount else caughtCount

            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        when {
                            !active -> Color.Transparent
                            tab == CollectionTab.Favourites -> PokeRed.copy(alpha = 0.20f)
                            else -> PokeYellow.copy(alpha = 0.15f)
                        },
                    )
                    .clickable { onSelect(tab) }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (tab) {
                    CollectionTab.Favourites -> HeartIcon(
                        iconSize = 14.dp,
                        filled = active,
                        fillColor = PokeRed,
                        outlineColor = Color.White.copy(alpha = 0.45f),
                    )
                    CollectionTab.Caught -> PokeballIcon(size = 14.dp, caught = active)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = tab.label,
                    color = if (active) TextHigh else Color.White.copy(alpha = 0.35f),
                    fontSize = 14.sp,
                    fontFamily = DisplayFont,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(8.dp))
                CountBadge(count = count, active = active)
            }
        }
    }
}

/**
 * One collection tile. Only the id is stored in the collection, so the name and
 * types are fetched; a placeholder holds the card's footprint until they arrive.
 */
@Composable
private fun CollectionEntry(
    id: Int,
    isFavourite: Boolean,
    isCaught: Boolean,
    onClick: () -> Unit,
    onToggleFavourite: () -> Unit,
) {
    val summaryState by rememberLoadState(id) { PokeApi.summary(id) }
    val summary = summaryState.dataOrNull()

    if (summary == null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Radii.card)
                .background(Surface05)
                .padding(12.dp),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface05),
            )
            Text(
                text = pokedexNumber(id),
                color = TextLow,
                fontSize = 10.sp,
                fontFamily = MonoFont,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        return
    }

    PokemonCard(
        pokemon = summary,
        isFavourite = isFavourite,
        isCaught = isCaught,
        onToggleFavourite = onToggleFavourite,
        onClick = onClick,
        compact = true,
    )
}
