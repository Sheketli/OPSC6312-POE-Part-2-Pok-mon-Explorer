package com.pokemon.explorer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemon.explorer.data.PokeApi
import com.pokemon.explorer.data.PokemonDetail
import com.pokemon.explorer.data.STAT_LABELS
import com.pokemon.explorer.data.typeColor
import com.pokemon.explorer.state.AppState
import com.pokemon.explorer.ui.components.BackArrowIcon
import com.pokemon.explorer.ui.components.BadgeSize
import com.pokemon.explorer.ui.components.CircleIconButton
import com.pokemon.explorer.ui.components.CloseIcon
import com.pokemon.explorer.ui.components.GameButton
import com.pokemon.explorer.ui.components.GameButtonStyle
import com.pokemon.explorer.ui.components.LoadState
import com.pokemon.explorer.ui.components.PokeballSpinner
import com.pokemon.explorer.ui.components.SectionLabel
import com.pokemon.explorer.ui.components.SmallTextField
import com.pokemon.explorer.ui.components.TypeBadge
import com.pokemon.explorer.ui.components.dataOrNull
import com.pokemon.explorer.ui.components.pokedexNumber
import com.pokemon.explorer.ui.components.prettyName
import com.pokemon.explorer.ui.components.rememberLoadState
import com.pokemon.explorer.ui.theme.AppBackground
import com.pokemon.explorer.ui.theme.Border08
import com.pokemon.explorer.ui.theme.CompareBlue
import com.pokemon.explorer.ui.theme.DisplayFont
import com.pokemon.explorer.ui.theme.MonoFont
import com.pokemon.explorer.ui.theme.PokeRed
import com.pokemon.explorer.ui.theme.Radii
import com.pokemon.explorer.ui.theme.Surface05
import com.pokemon.explorer.ui.theme.Surface10
import com.pokemon.explorer.ui.theme.TextHigh
import com.pokemon.explorer.ui.theme.TextLow
import kotlinx.coroutines.launch

private const val DEFAULT_A = 25 // Pikachu
private const val DEFAULT_B = 6  // Charizard
private const val MAX_POKEDEX_ID = 1025
private const val ARENA_HEIGHT = 240

/**
 * Head-to-head comparison: two type-gradient halves with a VS badge on the seam,
 * inline pickers for each side, then a per-stat table highlighting the winner of
 * every row plus a base-stat-total bar.
 */
@Composable
fun CompareScreen(
    state: AppState,
    initialIdA: Int?,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
) {
    var idA by remember { mutableStateOf(initialIdA ?: DEFAULT_A) }
    var idB by remember { mutableStateOf(DEFAULT_B) }

    val stateA by rememberLoadState(idA) { PokeApi.detail(idA) }
    val stateB by rememberLoadState(idB) { PokeApi.detail(idB) }
    val pA = stateA.dataOrNull()
    val pB = stateB.dataOrNull()

    val colorA = pA?.types?.firstOrNull()?.let { typeColor(it) } ?: PokeRed
    val colorB = pB?.types?.firstOrNull()?.let { typeColor(it) } ?: CompareBlue

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CircleIconButton(onClick = { state.goBack() }) {
                BackArrowIcon(iconSize = 18.dp)
            }
            Text(
                text = "Compare Pokémon",
                color = TextHigh,
                fontSize = 18.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Black,
            )
        }

        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            val isWide = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
            
            // VS arena.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isWide) (ARENA_HEIGHT * 1.5).dp else ARENA_HEIGHT.dp)
                    .background(AppBackground),
            ) {
                Row(Modifier.fillMaxSize()) {
                    PokemonHalf(
                        detail = pA,
                        loading = stateA is LoadState.Loading,
                        mirrored = false,
                        isWide = isWide,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                    PokemonHalf(
                        detail = pB,
                        loading = stateB is LoadState.Loading,
                        mirrored = true,
                        isWide = isWide,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                }

                // VS badge, floating over the seam.
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(46.dp)
                        .clip(Radii.badge)
                        .background(Brush.linearGradient(listOf(PokeRed, CompareBlue))),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "VS",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = DisplayFont,
                        fontWeight = FontWeight.Black,
                    )
                }
            }

            Box(Modifier.fillMaxWidth().height(1.dp).background(Border08))

            // Pickers.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PokemonPicker(
                    label = "A",
                    onPick = { idA = it },
                    modifier = Modifier.weight(1f),
                )
                PokemonPicker(
                    label = "B",
                    onPick = { idB = it },
                    modifier = Modifier.weight(1f),
                )
            }

            Box(Modifier.fillMaxWidth().height(1.dp).background(Border08))

            if (pA != null && pB != null) {
                StatComparison(pA = pA, pB = pB, colorA = colorA, colorB = colorB)
            }
        }
    }
}

/** One contestant's half of the arena, bottom-aligned like a battle intro. */
@Composable
private fun PokemonHalf(
    detail: PokemonDetail?,
    loading: Boolean,
    mirrored: Boolean,
    isWide: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val color = detail?.types?.firstOrNull()?.let { typeColor(it) } ?: Color(0xFF555555)

    val background = if (detail != null) {
        val stops = listOf(
            color.copy(alpha = 0.38f),
            color.copy(alpha = 0.15f),
            Color.Transparent,
        )
        // Mirror the right half so both gradients fade towards the centre.
        Brush.linearGradient(if (mirrored) stops.reversed() else stops)
    } else {
        Brush.linearGradient(listOf(Surface05, Surface05))
    }

    Column(
        modifier = modifier
            .background(background)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        if (loading) {
            PokeballSpinner(size = 36.dp, modifier = Modifier.padding(bottom = 16.dp))
        }
        if (detail != null) {
            FloatingArtwork(
                url = PokeApi.artworkUrl(detail.id),
                contentDescription = detail.name,
                placeholderColor = color.copy(alpha = 0.15f),
                modifier = Modifier.size(if (isWide) 160.dp else 112.dp),
            )
            Text(
                text = pokedexNumber(detail.id),
                color = TextLow,
                fontSize = 10.sp,
                fontFamily = MonoFont,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = prettyName(detail.name),
                color = TextHigh,
                fontSize = 14.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 6.dp),
            ) {
                detail.types.forEach { TypeBadge(type = it, size = BadgeSize.Small) }
            }
        }
    }
}

/**
 * Inline picker for one side. Accepts a Pokédex number (1–1025) or a Pokémon name;
 * names are resolved through the API so the "Name or #" placeholder is accurate.
 */
@Composable
private fun PokemonPicker(
    label: String,
    onPick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editing by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Box(modifier) {
        if (!editing) {
            Text(
                text = "Change $label",
                color = TextLow,
                fontSize = 12.sp,
                fontFamily = DisplayFont,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { editing = true }
                    .padding(vertical = 8.dp),
            )
            return@Box
        }

        val submit: () -> Unit = {
            val trimmed = input.trim()
            val asNumber = trimmed.toIntOrNull()
            if (asNumber != null && asNumber in 1..MAX_POKEDEX_ID) {
                onPick(asNumber)
                editing = false
                input = ""
            } else if (asNumber == null && trimmed.isNotEmpty()) {
                scope.launch {
                    PokeApi.find(trimmed)?.let { found ->
                        onPick(found.id)
                        editing = false
                        input = ""
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SmallTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = "Name or #",
                onImeAction = submit,
                modifier = Modifier.weight(1f),
            )
            GameButton(
                style = GameButtonStyle.Red,
                onClick = submit,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "Go",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = DisplayFont,
                    fontWeight = FontWeight.Bold,
                )
            }
            Box(Modifier.clickable { editing = false }) {
                CloseIcon(iconSize = 14.dp, color = TextLow)
            }
        }
    }
}

/** Per-stat table with the winning side highlighted on every row. */
@Composable
private fun StatComparison(
    pA: PokemonDetail,
    pB: PokemonDetail,
    colorA: Color,
    colorB: Color,
) {
    val totalA = pA.totalStats
    val totalB = pB.totalStats

    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {
        SectionLabel(text = "Stat Comparison")

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            pA.stats.forEach { stat ->
                val valueA = stat.value
                val valueB = pB.stats.firstOrNull { it.name == stat.name }?.value ?: 0
                val aWins = valueA >= valueB

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // A: value then bar, growing right-to-left towards the label.
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = valueA.toString(),
                            color = if (aWins) TextHigh else TextLow,
                            fontSize = 12.sp,
                            fontFamily = MonoFont,
                            fontWeight = FontWeight.Bold,
                        )
                        ComparisonBar(
                            fraction = valueA / 255f,
                            color = colorA,
                            highlighted = aWins,
                            alignEnd = true,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Text(
                        text = STAT_LABELS[stat.name] ?: stat.name,
                        color = TextLow,
                        fontSize = 10.sp,
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.width(52.dp),
                    )

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ComparisonBar(
                            fraction = valueB / 255f,
                            color = colorB,
                            highlighted = !aWins,
                            alignEnd = false,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = valueB.toString(),
                            color = if (!aWins) TextHigh else TextLow,
                            fontSize = 12.sp,
                            fontFamily = MonoFont,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        // Base stat totals.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .clip(Radii.card)
                .background(Surface05)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TotalCell(value = totalA, color = colorA, wins = totalA >= totalB)
            Text(
                text = "BASE STAT TOTAL",
                color = TextLow,
                fontSize = 10.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp),
            )
            TotalCell(value = totalB, color = colorB, wins = totalB >= totalA)
        }

        // Proportional split of the two totals.
        val sum = (totalA + totalB).coerceAtLeast(1)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Surface10),
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxWidth(totalA.toFloat() / sum)
                        .height(12.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(colorA),
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Surface10),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(totalB.toFloat() / sum)
                        .height(12.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(colorB),
                )
            }
        }
    }
}

/** Declared as a [RowScope] extension so `Modifier.weight` is available. */
@Composable
private fun RowScope.TotalCell(value: Int, color: Color, wins: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
        Text(
            text = value.toString(),
            color = if (wins) color else TextLow,
            fontSize = 20.sp,
            fontFamily = MonoFont,
            fontWeight = FontWeight.Black,
        )
        Text(text = "Total", color = TextLow, fontSize = 12.sp)
    }
}

/** A single stat bar; the winner of the row is drawn at full strength. */
@Composable
private fun ComparisonBar(
    fraction: Float,
    color: Color,
    highlighted: Boolean,
    alignEnd: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(8.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(Surface10),
    ) {
        Box(
            modifier = Modifier
                .align(if (alignEnd) Alignment.CenterEnd else Alignment.CenterStart)
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(8.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(if (highlighted) color else color.copy(alpha = 0.38f)),
        )
    }
}
