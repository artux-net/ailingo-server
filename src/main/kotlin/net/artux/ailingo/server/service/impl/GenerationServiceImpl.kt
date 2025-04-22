package net.artux.ailingo.server.service.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.artux.ailingo.server.model.TextModel
import net.artux.ailingo.server.service.GenerationService
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class GenerationServiceImpl(private val restTemplate: RestTemplate) : GenerationService {

    companion object {
        private val imageBaseUrl = "https://image.pollinations.ai"
        private val textBaseUrl = "https://text.pollinations.ai"
    }

    override suspend fun generateImage(
        prompt: String,
        model: String?,
        seed: Int?,
        width: Int?,
        height: Int?,
        nologo: Boolean?,
        private: Boolean?,
        enhance: Boolean?,
        safe: Boolean?
    ): ByteArray {
        val uri = UriComponentsBuilder.fromHttpUrl(imageBaseUrl)
            .pathSegment("prompt", prompt)
            .apply {
                model?.let { queryParam("model", it) }
                seed?.let { queryParam("seed", it) }
                width?.let { queryParam("width", it) }
                height?.let { queryParam("height", it) }
                nologo?.let { queryParam("nologo", it) }
                private?.let { queryParam("private", it) }
                enhance?.let { queryParam("enhance", it) }
                safe?.let { queryParam("safe", it) }
            }
            .build()
            .toUri()

        val response = withContext(Dispatchers.IO) {
            restTemplate.getForEntity(uri, ByteArray::class.java)
        }

        if (response.statusCode == HttpStatus.OK && response.body != null) {
            return response.body!!
        } else {
            throw RuntimeException("Failed to fetch image. Status code: ${response.statusCode}")
        }
    }

    override suspend fun getImageModels(): List<String> {
        return withContext(Dispatchers.IO) {
            restTemplate.getForObject("$imageBaseUrl/models", Array<String>::class.java)
        }?.toList() ?: emptyList()
    }

    override suspend fun generateText(
        prompt: String,
        model: String?,
        seed: Int?,
        json: Boolean?,
        system: String?
    ): String {
        val uri = UriComponentsBuilder.fromHttpUrl(textBaseUrl)
            .pathSegment(prompt)
            .apply {
                model?.let { queryParam("model", it) }
                seed?.let { queryParam("seed", it) }
                json?.let { queryParam("json", it) }
                system?.let { queryParam("system", it) }
            }
            .build()
            .toUri()

        val response = withContext(Dispatchers.IO) {
            restTemplate.getForEntity(uri, String::class.java)
        }

        if (response.statusCode == HttpStatus.OK && response.body != null) {
            return response.body!!
        } else {
            throw RuntimeException("Failed to fetch text. Status code: ${response.statusCode}")
        }
    }

    override suspend fun getTextModels(): List<String> {
        val uri = UriComponentsBuilder.fromHttpUrl("https://text.pollinations.ai")
            .pathSegment("models")
            .build()
            .toUri()

        val responseType = object : ParameterizedTypeReference<List<TextModel>>() {}

        val responseEntity = withContext(Dispatchers.IO) {
            restTemplate.exchange(uri, HttpMethod.GET, null, responseType)
        }

        return responseEntity.body?.map { it.name } ?: emptyList()
    }
}