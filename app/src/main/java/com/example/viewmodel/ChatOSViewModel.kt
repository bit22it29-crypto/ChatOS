package com.example.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GeminiService
import com.example.model.*
import com.example.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChatOSViewModel : ViewModel() {

    private val geminiService = GeminiService()

    // --- Active Navigation Tab ---
    private val _currentTab = MutableStateFlow(WorkspaceTab.CHAT)
    val currentTab: StateFlow<WorkspaceTab> = _currentTab.asStateFlow()

    // --- Multi-Theme Engine State (Obsidian Black & Red Default) ---
    private val _themeConfig = MutableStateFlow(
        MultiThemeConfig(
            globalTheme = ThemeRegistry.OBSIDIAN_CRIMSON,
            sidebarTheme = ThemeRegistry.OBSIDIAN_CRIMSON,
            topBarTheme = ThemeRegistry.OBSIDIAN_CRIMSON,
            chatTheme = ThemeRegistry.OBSIDIAN_CRIMSON,
            editorTheme = ThemeRegistry.OBSIDIAN_CRIMSON,
            islandTheme = ThemeRegistry.OBSIDIAN_CRIMSON,
            activePresetId = "preset_obsidian_crimson"
        )
    )
    val themeConfig: StateFlow<MultiThemeConfig> = _themeConfig.asStateFlow()

    // --- UI Modals ---
    private val _isCommandPaletteOpen = MutableStateFlow(false)
    val isCommandPaletteOpen: StateFlow<Boolean> = _isCommandPaletteOpen.asStateFlow()

    private val _isThemeBlenderOpen = MutableStateFlow(false)
    val isThemeBlenderOpen: StateFlow<Boolean> = _isThemeBlenderOpen.asStateFlow()

    // --- Models Registry ---
    val availableModels = listOf(
        AIModel("gemini-2.5-flash", "Gemini 2.5 Flash", "Google DeepMind", "1M tokens", "Native Multimodal", "Cloud API (Real)", false, "REST API"),
        AIModel("llama-3.3-70b", "Llama 3.3 70B (Q4_K_M)", "Meta / Local", "128k tokens", "70 Billion", "Snug (14.2 GB)", true, "vLLM"),
        AIModel("deepseek-r1-8b", "DeepSeek-R1 Distill 8B", "DeepSeek / Local", "64k tokens", "8 Billion", "Ideal (VRAM 5.1 GB)", true, "llama.cpp"),
        AIModel("claude-3.7-sonnet", "Claude 3.7 Sonnet (Hybrid)", "Anthropic", "200k tokens", "Hybrid Thinking", "Cloud API", false, "REST API"),
        AIModel("qwen-2.5-coder", "Qwen 2.5 Coder 7B", "Alibaba / Local", "32k tokens", "7 Billion", "Ideal (VRAM 4.4 GB)", true, "SGLang"),
        AIModel("mistral-nemo-12b", "Mistral NeMo 12B", "Mistral AI / Local", "128k tokens", "12 Billion", "Snug (7.8 GB)", true, "llama.cpp")
    )

    private val _selectedModel = MutableStateFlow(availableModels[0])
    val selectedModel: StateFlow<AIModel> = _selectedModel.asStateFlow()

    // --- Chat & Agents State ---
    private val _isAgentMode = MutableStateFlow(true)
    val isAgentMode: StateFlow<Boolean> = _isAgentMode.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // --- Deep Research State ---
    private val _researchSession = MutableStateFlow(
        DeepResearchSession(
            id = "res_1",
            topic = "Autonomous Local AI Agents: MCP Protocol vs Tool Use in Edge Workspaces",
            stepIndex = 0,
            statusMessage = "Ready for research inquiry",
            sources = listOf(
                ResearchSource("src_1", "Anthropic MCP Specification v1.0", "https://modelcontextprotocol.io/spec", "Architecture specification for local client-server tool integration.", 98),
                ResearchSource("src_2", "Local LLM Serving Benchmarks: vLLM vs llama.cpp", "https://arxiv.org/abs/2403.11902", "Throughput evaluation across ARM64 & discrete GPUs.", 94),
                ResearchSource("src_3", "ChromaDB Embedded Vector Retrieval", "https://docs.trychroma.com/embedded", "In-process vector database optimization for agentic memory.", 91)
            ),
            synthesisReport = "# Deep Research: Local AI Agents Architecture\n\n## 1. Executive Summary\nLocal-first AI workspaces leverage three core pillars: **Model Context Protocol (MCP)**, **ChromaDB vector persistence**, and **Quantized Local Serving**.\n\n## 2. Key Findings\n- **Zero Latency RPC**: MCP stdio transport delivers sub-5ms tool latency on localhost.\n- **Memory Efficiency**: 4-bit GGUF quantization retains 98.4% of FP16 reasoning capability at 1/4 the VRAM.\n- **Approval Gates**: Sandboxing tools behind interactive confirmation prevents accidental destructive operations."
        )
    )
    val researchSession: StateFlow<DeepResearchSession> = _researchSession.asStateFlow()

    // --- Hardware & Cookbook State ---
    private val _hardwareScan = MutableStateFlow(HardwareScanResult())
    val hardwareScan: StateFlow<HardwareScanResult> = _hardwareScan.asStateFlow()

    private val _activeServingEngine = MutableStateFlow("llama.cpp")
    val activeServingEngine: StateFlow<String> = _activeServingEngine.asStateFlow()

    private val _benchmarkTokensPerSec = MutableStateFlow(42.6f)
    val benchmarkTokensPerSec: StateFlow<Float> = _benchmarkTokensPerSec.asStateFlow()

    // --- Model Compare State ---
    private val _compareEvaluation = MutableStateFlow(
        CompareEvaluation(
            prompt = "Write a high-performance concurrent task scheduler in Kotlin using Coroutines and Channels with backpressure handling.",
            modelA = availableModels[1], // Llama 3.3 70B
            modelB = availableModels[2], // DeepSeek-R1 8B
            responseA = "```kotlin\nclass ConcurrentScheduler<T>(\n    private val concurrency: Int = 8\n) {\n    private val channel = Channel<suspend () -> T>(capacity = Channel.BUFFERED)\n    \n    fun start(scope: CoroutineScope) = repeat(concurrency) {\n        scope.launch(Dispatchers.Default) {\n            for (task in channel) task()\n        }\n    }\n    \n    suspend fun submit(task: suspend () -> T) = channel.send(task)\n}\n```\n*Analysis: Clean standard channel pattern with structured concurrency.*",
            responseB = "```kotlin\nclass BackpressureScheduler<T>(\n    private val poolSize: Int = 8\n) {\n    private val queue = Channel<suspend () -> T>(capacity = 64, onBufferOverflow = BufferOverflow.SUSPEND)\n    \n    fun launchWorkers(scope: CoroutineScope) = (1..poolSize).map {\n        scope.launch(Dispatchers.IO) {\n            for (item in queue) {\n                try { item() } catch (e: Exception) { Log.e(\"Scheduler\", \"Error: \${e.message}\") }\n            }\n        }\n    }\n}\n```\n*Analysis: Includes explicit buffer overflow suspension and worker error boundary.*",
            latencyMsA = 840L,
            latencyMsB = 410L,
            tokPerSecA = 38.2f,
            tokPerSecB = 64.8f,
            isRevealed = false
        )
    )
    val compareEvaluation: StateFlow<CompareEvaluation> = _compareEvaluation.asStateFlow()

    // --- Documents & Editor State ---
    private val _documents = MutableStateFlow(
        listOf(
            DocumentTab(
                id = "doc_1",
                filename = "system_architecture.md",
                language = "markdown",
                content = """
# ChatOS System Architecture
**Version**: 2.4.0-PRO
**Runtime**: Local Edge Engine + Gemini REST API

### 1. The Multi-Theme Engine
ChatOS integrates **26 discrete A–Z themes** with real-time RGB interpolation and section isolation:
- `SectionThemeProvider` grants granular theme independence to Sidebar, Chat, Editor, and Island.
- `lerpThemes()` creates dynamic blended gradients.

### 2. Autonomous Agent Tool Gate
- Execution approvals protect the filesystem, shell sandbox, and Chroma vector memory.
- Multi-step tool calls stream feedback directly to the conversation timeline.
                """.trimIndent()
            ),
            DocumentTab(
                id = "doc_2",
                filename = "agent_executor.kt",
                language = "kotlin",
                content = """
package com.example.agent

class AgentExecutionEngine(
    private val memoryStore: ChromaDBClient,
    private val shellSandbox: ShellExecutor
) {
    suspend fun executeToolPlan(plan: ToolPlan): ExecutionResult {
        return when (plan.tool) {
            "searxng" -> shellSandbox.searchWeb(plan.query)
            "fs_write" -> shellSandbox.writeWorkspace(plan.path, plan.content)
            else -> ExecutionResult.UnknownTool
        }
    }
}
                """.trimIndent()
            ),
            DocumentTab(
                id = "doc_3",
                filename = "hardware_benchmark.csv",
                language = "csv",
                content = "Model,Parameters,Quantization,VRAM_GB,Throughput_TokSec,Fit_Score\nLlama-3.3-70B,70B,Q4_K_M,14.2,38.2,Snug\nDeepSeek-R1-8B,8B,Q8_0,5.1,64.8,Ideal\nQwen2.5-Coder-7B,7B,Q4_K_M,4.4,72.4,Ideal\nMistral-NeMo-12B,12B,Q4_K_M,7.8,48.1,Snug"
            )
        )
    )
    val documents: StateFlow<List<DocumentTab>> = _documents.asStateFlow()

    private val _activeDocId = MutableStateFlow("doc_1")
    val activeDocId: StateFlow<String> = _activeDocId.asStateFlow()

    // --- Email State ---
    private val _emails = MutableStateFlow(
        listOf(
            EmailMessage(
                id = "mail_1",
                senderName = "Dr. Elena Rostova",
                senderAddress = "elena.r@quantum-lab.org",
                subject = "Research Preprint: Quantized LLMs on Embedded NPUs",
                snippet = "Attached is the latest benchmark draft for SGLang vs vLLM throughput...",
                body = "Hi Team,\n\nWe evaluated the performance of 4-bit quantized mixture-of-experts on Snapdragon and Apple Silicon NPUs. The token throughput reached 78 tok/s under 8W thermal limits.\n\nCould your agent run a validation check on our test script?\n\nBest,\nElena",
                timestamp = "10:42 AM",
                category = "URGENT",
                isRead = false,
                aiSummary = "Elena shared new NPU benchmark data (78 tok/s) and requested an agent validation run.",
                suggestedReply = "Hi Elena,\n\nThanks for the preprint! I've loaded your test script into our ChatOS sandbox and queued the validation run across our local vLLM and SGLang instances. Will share output logs shortly.\n\nBest regards,"
            ),
            EmailMessage(
                id = "mail_2",
                senderName = "GitHub Notifications",
                senderAddress = "notifications@github.com",
                subject = "[PR #142] Merged: Multi-Theme Realtime RGB Interpolator",
                snippet = "Pull request #142 has been merged into main by branch maintainer...",
                body = "Merged commit #9f82d1: Added 26 A–Z themes and per-section theme overriding support with zero-jank Compose animations.",
                timestamp = "08:15 AM",
                category = "ACTION_REQUIRED",
                isRead = true,
                aiSummary = "Theme engine PR #142 successfully merged into main.",
                suggestedReply = null
            ),
            EmailMessage(
                id = "mail_3",
                senderName = "ArXiv Digest Agent",
                senderAddress = "digest@chatos-agent.local",
                subject = "Daily AI Papers: Vector Memory Optimizations",
                snippet = "3 new papers found matching your keywords (ChromaDB, MCP, Local RAG)...",
                body = "1. GraphRAG with In-Memory Embeddings\n2. Sub-millisecond Tool Routing for Autonomous Agents\n3. Zero-Copy Context Caching in Llama.cpp",
                timestamp = "Yesterday",
                category = "NEWSLETTER",
                isRead = true,
                aiSummary = "Daily paper summary on GraphRAG, tool routing, and zero-copy context caching.",
                suggestedReply = null
            )
        )
    )
    val emails: StateFlow<List<EmailMessage>> = _emails.asStateFlow()

    private val _selectedEmailId = MutableStateFlow<String?>("mail_1")
    val selectedEmailId: StateFlow<String?> = _selectedEmailId.asStateFlow()

    // --- Notes, Tasks, Calendar State ---
    private val _notes = MutableStateFlow(
        listOf(
            NoteItem("note_1", "Odysseus Architecture Overhaul", "Replacing static monochromatic themes with **A–Z Dynamic Multi-Theme Engine**. Every section (Sidebar, Chat, Editor, Floating Island) is independently themable.", listOf("Architecture", "UI", "ChatOS"), "Aug 21"),
            NoteItem("note_2", "Local Tool Approval Protocol", "Never execute raw bash or filesystem writes silently. Present an interactive **Approval Gate** card with argument inspector.", listOf("Security", "Agent"), "Aug 20"),
            NoteItem("note_3", "SearXNG Deep Research Pipeline", "4-step iterative crawler: 1) Query Deconstruction -> 2) SearXNG Query -> 3) Source Scoring -> 4) Markdown Synthesis.", listOf("DeepResearch", "Search"), "Aug 19")
        )
    )
    val notes: StateFlow<List<NoteItem>> = _notes.asStateFlow()

    private val _tasks = MutableStateFlow(
        listOf(
            TaskItem("task_1", "Calibrate 26 A–Z Color Tokens for WCAG AAA", true, "HIGH", "UI Agent", "Today"),
            TaskItem("task_2", "Benchmark llama.cpp vs SGLang on ARM64", false, "HIGH", "Cookbook Agent", "Tomorrow"),
            TaskItem("task_3", "Index ChromaDB Vector Memory for Local Notes", false, "MEDIUM", "Memory Agent", "Aug 23"),
            TaskItem("task_4", "Validate MCP Stdio Transport Latency", true, "LOW", "Integrations Agent", "Aug 24")
        )
    )
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    private val _calendarEvents = MutableStateFlow(
        listOf(
            CalendarEventItem("cal_1", "Deep Research: Local MoE Architecture", "11:00 AM - 12:00 PM", "Today", true, "Autonomous crawl & synthesis scheduled by Agent Core"),
            CalendarEventItem("cal_2", "Model Comparison Evaluation (Llama vs DeepSeek)", "02:30 PM - 03:00 PM", "Today", false, null),
            CalendarEventItem("cal_3", "Nightly Vector Store Re-indexing (ChromaDB)", "02:00 AM - 02:15 AM", "Tomorrow", true, "Cron scheduled automated background task")
        )
    )
    val calendarEvents: StateFlow<List<CalendarEventItem>> = _calendarEvents.asStateFlow()

    // --- Chroma Memory & Skills State ---
    private val _chromaMemories = MutableStateFlow(
        listOf(
            ChromaMemoryItem("mem_1", "user_theme_preference", "User prefers dark cyber/aurora aesthetics with high-contrast monospace code blocks.", 0.12f, "User Profile", "10m ago"),
            ChromaMemoryItem("mem_2", "hardware_specs", "Snapdragon 8 Gen 3, 16GB RAM, Vulkan Adreno 750, 45 TOPS NPU.", 0.18f, "Hardware", "1h ago"),
            ChromaMemoryItem("mem_3", "active_project_spec", "ChatOS with 26 distinct A–Z themes and per-section theme overrides.", 0.05f, "Project Spec", "2h ago"),
            ChromaMemoryItem("mem_4", "tool_gate_policy", "Always prompt for user confirmation before executing bash commands.", 0.22f, "Security", "Yesterday")
        )
    )
    val chromaMemories: StateFlow<List<ChromaMemoryItem>> = _chromaMemories.asStateFlow()

    private val _agentSkills = MutableStateFlow(
        listOf(
            AgentSkill("skill_claude", "Claude Code Copilot", "Terminal-grade codebase understanding and patch generation", "claude-code --autonomous", true, "Anthropic", "v2.1"),
            AgentSkill("skill_searxng", "SearXNG Deep Crawler", "Multi-engine privacy search scraper with credibility analyzer", "searxng-crawl --depth 3", true, "ChatOS Core", "v1.4"),
            AgentSkill("skill_chroma", "ChromaDB RAG Agent", "Vector similarity retrieval with dynamic re-ranking", "chroma-rag --k 5", true, "Chroma", "v0.5.2"),
            AgentSkill("skill_docker", "Docker Sandbox Tool", "Isolated container runner for executing untrusted scripts", "docker run --rm sandbox", false, "Docker MCP", "v1.0")
        )
    )
    val agentSkills: StateFlow<List<AgentSkill>> = _agentSkills.asStateFlow()

    // --- MCP Servers State ---
    private val _mcpServers = MutableStateFlow(
        listOf(
            MCPServer("mcp_fs", "Filesystem MCP", "stdio", "/usr/bin/mcp-server-filesystem", "CONNECTED", 2, listOf("read_file", "write_file", "list_dir", "grep")),
            MCPServer("mcp_memory", "Chroma Memory MCP", "http-sse", "http://127.0.0.1:8000/sse", "CONNECTED", 4, listOf("query_memory", "store_vector", "delete_memory")),
            MCPServer("mcp_email", "Email Client MCP", "stdio", "/usr/bin/mcp-email-imap", "CONNECTED", 12, listOf("search_inbox", "draft_reply", "triage_messages")),
            MCPServer("mcp_docker", "Docker Execution MCP", "stdio", "/usr/bin/mcp-docker-runner", "STANDBY", 1, listOf("run_container", "inspect_logs"))
        )
    )
    val mcpServers: StateFlow<List<MCPServer>> = _mcpServers.asStateFlow()

    init {
        // Initialize with a welcome message
        _messages.value = listOf(
            ChatMessage(
                id = "init_1",
                role = MessageRole.ASSISTANT,
                text = "Welcome to **ChatOS** — the Next-Gen Local-First AI Workspace running in **Obsidian Crimson** with **${ThemeRegistry.ALL_THEMES.size} Multi-Themes**.\n\n✨ **Core Highlights**:\n- **Obsidian & Red + A–Z Theme Studio**: Choose from ${ThemeRegistry.ALL_THEMES.size} handcrafted themes, custom canvas/accent combos, or curated presets.\n- **Per-Section Theming**: Assign different themes to the Sidebar, Top Bar, Chat, Editor, and Island simultaneously.\n- **Live Theme Blender**: Blend any two themes with real-time gradient cross-fading.\n- **Autonomous Agents**: Shell, web search, files, and Chroma memory behind interactive **Approval Gates**.\n- **Full Suite**: Deep Research, Hardware Cookbook, Model Compare, Markdown Docs, Email Triage, and MCP Client.",
                timestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                modelName = _selectedModel.value.name,
                toolCalls = listOf(
                    ToolCall(
                        id = "tool_init_1",
                        toolName = "system_hardware_probe",
                        command = "hwprobe --detect-npu --detect-vram --format json",
                        arguments = "{\"target\": \"all_devices\"}",
                        output = "Detected 8 Cores (Snapdragon 8 Gen 3), 16GB Unified Memory, Vulkan Adreno 750 (8GB VRAM), 45 TOPS NPU.",
                        status = ToolStatus.COMPLETED,
                        executionTimeMs = 38L
                    )
                )
            )
        )
    }

    // --- Navigation ---
    fun selectTab(tab: WorkspaceTab) {
        _currentTab.value = tab
    }

    // --- Model Selection ---
    fun selectModel(model: AIModel) {
        _selectedModel.value = model
    }

    fun toggleAgentMode() {
        _isAgentMode.value = !_isAgentMode.value
    }

    // --- Multi-Theme Actions ---
    fun setGlobalTheme(theme: ChatOSTheme) {
        _themeConfig.update {
            it.copy(
                globalTheme = theme,
                sidebarTheme = null,
                topBarTheme = null,
                chatTheme = null,
                editorTheme = null,
                islandTheme = null,
                activePresetId = null
            )
        }
    }

    fun setSectionTheme(section: WorkspaceSection, theme: ChatOSTheme?) {
        _themeConfig.update { config ->
            when (section) {
                WorkspaceSection.GLOBAL -> config.copy(globalTheme = theme ?: ThemeRegistry.DEFAULT_THEME, activePresetId = null)
                WorkspaceSection.SIDEBAR -> config.copy(sidebarTheme = theme, activePresetId = null)
                WorkspaceSection.TOP_BAR -> config.copy(topBarTheme = theme, activePresetId = null)
                WorkspaceSection.CHAT -> config.copy(chatTheme = theme, activePresetId = null)
                WorkspaceSection.EDITOR -> config.copy(editorTheme = theme, activePresetId = null)
                WorkspaceSection.ISLAND -> config.copy(islandTheme = theme, activePresetId = null)
            }
        }
    }

    fun resetSectionsToGlobal() {
        _themeConfig.update {
            it.copy(
                sidebarTheme = null,
                topBarTheme = null,
                chatTheme = null,
                editorTheme = null,
                islandTheme = null,
                activePresetId = null
            )
        }
    }

    fun setBlendSecondaryTheme(theme: ChatOSTheme?, intensity: Float) {
        _themeConfig.update { it.copy(blendSecondaryTheme = theme, blendIntensity = intensity) }
    }

    fun applyThemePreset(preset: ThemePreset) {
        val global = ThemeRegistry.getById(preset.globalThemeId)
        val sidebar = ThemeRegistry.getById(preset.sidebarThemeId)
        val topBar = ThemeRegistry.getById(preset.topBarThemeId)
        val chat = ThemeRegistry.getById(preset.chatThemeId)
        val editor = ThemeRegistry.getById(preset.editorThemeId)
        val island = ThemeRegistry.getById(preset.islandThemeId)
        val blend = preset.blendThemeId?.let { ThemeRegistry.getById(it) }

        _themeConfig.value = MultiThemeConfig(
            globalTheme = global,
            sidebarTheme = sidebar,
            topBarTheme = topBar,
            chatTheme = chat,
            editorTheme = editor,
            islandTheme = island,
            blendSecondaryTheme = blend,
            blendIntensity = preset.blendAmount,
            activePresetId = preset.id
        )
    }

    fun applyCustomTheme(
        customName: String,
        canvasBackground: Color,
        surfaceColor: Color,
        surfaceVariantColor: Color,
        accentColor: Color,
        secondaryColor: Color,
        materialStyle: ThemeMaterialStyle
    ) {
        val custom = ChatOSTheme(
            id = "custom_${customName.lowercase().replace(" ", "_")}",
            badgeCode = "★",
            letter = '★',
            name = customName,
            tagLine = "Custom forged workspace palette",
            emotionalTone = "Personalized, tailored, distinctive",
            materialStyle = materialStyle,
            category = ThemeCategory.OBSIDIAN_RED,
            primary = accentColor,
            onPrimary = Color.White,
            primaryContainer = lerp(canvasBackground, accentColor, 0.30f),
            onPrimaryContainer = lerp(accentColor, Color.White, 0.78f),
            secondary = secondaryColor,
            onSecondary = Color.White,
            secondaryContainer = lerp(canvasBackground, secondaryColor, 0.24f),
            onSecondaryContainer = lerp(secondaryColor, Color.White, 0.75f),
            tertiary = lerp(accentColor, secondaryColor, 0.5f),
            background = canvasBackground,
            onBackground = Color(0xFFF6F2F3),
            surface = surfaceColor,
            onSurface = Color(0xFFFAFAFC),
            surfaceVariant = surfaceVariantColor,
            onSurfaceVariant = Color(0xFFA8A2A6),
            surfaceBorder = lerp(surfaceVariantColor, accentColor, 0.34f),
            accentGlow = accentColor,
            codeBackground = lerp(canvasBackground, Color.Black, 0.4f),
            terminalPrompt = accentColor,
            agentBadgeColor = secondaryColor
        )
        _themeConfig.value = MultiThemeConfig(
            globalTheme = custom,
            blendSecondaryTheme = null,
            blendIntensity = 0f,
            activePresetId = null
        )
    }

    fun randomizeTheme() {
        val currentId = _themeConfig.value.globalTheme.id
        val candidates = ThemeRegistry.ALL_THEMES.filter { it.id != currentId }
        val picked = candidates.randomOrNull() ?: ThemeRegistry.DEFAULT_THEME
        setGlobalTheme(picked)
    }

    // --- Dialog Toggles ---
    fun openCommandPalette() { _isCommandPaletteOpen.value = true }
    fun closeCommandPalette() { _isCommandPaletteOpen.value = false }
    fun openThemeBlender() { _isThemeBlenderOpen.value = true }
    fun closeThemeBlender() { _isThemeBlenderOpen.value = false }

    // --- Chat Actions ---
    fun sendMessage(promptText: String) {
        if (promptText.isBlank()) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            text = promptText.trim(),
            timestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
            modelName = "User"
        )

        _messages.update { it + userMessage }
        _isGenerating.value = true

        viewModelScope.launch {
            try {
                val history = _messages.value.map { (if (it.role == MessageRole.USER) "user" else "model") to it.text }
                val (responseText, toolCalls) = geminiService.generateChatResponse(
                    prompt = promptText,
                    isAgentMode = _isAgentMode.value,
                    conversationHistory = history
                )

                val assistantMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = MessageRole.ASSISTANT,
                    text = responseText,
                    timestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                    modelName = _selectedModel.value.name,
                    toolCalls = toolCalls
                )

                _messages.update { it + assistantMessage }
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = MessageRole.ASSISTANT,
                    text = "Encountered issue generating response: ${e.message ?: "Unknown error"}. Switched to local fallback.",
                    timestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                    modelName = _selectedModel.value.name
                )
                _messages.update { it + errorMessage }
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun approveToolCall(messageId: String, toolId: String) {
        viewModelScope.launch {
            _messages.update { list ->
                list.map { msg ->
                    if (msg.id == messageId) {
                        msg.copy(
                            toolCalls = msg.toolCalls.map { tool ->
                                if (tool.id == toolId) tool.copy(status = ToolStatus.RUNNING) else tool
                            }
                        )
                    } else msg
                }
            }

            delay(600) // Simulate tool execution in sandbox

            _messages.update { list ->
                list.map { msg ->
                    if (msg.id == messageId) {
                        msg.copy(
                            toolCalls = msg.toolCalls.map { tool ->
                                if (tool.id == toolId) {
                                    val simulatedOutput = when (tool.toolName) {
                                        "searxng_web_search" -> "Found 5 sources. Top result: \"Anthropic Model Context Protocol Specs\" (Confidence: 98.4%)."
                                        "filesystem_workspace" -> "File /workspace/generated_module.kt written successfully (4,210 bytes)."
                                        "bash_sandbox" -> "Linux chatos-edge-arm64 6.6.21 #1 SMP PREEMPT\nMemory: 16GB Total (9.4GB Free)\nGPU: Adreno 750 Vulkan Ready"
                                        "chromadb_vector_memory" -> "Retrieved 3 matched vectors with cosine similarity > 0.88."
                                        else -> "Command executed with exit code 0."
                                    }
                                    tool.copy(
                                        status = ToolStatus.COMPLETED,
                                        output = simulatedOutput,
                                        executionTimeMs = 42L
                                    )
                                } else tool
                            }
                        )
                    } else msg
                }
            }
        }
    }

    fun rejectToolCall(messageId: String, toolId: String) {
        _messages.update { list ->
            list.map { msg ->
                if (msg.id == messageId) {
                    msg.copy(
                        toolCalls = msg.toolCalls.map { tool ->
                            if (tool.id == toolId) tool.copy(status = ToolStatus.REJECTED, output = "Execution blocked by user approval gate.") else tool
                        }
                    )
                } else msg
            }
        }
    }

    // --- Deep Research Engine ---
    fun runDeepResearch(topic: String) {
        viewModelScope.launch {
            _researchSession.value = _researchSession.value.copy(
                topic = topic,
                stepIndex = 1,
                statusMessage = "Step 1/4: Deconstructing inquiry into sub-queries...",
                isRunning = true
            )
            delay(1000)

            _researchSession.value = _researchSession.value.copy(
                stepIndex = 2,
                statusMessage = "Step 2/4: Crawling SearXNG across Google, Bing, ArXiv & GitHub...",
                sources = listOf(
                    ResearchSource("src_a", "Evaluation of Multi-Agent Systems on Edge Devices", "https://arxiv.org/abs/2405.09914", "Deep analysis of sub-10ms tool calling architectures.", 97),
                    ResearchSource("src_b", "SearXNG JSON API Integration Guide", "https://docs.searxng.org/dev", "Local privacy-respecting search aggregation engine.", 93),
                    ResearchSource("src_c", "Vector Memory Indexing in ChromaDB", "https://trychroma.com/blog/embedded-agents", "Optimizing HNSW graphs for low-memory environments.", 90)
                )
            )
            delay(1200)

            _researchSession.value = _researchSession.value.copy(
                stepIndex = 3,
                statusMessage = "Step 3/4: Extracting high-confidence factual citations...",
            )
            delay(1000)

            val generatedReport = """
# Comprehensive Research Synthesis: $topic

## 1. Executive Summary
This investigation analyzed the technical requirements, architecture, and deployment parameters for **$topic**.

## 2. Key Technical Findings
- **High-Throughput Local Execution**: Utilizing 4-bit quantizations (GGUF) achieves 40–70 tok/s on modern edge hardware.
- **Privacy & Safety**: SearXNG query federation prevents search leakages, while interactive Approval Gates safeguard system resources.
- **ChromaDB Vector Retrieval**: Sub-10ms similarity searches allow autonomous agents to retain long-term cross-session knowledge.

## 3. Recommended Implementation Roadmap
1. Deploy local MCP Server with stdio protocol.
2. Configure dynamic theme tokens for full section isolation.
3. Integrate auto-triage filters for multi-agent dispatching.
            """.trimIndent()

            _researchSession.value = _researchSession.value.copy(
                stepIndex = 4,
                statusMessage = "Step 4/4: Research report synthesized successfully.",
                synthesisReport = generatedReport,
                isRunning = false
            )
        }
    }

    // --- Compare Voting ---
    fun castCompareVote(vote: String) {
        _compareEvaluation.update { it.copy(userVote = vote, isRevealed = true) }
    }

    // --- Document Editing ---
    fun selectDocument(docId: String) {
        _activeDocId.value = docId
    }

    fun updateDocumentContent(docId: String, newContent: String) {
        _documents.update { list ->
            list.map { if (it.id == docId) it.copy(content = newContent, isModified = true) else it }
        }
    }

    // --- Email Selection & AI Reply ---
    fun selectEmail(emailId: String) {
        _selectedEmailId.value = emailId
        _emails.update { list ->
            list.map { if (it.id == emailId) it.copy(isRead = true) else it }
        }
    }

    // --- Tasks & Calendar ---
    fun toggleTaskCompleted(taskId: String) {
        _tasks.update { list ->
            list.map { if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it }
        }
    }

    fun toggleSkillInstalled(skillId: String) {
        _agentSkills.update { list ->
            list.map { if (it.id == skillId) it.copy(isInstalled = !it.isInstalled) else it }
        }
    }

    fun toggleMcpServer(serverId: String) {
        _mcpServers.update { list ->
            list.map {
                if (it.id == serverId) {
                    val nextStatus = if (it.status == "CONNECTED") "STANDBY" else "CONNECTED"
                    it.copy(status = nextStatus)
                } else it
            }
        }
    }
}
