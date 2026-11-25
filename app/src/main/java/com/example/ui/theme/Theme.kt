package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.theme.ChatOSTheme
import com.example.theme.LocalChatOSTheme
import com.example.theme.MultiThemeConfig
import com.example.theme.WorkspaceSection
import com.example.theme.toMaterialColorScheme

@Composable
fun ChatOSWorkspaceTheme(
    config: MultiThemeConfig = MultiThemeConfig(),
    content: @Composable () -> Unit
) {
    val globalTheme = config.getThemeForSection(WorkspaceSection.GLOBAL)
    val materialColorScheme = globalTheme.toMaterialColorScheme()

    CompositionLocalProvider(
        LocalChatOSTheme provides globalTheme
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = Typography,
            content = content
        )
    }
}
