package net.artux.ailingo.server.service.impl

import net.artux.ailingo.server.model.DictionaryResponse
import net.artux.ailingo.server.service.DictionaryService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException

@Service
class DictionaryServiceImpl(
    private val restTemplate: RestTemplate
) : DictionaryService {

    companion object {
        private const val API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/{word}"
    }

    override fun getWordDefinition(word: String): List<DictionaryResponse> {
        return try {
            val response = restTemplate.getForEntity(
                API_URL,
                Array<DictionaryResponse>::class.java,
                word
            )
            response.body?.toList() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Слово '$word' не найдено в словаре")
        } catch (e: HttpClientErrorException.NotFound) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Слово '$word' не найдено в словаре")
        } catch (e: RestClientException) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при получении определения слова: ${e.message}")
        }
    }
}