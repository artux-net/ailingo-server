package net.artux.ailingo.server

import net.artux.ailingo.server.model.PromptRequest
import net.artux.ailingo.server.service.ChatService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class LanguageResponseTest {

    @Autowired
    private lateinit var chatService: ChatService

    @Test
    fun `test that model responds in English when user writes in Russian`() {
        // Given
        val russianInput = "Привет, как дела?"
        val systemPrompt = """
            You are a helpful assistant. You must ALWAYS respond in English, regardless of the input language.
            Never switch to other languages, even if the user writes in a different language.
            If the user writes in a non-English language, acknowledge their message in English and continue the conversation in English.
        """.trimIndent()

        val promptRequest = PromptRequest(
            systemPrompt = systemPrompt,
            userInput = russianInput
        )

        // When
        val response = chatService.testPrompt(promptRequest)

        // Then
        assertNotNull(response, "Response should not be null")
        assertTrue(response.isNotBlank(), "Response should not be blank")
        assertFalse(containsRussianCharacters(response), "Response should not contain Russian characters")
    }

    private fun containsRussianCharacters(text: String): Boolean {
        val russianPattern = "[а-яА-ЯёЁ]".toRegex()
        return russianPattern.containsMatchIn(text)
    }
}
