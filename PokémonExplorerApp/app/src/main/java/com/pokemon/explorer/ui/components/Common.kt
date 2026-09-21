package com.pokemon.explorer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemon.explorer.ui.theme.Border08
import com.pokemon.explorer.ui.theme.DisplayFont
import com.pokemon.explorer.ui.theme.MonoFont
import com.pokemon.explorer.ui.theme.PokeRed
import com.pokemon.explorer.ui.theme.PokeYellow
import com.pokemon.explorer.ui.theme.Radii
import com.pokemon.explorer.ui.theme.Surface04
import com.pokemon.explorer.ui.theme.Surface05
import com.pokemon.explorer.ui.theme.Surface06
import com.pokemon.explorer.ui.theme.Surface07
import com.pokemon.explorer.ui.theme.Surface08
import com.pokemon.explorer.ui.theme.Surface10
import com.pokemon.explorer.ui.theme.TextFaint
import com.pokemon.explorer.ui.theme.TextHigh
import com.pokemon.explorer.ui.theme.TextLow

/**
 * Minimum touch target from the design brief (section 16). Interactive controls keep
 * their designed visual size and pad their hit area out to this.
 */
val MinTouchTarget = 48.dp

/**
 * Circular translucent icon button used for back, settings and header actions.
 *
 * The visible circle is [diameter] wide, but the touch area is padded out to at least
 * 48dp so it meets the design brief's minimum touch target. The padding is transparent,
 * so the button still looks like the smaller circle in the original design.
 */
@Composable
fun CircleIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    diameter: Int = 36,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .sizeIn(minWidth = MinTouchTarget, minHeight = MinTouchTarget)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(diameter.dp)
                .clip(Radii.badge)
                .background(Surface08),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

/** Standard screen header: optional back button plus a title row. */
@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (onBack != null) {
            CircleIconButton(onClick = onBack) {
                BackArrowIcon(iconSize = 18.dp)
            }
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextHigh,
                fontSize = 20.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Black,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = TextLow,
                    fontSize = 12.sp,
                    fontFamily = DisplayFont,
                )
            }
        }
        actions()
    }
}

/** Uppercase group heading used on the Settings screen. */
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title.uppercase(),
        color = TextFaint,
        fontSize = 10.sp,
        fontFamily = DisplayFont,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        modifier = modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
    )
}

/** Rounded container that groups rows in Settings. */
@Composable
fun SettingsGroup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(Radii.card)
            .background(Surface04)
            .border(1.dp, Border08, Radii.card),
    ) {
        content()
    }
}

/** A single label/value row inside a [SettingsGroup]. */
@Composable
fun SettingRow(
    label: String,
    modifier: Modifier = Modifier,
    sub: String? = null,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = label,
                color = TextHigh,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (!sub.isNullOrBlank()) {
                Text(
                    text = sub,
                    color = TextLow,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        Box(Modifier.padding(start = 16.dp)) { trailing() }
    }
}

/** Segmented control, e.g. the System / Light / Dark picker in Settings. */
@Composable
fun <T> SegmentedControl(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(Radii.input)
            .background(Surface07)
            .border(1.dp, com.pokemon.explorer.ui.theme.Border10, Radii.input)
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        options.forEach { option ->
            val active = option == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (active) PokeRed.copy(alpha = 0.80f) else Color.Transparent)
                    .clickable { onSelect(option) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = label(option),
                    color = if (active) Color.White else TextLow,
                    fontSize = 12.sp,
                    fontFamily = DisplayFont,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

/**
 * On/off switch styled in the app's red rather than the Material default. The track is
 * 44x24dp, wrapped in a 48dp-tall touch area.
 */
@Composable
fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .sizeIn(minWidth = MinTouchTarget, minHeight = MinTouchTarget)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 24.dp)
                .clip(Radii.badge)
                .background(if (checked) PokeRed else Surface10),
        ) {
            Box(
                modifier = Modifier
                    .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                    .padding(horizontal = 2.dp)
                    .size(20.dp)
                    .clip(Radii.badge)
                    .background(Color.White),
            )
        }
    }
}

/** Small pill showing a count, used on the Collection tabs. */
@Composable
fun CountBadge(count: Int, active: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = count.toString(),
        color = TextHigh,
        fontSize = 10.sp,
        fontFamily = MonoFont,
        modifier = modifier
            .clip(Radii.badge)
            .background(if (active) Color.White.copy(alpha = 0.15f) else Surface06)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

/**
 * Shared empty state: circular glyph, headline, explanation and one clear action.
 */
@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    glyph: @Composable () -> Unit = {},
    action: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(Radii.badge)
                .background(Surface05),
            contentAlignment = Alignment.Center,
        ) {
            glyph()
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = TextHigh,
                fontSize = 18.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                color = TextLow,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
        action()
    }
}

/** Thin 1dp divider matching the app's hairline borders. */
@Composable
fun HairlineDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Surface07),
    )
}

/** Small stat card used for Height / Weight on the Detail screen. */
@Composable
fun InfoCard(
    icon: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(Radii.card)
            .background(Surface05)
            .border(1.dp, Border08, Radii.card)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = icon, fontSize = 20.sp)
        Text(
            text = value,
            color = TextHigh,
            fontSize = 16.sp,
            fontFamily = MonoFont,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = label,
            color = TextLow,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

/** Full-width chip used for abilities and moves. */
@Composable
fun InfoChip(
    text: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (highlighted) Color(0xFFFFCB05).copy(alpha = 0.12f) else Surface07,
            )
            .border(
                1.dp,
                if (highlighted) Color(0xFFFFCB05).copy(alpha = 0.30f) else com.pokemon.explorer.ui.theme.Border10,
                RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            color = if (highlighted) Color(0xFFFFCB05) else TextHigh,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/** `pikachu` -> `Pikachu`, `lightning-rod` -> `Lightning Rod`. */
fun prettyName(raw: String): String =
    raw.replace('-', ' ')
        .split(' ')
        .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }

/** Placeholder text colour shared by the app's text fields. */
val PlaceholderColor: Color @Composable get() = TextFaint

/** Background tint for unselected filter chips. */
val ChipIdleBackground: Color @Composable get() = Surface08

/**
 * The app's signature logo: yellow dots above "Pokémon" in red and "Explorer" in white.
 */
@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    size: Float = 1f,
    showIcon: Boolean = false,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (showIcon) {
            FullPokeball(
                size = (80 * size).dp,
                opening = true,
                modifier = Modifier.padding(bottom = (16 * size).dp)
            )
        }
        Text(
            text = "· · ·",
            color = PokeYellow,
            fontSize = (13 * size).sp,
            fontFamily = DisplayFont,
            fontWeight = FontWeight.Bold,
            letterSpacing = (6 * size).sp,
        )
        Text(
            text = "Pokémon",
            color = PokeRed,
            fontSize = (36 * size).sp,
            fontFamily = DisplayFont,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Explorer",
            color = TextHigh,
            fontSize = (36 * size).sp,
            fontFamily = DisplayFont,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
        )
    }
}
