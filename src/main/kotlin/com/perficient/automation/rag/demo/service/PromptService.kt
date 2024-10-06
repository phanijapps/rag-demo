package com.perficient.automation.rag.demo.service

import com.perficient.automation.rag.demo.PromptRequest
import com.perficient.automation.rag.demo.util.logInfo
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.stereotype.Service

@Service
class PromptService {

    fun generatePrompt(promptRequest: PromptRequest, context: String): Prompt {
        val prompt = StringBuilder().apply {
            append(
                """
                You are a helpful assistant and your your job is to use the information from the DOCUMENTS section to provide accurate answers. If unsure or if the answer isn't found in the DOCUMENTS section, 
                simply state that you don't know the answer.
                """
            )
            append("QUESTION:\n")
            append(promptRequest.prompt + "\n")
            append("DOCUMENTS:\n")
            append(context)
        }.toString()

        logInfo { prompt }

        if (promptRequest.temperature > 0) {
            return Prompt(
                prompt,
                OpenAiChatOptions.builder()
                    .withTemperature(promptRequest.temperature)
                    .build()
            )
        } else
            return Prompt(promptRequest.prompt)
    }


}