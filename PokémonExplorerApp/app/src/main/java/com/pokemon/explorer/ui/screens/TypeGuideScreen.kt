package com.pokemon.explorer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemon.explorer.data.ALL_TYPES
import com.pokemon.explorer.data.TYPE_ICONS
import com.pokemon.explorer.data.TYPE_MATCHUPS
import com.pokemon.explorer.data.contrastOn
import com.pokemon.explorer.data.typeColor
import com.pokemon.explorer.state.AppState
import com.pokemon.explorer.state.Screen
import com.pokemon.explorer.ui.components.GameButton
import com.pokemon.explorer.ui.components.GameButtonStyle
import com.pokemon.explorer.ui.components.TypeBadge
import com.pokemon.explorer.ui.components.BadgeSize
import com.pokemon.explorer.ui.theme.DisplayFont
import com.pokemon.explorer.ui.theme.TextHigh
import com.pokemon.explorer.ui.theme.TextLow
import com.pokemon.explorer.ui.theme.typeGradient

private data class MatchupRow(
    val symbol: String,
    val label: String,
    val types: List<String>,
    val accent: Color,
)

/**
 * Battle reference: all 18 types as bold type-coloured cards, with a full
 * strong / weak / resistant / immune breakdown for the selected type.
 */
@Composable
fun TypeGuideScreen(
    state: AppState,
    modifier: Modifier = Modifier,
) {
    var selected by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = buildAnnotatedString {
                    append("Type ")
                    withStyle(SpanStyle(color = Color(0xFF7AC74C))) { append("Guide") }
                },
                color = TextHigh,
                fontSize = 20.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = "Select a type to see matchups",
                color = TextLow,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
        ) {
            selected?.let { type ->
                val matchup = TYPE_MATCHUPS[type]
                if (matchup != null) {
                    val color = typeColor(type)
                    val rows = listOf(
                        MatchupRow(
                            symbol = "⚔️",
                            label = "Super Effective Against",
                            types = matchup.strongAgainst,
                            accent = Color(0xFF7AC74C),
                        ),
                        MatchupRow(
                            symbol = "🛡️",
                            label = "Not Very Effective Against",
                            types = matchup.weakAgainst,
                            accent = Color(0xFFEE1515),
                        ),
                        MatchupRow(
                            symbol = "💪",
                            label = "Resistant To",
                            types = matchup.resistantTo,
                            accent = Color(0xFF6390F0),
                        ),
                        MatchupRow(
                            symbol = "🚫",
                            label = "Immune To",
                            types = matchup.immuneTo,
                            accent = Color(0xFFF7D02C),
                        ),
                    ).filter { it.types.isNotEmpty() }

                    SelectedTypePanel(
                        type = type,
                        color = color,
                        rows = rows,
                        onClear = { selected = null },
                        onPickRelated = { selected = it },
                        onViewPokemon = {
                            state.navigate(Screen.SEARCH, filterType = type)
                        },
                    )
                }
            }

            Text(
                text = if (selected == null) "SELECT A TYPE" else "ALL TYPES",
                color = Color.White.copy(alpha = 0.35f),
                fontSize = 12.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
            )

            // Chunked into rows of three rather than using an experimental flow layout.
            ALL_TYPES.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    row.forEach { type ->
                        TypeCard(
                            type = type,
                            selected = type == selected,
                            onClick = { selected = if (selected == type) null else type },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TypeCard(
    type: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = typeColor(type)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(listOf(color, color.copy(alpha = 0.80f))),
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color.White.copy(alpha = 0.60f) else color,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(text = TYPE_ICONS[type] ?: "?", fontSize = 20.sp)
        Text(
            text = type,
            color = contrastOn(color),
            fontSize = 14.sp,
            fontFamily = DisplayFont,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

@Composable
private fun SelectedTypePanel(
    type: String,
    color: Color,
    rows: List<MatchupRow>,
    onClear: () -> Unit,
    onPickRelated: (String) -> Unit,
    onViewPokemon: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(typeGradient(color, color, startAlpha = 0.19f, endAlpha = 0.06f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(24.dp)),
    ) {
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .size(128.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(color.copy(alpha = 0.20f)),
        )

        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = TYPE_ICONS[type] ?: "?", fontSize = 18.sp)
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "$type Type",
                        color = TextHigh,
                        fontSize = 18.sp,
                        fontFamily = DisplayFont,
                        fontWeight = FontWeight.Black,
                    )
                    Text(text = "Full matchup breakdown", color = TextLow, fontSize = 12.sp)
                }
                Text(
                    text = "Clear",
                    color = TextLow,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable(onClick = onClear),
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rows.forEach { row ->
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(text = row.symbol, fontSize = 14.sp)
                            Text(
                                text = row.label,
                                color = row.accent,
                                fontSize = 12.sp,
                                fontFamily = DisplayFont,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Row(
                            modifier = Modifier.padding(top = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            row.types.forEach { related ->
                                Box(Modifier.clickable { onPickRelated(related) }) {
                                    TypeBadge(type = related, size = BadgeSize.Small)
                                }
                            }
                        }
                    }
                }
            }

            GameButton(
                style = GameButtonStyle.Red,
                onClick = onViewPokemon,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                contentPadding = PaddingValues(vertical = 10.dp),
            ) {
                Text(
                    text = "View $type Pokémon →",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = DisplayFont,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
