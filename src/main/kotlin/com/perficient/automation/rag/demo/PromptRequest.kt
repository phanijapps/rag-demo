package com.perficient.automation.rag.demo

data class PromptRequest(
    val prompt: String,
    val negativePrompt: String,
    val temperature: Float
)
