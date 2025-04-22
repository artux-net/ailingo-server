package net.artux.ailingo.server.service

import net.artux.ailingo.server.model.ImageUploadRequest
import net.artux.ailingo.server.model.ImgbbResponse

interface UploadService {
    suspend fun uploadImage(request: ImageUploadRequest): ImgbbResponse
}