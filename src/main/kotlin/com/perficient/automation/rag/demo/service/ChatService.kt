package com.perficient.automation.rag.demo.service

import com.perficient.automation.rag.demo.util.logInfo
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.EmbeddingResponse
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

@Service
class ChatService (val chatModel: ChatModel, val embeddingModel: EmbeddingModel, val vectorStore: VectorStore){

    /***
     *
     */
    fun generateAIResponse(prompt: String) : ChatResponse {
        var promptTemplateText = """
            Your task is to respond THE  HISTORY  AND  CULTURE  OF  THE  INDIAN  PEOPLE VOLUME  XI STRUGGLE FOR FREEDOM.
            Use the information from the DOCUMENTS section to provide accurate answers. If unsure or if the answer isn't found in the DOCUMENTS section, 
            simply state that you don't know the answer.
            
            QUESTION:
            ${prompt}
            
            DOCUMETS:
            ${findNearestNeighbor(prompt)}
        """.trimIndent()

        logInfo { promptTemplateText }

        return chatModel.call(
            Prompt(
                promptTemplateText
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