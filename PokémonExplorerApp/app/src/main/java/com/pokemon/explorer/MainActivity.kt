package com.pokemon.explorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pokemon.explorer.state.AppState
import com.pokemon.explorer.ui.AppRoot
import com.pokemon.explorer.ui.screens.SplashScreen
import com.pokemon.explorer.ui.theme.PokemonExplorerTheme

/**
 * Single-activity host.
 *
 * The splash plays once on cold start, then the shell takes over. Back handling is
 * delegated to [AppState] so it pops the active tab's stack, falling through to the
 * system behaviour (leaving the app) at a tab root.
 */
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val state: AppState = viewModel()
            val windowSizeClass = calculateWindowSizeClass(this)
            
            PokemonExplorerTheme(themeSetting = state.theme) {
                var splashAnimationDone by remember { mutableStateOf(false) }
                val showSplash = !splashAnimationDone || !state.isAuthChecked

                BackHandler(enabled = !showSplash && state.canGoBack) {
                    state.goBack()
                }

                Crossfade(
                    targetState = showSplash,
                    animationSpec = tween(durationMillis = 400),
                    label = "splash-crossfade",
                ) { splash ->
                    if (splash) {
                        SplashScreen(onComplete = { splashAnimationDone = true })
                    } else {
                        AppRoot(state, windowSizeClass = windowSizeClass)
                    }
                }
            }
        }
    }
}
