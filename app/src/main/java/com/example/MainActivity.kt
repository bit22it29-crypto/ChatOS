package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.WorkspaceTab
import com.example.theme.SectionThemeProvider
import com.example.theme.WorkspaceSection
import com.example.ui.components.CommandPaletteDialog
import com.example.ui.components.FloatingActionIsland
import com.example.ui.components.ThemeBlenderModal
import com.example.ui.components.TopCommandBar
import com.example.ui.navigation.ChatOSDrawer
import com.example.ui.screens.*
import com.example.ui.theme.ChatOSWorkspaceTheme
import com.example.viewmodel.ChatOSViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        setContent {
            val viewModel: ChatOSViewModel = viewModel()
            val themeConfig by viewModel.themeConfig.collectAsState()

            ChatOSWorkspaceTheme(config = themeConfig) {
                ChatOSApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatOSApp(viewModel: ChatOSViewModel) {
    val themeConfig by viewModel.themeConfig.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val isPaletteOpen by viewModel.isCommandPaletteOpen.collectAsState()
    val isBlenderOpen by viewModel.isThemeBlenderOpen.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val isAgentMode by viewModel.isAgentMode.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val researchSession by viewModel.researchSession.collectAsState()
    val hardwareScan by viewModel.hardwareScan.collectAsState()
    val activeEngine by viewModel.activeServingEngine.collectAsState()
    val tokensPerSec by viewModel.benchmarkTokensPerSec.collectAsState()
    val compareEval by viewModel.compareEvaluation.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val activeDocId by viewModel.activeDocId.collectAsState()
    val emails by viewModel.emails.collectAsState()
    val selectedEmailId by viewModel.selectedEmailId.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val calEvents by viewModel.calendarEvents.collectAsState()
    val memories by viewModel.chromaMemories.collectAsState()
    val skills by viewModel.agentSkills.collectAsState()
    val mcpServers by viewModel.mcpServers.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SectionThemeProvider(
                section = WorkspaceSection.SIDEBAR,
                config = themeConfig
            ) {
                ModalDrawerSheet(
                    drawerContainerColor = themeConfig.getThemeForSection(WorkspaceSection.SIDEBAR).surface,
                    modifier = Modifier.width(280.dp)
                ) {
                    ChatOSDrawer(
                        currentTab = currentTab,
                        activeTheme = themeConfig.globalTheme,
                        onSelectTab = { viewModel.selectTab(it) },
                        onNewChat = { viewModel.sendMessage("Hello ChatOS Agent!") },
                        onOpenSearch = { viewModel.openCommandPalette() },
                        onQuickSelectTheme = { viewModel.setGlobalTheme(it) },
                        onOpenThemeBlender = { viewModel.openThemeBlender() },
                        onCloseDrawer = { coroutineScope.launch { drawerState.close() } }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                SectionThemeProvider(
                    section = WorkspaceSection.TOP_BAR,
                    config = themeConfig
                ) {
                    TopCommandBar(
                        currentModel = selectedModel,
                        availableModels = viewModel.availableModels,
                        onSelectModel = { viewModel.selectModel(it) },
                        onOpenCommandPalette = { viewModel.openCommandPalette() },
                        onOpenThemeBlender = { viewModel.openThemeBlender() },
                        onToggleSidebar = {
                            coroutineScope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        },
                        isAgentActive = isAgentMode,
                        onToggleAgentMode = { viewModel.toggleAgentMode() },
                        modifier = Modifier.statusBarsPadding()
                    )
                }
            },
            bottomBar = {
                SectionThemeProvider(
                    section = WorkspaceSection.ISLAND,
                    config = themeConfig
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(bottom = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FloatingActionIsland(
                            isGenerating = isGenerating,
                            onOpenThemeStudio = { viewModel.openThemeBlender() },
                            onQuickPrompt = { prompt -> viewModel.sendMessage(prompt) }
                        )
                    }
                }
            },
            containerColor = themeConfig.getThemeForSection(WorkspaceSection.GLOBAL).background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                    when (tab) {
                        WorkspaceTab.CHAT -> {
                            SectionThemeProvider(
                                section = WorkspaceSection.CHAT,
                                config = themeConfig
                            ) {
                                ChatAndAgentsScreen(
                                    messages = messages,
                                    selectedModel = selectedModel,
                                    isAgentMode = isAgentMode,
                                    isGenerating = isGenerating,
                                    onSendMessage = { viewModel.sendMessage(it) },
                                    onApproveTool = { mId, tId -> viewModel.approveToolCall(mId, tId) },
                                    onRejectTool = { mId, tId -> viewModel.rejectToolCall(mId, tId) },
                                    onToggleAgentMode = { viewModel.toggleAgentMode() }
                                )
                            }
                        }

                        WorkspaceTab.DEEP_RESEARCH -> {
                            SectionThemeProvider(
                                section = WorkspaceSection.EDITOR,
                                config = themeConfig
                            ) {
                                DeepResearchScreen(
                                    session = researchSession,
                                    onStartResearch = { viewModel.runDeepResearch(it) }
                                )
                            }
                        }

                        WorkspaceTab.COOKBOOK -> {
                            SectionThemeProvider(
                                section = WorkspaceSection.GLOBAL,
                                config = themeConfig
                            ) {
                                CookbookScreen(
                                    hardwareScan = hardwareScan,
                                    availableModels = viewModel.availableModels,
                                    selectedModel = selectedModel,
                                    onSelectModel = { viewModel.selectModel(it) },
                                    activeEngine = activeEngine,
                                    tokensPerSec = tokensPerSec
                                )
                            }
                        }

                        WorkspaceTab.COMPARE -> {
                            SectionThemeProvider(
                                section = WorkspaceSection.EDITOR,
                                config = themeConfig
                            ) {
                                ModelCompareScreen(
                                    evaluation = compareEval,
                                    onCastVote = { viewModel.castCompareVote(it) }
                                )
                            }
                        }

                        WorkspaceTab.DOCUMENTS -> {
                            SectionThemeProvider(
                                section = WorkspaceSection.EDITOR,
                                config = themeConfig
                            ) {
                                DocumentsEditorScreen(
                                    documents = documents,
                                    activeDocId = activeDocId,
                                    onSelectDocument = { viewModel.selectDocument(it) },
                                    onUpdateContent = { docId, content -> viewModel.updateDocumentContent(docId, content) }
                                )
                            }
                        }

                        WorkspaceTab.EMAIL -> {
                            SectionThemeProvider(
                                section = WorkspaceSection.GLOBAL,
                                config = themeConfig
                            ) {
                                EmailScreen(
                                    emails = emails,
                                    selectedEmailId = selectedEmailId,
                                    onSelectEmail = { viewModel.selectEmail(it) }
                                )
                            }
                        }

                        WorkspaceTab.NOTES_TASKS -> {
                            SectionThemeProvider(
                                section = WorkspaceSection.GLOBAL,
                                config = themeConfig
                            ) {
                                NotesTasksCalendarScreen(
                                    notes = notes,
                                    tasks = tasks,
                                    calendarEvents = calEvents,
                                    onToggleTask = { viewModel.toggleTaskCompleted(it) }
                                )
                            }
                        }

                        WorkspaceTab.MEMORY_SKILLS -> {
                            SectionThemeProvider(
                                section = WorkspaceSection.GLOBAL,
                                config = themeConfig
                            ) {
                                MemorySkillsScreen(
                                    chromaMemories = memories,
                                    agentSkills = skills,
                                    onToggleSkill = { viewModel.toggleSkillInstalled(it) }
                                )
                            }
                        }

                        WorkspaceTab.MCP_INTEGRATIONS -> {
                            SectionThemeProvider(
                                section = WorkspaceSection.GLOBAL,
                                config = themeConfig
                            ) {
                                MCPIntegrationsScreen(
                                    mcpServers = mcpServers,
                                    onToggleServer = { viewModel.toggleMcpServer(it) }
                                )
                            }
                        }

                        WorkspaceTab.THEME_STUDIO -> {
                            SectionThemeProvider(
                                section = WorkspaceSection.GLOBAL,
                                config = themeConfig
                            ) {
                                ThemeStudioScreen(
                                    config = themeConfig,
                                    onSetGlobalTheme = { viewModel.setGlobalTheme(it) },
                                    onSetSectionTheme = { sec, th -> viewModel.setSectionTheme(sec, th) },
                                    onResetSections = { viewModel.resetSectionsToGlobal() },
                                    onSetBlend = { th, amt -> viewModel.setBlendSecondaryTheme(th, amt) },
                                    onApplyPreset = { viewModel.applyThemePreset(it) },
                                    onApplyCustomTheme = { name, bg, surf, surfVar, accent, secAccent, style ->
                                        viewModel.applyCustomTheme(name, bg, surf, surfVar, accent, secAccent, style)
                                    },
                                    onRandomizeTheme = { viewModel.randomizeTheme() }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Modals
        CommandPaletteDialog(
            isOpen = isPaletteOpen,
            onDismiss = { viewModel.closeCommandPalette() },
            onSelectTab = { viewModel.selectTab(it) },
            onSelectModel = { viewModel.selectModel(it) },
            onSelectTheme = { viewModel.setGlobalTheme(it) },
            availableModels = viewModel.availableModels
        )

        ThemeBlenderModal(
            isOpen = isBlenderOpen,
            onDismiss = { viewModel.closeThemeBlender() },
            config = themeConfig,
            onSetGlobalTheme = { viewModel.setGlobalTheme(it) },
            onSetSectionTheme = { sec, th -> viewModel.setSectionTheme(sec, th) },
            onResetSections = { viewModel.resetSectionsToGlobal() },
            onSetBlend = { th, amt -> viewModel.setBlendSecondaryTheme(th, amt) },
            onApplyPreset = { viewModel.applyThemePreset(it) },
            onApplyCustomTheme = { name, bg, surf, surfVar, accent, secAccent, style ->
                viewModel.applyCustomTheme(name, bg, surf, surfVar, accent, secAccent, style)
            },
            onRandomizeTheme = { viewModel.randomizeTheme() }
        )
    }
}
