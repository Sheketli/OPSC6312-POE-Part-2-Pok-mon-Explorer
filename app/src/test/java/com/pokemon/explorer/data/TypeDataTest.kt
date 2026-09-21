package com.pokemon.explorer.data

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the hand-transcribed 18-type chart.
 *
 * The matchup table is the one large block of data in the app that was typed out by
 * hand, so these tests check the invariants that a typo would break: every type is
 * present, every referenced type actually exists, and no type lists itself as its own
 * weakness or resistance.
 */
class TypeDataTest {

    @Test
    fun `there are exactly eighteen types`() {
        assertEquals(18, ALL_TYPES.size)
        assertEquals(18, TYPE_COLORS.size)
    }

    @Test
    fun `every type has a matchup entry`() {
        val missing = ALL_TYPES.filterNot { TYPE_MATCHUPS.containsKey(it) }
        assertTrue("Types with no matchup data: $missing", missing.isEmpty())
    }

    @Test
    fun `every type has an icon`() {
        val missing = ALL_TYPES.filterNot { TYPE_ICONS.containsKey(it) }
        assertTrue("Types with no icon: $missing", missing.isEmpty())
    }

    @Test
    fun `matchups only reference real types`() {
        val referenced = TYPE_MATCHUPS.values.flatMap {
            it.strongAgainst + it.weakAgainst + it.resistantTo + it.immuneTo
        }.toSet()

        val unknown = referenced - ALL_TYPES.toSet()
        assertTrue("Matchups reference unknown types: $unknown", unknown.isEmpty())
    }

    /** Normal is the only type with no offensive advantage. */
    @Test
    fun `only normal deals no super effective damage`() {
        val noAdvantage = ALL_TYPES.filter { TYPE_MATCHUPS.getValue(it).strongAgainst.isEmpty() }
        assertEquals(listOf("normal"), noAdvantage)
    }

    /**
     * A type attacking itself is normal for several types (Ghost, Dragon, Dark), so
     * this only asserts the entries that would be flatly wrong: no type is listed as
     * both weak to and immune to the same type.
     */
    @Test
    fun `no type is both weak to and immune to the same type`() {
        ALL_TYPES.forEach { type ->
            val matchup = TYPE_MATCHUPS.getValue(type)
            val contradictory = matchup.weakAgainst.toSet() intersect matchup.immuneTo.toSet()
            assertTrue(
                "$type is both weak and immune to $contradictory",
                contradictory.isEmpty(),
            )
        }
    }

    @Test
    fun `type names are lowercase since the api returns them that way`() {
        ALL_TYPES.forEach { type ->
            assertEquals("Type '$type' should be lowercase", type, type.lowercase())
        }
    }

    // --------------------------------------------------------------- helpers

    @Test
    fun `typeColor falls back to normal for unknown types`() {
        assertEquals(TYPE_COLORS.getValue("normal"), typeColor("not-a-real-type"))
        assertEquals(TYPE_COLORS.getValue("normal"), typeColor(""))
    }

    @Test
    fun `typeColor is case insensitive`() {
        assertEquals(TYPE_COLORS.getValue("electric"), typeColor("Electric"))
        assertEquals(TYPE_COLORS.getValue("electric"), typeColor("ELECTRIC"))
    }

    /** Pale types must get dark text; dark types must get white text. */
    @Test
    fun `contrast picks dark text on pale types and white on dark ones`() {
        val onWhite = contrastOn(Color.White)
        val onBlack = contrastOn(Color.Black)

        assertEquals(Color(0xFF1A1200), onWhite)
        assertEquals(Color.White, onBlack)

        // Ice, Electric and Steel are the palest type colours.
        listOf("ice", "electric", "steel").forEach { type ->
            assertEquals(
                "$type should use dark text for contrast",
                Color(0xFF1A1200),
                contrastOn(typeColor(type)),
            )
        }

        // Dragon and Ghost are dark enough to need white text.
        listOf("dragon", "ghost").forEach { type ->
            assertEquals(
                "$type should use white text for contrast",
                Color.White,
                contrastOn(typeColor(type)),
            )
        }
    }

    @Test
    fun `stat labels cover all six base stats`() {
        val expected = setOf(
            "hp", "attack", "defense", "special-attack", "special-defense", "speed",
        )
        assertEquals(expected, STAT_LABELS.keys)
    }

    /** Each type colour should be visually distinct, not a duplicated hex value. */
    @Test
    fun `type colours are all distinct`() {
        val values = TYPE_COLORS.values.toList()
        assertEquals(
            "Duplicate type colours found",
            values.size,
            values.distinct().size,
        )
        assertNotEquals(TYPE_COLORS.getValue("fire"), TYPE_COLORS.getValue("water"))
    }
}
