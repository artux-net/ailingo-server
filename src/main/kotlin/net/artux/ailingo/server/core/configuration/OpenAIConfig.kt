package net.artux.ailingo.server.core.configuration

import org.springframework.ai.chat.client.ChatClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAIConfig {
    @Bean
    fun chatClient(builder: ChatClient.Builder): ChatClient = builder.build()
}