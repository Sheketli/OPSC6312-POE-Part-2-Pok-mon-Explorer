package com.pokemon.explorer.ui.screens

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pokemon.explorer.state.AppState

/**
 * Previews for the screens that are entirely self-contained.
 *
 * Explore, Search, Detail, Compare and Collection all fetch from PokéAPI, which never
 * resolves in the preview renderer — they would only ever show their loading states.
 * Type Guide, Settings and Splash hold no network state, so these render fully and
 * are the ones worth having in the preview pane.
 *
 * To check the data-driven screens, run the app on a device or emulator.
 */

@Preview(name = "Type Guide", showBackground = true, backgroundColor = 0xFF0D1020, heightDp = 900)
@Composable
private fun TypeGuidePreview() {
    TypeGuideScreen(state = AppState(Application()))
}

@Preview(name = "Settings", showBackground = true, backgroundColor = 0xFF0D1020, heightDp = 900)
@Composable
private fun SettingsPreview() {
    SettingsScreen(state = AppState(Application()))
}

@Preview(name = "Splash", showBackground = true, backgroundColor = 0xFF0D1020, heightDp = 800)
@Composable
private fun SplashPreview() {
    SplashScreen(onComplete = {})
}
