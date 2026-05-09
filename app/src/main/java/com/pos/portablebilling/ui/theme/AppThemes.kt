package com.pos.portablebilling.ui.theme

import androidx.compose.ui.graphics.Color

data class AppTheme(val name: String, val start: Color, val end: Color)

val appThemes = listOf(
    AppTheme("Indigo",   Color(0xFF6200EA), Color(0xFF3F51B5)),
    AppTheme("Ocean",    Color(0xFF0277BD), Color(0xFF00ACC1)),
    AppTheme("Forest",   Color(0xFF2E7D32), Color(0xFF00897B)),
    AppTheme("Sunset",   Color(0xFFE64A19), Color(0xFFF9A825)),
    AppTheme("Rose",     Color(0xFFC2185B), Color(0xFF880E4F)),
    AppTheme("Midnight", Color(0xFF1A1A2E), Color(0xFF16213E)),
)

// Single accent colors for soft card design
val cardAccents = listOf(
    Color(0xFF3F51B5), // Indigo
    Color(0xFFD81B60), // Pink
    Color(0xFF00897B), // Teal
    Color(0xFFEF6C00), // Orange
    Color(0xFF1976D2), // Blue
    Color(0xFF7B1FA2), // Purple
    Color(0xFF2E7D32), // Green
    Color(0xFFC62828), // Red
)
