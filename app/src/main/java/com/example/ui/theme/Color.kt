package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Premium Luxury Color Palette - "إثراء المصممين" (Ethraa Designers)
val LuxuryGold = Color(0xFFD4AF37)         // Classic Metallic Gold (الأزرق والذهبي سابقاً)
val LuxuryGoldBright = Color(0xFFFFD700)   // Shiny Accent Gold
val LuxuryGoldSoft = Color(0xFFE5C158)     // Soft Metallic gold
val LuxurySilver = Color(0xFFC0C0C0)       // Brushed Architectural Silver
val LuxurySilverDark = Color(0xFF7E7E82)   // Medium Silver for borders and secondary texts
val LuxurySilverLight = Color(0xFFE5E5EA)  // Light Silver for primary texts
val LuxuryBlackDeep = Color(0xFF050505)    // Pure Obsidian Black for background
val LuxuryBlackElevated = Color(0xFF111113)// Elevated surface container (Cards)
val LuxuryBlackVariant = Color(0xFF1C1C1E) // For text fields & borders

val LuxurySuccess = Color(0xFF10B981)      // Luxury Emerald Success Green
val LuxuryError = Color(0xFFD32F2F)        // Dark Premium Crimson Red

// Light Theme overrides mapped to luxury dark as well to ensure consistent premium branding
val PrimaryLight = LuxuryGold
val SecondaryLight = LuxurySilver
val TertiaryLight = LuxuryGoldBright
val BackgroundLight = LuxuryBlackDeep
val SurfaceLight = LuxuryBlackElevated
val OnPrimaryLight = Color(0xFF000000)
val OnSecondaryLight = Color(0xFF000000)
val OnBackgroundLight = LuxurySilverLight
val OnSurfaceLight = Color(0xFFFFFFFF)

// Dark Theme Palette mappings
val PrimaryDark = LuxuryGold
val SecondaryDark = LuxurySilver
val TertiaryDark = LuxuryGoldBright
val BackgroundDark = LuxuryBlackDeep
val SurfaceDark = LuxuryBlackElevated
val OnPrimaryDark = Color(0xFF000000)
val OnSecondaryDark = Color(0xFF000000)
val OnBackgroundDark = LuxurySilverLight
val OnSurfaceDark = Color(0xFFFFFFFF)
val SurfaceVariantDark = LuxuryBlackVariant

