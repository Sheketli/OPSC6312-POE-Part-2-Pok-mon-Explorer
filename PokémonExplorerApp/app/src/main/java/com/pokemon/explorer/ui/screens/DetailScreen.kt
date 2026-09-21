package com.pokemon.explorer.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemon.explorer.data.PokeApi
import com.pokemon.explorer.data.PokemonDetail
import com.pokemon.explorer.data.typeColor
import com.pokemon.explorer.state.AppState
import com.pokemon.explorer.state.Screen
import com.pokemon.explorer.ui.components.BackArrowIcon
import com.pokemon.explorer.ui.components.BadgeSize
import com.pokemon.explorer.ui.components.CircleIconButton
import com.pokemon.explorer.ui.components.GameButton
import com.pokemon.explorer.ui.components.GameButtonStyle
import com.pokemon.explorer.ui.components.HeartIcon
import com.pokemon.explorer.ui.components.InfoCard
import com.pokemon.explorer.ui.components.InfoChip
import com.pokemon.explorer.ui.components.LoadState
import com.pokemon.explorer.ui.components.PokeballIcon
import com.pokemon.explorer.ui.components.PokeballSpinner
import com.pokemon.explorer.ui.components.SectionLabel
import com.pokemon.explorer.ui.components.SkeletonDetail
import com.pokemon.explorer.ui.components.StatBar
import com.pokemon.explorer.ui.components.SwapIcon
import com.pokemon.explorer.ui.components.TypeBadge
import com.pokemon.explorer.ui.components.dataOrNull
import com.pokemon.explorer.ui.components.isLoading
import com.pokemon.explorer.ui.components.pokedexNumber
import com.pokemon.explorer.ui.components.prettyName
import com.pokemon.explorer.ui.components.rememberLoadState
import com.pokemon.explorer.ui.theme.Border08
import com.pokemon.explorer.ui.theme.Border12
import com.pokemon.explorer.ui.theme.DisplayFont
import com.pokemon.explorer.ui.theme.MonoFont
import com.pokemon.explorer.ui.theme.PokeRed
import com.pokemon.explorer.ui.theme.PokeYellow
import com.pokemon.explorer.ui.theme.Radii
import com.pokemon.explorer.ui.theme.Surface04
import com.pokemon.explorer.ui.theme.TextHigh
import com.pokemon.explorer.ui.theme.TextLow
import com.pokemon.explorer.ui.theme.typeGradient
import kotlinx.coroutines.delay
import java.util.Locale

/** How long the favourite / catch buttons stay in their popped state. */
private const val PULSE_DURATION_MS = 400L

/** One metre/kilogram value, e.g. 0.4 m from a raw decimetre value of 4. */
private fun formatMetric(value: Int, unit: String): String =
    String.format(Locale.US, "%.1f %s", value / 10f, unit)

/**
 * The full Pokédex entry: hero artwork on a type-coloured panel with a Normal/Shiny
 * toggle, physical info, abilities, animated base stats, moves and the three
 * collection actions.
 */
@Composable
fun DetailScreen(
    state: AppState,
    id: Int?,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
) {
    val detailState by rememberLoadState(id) { id?.let { PokeApi.detail(it) } }
    val detail: PokemonDetail? = detailState.dataOrNull()

    var shiny by remember { mutableStateOf(false) }
    var favouritePulse by remember { mutableStateOf(false) }
    var catchPulse by remember { mutableStateOf(false) }

    // The pulse is a one-shot flourish, so reset it once it has played.
    LaunchedEffect(favouritePulse) {
        if (favouritePulse) {
            delay(PULSE_DURATION_MS)
            favouritePulse = false
        }
    }
    LaunchedEffect(catchPulse) {
        if (catchPulse) {
            delay(PULSE_DURATION_MS)
            catchPulse = false
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Top bar: back, favourite, compare.
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(onClick = { state.goBack() }) {
                BackArrowIcon(iconSize = 18.dp)
            }
            Spacer(Modifier.weight(1f))
            CircleIconButton(
                onClick = {
                    detail?.let {
                        state.toggleFavourite(it.id)
                        favouritePulse = true
                    }
                },
            ) {
                HeartIcon(
                    iconSize = 18.dp,
                    filled = detail != null && state.favourites.contains(detail.id),
                    fillColor = PokeRed,
                    outlineColor = Color.White.copy(alpha = 0.70f),
                    modifier = Modifier.scale(if (favouritePulse) 1.2f else 1f),
                )
            }
            Spacer(Modifier.width(8.dp))
            CircleIconButton(
                onClick = { detail?.let { state.navigate(Screen.COMPARE, idA = it.id) } },
            ) {
                SwapIcon(iconSize = 18.dp, color = Color.White.copy(alpha = 0.70f))
            }
        }

        when {
            detailState.isLoading -> SkeletonDetail()

            detailState is LoadState.Failure -> CentredMessage(
                title = "Pokémon info unavailable",
                message = "Tap back and try again",
                showSpinner = true,
            )

            detail == null -> CentredMessage(
                title = "No Pokémon was found.",
                message = "Try another search",
            )

            else -> DetailContent(
                detail = detail,
                shiny = shiny,
                onShinyChange = { shiny = it },
                isFavourite = state.favourites.contains(detail.id),
                isCaught = state.caught.contains(detail.id),
                favouritePulse = favouritePulse,
                catchPulse = catchPulse,
                windowSizeClass = windowSizeClass,
                onToggleFavourite = {
                    state.toggleFavourite(detail.id)
                    favouritePulse = true
                },
                onToggleCaught = {
                    state.toggleCaught(detail.id)
                    catchPulse = true
                },
                onCompare = { state.navigate(Screen.COMPARE, idA = detail.id) },
            )
        }
    }
}

@Composable
private fun CentredMessage(
    title: String,
    message: String,
    showSpinner: Boolean = false,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (showSpinner) PokeballSpinner(size = 48.dp)
        Text(
            text = title,
            color = TextHigh,
            fontSize = 18.sp,
            fontFamily = DisplayFont,
            fontWeight = FontWeight.Bold,
        )
        Text(text = message, color = TextLow, fontSize = 14.sp)
    }
}

@Composable
private fun DetailContent(
    detail: PokemonDetail,
    shiny: Boolean,
    onShinyChange: (Boolean) -> Unit,
    isFavourite: Boolean,
    isCaught: Boolean,
    favouritePulse: Boolean,
    catchPulse: Boolean,
    windowSizeClass: WindowSizeClass,
    onToggleFavourite: () -> Unit,
    onToggleCaught: () -> Unit,
    onCompare: () -> Unit,
) {
    val primary = detail.types.firstOrNull() ?: "normal"
    val secondary = detail.types.getOrNull(1) ?: primary
    val color = typeColor(primary)
    val color2 = typeColor(secondary)

    val isWide = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    if (isWide) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeroPanel(detail, shiny, onShinyChange, color, color2, isWide = true)
                
                Spacer(Modifier.height(24.dp))
                
                ActionButtons(
                    isFavourite, isCaught, favouritePulse, catchPulse,
                    onToggleFavourite, onToggleCaught, onCompare
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PhysicalInfo(detail)
                AbilitiesInfo(detail)
                BaseStatsInfo(detail)
                MovesInfo(detail)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
        ) {
            HeroPanel(detail, shiny, onShinyChange, color, color2, isWide = false)
            PhysicalInfo(detail)
            AbilitiesInfo(detail)
            BaseStatsInfo(detail)
            MovesInfo(detail)
            ActionButtons(
                isFavourite, isCaught, favouritePulse, catchPulse,
                onToggleFavourite, onToggleCaught, onCompare
            )
        }
    }
}

@Composable
private fun HeroPanel(
    detail: PokemonDetail,
    shiny: Boolean,
    onShinyChange: (Boolean) -> Unit,
    color: Color,
    color2: Color,
    isWide: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (isWide) 0.dp else 16.dp)
            .clip(Radii.hero)
            .background(typeGradient(color, color2))
            .border(1.dp, color.copy(alpha = 0.25f), Radii.hero),
    ) {
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = 48.dp, y = (-48).dp)
                .size(192.dp)
                .clip(Radii.badge)
                .background(color.copy(alpha = 0.15f)),
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-32).dp)
                .size(128.dp)
                .clip(Radii.badge)
                .background(color2.copy(alpha = 0.10f)),
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = pokedexNumber(detail.id),
                color = Color.White.copy(alpha = 0.35f),
                fontSize = 12.sp,
                fontFamily = MonoFont,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp, top = 16.dp),
            )

            FloatingArtwork(
                url = PokeApi.artworkUrl(detail.id, shiny = shiny),
                contentDescription = detail.name,
                placeholderColor = color.copy(alpha = 0.15f),
                modifier = Modifier.padding(top = 8.dp).size(if (isWide) 220.dp else 176.dp),
            )

            Text(
                text = prettyName(detail.name),
                color = TextHigh,
                fontSize = 24.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 4.dp),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp),
            ) {
                detail.types.forEach { TypeBadge(type = it, size = BadgeSize.Large) }
            }

            NormalShinyToggle(
                shiny = shiny,
                accentColor = color,
                onChange = onShinyChange,
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
            )
        }
    }
}

@Composable
private fun PhysicalInfo(detail: PokemonDetail) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        InfoCard(
            icon = "↕",
            value = formatMetric(detail.height, "m"),
            label = "Height",
            modifier = Modifier.weight(1f),
        )
        InfoCard(
            icon = "⚖",
            value = formatMetric(detail.weight, "kg"),
            label = "Weight",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun AbilitiesInfo(detail: PokemonDetail) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        SectionLabel(text = "Abilities")
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            detail.abilities.forEach { ability ->
                InfoChip(
                    text = prettyName(ability.name) +
                        if (ability.isHidden) " (Hidden)" else "",
                    highlighted = ability.isHidden,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BaseStatsInfo(detail: PokemonDetail) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionLabel(text = "Base Stats")
            Spacer(Modifier.weight(1f))
            Text(
                text = "Total",
                color = TextLow,
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 6.dp),
            )
            Text(
                text = detail.totalStats.toString(),
                color = TextHigh,
                fontSize = 14.sp,
                fontFamily = MonoFont,
                fontWeight = FontWeight.Black,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clip(Radii.card)
                .background(Surface04)
                .border(1.dp, Border08, Radii.card)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            detail.stats.forEachIndexed { index, stat ->
                StatBar(name = stat.name, value = stat.value, delay = index * 80)
            }
        }
    }
}

@Composable
private fun MovesInfo(detail: PokemonDetail) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        SectionLabel(text = "Moves")
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            detail.moves.chunked(2).forEach { group ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    group.forEach { move ->
                        InfoChip(text = prettyName(move), modifier = Modifier.weight(1f))
                    }
                    // Keep the last row aligned when it holds a single chip.
                    if (group.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActionButtons(
    isFavourite: Boolean,
    isCaught: Boolean,
    favouritePulse: Boolean,
    catchPulse: Boolean,
    onToggleFavourite: () -> Unit,
    onToggleCaught: () -> Unit,
    onCompare: () -> Unit,
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val buttonModifier = Modifier.sizeIn(minWidth = 100.dp)

        GameButton(
            style = if (isFavourite) GameButtonStyle.Red else GameButtonStyle.Outline,
            onClick = onToggleFavourite,
            modifier = buttonModifier,
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            ActionButtonLabel(
                label = if (isFavourite) "Saved" else "Favourite",
                labelColor = Color.White,
                pulse = favouritePulse,
            ) {
                HeartIcon(
                    iconSize = 18.dp,
                    filled = isFavourite,
                    fillColor = Color.White,
                    outlineColor = Color.White,
                )
            }
        }

        GameButton(
            style = if (isCaught) GameButtonStyle.Yellow else GameButtonStyle.Outline,
            onClick = onToggleCaught,
            modifier = buttonModifier,
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            ActionButtonLabel(
                label = if (isCaught) "Caught!" else "Catch",
                labelColor = if (isCaught) Color(0xFF1A1200) else Color.White,
                pulse = catchPulse,
            ) {
                PokeballIcon(size = 18.dp, caught = isCaught)
            }
        }

        GameButton(
            style = GameButtonStyle.Outline,
            onClick = onCompare,
            modifier = buttonModifier,
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            ActionButtonLabel(label = "Compare", labelColor = Color.White, pulse = false) {
                SwapIcon(iconSize = 18.dp, color = Color.White)
            }
        }
    }
}

/** Icon above label, with a springy pop the moment the action fires. */
@Composable
private fun RowScope.ActionButtonLabel(
    label: String,
    labelColor: Color,
    pulse: Boolean,
    icon: @Composable () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (pulse) 1.25f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "action-pulse",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.scale(scale),
    ) {
        icon()
        Text(
            text = label,
            color = labelColor,
            fontSize = 12.sp,
            fontFamily = DisplayFont,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

/** Segmented Normal / Shiny switch. */
@Composable
private fun NormalShinyToggle(
    shiny: Boolean,
    accentColor: Color,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(Color.Black.copy(alpha = 0.30f))
            .border(1.dp, Border12, RoundedCornerShape(percent = 50))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        listOf("Normal" to false, "Shiny ✦" to true).forEach { (label, value) ->
            val active = shiny == value
            Text(
                text = label,
                color = when {
                    !active -> Color.White.copy(alpha = 0.40f)
                    value -> Color(0xFF1A1200)
                    else -> Color.White
                },
                fontSize = 12.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(
                        when {
                            !active -> Color.Transparent
                            value -> PokeYellow
                            else -> accentColor
                        },
                    )
                    .clickable { onChange(value) }
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }
    }
}
