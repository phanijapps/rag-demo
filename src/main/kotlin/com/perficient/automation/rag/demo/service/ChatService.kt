package com.perficient.automation.rag.demo.service

import com.perficient.automation.rag.demo.PromptRequest
import com.perficient.automation.rag.demo.util.logInfo
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.EmbeddingResponse
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

@Service
class ChatService (val chatModel: ChatModel,
                   val embeddingModel: EmbeddingModel,
                   val promptService: PromptService,
                   val vectorStore: VectorStore){

    /***
     *
     */
    fun generateAIResponse(prompt: PromptRequest) : ChatResponse {


        return chatModel.call(
            promptService.generatePrompt(
                prompt,
                findNearestNeighbor(prompt.prompt)
            )
        )

    }


    fun generateEmbeddingAIResponse(prompt: String) : EmbeddingResponse {
        return embeddingModel.embedForResponse(listOf(prompt))

    }

    private fun findNearestNeighbor(prompt: String) : String {
        val docs = vectorStore.similaritySearch(prompt)
        return docs.map {
            it.content.toString()
        }.joinToString(separator = "")
    }
}