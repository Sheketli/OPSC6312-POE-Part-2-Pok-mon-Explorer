package com.pokemon.explorer.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pokemon.explorer.data.PokeApi
import com.pokemon.explorer.data.PokemonDetail
import com.pokemon.explorer.data.PokemonSummary
import com.pokemon.explorer.data.typeColor
import com.pokemon.explorer.state.AppState
import com.pokemon.explorer.state.Screen
import com.pokemon.explorer.ui.components.CircleIconButton
import com.pokemon.explorer.ui.components.GameButton
import com.pokemon.explorer.ui.components.GameButtonStyle
import com.pokemon.explorer.ui.components.PokeballIcon
import com.pokemon.explorer.ui.components.PokemonCard
import com.pokemon.explorer.ui.components.SearchIcon
import com.pokemon.explorer.ui.components.SettingsIcon
import com.pokemon.explorer.ui.components.SkeletonCard
import com.pokemon.explorer.ui.components.TypeBadge
import com.pokemon.explorer.ui.components.dataOrNull
import com.pokemon.explorer.ui.components.isLoading
import com.pokemon.explorer.ui.components.pokedexNumber
import com.pokemon.explorer.ui.components.prettyName
import com.pokemon.explorer.ui.components.rememberLoadState
import com.pokemon.explorer.ui.theme.Border10
import com.pokemon.explorer.ui.theme.DisplayFont
import com.pokemon.explorer.ui.theme.MonoFont
import com.pokemon.explorer.ui.theme.PokeRed
import com.pokemon.explorer.ui.theme.PokeYellow
import com.pokemon.explorer.ui.theme.Radii
import com.pokemon.explorer.ui.theme.Surface07
import com.pokemon.explorer.ui.theme.TextHigh
import com.pokemon.explorer.ui.theme.TextLow
import com.pokemon.explorer.ui.theme.typeGradient
import kotlin.random.Random

private const val FEATURED_ID = 25 // Pikachu
private const val LIST_PAGE_SIZE = 20

private val TYPE_FILTERS = listOf(
    "All", "fire", "water", "grass", "electric", "psychic", "dragon", "ghost", "fairy",
)

/**
 * Explore: a featured hero Pokémon, a Random Discovery action, type filter chips and
 * a two-column trading-card grid of the first page of the National Pokédex.
 *
 * The screen is a single [LazyVerticalGrid]; headers and the hero span both columns,
 * so everything scrolls in one container instead of nesting a grid inside a list.
 */
@Composable
fun HomeScreen(
    state: AppState,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
) {
    var activeFilter by remember { mutableStateOf("All") }

    val featuredState by rememberLoadState { PokeApi.detail(FEATURED_ID) }
    val listState by rememberLoadState { PokeApi.list(LIST_PAGE_SIZE, 0) }

    val items: List<PokemonSummary> = listState.dataOrNull().orEmpty()
    val filtered = if (activeFilter == "All") {
        items
    } else {
        items.filter { it.types.contains(activeFilter) }
    }
    val listLoading = listState.isLoading

    val columns = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> GridCells.Fixed(2)
        WindowWidthSizeClass.Medium -> GridCells.Fixed(3)
        WindowWidthSizeClass.Expanded -> GridCells.Adaptive(minSize = 180.dp)
        else -> GridCells.Fixed(2)
    }

    LazyVerticalGrid(
        columns = columns,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            HomeAppBar(state = state, onOpenSettings = { state.navigate(Screen.SETTINGS) })
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            SearchBarButton(onClick = { state.navigate(Screen.SEARCH) })
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            FeaturedCard(
                featured = featuredState.dataOrNull(),
                loading = featuredState.isLoading,
                onViewDetails = { id -> state.navigate(Screen.DETAIL, id = id) },
                onRandom = {
                    // Any Pokémon from the first 898, as in the web app.
                    state.navigate(Screen.DETAIL, id = Random.nextInt(1, 899))
                },
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            FilterChips(active = activeFilter, onSelect = { activeFilter = it })
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (activeFilter == "All") {
                        "All Pokémon"
                    } else {
                        prettyName(activeFilter) + " Type"
                    },
                    color = TextHigh,
                    fontSize = 16.sp,
                    fontFamily = DisplayFont,
                    fontWeight = FontWeight.Black,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (listLoading) "..." else filtered.size.toString(),
                    color = TextLow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "See all →",
                    color = TextLow,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { state.navigate(Screen.SEARCH) },
                )
            }
        }

        if (listLoading) {
            items(6) { SkeletonCard() }
        } else {
            items(filtered, key = { it.id }) { pokemon ->
                PokemonCard(
                    pokemon = pokemon,
                    isFavourite = state.favourites.contains(pokemon.id),
                    isCaught = state.caught.contains(pokemon.id),
                    onToggleFavourite = { state.toggleFavourite(pokemon.id) },
                    onClick = { state.navigate(Screen.DETAIL, id = pokemon.id) },
                )
            }
        }
    }
}

@Composable
private fun HomeAppBar(state: AppState, onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "WELCOME BACK, ${state.currentUser?.username?.uppercase() ?: "EXPLORER"}",
                color = TextLow,
                fontSize = 10.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Text(
                text = buildAnnotatedString {
                    append("Pokémon ")
                    withStyle(SpanStyle(color = PokeRed)) { append("Explorer") }
                },
                color = TextHigh,
                fontSize = 20.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Black,
            )
        }
        CircleIconButton(onClick = onOpenSettings) {
            SettingsIcon(iconSize = 18.dp)
        }
    }
}

@Composable
private fun SearchBarButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.card)
            .background(Surface07)
            .border(1.dp, Border10, Radii.card)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SearchIcon(iconSize = 16.dp, color = Color.White.copy(alpha = 0.40f))
        Text(
            text = "Search Pokémon by name or #",
            color = Color.White.copy(alpha = 0.35f),
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
        PokeballIcon(size = 20.dp)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FeaturedCard(
    featured: PokemonDetail?,
    loading: Boolean,
    onViewDetails: (Int) -> Unit,
    onRandom: () -> Unit,
) {
    if (loading) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(176.dp)
                .clip(Radii.hero)
                .background(Surface07),
        )
        return
    }

    val detail = featured ?: return
    val primary = detail.types.firstOrNull() ?: "normal"
    val secondary = detail.types.getOrNull(1) ?: primary
    val color = typeColor(primary)
    val color2 = typeColor(secondary)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.hero)
            .background(typeGradient(color, color2))
            .border(1.dp, color.copy(alpha = 0.25f), Radii.hero),
    ) {
        // Decorative circles bleeding out of the corner.
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = 32.dp, y = (-32).dp)
                .size(160.dp)
                .clip(Radii.badge)
                .background(color.copy(alpha = 0.20f)),
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-32).dp)
                .size(96.dp)
                .clip(Radii.badge)
                .background(color2.copy(alpha = 0.10f)),
        )

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "${pokedexNumber(detail.id)} · Featured",
                    color = Color.White.copy(alpha = 0.50f),
                    fontSize = 12.sp,
                    fontFamily = MonoFont,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = prettyName(detail.name),
                    color = TextHigh,
                    fontSize = 24.sp,
                    fontFamily = DisplayFont,
                    fontWeight = FontWeight.Black,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    detail.types.forEach { TypeBadge(type = it) }
                }
                detail.flavorText?.let { text ->
                    Text(
                        text = text,
                        color = Color.White.copy(alpha = 0.50f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 12.dp),
                ) {
                    GameButton(
                        style = GameButtonStyle.Red,
                        onClick = { onViewDetails(detail.id) },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = "View Details",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontFamily = DisplayFont,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    GameButton(
                        style = GameButtonStyle.Yellow,
                        onClick = onRandom,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        PokeballIcon(size = 16.dp)
                        Text(
                            text = "Random",
                            color = Color(0xFF1A1200),
                            fontSize = 14.sp,
                            fontFamily = DisplayFont,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            FloatingArtwork(
                url = PokeApi.artworkUrl(detail.id),
                contentDescription = detail.name,
                placeholderColor = color.copy(alpha = 0.15f),
                modifier = Modifier.width(80.dp).height(80.dp),
            )
        }
    }
}

/** Artwork with the app's signature slow floating motion. */
@Composable
fun FloatingArtwork(
    url: String,
    contentDescription: String?,
    placeholderColor: Color,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "float")
    val offsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "float-offset",
    )

    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        placeholder = ColorPainter(placeholderColor),
        error = ColorPainter(placeholderColor),
        modifier = modifier.graphicsLayer { translationY = offsetY },
    )
}

@Composable
private fun FilterChips(
    active: String,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TYPE_FILTERS.forEach { filter ->
            val selected = filter == active
            val color = if (filter == "All") PokeYellow else typeColor(filter)
            val labelColor = when {
                !selected -> Color.White.copy(alpha = 0.50f)
                filter == "All" -> Color(0xFF1A1200)
                else -> Color.White
            }

            Text(
                text = if (filter == "All") "✦ All" else filter,
                color = labelColor,
                fontSize = 12.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(if (selected) color else Surface07)
                    .border(
                        width = 1.dp,
                        color = if (selected) color else Color.Transparent,
                        shape = RoundedCornerShape(percent = 50),
                    )
                    .clickable { onSelect(filter) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
    }
}
