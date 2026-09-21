package com.pokemon.explorer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pokemon.explorer.data.ALL_TYPES
import com.pokemon.explorer.data.PokemonSummary
import com.pokemon.explorer.state.ThemeSetting
import com.pokemon.explorer.ui.theme.AppBackground

/**
 * Design-system previews, rendered in Android Studio's preview pane.
 *
 * These are for eyeballing components without a device or emulator. Everything here
 * is local and needs no network, so the previews always render at full fidelity.
 */

@Preview(name = "Type badges", showBackground = true, backgroundColor = 0xFF0D1020, widthDp = 320)
@Composable
private fun TypeBadgePreview() {
    Column(
        modifier = Modifier.background(AppBackground).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ALL_TYPES.chunked(6).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { TypeBadge(type = it, size = BadgeSize.Small) }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TypeBadge(type = "electric")
            TypeBadge(type = "dragon", size = BadgeSize.Large)
        }
    }
}

@Preview(name = "Poké Balls", showBackground = true, backgroundColor = 0xFF0D1020)
@Composable
private fun PokeballPreview() {
    Row(
        modifier = Modifier.background(AppBackground).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PokeballIcon(size = 32.dp)
        PokeballIcon(size = 32.dp, filled = true)
        PokeballIcon(size = 32.dp, caught = true)
        FullPokeball(size = 48.dp)
        FullPokeball(size = 48.dp, opening = true)
        PokeballSpinner(size = 40.dp)
    }
}

@Preview(name = "Buttons", showBackground = true, backgroundColor = 0xFF0D1020, widthDp = 300)
@Composable
private fun GameButtonPreview() {
    Column(
        modifier = Modifier.background(AppBackground).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GameButton(style = GameButtonStyle.Red, onClick = {}) {
                Text("View Details", color = Color.White)
            }
            GameButton(style = GameButtonStyle.Yellow, onClick = {}) {
                Text("Random")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GameButton(style = GameButtonStyle.Outline, onClick = {}) {
                Text("Favourite", color = Color.White)
            }
            GameButton(style = GameButtonStyle.Outline, onClick = {}) {
                Text("Catch", color = Color.White)
            }
        }
    }
}

@Preview(name = "Stat bars", showBackground = true, backgroundColor = 0xFF0D1020, widthDp = 320)
@Composable
private fun StatBarPreview() {
    Column(
        modifier = Modifier.background(AppBackground).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatBar(name = "hp", value = 35)
        StatBar(name = "attack", value = 55)
        StatBar(name = "defense", value = 40)
        StatBar(name = "special-attack", value = 50, delay = 240)
        StatBar(name = "special-defense", value = 50, delay = 320)
        StatBar(name = "speed", value = 90, delay = 400)
    }
}

@Preview(name = "Pokémon card", showBackground = true, backgroundColor = 0xFF0D1020, widthDp = 200)
@Composable
private fun PokemonCardPreview() {
    val pokemon = PokemonSummary(id = 25, name = "pikachu", types = listOf("electric"))

    Row(
        modifier = Modifier.background(AppBackground).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PokemonCard(
            pokemon = pokemon,
            isFavourite = true,
            isCaught = true,
            onToggleFavourite = {},
            onClick = {},
            modifier = Modifier.weight(1f),
        )
        PokemonCard(
            pokemon = PokemonSummary(
                id = 6,
                name = "charizard",
                types = listOf("fire", "flying"),
            ),
            isFavourite = false,
            isCaught = false,
            onToggleFavourite = {},
            onClick = {},
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(name = "Info cards and chips", showBackground = true, backgroundColor = 0xFF0D1020)
@Composable
private fun InfoPreview() {
    Column(
        modifier = Modifier.background(AppBackground).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoCard(icon = "↕", value = "0.4 m", label = "Height", modifier = Modifier.weight(1f))
            InfoCard(icon = "⚖", value = "6.0 kg", label = "Weight", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoChip(text = "Static")
            InfoChip(text = "Lightning Rod (Hidden)", highlighted = true)
        }
    }
}

@Preview(name = "Controls", showBackground = true, backgroundColor = 0xFF0D1020, widthDp = 320)
@Composable
private fun ControlsPreview() {
    var theme by remember { mutableStateOf(ThemeSetting.Dark) }
    var toggled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.background(AppBackground).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SegmentedControl(
            options = ThemeSetting.entries.toList(),
            selected = theme,
            label = { it.label },
            onSelect = { theme = it },
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ToggleSwitch(checked = toggled, onCheckedChange = { toggled = it })
            CountBadge(count = 21, active = true)
            CountBadge(count = 3, active = false)
        }
        SectionLabel(text = "Base Stats")
        HairlineDivider()
    }
}

@Preview(name = "Empty state", showBackground = true, backgroundColor = 0xFF0D1020, widthDp = 320)
@Composable
private fun EmptyStatePreview() {
    PreviewSurface {
        EmptyState(
            title = "No Favourites Yet",
            message = "Tap the heart icon on any Pokémon to add it here.",
            glyph = {
                HeartIcon(
                    iconSize = 36.dp,
                    filled = false,
                    fillColor = Color.White,
                    outlineColor = Color.White.copy(alpha = 0.30f),
                )
            },
            action = {
                GameButton(style = GameButtonStyle.Red, onClick = {}) {
                    Text("Browse Pokémon", color = Color.White)
                }
            },
        )
    }
}

@Preview(name = "Skeletons", showBackground = true, backgroundColor = 0xFF0D1020, widthDp = 200)
@Composable
private fun SkeletonPreview() {
    Row(
        modifier = Modifier.background(AppBackground).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SkeletonCard(Modifier.weight(1f))
        SkeletonCard(Modifier.weight(1f))
    }
}

@Preview(name = "Loading state", showBackground = true, backgroundColor = 0xFF0D1020)
@Composable
private fun LoadingStatePreview() {
    Column(
        modifier = Modifier.background(AppBackground).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PokeballSpinner(size = 48.dp)
        Text("Searching...", color = Color.White.copy(alpha = 0.4f))
    }
}

/** Paints the app background behind a preview that has no chrome of its own. */
@Composable
private fun PreviewSurface(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppBackground)
            .padding(vertical = 8.dp),
    ) {
        content()
    }
}
