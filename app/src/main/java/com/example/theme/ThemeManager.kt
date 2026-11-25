package com.example.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.lerp

enum class WorkspaceSection(val displayName: String, val description: String) {
    GLOBAL("Global Theme", "Base theme for the overall workspace canvas"),
    SIDEBAR("Sidebar Navigation", "Left panel navigation bar & quick launchers"),
    TOP_BAR("Top Command Bar", "Header with model selector & command actions"),
    CHAT("Chat & Agents", "Messages, conversation stream & tool approval cards"),
    EDITOR("Docs & Research", "Writing space, code editor, deep research and compare"),
    ISLAND("Floating Action Island", "Bottom floating agent status & tool launcher")
}

data class ThemePreset(
    val id: String,
    val name: String,
    val description: String,
    val globalThemeId: String,
    val sidebarThemeId: String,
    val topBarThemeId: String,
    val chatThemeId: String,
    val editorThemeId: String,
    val islandThemeId: String,
    val blendThemeId: String? = null,
    val blendAmount: Float = 0f
)

object PresetRegistry {
    val PRESETS = listOf(
        ThemePreset(
            id = "preset_obsidian_crimson",
            name = "Obsidian Crimson Core",
            description = "Signature Obsidian Black & Crimson Red workspace with Carbon Scarlet sidebar",
            globalThemeId = "obsidian_crimson",
            sidebarThemeId = "carbon_scarlet",
            topBarThemeId = "obsidian_crimson",
            chatThemeId = "obsidian_crimson",
            editorThemeId = "vanta_ruby",
            islandThemeId = "obsidian_crimson"
        ),
        ThemePreset(
            id = "preset_crimson_katana",
            name = "Crimson Katana Suite",
            description = "Forged iron black with arterial red chat and Monolith Noir writing space",
            globalThemeId = "crimson_katana",
            sidebarThemeId = "obsidian_crimson",
            topBarThemeId = "crimson_katana",
            chatThemeId = "obsidian_crimson",
            editorThemeId = "monolith_noir",
            islandThemeId = "crimson_katana"
        ),
        ThemePreset(
            id = "preset_blood_moon",
            name = "Blood Moon Overdrive",
            description = "Deep carmine eclipse fused with Red Team Terminal and Hot Magma glow",
            globalThemeId = "blood_moon",
            sidebarThemeId = "vanta_ruby",
            topBarThemeId = "blood_moon",
            chatThemeId = "obsidian_crimson",
            editorThemeId = "red_team_terminal",
            islandThemeId = "blood_moon",
            blendThemeId = "H",
            blendAmount = 0.2f
        ),
        ThemePreset(
            id = "preset_vanta_stealth",
            name = "Vanta AMOLED Stealth",
            description = "Zero-light AMOLED pure black with glowing ruby and titanium accents",
            globalThemeId = "vanta_ruby",
            sidebarThemeId = "monolith_noir",
            topBarThemeId = "vanta_ruby",
            chatThemeId = "vanta_ruby",
            editorThemeId = "carbon_scarlet",
            islandThemeId = "obsidian_crimson"
        ),
        ThemePreset(
            id = "preset_focus",
            name = "Focus Architect",
            description = "Ivory Silk clarity with Obsidian Gold depth for distraction-free deep work",
            globalThemeId = "O",
            sidebarThemeId = "L",
            topBarThemeId = "O",
            chatThemeId = "I",
            editorThemeId = "I",
            islandThemeId = "O"
        ),
        ThemePreset(
            id = "preset_late_night",
            name = "Late Night Hacker",
            description = "Terminal Pro phosphor green mixed with Cyber Neon Tokyo lasers",
            globalThemeId = "T",
            sidebarThemeId = "C",
            topBarThemeId = "T",
            chatThemeId = "T",
            editorThemeId = "R",
            islandThemeId = "C",
            blendThemeId = "C",
            blendAmount = 0.25f
        ),
        ThemePreset(
            id = "preset_deep_research",
            name = "Deep Research Voyager",
            description = "Galactic Void infinity combined with Aurora Borealis bioluminescence",
            globalThemeId = "G",
            sidebarThemeId = "A",
            topBarThemeId = "G",
            chatThemeId = "A",
            editorThemeId = "G",
            islandThemeId = "A",
            blendThemeId = "A",
            blendAmount = 0.35f
        ),
        ThemePreset(
            id = "preset_cyberpunk",
            name = "Neo-Tokyo Cyberpunk",
            description = "High-voltage Neon Noir rose and Tokyo cyan for intense creative sessions",
            globalThemeId = "C",
            sidebarThemeId = "N",
            topBarThemeId = "C",
            chatThemeId = "C",
            editorThemeId = "N",
            islandThemeId = "C"
        ),
        ThemePreset(
            id = "preset_solstice",
            name = "Glacial Solstice",
            description = "Permafrost ice blue and Frosted Glass for soothing Scandinavian calm",
            globalThemeId = "W",
            sidebarThemeId = "F",
            topBarThemeId = "W",
            chatThemeId = "F",
            editorThemeId = "W",
            islandThemeId = "F"
        ),
        ThemePreset(
            id = "preset_volcanic",
            name = "Volcanic Magma",
            description = "Hot Magma basalt rift and Solar Flare energy for turbocharged workflows",
            globalThemeId = "H",
            sidebarThemeId = "S",
            topBarThemeId = "H",
            chatThemeId = "H",
            editorThemeId = "S",
            islandThemeId = "H"
        ),
        ThemePreset(
            id = "preset_quantum",
            name = "Quantum Superposition",
            description = "Zero Gravity weightlessness fused with Quantum Flux entangled indigo",
            globalThemeId = "Q",
            sidebarThemeId = "Z",
            topBarThemeId = "Q",
            chatThemeId = "Q",
            editorThemeId = "Z",
            islandThemeId = "Z"
        ),
        ThemePreset(
            id = "preset_brutalist",
            name = "Industrial Brutalist",
            description = "Raw Concrete slate and Kinetic Energy yellow alerts for utilitarian precision",
            globalThemeId = "B",
            sidebarThemeId = "U",
            topBarThemeId = "B",
            chatThemeId = "B",
            editorThemeId = "K",
            islandThemeId = "B"
        )
    )
}

data class MultiThemeConfig(
    val globalTheme: ChatOSTheme = ThemeRegistry.DEFAULT_THEME,
    val sidebarTheme: ChatOSTheme? = null,
    val topBarTheme: ChatOSTheme? = null,
    val chatTheme: ChatOSTheme? = null,
    val editorTheme: ChatOSTheme? = null,
    val islandTheme: ChatOSTheme? = null,
    val blendSecondaryTheme: ChatOSTheme? = null,
    val blendIntensity: Float = 0f,
    val activePresetId: String? = "preset_obsidian_crimson"
) {
    fun getThemeForSection(section: WorkspaceSection): ChatOSTheme {
        val base = when (section) {
            WorkspaceSection.GLOBAL -> globalTheme
            WorkspaceSection.SIDEBAR -> sidebarTheme ?: globalTheme
            WorkspaceSection.TOP_BAR -> topBarTheme ?: globalTheme
            WorkspaceSection.CHAT -> chatTheme ?: globalTheme
            WorkspaceSection.EDITOR -> editorTheme ?: globalTheme
            WorkspaceSection.ISLAND -> islandTheme ?: globalTheme
        }

        return if (blendSecondaryTheme != null && blendIntensity > 0.01f) {
            blendThemes(base, blendSecondaryTheme, blendIntensity)
        } else {
            base
        }
    }
}

/**
 * High-performance smooth blending of two ChatOS themes by interpolating all RGB channels.
 */
fun blendThemes(a: ChatOSTheme, b: ChatOSTheme, fraction: Float): ChatOSTheme {
    val f = fraction.coerceIn(0f, 1f)
    return ChatOSTheme(
        id = "blend_${a.id}_${b.id}",
        badgeCode = "${a.badgeCode}×${b.badgeCode}",
        letter = a.letter,
        name = "${a.name} × ${b.name}",
        tagLine = "Live Blend [${(100 - f * 100).toInt()}% ${a.name} / ${(f * 100).toInt()}% ${b.name}]",
        emotionalTone = "Harmonized fusion of ${a.name} and ${b.name}",
        materialStyle = if (f < 0.5f) a.materialStyle else b.materialStyle,
        category = a.category,
        primary = lerp(a.primary, b.primary, f),
        onPrimary = lerp(a.onPrimary, b.onPrimary, f),
        primaryContainer = lerp(a.primaryContainer, b.primaryContainer, f),
        onPrimaryContainer = lerp(a.onPrimaryContainer, b.onPrimaryContainer, f),
        secondary = lerp(a.secondary, b.secondary, f),
        onSecondary = lerp(a.onSecondary, b.onSecondary, f),
        secondaryContainer = lerp(a.secondaryContainer, b.secondaryContainer, f),
        onSecondaryContainer = lerp(a.onSecondaryContainer, b.onSecondaryContainer, f),
        tertiary = lerp(a.tertiary, b.tertiary, f),
        background = lerp(a.background, b.background, f),
        onBackground = lerp(a.onBackground, b.onBackground, f),
        surface = lerp(a.surface, b.surface, f),
        onSurface = lerp(a.onSurface, b.onSurface, f),
        surfaceVariant = lerp(a.surfaceVariant, b.surfaceVariant, f),
        onSurfaceVariant = lerp(a.onSurfaceVariant, b.onSurfaceVariant, f),
        surfaceBorder = lerp(a.surfaceBorder, b.surfaceBorder, f),
        accentGlow = lerp(a.accentGlow, b.accentGlow, f),
        codeBackground = lerp(a.codeBackground, b.codeBackground, f),
        terminalPrompt = lerp(a.terminalPrompt, b.terminalPrompt, f),
        agentBadgeColor = lerp(a.agentBadgeColor, b.agentBadgeColor, f),
        isDark = a.isDark
    )
}

fun ChatOSTheme.toMaterialColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        outline = surfaceBorder
    )
}

val LocalChatOSTheme = compositionLocalOf { ThemeRegistry.DEFAULT_THEME }
val LocalSectionTheme = compositionLocalOf { WorkspaceSection.GLOBAL }

@Composable
fun SectionThemeProvider(
    section: WorkspaceSection,
    config: MultiThemeConfig,
    content: @Composable () -> Unit
) {
    val theme = config.getThemeForSection(section)
    CompositionLocalProvider(
        LocalChatOSTheme provides theme,
        LocalSectionTheme provides section
    ) {
        content()
    }
}
