package com.perficient.automation.rag.demo.config

import com.perficient.automation.rag.demo.util.logInfo
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.document.MetadataMode
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.ai.openai.OpenAiEmbeddingOptions
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.ai.vectorstore.PgVectorStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class OpenAIConfig {

    @Bean
    fun chatModel() : ChatModel {

        var openAiApi = OpenAiApi(System.getenv("OPEN_AI_KEY"));

        var openAiChatOptions = OpenAiChatOptions.builder()
            .withModel("gpt-3.5-turbo")
            .withTemperature(0.4f)
            .withMaxTokens(200)
            .build();

        return OpenAiChatModel(openAiApi, openAiChatOptions);
    }

    @Bean
    fun openAiEmbeddingModel(openAiApi : OpenAiApi) : EmbeddingModel{
        logInfo { "Retruning Embedding model" }
        return OpenAiEmbeddingModel(
            openAiApi,
            MetadataMode.EMBED,
            OpenAiEmbeddingOptions.builder().withModel("text-embedding-ada-002")
                .build()
        );
    }


    @Bean
    fun vectorStore(
        jdbcTemplate: JdbcTemplate,
        openAiEmbeddingClient: EmbeddingModel
    ) : PgVectorStore {
        logInfo { "Initializing PG Vector Store" }
        return PgVectorStore(jdbcTemplate,openAiEmbeddingClient)

    }

}