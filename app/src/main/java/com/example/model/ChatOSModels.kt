package com.example.model

enum class WorkspaceTab(val title: String, val iconName: String, val badge: String? = null) {
    CHAT("Chat & Agents", "chat", "Live"),
    DEEP_RESEARCH("Deep Research", "travel_explore", "SearXNG"),
    COOKBOOK("Cookbook", "memory", "Local"),
    COMPARE("Model Compare", "compare_arrows", "Blind"),
    DOCUMENTS("Documents", "edit_document", null),
    EMAIL("Email & Triage", "mail", "IMAP"),
    NOTES_TASKS("Notes & Cal", "event_note", null),
    MEMORY_SKILLS("Chroma & Skills", "psychology", "DB"),
    MCP_INTEGRATIONS("MCP Servers", "hub", "RPC"),
    THEME_STUDIO("A–Z Themes", "palette", "26")
}

enum class MessageRole {
    USER, ASSISTANT, AGENT_TOOL, SYSTEM
}

enum class ToolStatus {
    PENDING_APPROVAL,
    RUNNING,
    COMPLETED,
    REJECTED
}

data class ToolCall(
    val id: String,
    val toolName: String,
    val command: String,
    val arguments: String,
    val output: String? = null,
    val status: ToolStatus = ToolStatus.PENDING_APPROVAL,
    val executionTimeMs: Long = 0L
)

data class ChatMessage(
    val id: String,
    val role: MessageRole,
    val text: String,
    val reasoningText: String? = null,
    val timestamp: String,
    val modelName: String = "Gemini 2.5 Flash",
    val toolCalls: List<ToolCall> = emptyList(),
    val isStreaming: Boolean = false,
    val reactionEmoji: String? = null
)

data class AIModel(
    val id: String,
    val name: String,
    val provider: String,
    val contextWindow: String,
    val parameters: String,
    val fitStatus: String, // "Ideal (VRAM)", "Snug", "Cloud API"
    val isLocal: Boolean,
    val defaultServing: String // "llama.cpp", "vLLM", "SGLang", "REST API"
)

data class HardwareScanResult(
    val cpuModel: String = "Snapdragon 8 Gen 3 / ARM64",
    val totalCores: Int = 8,
    val totalRamGB: Double = 16.0,
    val availableRamGB: Double = 9.4,
    val gpuModel: String = "Adreno 750 / Metal Vulkan",
    val vramGB: Double = 8.0,
    val npuTops: Int = 45,
    val temperatureCelsius: Int = 38
)

data class ResearchSource(
    val id: String,
    val title: String,
    val url: String,
    val snippet: String,
    val credibilityScore: Int // 0 - 100
)

data class DeepResearchSession(
    val id: String,
    val topic: String,
    val stepIndex: Int, // 0: Idle, 1: Query Deconstruction, 2: SearXNG Crawl, 3: Evidence Extraction, 4: Deep Synthesis
    val statusMessage: String,
    val sources: List<ResearchSource> = emptyList(),
    val synthesisReport: String = "",
    val isRunning: Boolean = false
)

data class CompareEvaluation(
    val prompt: String,
    val modelA: AIModel,
    val modelB: AIModel,
    val responseA: String,
    val responseB: String,
    val latencyMsA: Long,
    val latencyMsB: Long,
    val tokPerSecA: Float,
    val tokPerSecB: Float,
    val userVote: String? = null, // "A", "B", "TIE"
    val isRevealed: Boolean = false
)

data class DocumentTab(
    val id: String,
    val filename: String,
    val language: String, // markdown, html, python, csv
    val content: String,
    val isModified: Boolean = false
)

data class EmailMessage(
    val id: String,
    val senderName: String,
    val senderAddress: String,
    val subject: String,
    val snippet: String,
    val body: String,
    val timestamp: String,
    val category: String, // "URGENT", "ACTION_REQUIRED", "NEWSLETTER", "SYSTEM"
    val isRead: Boolean = false,
    val aiSummary: String = "",
    val suggestedReply: String? = null
)

data class NoteItem(
    val id: String,
    val title: String,
    val content: String,
    val tags: List<String>,
    val date: String
)

data class TaskItem(
    val id: String,
    val title: String,
    val isCompleted: Boolean,
    val priority: String, // "HIGH", "MEDIUM", "LOW"
    val assignedAgent: String?,
    val dueDate: String
)

data class CalendarEventItem(
    val id: String,
    val title: String,
    val time: String,
    val date: String,
    val isAgentScheduled: Boolean,
    val agentTaskDetails: String?
)

data class ChromaMemoryItem(
    val id: String,
    val memoryKey: String,
    val content: String,
    val similarityDistance: Float,
    val category: String, // "User Profile", "Project Spec", "Tool Credentials", "Agent Lesson"
    val timestamp: String
)

data class AgentSkill(
    val id: String,
    val name: String,
    val description: String,
    val command: String,
    val isInstalled: Boolean,
    val author: String,
    val version: String
)

data class MCPServer(
    val id: String,
    val name: String,
    val protocol: String, // "stdio", "http-sse"
    val uri: String,
    val status: String, // "CONNECTED", "CONNECTING", "STANDBY"
    val latencyMs: Int,
    val toolsProvided: List<String>
)
