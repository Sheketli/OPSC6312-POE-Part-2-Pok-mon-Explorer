package com.pokemon.explorer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemon.explorer.state.TabId
import com.pokemon.explorer.ui.theme.Border08
import com.pokemon.explorer.ui.theme.DisplayFont
import com.pokemon.explorer.ui.theme.PokeRed

/**
 * Bottom navigation with a game-UI feel. Each destination gets a bespoke icon rather
 * than a generic outline glyph, and the active tab is marked by a red rule underneath.
 */
@Composable
fun BottomNav(
    activeTab: TabId,
    onSelect: (TabId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A0916), Color(0xFF12112A)),
                ),
            ),
    ) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(Border08))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            TabId.entries.forEach { tab ->
                NavTab(
                    tab = tab,
                    active = tab == activeTab,
                    onClick = { onSelect(tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun NavTab(
    tab: TabId,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        when (tab) {
            TabId.Explore -> ExploreNavIcon(active = active)
            TabId.Collection -> CollectionNavIcon(active = active)
            TabId.Compare -> CompareNavIcon(active = active)
            TabId.Guide -> GuideNavIcon(active = active)
        }

        Text(
            text = tab.label,
            color = if (active) Color.White else Color.White.copy(alpha = 0.35f),
            fontSize = 10.sp,
            fontFamily = DisplayFont,
            fontWeight = FontWeight.Bold,
        )

        // Active indicator rule.
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(if (active) PokeRed else Color.Transparent),
        )
    }
}
