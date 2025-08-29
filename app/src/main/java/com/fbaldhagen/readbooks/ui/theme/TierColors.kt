package com.fbaldhagen.readbooks.ui.theme

import androidx.compose.ui.graphics.Color

object BadgeColors {
    val LockedPrimary = Color(0xFF5A5A5A)
    val LockedAccent = Color(0x4D8E8E8E) // Gray with alpha

    private val Tier1Primary = Color(0xFFCD7F32) // Bronze
    private val Tier1Accent = Color(0x4D663500)

    private val Tier2Primary = Color(0xFFC0C0C0) // Silver
    private val Tier2Accent = Color(0x4D8E8E8E)

    private val Tier3Primary = Color(0xFFFFD700) // Gold
    private val Tier3Accent = Color(0x4DB8860B)

    val tierPairs = listOf(
        Tier1Primary to Tier1Accent,
        Tier2Primary to Tier2Accent,
        Tier3Primary to Tier3Accent
    )
}