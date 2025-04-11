package net.artux.ailingo.server.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TranslationResponse(
    @JsonProperty("responseData")
    val responseData: ResponseData?,
    @JsonProperty("quotaFinished")
    val quotaFinished: Boolean?,
    @JsonProperty("mtLangSupported")
    val mtLangSupported: Any?,
    @JsonProperty("responseDetails")
    val responseDetails: String?,
    @JsonProperty("responseStatus")
    val responseStatus: Int?,
    @JsonProperty("responderId")
    val responderId: Any?,
    @JsonProperty("exception_code")
    val exceptionCode: Any?,
    @JsonProperty("matches")
    val matches: List<Match>?
)