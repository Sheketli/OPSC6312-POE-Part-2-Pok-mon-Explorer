package com.pokemon.explorer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemon.explorer.data.contrastOn
import com.pokemon.explorer.data.typeColor
import com.pokemon.explorer.ui.theme.DisplayFont

enum class BadgeSize(val horizontal: Int, val vertical: Int, val fontSize: TextUnit) {
    Small(8, 2, 10.sp),
    Medium(12, 4, 12.sp),
    Large(16, 6, 14.sp),
}

/**
 * A type pill, e.g. "ELECTRIC". The background is the canonical type colour and the
 * label flips between black and white so pale types stay readable.
 */
@Composable
fun TypeBadge(
    type: String,
    modifier: Modifier = Modifier,
    size: BadgeSize = BadgeSize.Medium,
) {
    val background = typeColor(type)
    Text(
        text = type.uppercase(),
        color = contrastOn(background),
        fontSize = size.fontSize,
        fontFamily = DisplayFont,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        maxLines = 1,
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(background)
            .padding(horizontal = size.horizontal.dp, vertical = size.vertical.dp),
    )
}
