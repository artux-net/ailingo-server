package net.artux.ailingo.server.model

import com.fasterxml.jackson.annotation.JsonProperty
import net.artux.ailingo.server.model.dictionary.ResponseData

data class TranslationResponse(
    @JsonProperty("responseData")
    val responseData: ResponseData?,
    @JsonProperty("quotaFinished")
    val quotaFinished: Boolean?,
    @JsonProperty("responseDetails")
    val responseDetails: String?,
    @JsonProperty("responseStatus")
    val responseStatus: Int?,
    @JsonProperty("matches")
    val matches: List<Match>?
)