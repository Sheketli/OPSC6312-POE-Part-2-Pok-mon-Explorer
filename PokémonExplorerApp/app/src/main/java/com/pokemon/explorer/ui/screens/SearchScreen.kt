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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemon.explorer.data.PokeApi
import com.pokemon.explorer.data.PokemonSummary
import com.pokemon.explorer.state.AppState
import com.pokemon.explorer.state.Screen
import com.pokemon.explorer.ui.components.AppTextField
import com.pokemon.explorer.ui.components.BackArrowIcon
import com.pokemon.explorer.ui.components.CircleIconButton
import com.pokemon.explorer.ui.components.CloseIcon
import com.pokemon.explorer.ui.components.EmptyState
import com.pokemon.explorer.ui.components.GameButton
import com.pokemon.explorer.ui.components.GameButtonStyle
import com.pokemon.explorer.ui.components.PokeballIcon
import com.pokemon.explorer.ui.components.PokeballSpinner
import com.pokemon.explorer.ui.components.PokemonCard
import com.pokemon.explorer.ui.components.SearchIcon
import com.pokemon.explorer.ui.components.dataOrNull
import com.pokemon.explorer.ui.components.isLoading
import com.pokemon.explorer.ui.components.rememberLoadState
import com.pokemon.explorer.ui.theme.AppBackground
import com.pokemon.explorer.ui.theme.Border08
import com.pokemon.explorer.ui.theme.Border10
import com.pokemon.explorer.ui.theme.DisplayFont
import com.pokemon.explorer.ui.theme.MonoFont
import com.pokemon.explorer.ui.theme.Surface07
import com.pokemon.explorer.ui.theme.TextLow
import kotlinx.coroutines.delay

private val POPULAR_SEARCHES = listOf(
    "pikachu", "charizard", "mewtwo", "eevee", "gengar", "lucario", "garchomp", "dragonite",
)

/**
 * Search by name or Pokédex number.
 *
 * Typing restarts the loader in [rememberLoadState], which both cancels the previous
 * request and provides the 400 ms debounce the web app got from `setTimeout`.
 */
@Composable
fun SearchScreen(
    state: AppState,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    val searchState by rememberLoadState(query) {
        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) {
            emptyList<PokemonSummary>()
        } else {
            // Cancelled when the query changes, which is the debounce.
            delay(400)
            listOfNotNull(PokeApi.find(trimmed))
        }
    }

    val results: List<PokemonSummary> = searchState.dataOrNull().orEmpty()
    val searching = searchState.isLoading
    val hasSearched = query.isNotBlank() && !searching

    Column(modifier = modifier.fillMaxSize()) {
        // Fixed header, so the field stays put while results scroll.
        Column(Modifier.fillMaxWidth().background(AppBackground)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CircleIconButton(onClick = { state.goBack() }) {
                    BackArrowIcon(iconSize = 18.dp)
                }
                Box(Modifier.weight(1f)) {
                    AppTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = "Search by name or Pokédex #",
                        focusRequester = focusRequester,
                        autoFocus = true,
                        leadingIcon = {
                            SearchIcon(
                                iconSize = 16.dp,
                                color = Color.White.copy(alpha = 0.40f),
                            )
                        },
                    )
                }
                if (query.isNotEmpty()) {
                    CircleIconButton(onClick = { query = "" }) {
                        CloseIcon(iconSize = 14.dp, color = Color.White.copy(alpha = 0.6f))
                    }
                }
            }

            if (results.isNotEmpty()) {
                Text(
                    text = "${results.size} Pokémon found",
                    color = TextLow,
                    fontSize = 12.sp,
                    fontFamily = MonoFont,
                    modifier = Modifier.padding(start = 20.dp, bottom = 12.dp),
                )
            }

            Box(Modifier.fillMaxWidth().height(1.dp).background(Border08))
        }

        when {
            searching -> {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    PokeballSpinner(size = 48.dp)
                    Text(text = "Searching...", color = TextLow, fontSize = 14.sp)
                }
            }

            hasSearched && results.isEmpty() -> {
                EmptyState(
                    title = "No Pokémon Found",
                    message = "Try searching by exact name or Pokédex number",
                    glyph = { PokeballIcon(size = 36.dp) },
                    action = {
                        GameButton(
                            style = GameButtonStyle.Red,
                            onClick = { query = "" },
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        ) {
                            Text(
                                text = "Clear Search",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontFamily = DisplayFont,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }
                    },
                )
            }

            query.isEmpty() -> {
                Column {
                    if (state.recentSearches.isNotEmpty()) {
                        SearchGroup(
                            title = "RECENT SEARCHES",
                            options = state.recentSearches,
                            onPick = { query = it }
                        )
                    }
                    SearchGroup(
                        title = "POPULAR SEARCHES",
                        options = POPULAR_SEARCHES,
                        onPick = { query = it }
                    )
                }
            }

            else -> {
                // Record successful searches in history.
                if (results.isNotEmpty()) {
                    state.addSearchToHistory(query)
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(results, key = { it.id }) { pokemon ->
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
    }
}

@Composable
private fun SearchGroup(
    title: String,
    options: List<String>,
    onPick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.30f),
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        // Rows of three so the chips wrap without relying on an experimental layout API.
        options.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { name ->
                    Text(
                        text = name,
                        color = Color.White.copy(alpha = 0.60f),
                        fontSize = 14.sp,
                        fontFamily = DisplayFont,
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(Surface07)
                            .border(1.dp, Border10, RoundedCornerShape(percent = 50))
                            .clickable { onPick(name) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }
        }
    }
}
