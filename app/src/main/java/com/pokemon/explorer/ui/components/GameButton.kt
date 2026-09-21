package com.pokemon.explorer.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pokemon.explorer.ui.theme.Radii

enum class GameButtonStyle { Red, Yellow, Outline }

private data class ButtonSkin(
    val top: Color,
    val bottom: Color,
    val hasLedge: Boolean,
)

private fun skinFor(style: GameButtonStyle): ButtonSkin = when (style) {
    GameButtonStyle.Red -> ButtonSkin(
        top = Color(0xFFF53030),
        bottom = Color(0xFFC00F0F),
        hasLedge = true,
    )
    GameButtonStyle.Yellow -> ButtonSkin(
        top = Color(0xFFFFD700),
        bottom = Color(0xFFE6A800),
        hasLedge = true,
    )
    GameButtonStyle.Outline -> ButtonSkin(
        top = Color.Transparent,
        bottom = Color.Transparent,
        hasLedge = false,
    )
}

private val LEDGE_COLORS = mapOf(
    GameButtonStyle.Red to Color(0xFF8A0000),
    GameButtonStyle.Yellow to Color(0xFF9A6F00),
)

/**
 * The chunky, bevelled "game UI" button from the design system.
 *
 * The bevel is a hard-edged colour block sitting 4dp below the face; pressing shifts
 * the face down onto it, which reads like a physical key being pushed.
 */
@Composable
fun GameButton(
    style: GameButtonStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    content: @Composable RowScope.() -> Unit,
) {
    val skin = skinFor(style)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    // Only shift when there is a ledge to press into.
    val shift by animateDpAsState(
        targetValue = if (pressed && skin.hasLedge) 3.dp else 0.dp,
        label = "button-press",
    )

    Box(modifier = modifier) {
        if (skin.hasLedge) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = 4.dp)
                    .clip(Radii.button)
                    .background(LEDGE_COLORS.getValue(style)),
            )
        }

        Box(
            modifier = Modifier
                .offset(y = shift)
                .clip(Radii.button)
                .then(
                    if (style == GameButtonStyle.Outline) {
                        Modifier
                            .background(Color.Transparent)
                            .border(2.dp, Color.White.copy(alpha = 0.25f), Radii.button)
                    } else {
                        Modifier.background(
                            Brush.verticalGradient(listOf(skin.top, skin.bottom)),
                        )
                    },
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick,
                )
                .padding(contentPadding),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.foundation.layout.Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                content = content,
            )
        }
    }
}
