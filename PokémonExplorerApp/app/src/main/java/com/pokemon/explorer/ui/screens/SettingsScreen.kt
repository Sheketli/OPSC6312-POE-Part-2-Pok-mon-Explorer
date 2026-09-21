package com.pokemon.explorer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemon.explorer.data.PokeApi
import com.pokemon.explorer.state.AppState
import com.pokemon.explorer.state.ThemeSetting
import com.pokemon.explorer.ui.components.BackArrowIcon
import com.pokemon.explorer.ui.components.ChevronRightIcon
import com.pokemon.explorer.ui.components.CircleIconButton
import com.pokemon.explorer.ui.components.SectionHeader
import com.pokemon.explorer.ui.components.SegmentedControl
import com.pokemon.explorer.ui.components.SettingRow
import com.pokemon.explorer.ui.components.SettingsGroup
import com.pokemon.explorer.ui.components.ToggleSwitch
import com.pokemon.explorer.ui.theme.Border08
import com.pokemon.explorer.ui.theme.DisplayFont
import com.pokemon.explorer.ui.theme.MonoFont
import com.pokemon.explorer.ui.theme.PokeRed
import com.pokemon.explorer.ui.theme.Radii
import com.pokemon.explorer.ui.theme.Surface04
import com.pokemon.explorer.ui.theme.TextHigh
import com.pokemon.explorer.ui.theme.TextLow

/**
 * On-brand settings: appearance, data, preferences and about.
 *
 * The theme preference is stored on [AppState] but the app currently only ships a
 * dark palette — see the README for the light-mode caveat.
 */
@Composable
fun SettingsScreen(
    state: AppState,
    modifier: Modifier = Modifier,
) {
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
                text = "Settings",
                color = TextHigh,
                fontSize = 20.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Black,
            )
        }

        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Appearance.
            SectionHeader(title = "Appearance")
            SettingsGroup {
                SettingRow(label = "Theme", sub = "Choose your display preference") {
                    SegmentedControl(
                        options = ThemeSetting.entries.toList(),
                        selected = state.theme,
                        label = { it.label },
                        onSelect = { state.selectTheme(it) },
                    )
                }
            }

            // Data.
            SectionHeader(title = "Data")
            SettingsGroup {
                SettingRow(label = "Search History", sub = "Save recent searches") {
                    ToggleSwitch(
                        checked = state.searchHistoryEnabled,
                        onCheckedChange = { state.updateSearchHistoryEnabled(it) },
                    )
                }
                DangerRow(
                    label = "Clear Cache",
                    sub = "Free up local storage",
                    buttonLabel = "Clear",
                    onClick = { PokeApi.clearCache() },
                )
                DangerRow(
                    label = "Clear Search History",
                    sub = "Remove past searches",
                    buttonLabel = "Clear",
                    onClick = { state.clearSearchHistory() },
                )
                DangerRow(
                    label = "Reset Collection",
                    sub = "Clear favourites & caught",
                    buttonLabel = "Reset",
                    onClick = { state.resetCollection() },
                )
            }

            // Preferences.
            SectionHeader(title = "Preferences")
            SettingsGroup {
                SettingRow(label = "Last Viewed Pokémon", sub = "Resume where you left off") {
                    ToggleSwitch(
                        checked = state.resumeLastViewed,
                        onCheckedChange = { state.updateResumeLastViewed(it) },
                    )
                }
                SettingRow(label = "Notifications", sub = "Pokédex updates & new Pokémon") {
                    ToggleSwitch(
                        checked = state.notificationsEnabled,
                        onCheckedChange = { state.updateNotificationsEnabled(it) },
                    )
                }
            }

            SectionHeader(title = "Account")
            SettingsGroup {
                SettingRow(label = "Logged in as", sub = state.currentUser?.username ?: "") {
                    Text(
                        text = "Active",
                        color = Color(0xFF7AC74C),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                DangerRow(
                    label = "Log Out",
                    sub = "Sign out of your account",
                    buttonLabel = "Logout",
                    onClick = { state.logout() },
                )
            }

            // About.
            val uriHandler = LocalUriHandler.current
            SectionHeader(title = "About")
            SettingsGroup {
                SettingRow(label = "Version", sub = "Latest build") {
                    Text(
                        text = "1.0.0",
                        color = TextLow,
                        fontSize = 12.sp,
                        fontFamily = MonoFont,
                    )
                }
                SettingRow(
                    label = "Data Source",
                    sub = "Pokémon data via PokéAPI",
                    modifier = Modifier.clickable { uriHandler.openUri("https://pokeapi.co/") }
                ) {
                    ChevronRightIcon(iconSize = 16.dp, color = Color.White.copy(alpha = 0.30f))
                }
                SettingRow(
                    label = "Privacy Policy",
                    sub = "How we handle your data",
                    modifier = Modifier.clickable { uriHandler.openUri("https://example.com/privacy") }
                ) {
                    ChevronRightIcon(iconSize = 16.dp, color = Color.White.copy(alpha = 0.30f))
                }
            }

            // Footer.
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .clip(Radii.card)
                        .background(Surface04)
                        .border(1.dp, Border08, Radii.card)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(text = "⚾", fontSize = 18.sp)
                    Text(
                        text = buildAnnotatedString {
                            append("Pokémon ")
                            withStyle(SpanStyle(color = PokeRed)) { append("Explorer") }
                        },
                        color = TextHigh,
                        fontSize = 14.sp,
                        fontFamily = DisplayFont,
                        fontWeight = FontWeight.Black,
                    )
                }

                Text(
                    text = "Data provided by PokéAPI.co\n" +
                        "Pokémon and all related names are trademarks of Nintendo / Game Freak.\n" +
                        "This app is a fan project, not affiliated with The Pokémon Company.",
                    color = Color.White.copy(alpha = 0.20f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }
    }
}

/** A settings row with a red destructive action button. */
@Composable
private fun DangerRow(
    label: String,
    sub: String,
    buttonLabel: String,
    onClick: () -> Unit,
) {
    SettingRow(label = label, sub = sub) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(PokeRed.copy(alpha = 0.12f))
                .border(1.dp, PokeRed.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(
                text = buttonLabel,
                color = PokeRed,
                fontSize = 12.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
