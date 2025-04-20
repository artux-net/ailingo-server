package net.artux.ailingo.server.model.dictionary

import com.fasterxml.jackson.annotation.JsonProperty

data class ResponseData(
    @JsonProperty("translatedText")
    val translatedText: String?,
    @JsonProperty("match")
    val match: Double?
)