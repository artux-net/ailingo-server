package net.artux.ailingo.server.service.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.artux.ailingo.server.model.ImageUploadRequest
import net.artux.ailingo.server.model.ImgbbResponse
import net.artux.ailingo.server.service.UploadService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class UploadServiceImpl(private val restTemplate: RestTemplate) : UploadService {

    private val imgbbApiKey = "f90248ad8f4b1e262a5e8e7603645cc1"
    private val imgbbApiUrl = "https://api.imgbb.com/1/upload"

    override suspend fun uploadImage(request: ImageUploadRequest): ImgbbResponse {
        return withContext(Dispatchers.IO) {
            val headers = HttpHeaders()
            headers.contentType = MediaType.MULTIPART_FORM_DATA

            val body: MultiValueMap<String, Any> = LinkedMultiValueMap()
            body.add("key", imgbbApiKey)
            body.add("image", request.image)
            body.add("name", request.name ?: "")
            body.add("expiration", request.expiration?.toString() ?: "")

            val requestEntity = HttpEntity(body, headers)

            try {
                restTemplate.postForObject(imgbbApiUrl, requestEntity, ImgbbResponse::class.java)
            } catch (e: Exception) {
                println("Error uploading image: ${e.message}")
                null
            } ?: ImgbbResponse(null, false, 500)
        }
    }
}