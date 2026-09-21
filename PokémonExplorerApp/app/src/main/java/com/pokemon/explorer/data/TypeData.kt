package com.pokemon.explorer.data

import androidx.compose.ui.graphics.Color

/**
 * Canonical Pokémon type colours, used for badges, card gradients and hero panels.
 * Ported verbatim from the web app's `typeData.ts`.
 */
val TYPE_COLORS: Map<String, Color> = linkedMapOf(
    "normal" to Color(0xFFA8A77A),
    "fire" to Color(0xFFEE8130),
    "water" to Color(0xFF6390F0),
    "electric" to Color(0xFFF7D02C),
    "grass" to Color(0xFF7AC74C),
    "ice" to Color(0xFF96D9D6),
    "fighting" to Color(0xFFC22E28),
    "poison" to Color(0xFFA33EA1),
    "ground" to Color(0xFFE2BF65),
    "flying" to Color(0xFFA98FF3),
    "psychic" to Color(0xFFF95587),
    "bug" to Color(0xFFA6B91A),
    "rock" to Color(0xFFB6A136),
    "ghost" to Color(0xFF735797),
    "dragon" to Color(0xFF6F35FC),
    "dark" to Color(0xFF705746),
    "steel" to Color(0xFFB7B7CE),
    "fairy" to Color(0xFFD685AD),
)

/** All 18 types, in Pokédex order. */
val ALL_TYPES: List<String> = TYPE_COLORS.keys.toList()

private val FALLBACK_TYPE_COLOR = Color(0xFFA8A77A)

/** Colour for a type name, falling back to Normal for unknown values. */
fun typeColor(type: String): Color = TYPE_COLORS[type.lowercase()] ?: FALLBACK_TYPE_COLOR

private fun luminance(color: Color): Float =
    0.299f * color.red + 0.587f * color.green + 0.114f * color.blue

/**
 * Picks black or white text for a given type background, so badges stay legible on
 * pale types (Electric, Ice, Steel) as well as dark ones.
 */
fun contrastOn(color: Color): Color =
    if (luminance(color) > 0.55f) Color(0xFF1A1200) else Color.White

data class TypeMatchup(
    val strongAgainst: List<String>,
    val weakAgainst: List<String>,
    val resistantTo: List<String>,
    val immuneTo: List<String>,
)

/**
 * Offensive / defensive type chart used by the Type Guide.
 *
 * "strongAgainst" is what the type deals super-effective damage to, "weakAgainst"
 * what it deals reduced damage to, and the last two are its defensive resistances
 * and immunities.
 */
val TYPE_MATCHUPS: Map<String, TypeMatchup> = mapOf(
    "normal" to TypeMatchup(
        strongAgainst = emptyList(),
        weakAgainst = listOf("fighting"),
        resistantTo = emptyList(),
        immuneTo = listOf("ghost"),
    ),
    "fire" to TypeMatchup(
        strongAgainst = listOf("grass", "ice", "bug", "steel"),
        weakAgainst = listOf("water", "ground", "rock"),
        resistantTo = listOf("fire", "grass", "ice", "bug", "steel", "fairy"),
        immuneTo = emptyList(),
    ),
    "water" to TypeMatchup(
        strongAgainst = listOf("fire", "ground", "rock"),
        weakAgainst = listOf("electric", "grass"),
        resistantTo = listOf("fire", "water", "ice", "steel"),
        immuneTo = emptyList(),
    ),
    "electric" to TypeMatchup(
        strongAgainst = listOf("water", "flying"),
        weakAgainst = listOf("ground"),
        resistantTo = listOf("electric", "flying", "steel"),
        immuneTo = emptyList(),
    ),
    "grass" to TypeMatchup(
        strongAgainst = listOf("water", "ground", "rock"),
        weakAgainst = listOf("fire", "ice", "poison", "flying", "bug"),
        resistantTo = listOf("water", "electric", "grass", "ground"),
        immuneTo = emptyList(),
    ),
    "ice" to TypeMatchup(
        strongAgainst = listOf("grass", "ground", "flying", "dragon"),
        weakAgainst = listOf("fire", "fighting", "rock", "steel"),
        resistantTo = listOf("ice"),
        immuneTo = emptyList(),
    ),
    "fighting" to TypeMatchup(
        strongAgainst = listOf("normal", "ice", "rock", "dark", "steel"),
        weakAgainst = listOf("flying", "psychic", "fairy"),
        resistantTo = listOf("bug", "rock", "dark"),
        immuneTo = emptyList(),
    ),
    "poison" to TypeMatchup(
        strongAgainst = listOf("grass", "fairy"),
        weakAgainst = listOf("ground", "psychic"),
        resistantTo = listOf("grass", "fighting", "poison", "bug", "fairy"),
        immuneTo = emptyList(),
    ),
    "ground" to TypeMatchup(
        strongAgainst = listOf("fire", "electric", "poison", "rock", "steel"),
        weakAgainst = listOf("water", "grass", "ice"),
        resistantTo = listOf("poison", "rock"),
        immuneTo = listOf("electric"),
    ),
    "flying" to TypeMatchup(
        strongAgainst = listOf("grass", "fighting", "bug"),
        weakAgainst = listOf("electric", "ice", "rock"),
        resistantTo = listOf("grass", "fighting", "bug"),
        immuneTo = listOf("ground"),
    ),
    "psychic" to TypeMatchup(
        strongAgainst = listOf("fighting", "poison"),
        weakAgainst = listOf("bug", "ghost", "dark"),
        resistantTo = listOf("fighting", "psychic"),
        immuneTo = emptyList(),
    ),
    "bug" to TypeMatchup(
        strongAgainst = listOf("grass", "psychic", "dark"),
        weakAgainst = listOf("fire", "flying", "rock"),
        resistantTo = listOf("grass", "fighting", "ground"),
        immuneTo = emptyList(),
    ),
    "rock" to TypeMatchup(
        strongAgainst = listOf("fire", "ice", "flying", "bug"),
        weakAgainst = listOf("water", "grass", "fighting", "ground", "steel"),
        resistantTo = listOf("normal", "fire", "poison", "flying"),
        immuneTo = emptyList(),
    ),
    "ghost" to TypeMatchup(
        strongAgainst = listOf("psychic", "ghost"),
        weakAgainst = listOf("ghost", "dark"),
        resistantTo = listOf("poison", "bug"),
        immuneTo = listOf("normal", "fighting"),
    ),
    "dragon" to TypeMatchup(
        strongAgainst = listOf("dragon"),
        weakAgainst = listOf("ice", "dragon", "fairy"),
        resistantTo = listOf("fire", "water", "electric", "grass"),
        immuneTo = emptyList(),
    ),
    "dark" to TypeMatchup(
        strongAgainst = listOf("psychic", "ghost"),
        weakAgainst = listOf("fighting", "bug", "fairy"),
        resistantTo = listOf("ghost", "dark"),
        immuneTo = listOf("psychic"),
    ),
    "steel" to TypeMatchup(
        strongAgainst = listOf("ice", "rock", "fairy"),
        weakAgainst = listOf("fire", "fighting", "ground"),
        resistantTo = listOf(
            "normal", "grass", "ice", "flying", "psychic",
            "bug", "rock", "dragon", "steel", "fairy",
        ),
        immuneTo = listOf("poison"),
    ),
    "fairy" to TypeMatchup(
        strongAgainst = listOf("fighting", "dragon", "dark"),
        weakAgainst = listOf("poison", "steel"),
        resistantTo = listOf("fighting", "bug", "dark"),
        immuneTo = listOf("dragon"),
    ),
)

/** Short, HP-counter style labels for the six base stats. */
val STAT_LABELS: Map<String, String> = mapOf(
    "hp" to "HP",
    "attack" to "ATK",
    "defense" to "DEF",
    "special-attack" to "SP.ATK",
    "special-defense" to "SP.DEF",
    "speed" to "SPD",
)

/** Emoji glyphs used as the type icons on the Type Guide cards. */
val TYPE_ICONS: Map<String, String> = mapOf(
    "normal" to "⭐",
    "fire" to "🔥",
    "water" to "💧",
    "electric" to "⚡",
    "grass" to "🌿",
    "ice" to "❄️",
    "fighting" to "👊",
    "poison" to "☠️",
    "ground" to "🏔️",
    "flying" to "🦅",
    "psychic" to "🔮",
    "bug" to "🐛",
    "rock" to "💎",
    "ghost" to "👻",
    "dragon" to "🐉",
    "dark" to "🌑",
    "steel" to "⚙️",
    "fairy" to "✨",
)
