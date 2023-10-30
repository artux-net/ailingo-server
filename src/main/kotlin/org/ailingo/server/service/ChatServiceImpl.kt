package org.ailingo.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.lilittlecat.chatgpt.offical.ChatGPT
import com.lilittlecat.chatgpt.offical.entity.*
import com.lilittlecat.chatgpt.offical.exception.BizException
import com.lilittlecat.chatgpt.offical.exception.Error
import jakarta.annotation.PostConstruct
import lombok.extern.slf4j.Slf4j
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.*

@Service
@Slf4j
class ChatServiceImpl : ChatService {

    private final var apiKey = "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"
    protected var client: OkHttpClient = OkHttpClient()

    private lateinit var context: String
    private lateinit var userRole: String
    private lateinit var aiRole: String
    private lateinit var chat: ChatGPT
    private var history: LinkedList<Message> = LinkedList()
    private var objectMapper: ObjectMapper = ObjectMapper()

    @PostConstruct
    fun init() {
        chat = ChatGPT(apiKey)

        setContext(
                    "You are waiter. User decide to go to a restaurant to enjoy delicious food and a pleasant atmosphere. " +
                    "When User walked inside, the waiter greeted you and offered User a menu. " +
                    "waiter was very friendly and willing to help you with your food choices. " +
                    "User asked him questions about the composition of the dishes and asked about his recommendations. " +
                    "After user placed order, the waiter went into the kitchen to give it to the chef. " +
                    "User could relax and enjoy socializing with company at the table or simply enjoy the atmosphere of the restaurant. " +
                    "When the dishes were ready, the waiter brought them to user table and checked if everything was fine. " +
                    "User could ask him about how the dishes are prepared or ask for a recommendation for next time. " +
                    "At the end of user meal, the waiter brought you the bill and helped you pay for it. " +
                    "User could leave him a tip if you wanted and bid him farewell, knowing that your evening at the restaurant had gone well."
        )
        setUserRole("User")
        setAIRole("Waiter")
    }

    override fun setContext(context: String) {
        this.context = context
    }

    fun clearContext() {
        history.clear()
        history.addFirst(Message.builder().role("system").content(context).build())
    }

    override fun setAIRole(aiRole: String) {
        this.aiRole = "\n$aiRole: "
    }

    override fun setUserRole(userRole: String) {
        this.userRole = "\n$userRole: "
    }

    fun makeCall(message: String):ChatCompletionResponseBody{
        val message = Message.builder().role("user").content(message).build()
        history.add(message)

        val requestBody = ChatCompletionRequestBody.builder()
            .model("gpt-3.5-turbo")
            .messages(history)
            .build()

        val body: RequestBody =
            objectMapper.writeValueAsString(requestBody)
                .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request: Request = Request.Builder()
            .url(Constant.CHAT_COMPLETION_API_URL)
            .header("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                return if (!response.isSuccessful) {
                    if (response.body == null) {
                        println("Request failed: ${response.message}, please try again")
                        throw BizException(response.code, "Request failed")
                    } else {
                        println("Request failed: ${response.body!!.string()}, please try again")
                        throw BizException(response.code, response.body!!.string())
                    }
                } else {
                    //assert(response.body() != null)
                    val bodyString: String = response.body!!.string()
                    objectMapper.readValue(
                        bodyString,
                        ChatCompletionResponseBody::class.java
                    )
                }
            }
        } catch (e: IOException) {
            println("Request failed: ${e.message}")
            throw BizException(Error.SERVER_HAD_AN_ERROR.code, e.message)
        }
    }


    override fun getResponse(message: String): String {
        val response = makeCall(message)
        val choices: List<ChatCompletionResponseBody.Choice> = response.getChoices()
        val result = StringBuilder()
        for (choice in choices) {
            result.append(choice.getMessage().getContent())
        }
        history.add(Message.builder().role("assistant").content(result.toString()).build())
        if (history.size>7)
            history.removeAt(1)

        return result.toString()
    }
}
