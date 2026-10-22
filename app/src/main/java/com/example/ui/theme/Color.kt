package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==================================================
// NEXUS FINTECH DESIGN SYSTEM
// Premium dark-mode aesthetic with neon lime-green accent
// ==================================================

// Core Backgrounds & Surfaces
val FintechBackground = Color(0xFF0A0A0A)
val FintechSurface1 = Color(0xFF141414)      // Primary cards (#141414 - #161616)
val FintechSurface2 = Color(0xFF1A1A1A)      // Raised cards & secondary (#181818 - #1E1E1E)
val FintechSurface3 = Color(0xFF222222)      // Inputs, chip backgrounds (#222222 - #262626)
val FintechBorder = Color(0xFF262626)        // Thin subtle borders
val FintechBorderSubtle = Color(0xFF1E1E1E)
val FintechDivider = Color(0xFF1F1F1F)

// Neon Lime-Green Accents (#B6F23A / #A3E635)
val FintechLime = Color(0xFFB6F23A)          // Primary neon lime accent
val FintechLimeLight = Color(0xFFA3E635)     // Secondary lime tint
val FintechLimeDark = Color(0xFF0A0A0A)      // High contrast text/icon on lime
val FintechLimeGlow = Color(0x33B6F23A)      // Glow overlay (20% alpha)
val FintechLimeSubtle = Color(0x1FB6F23A)    // Badge background (12% alpha)
val FintechLimeMuted = Color(0x4DB6F23A)     // 30% alpha for inactive track

// Semantic Red (Strictly for negative values and expenses)
val FintechRed = Color(0xFFEF4444)           // Negative / Expense red
val FintechRedSubtle = Color(0x1FEF4444)     // Expense badge background
val FintechRedSparkline = Color(0xFFF87171)

// Typography & Text Hierarchy
val FintechTextPrimary = Color(0xFFFFFFFF)   // Pure white for headings & key figures
val FintechTextSecondary = Color(0xFF9CA3AF) // Muted gray (#9CA3AF / #A1A1AA)
val FintechTextMuted = Color(0xFF6B7280)     // Faint captions (#6B7280 / #71717A)

// Compatibility Aliases for existing components
val NexusBackground = FintechBackground
val NexusSurface1 = FintechSurface1
val NexusSurface2 = FintechSurface2
val NexusSurface3 = FintechSurface3
val NexusBorder = FintechBorder
val NexusBorderSubtle = FintechBorderSubtle
val NexusDivider = FintechDivider

val NexusIndigo = FintechLime
val NexusIndigoLight = FintechLimeLight
val NexusIndigoDark = FintechLimeDark
val NexusIndigoSoft = FintechLimeSubtle
val NexusIndigoMuted = FintechLimeMuted

val NexusSuccess = FintechLime
val NexusSuccessSoft = FintechLimeSubtle
val NexusWarning = Color(0xFFF59E0B)
val NexusWarningSoft = Color(0x1FF59E0B)
val NexusError = FintechRed
val NexusErrorSoft = FintechRedSubtle
val NexusInfo = FintechLime

val NexusTextPrimary = FintechTextPrimary
val NexusTextSecondary = FintechTextSecondary
val NexusTextMuted = FintechTextMuted
