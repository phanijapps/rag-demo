package com.perficient.automation.rag.demo.controller

import com.perficient.automation.rag.demo.PromptRequest
import com.perficient.automation.rag.demo.service.ChatService
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.embedding.EmbeddingResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/bot")
class ChatController(val chatService: ChatService) {

    @PostMapping("/chat")
    fun chat(@RequestBody promptRequest: PromptRequest) : ChatResponse {
        return chatService.generateAIResponse(promptRequest);
    }


    @PostMapping("/embedding")
    fun embedding(@RequestBody query: String) : EmbeddingResponse {
        return chatService.generateEmbeddingAIResponse(query);
    }

}