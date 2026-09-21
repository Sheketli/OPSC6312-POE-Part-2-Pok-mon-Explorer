package com.pokemon.explorer.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.pokemon.explorer.ui.theme.Radii
import com.pokemon.explorer.ui.theme.Surface05

/**
 * A moving highlight used by every skeleton placeholder. The sweep is a diagonal
 * gradient translated across the shape, matching the web app's shimmer.
 */
@Composable
fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = -400f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
        ),
        label = "shimmer-offset",
    )
    return Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.03f),
            Color.White.copy(alpha = 0.08f),
            Color.White.copy(alpha = 0.03f),
        ),
        start = Offset(offset, 0f),
        end = Offset(offset + 400f, 220f),
    )
}

/** A single shimmering placeholder block. */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    Box(modifier = modifier.clip(shape).background(rememberShimmerBrush()))
}

/** Placeholder matching the footprint of [PokemonCard]. */
@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(Radii.card)
            .background(Surface05)
            .padding(12.dp),
    ) {
        SkeletonBox(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            shape = RoundedCornerShape(12.dp),
        )
        Box(Modifier.height(12.dp))
        SkeletonBox(Modifier.size(width = 40.dp, height = 12.dp))
        Box(Modifier.height(6.dp))
        SkeletonBox(Modifier.size(width = 80.dp, height = 16.dp))
        Box(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            SkeletonBox(Modifier.size(width = 48.dp, height = 16.dp), Radii.badge)
            SkeletonBox(Modifier.size(width = 48.dp, height = 16.dp), Radii.badge)
        }
    }
}

/** Placeholder matching the Detail screen while its data loads. */
@Composable
fun SkeletonDetail(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        SkeletonBox(Modifier.fillMaxWidth().height(256.dp), RoundedCornerShape(0.dp))
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SkeletonBox(Modifier.size(width = 128.dp, height = 24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonBox(Modifier.size(width = 64.dp, height = 24.dp), Radii.badge)
                SkeletonBox(Modifier.size(width = 64.dp, height = 24.dp), Radii.badge)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SkeletonBox(Modifier.weight(1f).height(64.dp), RoundedCornerShape(12.dp))
                SkeletonBox(Modifier.weight(1f).height(64.dp), RoundedCornerShape(12.dp))
            }
            repeat(6) {
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SkeletonBox(Modifier.size(width = 48.dp, height = 12.dp))
                    SkeletonBox(Modifier.weight(1f).height(10.dp), Radii.badge)
                    SkeletonBox(Modifier.size(width = 32.dp, height = 16.dp))
                }
            }
        }
    }
}
