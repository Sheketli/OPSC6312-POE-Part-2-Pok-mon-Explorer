package com.pokemon.explorer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.pokemon.explorer.state.ThemeSetting

// ------------------------------------------------------------------ palette

/** Poké Ball red — the primary brand colour. */
val PokeRed = Color(0xFFEE1515)

/** Electric yellow — the energy/accent colour (Random Discovery, caught state). */
val PokeYellow = Color(0xFFFFCB05)

/** Deep navy the app is painted on: a "Pokédex at night", not inverted grey. */
private val DarkAppBackground = Color(0xFF0D1020)

val AppBackground: Color
    @Composable
    get() = MaterialTheme.colorScheme.background

/** Water/steel blue used for the Compare accent. */
val CompareBlue = Color(0xFF6390F0)

// Surfaces are translucent white over [AppBackground] in dark mode, 
// and translucent black in light mode.
val Surface04: Color @Composable get() = if (isLight()) Color.Black.copy(alpha = 0.04f) else Color.White.copy(alpha = 0.04f)
val Surface05: Color @Composable get() = if (isLight()) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.05f)
val Surface06: Color @Composable get() = if (isLight()) Color.Black.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.06f)
val Surface07: Color @Composable get() = if (isLight()) Color.Black.copy(alpha = 0.07f) else Color.White.copy(alpha = 0.07f)
val Surface08: Color @Composable get() = if (isLight()) Color.Black.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.08f)
val Surface10: Color @Composable get() = if (isLight()) Color.Black.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.10f)
val Border08: Color @Composable get() = if (isLight()) Color.Black.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.08f)
val Border10: Color @Composable get() = if (isLight()) Color.Black.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.10f)
val Border12: Color @Composable get() = if (isLight()) Color.Black.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.12f)

// Text tints.
val TextHigh: Color @Composable get() = MaterialTheme.colorScheme.onBackground
val TextMedium: Color @Composable get() = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
val TextLow: Color @Composable get() = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.40f)
val TextFaint: Color @Composable get() = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.30f)

@Composable
private fun isLight() = MaterialTheme.colorScheme.background.luminance() > 0.5f

// ------------------------------------------------------------------ spacing

/** 8dp grid, per the design system. */
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
}

/** Corner radii: soft for UI chrome, tighter for chips and badges. */
object Radii {
    val chip = RoundedCornerShape(percent = 50)
    val badge = RoundedCornerShape(percent = 50)
    val input = RoundedCornerShape(12.dp)
    val button = RoundedCornerShape(14.dp)
    val card = RoundedCornerShape(16.dp)
    val panel = RoundedCornerShape(24.dp)
    val hero = RoundedCornerShape(28.dp)
}

// ------------------------------------------------------------------- fonts

/**
 * The web app used Nunito (display), Outfit (body) and JetBrains Mono (numbers).
 * Those are Google Fonts, so they can't be fetched at build time here — these
 * families stand in as platform equivalents.
 *
 * To match the original exactly, drop the three TTFs into
 * `app/src/main/res/font/` and swap these for
 * `FontFamily(Font(R.font.nunito_bold, FontWeight.Bold), ...)`.
 */
val DisplayFont = FontFamily.SansSerif
val BodyFont = FontFamily.SansSerif
val MonoFont = FontFamily.Monospace

// ------------------------------------------------------------------- theme

private val DarkColorScheme = darkColorScheme(
    primary = PokeRed,
    onPrimary = Color.White,
    secondary = PokeYellow,
    onSecondary = Color(0xFF1A1200),
    background = DarkAppBackground,
    onBackground = Color.White,
    surface = DarkAppBackground,
    onSurface = Color.White,
    surfaceVariant = Color.White.copy(alpha = 0.08f),
    onSurfaceVariant = Color.White.copy(alpha = 0.55f),
    outline = Color.White.copy(alpha = 0.12f),
)

private val LightColorScheme = lightColorScheme(
    primary = PokeRed,
    onPrimary = Color.White,
    secondary = PokeYellow,
    onSecondary = Color(0xFF1A1200),
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color.Black.copy(alpha = 0.05f),
    onSurfaceVariant = Color.Black.copy(alpha = 0.6f),
    outline = Color.Black.copy(alpha = 0.12f),
)

/**
 * App-wide theme.
 *
 * The design brief specifies a single dark "Pokédex at night" look, but we support
 * light mode by swapping the Material color scheme.
 */
@Composable
fun PokemonExplorerTheme(
    themeSetting: ThemeSetting = ThemeSetting.Dark,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeSetting) {
        ThemeSetting.Light -> false
        ThemeSetting.Dark -> true
        ThemeSetting.System -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content,
    )
}

/**
 * The soft type-coloured wash behind hero panels and cards: a diagonal gradient
 * from the primary type colour into the secondary, fading into the page background.
 */
@Composable
fun typeGradient(
    primary: Color,
    secondary: Color = primary,
    startAlpha: Float = 0.33f,
    endAlpha: Float = 0.19f,
    fadeTo: Color = MaterialTheme.colorScheme.background,
): Brush = Brush.linearGradient(
    listOf(
        primary.copy(alpha = startAlpha),
        secondary.copy(alpha = endAlpha),
        fadeTo,
    ),
)
